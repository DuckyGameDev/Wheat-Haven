package com.ducky.wheatwasteland.dimension;

import com.ducky.wheatwasteland.WheatWastelandMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public final class ModDimensions {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(
            WheatWastelandMod.MOD_ID, "wheat_wasteland");

    public static final ResourceKey<Level> WHEAT_WASTELAND = ResourceKey.create(Registries.DIMENSION, ID);
    public static final ResourceKey<DimensionType> WHEAT_WASTELAND_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, ID);

    private ModDimensions() {
    }
}
