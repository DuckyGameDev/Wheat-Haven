package com.ducky.wheathaven.portal;

import com.ducky.wheathaven.WheatHavenMod;
import com.ducky.wheathaven.dimension.ModDimensions;
import com.ducky.wheathaven.registry.ModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = WheatHavenMod.MOD_ID)
public final class PortalEvents {
    private PortalEvents() {
    }

    @SubscribeEvent
    public static void activateHayPortal(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().is(Items.BONE_MEAL) || !event.getLevel().getBlockState(event.getPos()).is(Blocks.HAY_BLOCK)) return;

        var shape = HayPortalShape.findFromFrame(event.getLevel(), event.getPos());
        if (shape.isEmpty()) return;

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        if (!event.getLevel().isClientSide) {
            shape.get().fill(event.getLevel());
            if (!event.getEntity().getAbilities().instabuild) event.getItemStack().shrink(1);
        }
    }

    @SubscribeEvent
    public static void dropReturnSeed(BlockDropsEvent event) {
        if (event.getLevel().dimension() != ModDimensions.WHEAT_HAVEN
                || !event.getState().is(Blocks.WHEAT)
                || event.getState().getValue(CropBlock.AGE) < CropBlock.MAX_AGE
                || event.getLevel().random.nextInt(8) != 0) {
            return;
        }

        var pos = event.getPos();
        event.getDrops().add(new ItemEntity(event.getLevel(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                new ItemStack(ModItems.RETURN_SEED.get())));
    }
}
