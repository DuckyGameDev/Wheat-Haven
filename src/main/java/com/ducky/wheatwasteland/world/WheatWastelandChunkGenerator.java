package com.ducky.wheatwasteland.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class WheatWastelandChunkGenerator extends ChunkGenerator {
    public static final MapCodec<WheatWastelandChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Biome.CODEC.fieldOf("biome").forGetter(generator -> generator.biome))
                    .apply(instance, WheatWastelandChunkGenerator::new));

    private static final int SURFACE_Y = 63;
    private static final int MIN_Y = -64;

    private final Holder<Biome> biome;

    public WheatWastelandChunkGenerator(Holder<Biome> biome) {
        super(new FixedBiomeSource(biome));
        this.biome = biome;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager,
                             StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {
        // The wasteland is intentionally solid and has no caves or ravines.
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState,
                             ChunkAccess chunk) {
        // Terrain is fully produced during fillFromNoise.
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // No generation-time creature spawns.
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return SURFACE_Y;
    }

    @Override
    public int getMinY() {
        return MIN_Y;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor level,
                             RandomState randomState) {
        return SURFACE_Y + 2;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState randomState) {
        BlockState[] states = new BlockState[level.getHeight()];
        int bottomY = level.getMinBuildHeight();

        for (int y = bottomY; y < level.getMaxBuildHeight(); y++) {
            states[y - bottomY] = stateAt(y);
        }
        return new NoiseColumn(bottomY, states);
    }

    @Override
    public void addDebugScreenInfo(List<String> text, RandomState randomState, BlockPos pos) {
        text.add("Wheat Wasteland Generator");
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState,
                                                         StructureManager structureManager, ChunkAccess chunk) {
        generateChunk(chunk);
        return CompletableFuture.completedFuture(chunk);
    }

    private static BlockState stateAt(int y) {
        if (y < MIN_Y || y > SURFACE_Y + 1) {
            return Blocks.AIR.defaultBlockState();
        }
        if (y == MIN_Y) {
            return Blocks.BEDROCK.defaultBlockState();
        }
        if (y < SURFACE_Y) {
            return Blocks.DIRT.defaultBlockState();
        }
        if (y == SURFACE_Y) {
            return Blocks.FARMLAND.defaultBlockState();
        }
        return Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 7);
    }

    private static void generateChunk(ChunkAccess chunk) {
        int startX = chunk.getPos().getMinBlockX();
        int startZ = chunk.getPos().getMinBlockZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int x = startX + localX;
                int z = startZ + localZ;
                for (int y = MIN_Y; y <= SURFACE_Y + 1; y++) {
                    chunk.setBlockState(pos.set(x, y, z), stateAt(y), false);
                }
            }
        }
    }
}
