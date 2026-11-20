package com.easyvillagerreset.easyVillagerReset

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigHolder
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.item.BlockItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.npc.villager.VillagerProfession
import net.minecraft.world.phys.AABB
import net.minecraft.network.chat.Component
import net.minecraft.world.item.trading.MerchantOffers
import net.minecraft.world.InteractionResult

class EasyVillagerReset : ModInitializer {

    companion object {
        // Created once at class-load time — no allocation on every block break
        private val WORKSTATIONS = setOf(
            "lectern", "barrel", "grindstone", "blast_furnace", "smoker",
            "cartography_table", "brewing_stand", "composter", "cauldron",
            "fletching_table", "loom", "smithing_table", "stonecutter"
        )
    }

    private lateinit var configHolder: ConfigHolder<EasyVillagerResetConfig>

    override fun onInitialize() {
        // Register Cloth Config
        configHolder = AutoConfig.register(EasyVillagerResetConfig::class.java, ::GsonConfigSerializer)

        // --- Event 1: Break a workstation → reset the closest villager within radius ---
        PlayerBlockBreakEvents.AFTER.register { world, player, pos, state, _ ->
            if (world !is ServerLevel) return@register

            // Early-exit: O(1) set lookup — skips entity search for non-workstation blocks
            val blockId = BuiltInRegistries.BLOCK.getKey(state.block).path
            if (blockId !in WORKSTATIONS) return@register

            // Find the CLOSEST villager within configured radius that has the configured nametag
            val config = configHolder.config
            val radius = config.resetRadius.toDouble()
            val targetName = config.villagerName
            val villager = world.getEntitiesOfClass(
                Villager::class.java,
                AABB(pos).inflate(radius)
            ) { v -> v.customName?.string == targetName }
                .minByOrNull { it.distanceToSqr(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) }
                ?: return@register

            villager.setOffers(MerchantOffers())
            villager.villagerXp = 0
            villager.tradingPlayer = null

            // Release POI claim + clear brain memories + reinitialize AI
            villager.releasePoi(MemoryModuleType.JOB_SITE)
            villager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE)
            villager.brain.eraseMemory(MemoryModuleType.JOB_SITE)
            villager.brain.eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE)
            villager.villagerData = villager.villagerData
                .withProfession(world.registryAccess(), VillagerProfession.NONE)
                .withLevel(1)
            villager.refreshBrain(world)

            player.sendOverlayMessage(Component.literal("Villager Reset!"))
        }

        // --- Event 2: Place a workstation → allow nearest jobless villager to accept it ---
        UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
            if (world is ServerLevel) {
                val heldItem = player.getItemInHand(hand)

                // Early-exit: must be holding a BlockItem (eliminates tools, food, etc. instantly)
                if (heldItem.item is BlockItem) {
                    val blockId = BuiltInRegistries.ITEM.getKey(heldItem.item).path
                    if (blockId in WORKSTATIONS) {
                        val placedPos = hitResult.blockPos.relative(hitResult.direction)
                        val config = configHolder.config
                        val radius = config.resetRadius.toDouble()
                        val targetName = config.villagerName

                        val villager = world.getEntitiesOfClass(
                            Villager::class.java,
                            AABB(placedPos).inflate(radius)
                        ) { v ->
                            v.villagerData.profession().`is`(VillagerProfession.NONE) && v.customName?.string == targetName
                        }
                            .minByOrNull {
                                it.distanceToSqr(
                                    placedPos.x + 0.5,
                                    placedPos.y + 0.5,
                                    placedPos.z + 0.5
                                )
                            }

                        villager?.let { v ->
                            v.releasePoi(MemoryModuleType.JOB_SITE)
                            v.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE)
                            v.brain.eraseMemory(MemoryModuleType.JOB_SITE)
                            v.brain.eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE)
                            v.refreshBrain(world)
                        }
                    }
                }
            }
            InteractionResult.PASS
        }
    }
}
