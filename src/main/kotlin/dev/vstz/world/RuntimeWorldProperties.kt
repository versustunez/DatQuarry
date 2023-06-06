package dev.vstz.world

import net.minecraft.world.SaveProperties
import net.minecraft.world.level.UnmodifiableLevelProperties

class RuntimeWorldProperties(saveProperties: SaveProperties,) : UnmodifiableLevelProperties(saveProperties, saveProperties.mainWorldProperties) {
}