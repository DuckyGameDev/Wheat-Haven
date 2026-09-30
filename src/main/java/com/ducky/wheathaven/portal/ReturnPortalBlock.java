package com.ducky.wheathaven.portal;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class ReturnPortalBlock extends Block implements Portal {
    public static final MapCodec<ReturnPortalBlock> CODEC = simpleCodec(ReturnPortalBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    private static final VoxelShape X_SHAPE = Block.box(0, 0, 7, 16, 16, 9);
    private static final VoxelShape Z_SHAPE = Block.box(7, 0, 0, 9, 16, 16);

    public ReturnPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) entity.setAsInsidePortal(this, pos);
    }

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel current, Entity entity, BlockPos pos) {
        ServerLevel overworld = current.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return null;

        DimensionTransition.PostDimensionTransition afterTravel =
                DimensionTransition.PLAY_PORTAL_SOUND.then(PortalTravelSound.PLAY).then(Entity::setPortalCooldown);
        if (entity instanceof ServerPlayer player) {
            // Reuse vanilla's bed/respawn-anchor validation and fall back to the world spawn
            // when the saved point no longer exists. `true` keeps a respawn anchor charge.
            return player.findRespawnPositionAndUseSpawnBlock(true, afterTravel);
        }
        return new DimensionTransition(overworld, entity, afterTravel);
    }

    @Override
    public Portal.Transition getLocalTransition() {
        return Portal.Transition.CONFUSION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
}
