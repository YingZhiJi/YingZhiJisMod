package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.*;

public class AshblaziteOreFeature extends Feature<NoneFeatureConfiguration> {

    // 从起点向外扩散的最大曼哈顿距离
    private static final int MAX_SPREAD = 8;

    public AshblaziteOreFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        ChunkPos chunkPos = new ChunkPos(origin);

        Set<BlockPos> placed = new HashSet<>();

        // ===== 第一次：整个地下 0-32 块（有六面判定） =====
        int count1 = random.nextInt(33);
        placeVein(level, random, chunkPos, count1,
                level.getMinBuildHeight(), level.getMaxBuildHeight(), true, placed);

        // ===== 第二次：Y > 0 随机 0-18 块（有六面判定） =====
        int count2 = random.nextInt(19);
        if (count2 > 0) {
            placeVein(level, random, chunkPos, count2,
                    1, level.getMaxBuildHeight(), true, placed);
        }

        // ===== 第三次：检测区块里已有燃灰矿，在它旁边生成 0-8 块（无判定） =====
        if (!placed.isEmpty()) {
            // 从本次已放置的矿石里随机挑一个当锚点
            List<BlockPos> anchors = new ArrayList<>(placed);
            BlockPos anchor = anchors.get(random.nextInt(anchors.size()));
            int count3 = random.nextInt(9);
            if (count3 > 0) {
                placeVeinFrom(level, random, anchor, count3, false, placed);
            }
        }

        return !placed.isEmpty();
    }

    /**
     * 在指定 Y 范围内随机选一个起点，尝试放置一个矿脉。
     */
    private void placeVein(WorldGenLevel level, RandomSource random, ChunkPos chunkPos,
                           int count, int minY, int maxY, boolean checkExposure,
                           Set<BlockPos> allPlaced) {
        if (count <= 0) return;
        int range = Math.max(1, maxY - minY);

        // 最多尝试 20 次找起点，20 次都不行就放弃整次生成
        for (int attempt = 0; attempt < 20; attempt++) {
            int x = chunkPos.getMinBlockX() + random.nextInt(16);
            int z = chunkPos.getMinBlockZ() + random.nextInt(16);
            int y = minY + random.nextInt(range);
            BlockPos start = new BlockPos(x, y, z);

            if (placeVeinFrom(level, random, start, count, checkExposure, allPlaced) > 0) {
                return;
            }
        }
    }

    /**
     * 从 start 开始 BFS 扩散，最多放 count 个方块。
     * checkExposure = true 时，每个方块都要六面至少一面接触空气或燃灰矿。
     * 返回实际放置数量。
     */
    private int placeVeinFrom(WorldGenLevel level, RandomSource random, BlockPos start,
                              int count, boolean checkExposure, Set<BlockPos> allPlaced) {
        if (!canReplace(level, start)) return 0;

        int placedCount = 0;
        Deque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(start);

        int attempts = count * 8;

        while (placedCount < count && attempts-- > 0 && !queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos)) continue;
            if (start.distManhattan(pos) > MAX_SPREAD) continue;
            if (!canReplace(level, pos)) continue;
            if (checkExposure && !hasExposedFace(level, pos)) continue;

            level.setBlock(pos, getOreState(pos), 2);
            allPlaced.add(pos.immutable());
            placedCount++;

            // 随机化邻居顺序，让矿脉形状更自然
            Direction[] dirs = Direction.values();
            int offset = random.nextInt(dirs.length);
            for (int i = 0; i < dirs.length; i++) {
                BlockPos next = pos.relative(dirs[(i + offset) % dirs.length]);
                if (!visited.contains(next)) queue.add(next);
            }
        }

        return placedCount;
    }

    /**
     * 根据 Y 高度返回正确的矿石方块状态。
     */
    private BlockState getOreState(BlockPos pos) {
        return pos.getY() < 0
                ? ModBlocks.ASHBLAZITE_DEEPSLATE_ORE.get().defaultBlockState()
                : ModBlocks.ASHBLAZITE_ORE.get().defaultBlockState();
    }

    /**
     * 判断目标位置能不能被矿石替换。
     */
    private boolean canReplace(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;
        if (!state.getFluidState().isEmpty()) return false;

        if (pos.getY() < 0) {
            return state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        }
        return state.is(BlockTags.STONE_ORE_REPLACEABLES);
    }

    /**
     * 六面判定：至少一面接触空气或燃灰矿。
     */
    private boolean hasExposedFace(WorldGenLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockState neighbor = level.getBlockState(pos.relative(dir));
            if (neighbor.isAir()
                    || neighbor.is(ModBlocks.ASHBLAZITE_ORE.get())
                    || neighbor.is(ModBlocks.ASHBLAZITE_DEEPSLATE_ORE.get())) {
                return true;
            }
        }
        return false;
    }
}