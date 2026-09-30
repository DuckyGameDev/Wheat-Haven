package com.ducky.wheathaven.registry;

import com.ducky.wheathaven.WheatHavenMod;
import com.ducky.wheathaven.portal.HavenPortalBlock;
import com.ducky.wheathaven.portal.ReturnPortalBlock;
import com.ducky.wheathaven.portal.ReturnPlantBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WheatHavenMod.MOD_ID);

    public static final DeferredBlock<HavenPortalBlock> HAVEN_PORTAL = BLOCKS.registerBlock(
            "haven_portal",
            HavenPortalBlock::new,
            BlockBehaviour.Properties.of().noCollission().noLootTable().strength(-1.0F).lightLevel(state -> 11).sound(SoundType.GLASS)
    );

    public static final DeferredBlock<ReturnPortalBlock> RETURN_PORTAL = BLOCKS.registerBlock(
            "return_portal",
            ReturnPortalBlock::new,
            BlockBehaviour.Properties.of().noCollission().noLootTable().strength(-1.0F).lightLevel(state -> 15).sound(SoundType.AMETHYST)
    );

    public static final DeferredBlock<ReturnPlantBlock> RETURN_PLANT = BLOCKS.registerBlock(
            "return_plant",
            ReturnPlantBlock::new,
            BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)
    );

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
