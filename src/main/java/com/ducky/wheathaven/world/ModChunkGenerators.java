package com.ducky.wheathaven.world;

import com.ducky.wheathaven.WheatHavenMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModChunkGenerators {
    private static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, WheatHavenMod.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<WheatHavenChunkGenerator>> WHEAT_HAVEN =
            CHUNK_GENERATORS.register("wheat_haven", () -> WheatHavenChunkGenerator.CODEC);

    private ModChunkGenerators() {
    }

    public static void register(IEventBus modEventBus) {
        CHUNK_GENERATORS.register(modEventBus);
    }
}
