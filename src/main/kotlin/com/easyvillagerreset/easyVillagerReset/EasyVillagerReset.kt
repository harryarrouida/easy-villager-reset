package com.easyvillagerreset.easyVillagerReset

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.registry.Registries
import net.minecraft.village.VillagerProfession
import net.minecraft.util.math.Box
import net.minecraft.text.Text
import net.minecraft.village.TradeOfferList
import net.minecraft.registry.entry.RegistryEntry

class EasyVillagerReset : ModInitializer {
    override fun onInitialize() {
        // Mod Initialized

        PlayerBlockBreakEvents.AFTER.register { world, player, pos, state, _ ->
            if (world.isClient) return@register

            // 2. Debug: Print what block was actually broken
            val blockId = Registries.BLOCK.getId(state.block).path
            if (isWorkstation(blockId)) {

                // 3. Find villagers
                val villagers = world.getEntitiesByClass(
                    VillagerEntity::class.java,
                    Box(pos).expand(5.0) // 5 blocks in all directions
                ) { true }



                villagers.firstOrNull()?.let { villager ->
                    // 1. Get the 'NONE' profession from the registry safely
                    val noneProfession = Registries.VILLAGER_PROFESSION.get(VillagerProfession.NONE)
                    val noneEntry = Registries.VILLAGER_PROFESSION.getEntry(noneProfession)

// 2. Update Villager Data (Profession + Level)
                    val data = villager.villagerData
                        .withProfession(noneEntry)
                        .withLevel(1)

                    villager.villagerData = data

// 3. Clear the trade state
                    villager.offers = TradeOfferList()
                    villager.experience = 0

// 4. Reset memory (Use the setter method to bypass the 'private' error)
// 4. Reset memory (Clear current customer)
                    villager.customer = null

// 5. Notify the player
                    player.sendMessage(Text.literal("Villager Reset!"), true)
                }
            }
        }
    }

    private fun isWorkstation(name: String): Boolean {
        // List of all valid job blocks
        val workstations = setOf(
            "lectern", "barrel", "grindstone", "blast_furnace", "smoker",
            "cartography_table", "brewing_stand", "composter", "cauldron",
            "fletching_table", "loom", "smithing_table", "stonecutter"
        )
        // 'bookshelf' is NOT a workstation, removed it to avoid confusion
        return workstations.contains(name)
    }
}