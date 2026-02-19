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

class EasyVillagerReset : ModInitializer {

    companion object {
        // Created once at class-load time — no allocation on every block break
        private val WORKSTATIONS = setOf(
            "lectern", "barrel", "grindstone", "blast_furnace", "smoker",
            "cartography_table", "brewing_stand", "composter", "cauldron",
            "fletching_table", "loom", "smithing_table", "stonecutter"
        )
    }

    override fun onInitialize() {
        // --- Event 1: Break a workstation → reset the closest villager within 5 blocks ---
        PlayerBlockBreakEvents.AFTER.register { world, player, pos, state, _ ->
            if (world.isClient) return@register

            // Early-exit: O(1) set lookup — skips entity search for non-workstation blocks
            val blockId = Registries.BLOCK.getId(state.block).path
            if (blockId !in WORKSTATIONS) return@register

            // Find the CLOSEST villager within 5 blocks
            val villager = world.getEntitiesByClass(
                VillagerEntity::class.java,
                Box(pos).expand(5.0)
            ) { true }
                .minByOrNull { it.squaredDistanceTo(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) }
                ?: return@register

            // Cache the NONE profession entry once per event, not inside any loop
            val noneEntry = Registries.VILLAGER_PROFESSION.getEntry(
                Registries.VILLAGER_PROFESSION.get(VillagerProfession.NONE)
            )

            villager.villagerData = villager.villagerData.withProfession(noneEntry).withLevel(1)
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

                        // Cache NONE profession once per event
                        val noneProfession = Registries.VILLAGER_PROFESSION.get(VillagerProfession.NONE)

                        val villager = world.getEntitiesByClass(
                            VillagerEntity::class.java,
                            Box(placedPos).expand(5.0)
                        ) { v -> v.villagerData.profession == noneProfession }
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