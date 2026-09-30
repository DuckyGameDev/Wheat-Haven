package com.ducky.wheathaven.portal;

import com.ducky.wheathaven.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.portal.DimensionTransition;

public final class PortalTravelSound {
    private PortalTravelSound() {
    }

    public static final DimensionTransition.PostDimensionTransition PLAY = entity ->
            entity.level().playSound(null, entity.blockPosition(), ModSounds.PORTAL_TRAVEL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
}
