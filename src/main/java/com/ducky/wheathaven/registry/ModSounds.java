package com.ducky.wheathaven.registry;

import com.ducky.wheathaven.WheatHavenMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, WheatHavenMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_TRAVEL = SOUNDS.register(
            "portal_travel",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WheatHavenMod.MOD_ID, "portal_travel"))
    );

    private ModSounds() {
    }

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}
