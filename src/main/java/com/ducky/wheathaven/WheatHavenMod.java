package com.ducky.wheathaven;

import com.ducky.wheathaven.world.ModChunkGenerators;
import com.ducky.wheathaven.registry.ModBlocks;
import com.ducky.wheathaven.registry.ModItems;
import com.ducky.wheathaven.registry.ModSounds;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(WheatHavenMod.MOD_ID)
public final class WheatHavenMod {
    public static final String MOD_ID = "wheathaven";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WheatHavenMod(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModChunkGenerators.register(modEventBus);
        LOGGER.info("🌾 Wheat Haven loaded!");
    }
}
