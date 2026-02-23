package com.easyvillagerreset.easyVillagerReset

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.autoconfig.AutoConfig

class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent ->
            AutoConfig.getConfigScreen(EasyVillagerResetConfig::class.java, parent).get()
        }
    }
}
