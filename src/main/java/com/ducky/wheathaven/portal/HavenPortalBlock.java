package com.ducky.wheathaven.portal;

import com.ducky.wheathaven.dimension.ModDimensions;
import com.ducky.wheathaven.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class HavenPortalBlock extends Block implements Portal {
    public static final MapCodec<HavenPortalBlock> CODEC = simpleCodec(HavenPortalBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    private static final VoxelShape X_SHAPE = Block.box(0, 0, 6, 16, 16, 10);
    private static final VoxelShape Z_SHAPE = Block.box(6, 0, 0, 10, 16, 16);

    public HavenPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (HayPortalShape.findFromInterior(level, pos, state.getValue(AXIS)).isEmpty()) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) entity.setAsInsidePortal(this, pos);
    }

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel current, Entity entity, BlockPos portalPos) {
        boolean leaving = current.dimension() == ModDimensions.WHEAT_HAVEN;
        ServerLevel target = current.getServer().getLevel(leaving ? Level.OVERWORLD : ModDimensions.WHEAT_HAVEN);
        if (target == null) return null;

        if (leaving) {
            return new DimensionTransition(target, entity, DimensionTransition.PLAY_PORTAL_SOUND.then(PortalTravelSound.PLAY));
        }

        int x = portalPos.getX();
        int z = portalPos.getZ();
        // The custom generator has a fixed surface. Heightmaps are not populated early enough
        // when a fresh destination chunk is generated, so querying one could return minY (-64).
        int y = 65;
        Vec3 destination = new Vec3(x + 0.5, y, z + 0.5);
        return new DimensionTransition(target, destination, Vec3.ZERO, entity.getYRot(), entity.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND.then(PortalTravelSound.PLAY).then(arrived -> {
                    arrived.setPortalCooldown();
                    if (arrived instanceof ServerPlayer player && !player.getInventory().contains(new ItemStack(ModItems.RETURN_SEED.get()))) {
                        player.getInventory().add(new ItemStack(ModItems.RETURN_SEED.get()));
                    }
                }));
    }

    @Override
    public Portal.Transition getLocalTransition() {
        return Portal.Transition.CONFUSION;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> state.cycle(AXIS);
            default -> state;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
}
