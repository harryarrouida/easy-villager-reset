package com.easyvillagerreset.easyVillagerReset

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry

@Config(name = "easy-villager-reset")
class EasyVillagerResetConfig : ConfigData {
    @ConfigEntry.BoundedDiscrete(min = 1, max = 32)
    @ConfigEntry.Gui.Tooltip
    var resetRadius: Int = 5

    @ConfigEntry.Gui.Tooltip
    var villagerName: String = "easy-villager"
}
