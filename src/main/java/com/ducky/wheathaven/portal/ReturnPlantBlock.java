package com.ducky.wheathaven.portal;

import com.ducky.wheathaven.registry.ModBlocks;
import com.ducky.wheathaven.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ReturnPlantBlock extends CropBlock {
    public static final MapCodec<ReturnPlantBlock> CODEC = simpleCodec(ReturnPlantBlock::new);

    public ReturnPlantBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends CropBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.RETURN_SEED.get();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        transformIfMature(level, pos);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        // Keep ticking a fully grown plant as well. This guarantees that plants which
        // reach age 7 naturally (or through another mod) still unfold into a portal.
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        super.performBonemeal(level, random, pos, state);
        transformIfMature(level, pos);
    }

    private void transformIfMature(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(this) && isMaxAge(state)) {
            Direction direction = getRightDirection(level, pos);
            Direction.Axis axis = direction.getAxis();
            BlockState portal = ModBlocks.RETURN_PORTAL.get().defaultBlockState().setValue(ReturnPortalBlock.AXIS, axis);
            BlockPos left = pos;
            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < 3; y++) {
                    BlockPos portalPos = left.relative(direction, x).above(y);
                    if (!portalPos.equals(pos) && !level.getBlockState(portalPos).isAir()) {
                        level.destroyBlock(portalPos, true);
                    }
                    level.setBlock(portalPos, portal, 3);
                }
            }
        }
    }

    private Direction getRightDirection(Level level, BlockPos pos) {
        Player nearestPlayer = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 12.0, false);
        // Clockwise is the player's right-hand side. The wheat in that column is
        // deliberately broken before the portal blocks are placed.
        return nearestPlayer == null ? Direction.EAST : nearestPlayer.getDirection().getClockWise();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.RETURN_SEED.get());
    }
}
