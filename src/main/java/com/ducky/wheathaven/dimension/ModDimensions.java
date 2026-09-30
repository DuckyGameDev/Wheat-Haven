package com.ducky.wheathaven.dimension;

import com.ducky.wheathaven.WheatHavenMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public final class ModDimensions {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(
            WheatHavenMod.MOD_ID, "wheat_haven");

    public static final ResourceKey<Level> WHEAT_HAVEN = ResourceKey.create(Registries.DIMENSION, ID);
    public static final ResourceKey<DimensionType> WHEAT_HAVEN_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, ID);

    private ModDimensions() {
    }
}
