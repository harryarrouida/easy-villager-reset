package com.easyvillagerreset.easyVillagerReset

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.village.VillagerProfession
import net.minecraft.util.math.Box
import net.minecraft.text.Text
import net.minecraft.village.TradeOfferList
import net.minecraft.util.ActionResult
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer

class EasyVillagerReset : ModInitializer {

    companion object {
        // Created once at class-load time — no allocation on every block break
        private val WORKSTATIONS = setOf(
            "lectern", "barrel", "grindstone", "blast_furnace", "smoker",
            "cartography_table", "brewing_stand", "composter", "cauldron",
            "fletching_table", "loom", "smithing_table", "stonecutter"
        )
    }

    private lateinit var configHolder: me.shedaniel.autoconfig.ConfigHolder<EasyVillagerResetConfig>
    private lateinit var noneProfession: VillagerProfession
    private lateinit var noneProfessionEntry: net.minecraft.registry.entry.RegistryEntry<VillagerProfession>

    override fun onInitialize() {
        // Register Cloth Config
        configHolder = AutoConfig.register(EasyVillagerResetConfig::class.java, ::GsonConfigSerializer)

        // Cache professions for performance
        noneProfession = Registries.VILLAGER_PROFESSION.get(VillagerProfession.NONE)!!
        noneProfessionEntry = Registries.VILLAGER_PROFESSION.getEntry(noneProfession)

        // --- Event 1: Break a workstation → reset the closest villager within radius ---
        PlayerBlockBreakEvents.AFTER.register { world, player, pos, state, _ ->
            if (world.isClient) return@register

            // Early-exit: O(1) set lookup — skips entity search for non-workstation blocks
            val blockId = Registries.BLOCK.getId(state.block).path
            if (blockId !in WORKSTATIONS) return@register

            // Find the CLOSEST villager within configured radius that has the configured nametag
            val config = configHolder.config
            val radius = config.resetRadius.toDouble()
            val targetName = config.villagerName
            val villager = world.getEntitiesByClass(
                VillagerEntity::class.java,
                Box(pos).expand(radius)
            ) { v -> v.customName?.string == targetName }
                .minByOrNull { it.squaredDistanceTo(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) }
                ?: return@register

            villager.villagerData = villager.villagerData.withProfession(noneProfessionEntry).withLevel(1)
            villager.offers = TradeOfferList()
            villager.experience = 0
            villager.customer = null

            // Release POI claim + clear brain memories + reinitialize AI
            villager.releaseTicketFor(MemoryModuleType.JOB_SITE)
            villager.releaseTicketFor(MemoryModuleType.POTENTIAL_JOB_SITE)
            villager.brain.forget(MemoryModuleType.JOB_SITE)
            villager.brain.forget(MemoryModuleType.POTENTIAL_JOB_SITE)
            villager.reinitializeBrain(world as ServerWorld)

            player.sendMessage(Text.literal("Villager Reset!"), true)
        }

        // --- Event 2: Place a workstation → allow nearest jobless villager to accept it ---
        UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
            if (!world.isClient) {
                val heldItem = player.getStackInHand(hand)

                // Early-exit: must be holding a BlockItem (eliminates tools, food, etc. instantly)
                if (heldItem.item is BlockItem) {
                    val blockId = Registries.ITEM.getId(heldItem.item).path
                    if (blockId in WORKSTATIONS) {
                        val placedPos = hitResult.blockPos.offset(hitResult.side)
                        val config = configHolder.config
                        val radius = config.resetRadius.toDouble()
                        val targetName = config.villagerName

                        val villager = world.getEntitiesByClass(
                            VillagerEntity::class.java,
                            Box(placedPos).expand(radius)
                        ) { v -> v.villagerData.profession == noneProfession && v.customName?.string == targetName }
                            .minByOrNull {
                                it.squaredDistanceTo(
                                    placedPos.x + 0.5,
                                    placedPos.y + 0.5,
                                    placedPos.z + 0.5
                                )
                            }

                        villager?.let { v ->
                            v.releaseTicketFor(MemoryModuleType.JOB_SITE)
                            v.releaseTicketFor(MemoryModuleType.POTENTIAL_JOB_SITE)
                            v.brain.forget(MemoryModuleType.JOB_SITE)
                            v.brain.forget(MemoryModuleType.POTENTIAL_JOB_SITE)
                            v.reinitializeBrain(world as ServerWorld)
                        }
                    }
                }
            }
            ActionResult.PASS
        }
    }
}