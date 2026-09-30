package com.ducky.wheathaven.portal;

import com.ducky.wheathaven.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public record HayPortalShape(BlockPos bottomLeft, Direction.Axis axis, Direction right, int width, int height) {
    private static final int MAX_SIZE = 21;

    public static Optional<HayPortalShape> findFromFrame(LevelAccessor level, BlockPos clicked) {
        for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
            Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
            for (int horizontal = -MAX_SIZE; horizontal <= MAX_SIZE; horizontal++) {
                for (int vertical = -MAX_SIZE; vertical <= MAX_SIZE; vertical++) {
                    BlockPos candidate = clicked.relative(right, horizontal).relative(Direction.UP, vertical);
                    HayPortalShape shape = validate(level, candidate, axis, true);
                    if (shape != null && shape.isFramePosition(clicked)) {
                        return Optional.of(shape);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<HayPortalShape> findFromInterior(LevelAccessor level, BlockPos interior, Direction.Axis axis) {
        Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        for (int horizontal = -MAX_SIZE; horizontal <= 0; horizontal++) {
            for (int vertical = -MAX_SIZE; vertical <= 0; vertical++) {
                BlockPos candidate = interior.relative(right, horizontal).relative(Direction.UP, vertical);
                HayPortalShape shape = validate(level, candidate, axis, false);
                if (shape != null && shape.isInteriorPosition(interior)) {
                    return Optional.of(shape);
                }
            }
        }
        return Optional.empty();
    }

    private static HayPortalShape validate(LevelAccessor level, BlockPos bottomLeft, Direction.Axis axis, boolean mustBeEmpty) {
        Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        if (!isHay(level, bottomLeft.below()) || !isHay(level, bottomLeft.relative(right.getOpposite()))) {
            return null;
        }

        int width = 0;
        while (width <= MAX_SIZE && isInterior(level.getBlockState(bottomLeft.relative(right, width)), mustBeEmpty)
                && isHay(level, bottomLeft.relative(right, width).below())) {
            width++;
        }
        if (width < 2 || width > MAX_SIZE || !isHay(level, bottomLeft.relative(right, width))) {
            return null;
        }

        for (int height = 0; height <= MAX_SIZE; height++) {
            if (!isHay(level, bottomLeft.relative(Direction.UP, height).relative(right.getOpposite()))
                    || !isHay(level, bottomLeft.relative(Direction.UP, height).relative(right, width))) {
                return null;
            }

            boolean topRow = true;
            for (int x = 0; x < width; x++) {
                if (!isHay(level, bottomLeft.relative(right, x).relative(Direction.UP, height))) {
                    topRow = false;
                    break;
                }
            }
            if (topRow) {
                return height >= 3 ? new HayPortalShape(bottomLeft.immutable(), axis, right, width, height) : null;
            }

            for (int x = 0; x < width; x++) {
                if (!isInterior(level.getBlockState(bottomLeft.relative(right, x).relative(Direction.UP, height)), mustBeEmpty)) {
                    return null;
                }
            }
        }
        return null;
    }

    private static boolean isHay(LevelAccessor level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.HAY_BLOCK);
    }

    private static boolean isInterior(BlockState state, boolean mustBeEmpty) {
        return state.isAir() || state.is(Blocks.FIRE) || (!mustBeEmpty && state.is(ModBlocks.HAVEN_PORTAL.get()));
    }

    public void fill(LevelAccessor level) {
        BlockState portal = ModBlocks.HAVEN_PORTAL.get().defaultBlockState().setValue(HavenPortalBlock.AXIS, axis);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                level.setBlock(bottomLeft.relative(right, x).above(y), portal, 18);
            }
        }
    }

    private boolean isInteriorPosition(BlockPos pos) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bottomLeft.relative(right, x).above(y).equals(pos)) return true;
            }
        }
        return false;
    }

    private boolean isFramePosition(BlockPos pos) {
        for (int x = -1; x <= width; x++) {
            if (bottomLeft.relative(right, x).below().equals(pos) || bottomLeft.relative(right, x).above(height).equals(pos)) return true;
        }
        for (int y = 0; y < height; y++) {
            if (bottomLeft.relative(right.getOpposite()).above(y).equals(pos) || bottomLeft.relative(right, width).above(y).equals(pos)) return true;
        }
        return false;
    }
}
