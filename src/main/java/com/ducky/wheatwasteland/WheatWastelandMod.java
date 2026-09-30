package com.ducky.wheatwasteland;

import com.ducky.wheatwasteland.world.ModChunkGenerators;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(WheatWastelandMod.MOD_ID)
public final class WheatWastelandMod {
    public static final String MOD_ID = "wheatwasteland";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WheatWastelandMod(IEventBus modEventBus) {
        ModChunkGenerators.register(modEventBus);
        LOGGER.info("🌾 Wheat Wasteland loaded!");
    }
}
