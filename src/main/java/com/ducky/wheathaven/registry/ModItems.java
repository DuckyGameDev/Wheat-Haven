package com.ducky.wheathaven.registry;

import com.ducky.wheathaven.WheatHavenMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WheatHavenMod.MOD_ID);

    public static final DeferredItem<ItemNameBlockItem> RETURN_SEED = ITEMS.register(
            "return_seed",
            () -> new ItemNameBlockItem(ModBlocks.RETURN_PLANT.get(), new Item.Properties())
    );

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
