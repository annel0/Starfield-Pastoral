package com.stardew.craft.mining;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * 矿井楼层生成器
 * 
 * 按照 Implementation Plan + MINING_REGISTRATION_CHECKLIST.md：
 * - 每层中心 (0, 64, floor*100)
 * - 尺寸：20×20 到 50×50（随机）
 * - 高度：6层（Y=64到Y=69）
 * - 外壳：mine_barrier 封印石
 * - 内部频次：主石头(80-90%) >> 装饰石头(5-15%) > 原版方块(1-5%)
 * - 装饰石头按用途选择：
 *   - banded_marble: 冰段洞窟装饰、宝箱房地面
 *   - limestone: 土段洞窟、靠近木桶/矿车区域
 *   - mossy_sandstone: 土段过渡、隐藏房间的潮湿角落
 *   - cracked_slate: 三段通用，适合边缘/墙面阴影区
 *   - scoria: 熔岩段洞窟、火山气息装饰
 *   - salt_rock: 冰段、特殊洞窟标志材
 */
public class MineFloorGenerator {

    private static final int GENERATION_VERSION = 7;
    private static final double ORE_RATE_MULTIPLIER = 0.25;
    // A类矿（直接采集）在洞窟表面的概率（比原版略高）
    private static final double SURFACE_MINERAL_RATE = 0.006;
    // B类矿（宝石矿石节点）生成概率倍率（比原版略高）
    private static final double GEM_NODE_RATE_MULTIPLIER = 1.4;
    // B类矿更偏向洞窟表面
    private static final double GEM_SURFACE_BIAS = 0.7;
    
    // 房间尺寸范围
    private static final int MIN_SIZE = 80;
    private static final int MAX_SIZE = 120;
    
    // 房间高度（固定10层，向上/向下各扩展2格）
    private static final int FLOOR_HEIGHT = 20;
    private static final int FLOOR_Y_START = 62;
    private static final int FLOOR_Y_END = FLOOR_Y_START + FLOOR_HEIGHT - 1; // 62-71
    
    // 中心安全区半径（3×3）
    private static final int SAFE_ZONE_RADIUS = 1;
    // 洞窟与安全区的缓冲距离（避免擦边）
    private static final int SAFE_ZONE_BUFFER = 2;
    private static final int SAFE_ZONE_Y_START = 65;
    private static final int SAFE_ZONE_Y_END = 67;

    // 洞窟与地板/天花板的最小间距（至少留 1-2 层）
    private static final int CAVE_VERTICAL_PADDING = 2;
    private static final String LADDER_HIGHLIGHT_TAG = "stardewcraft_mine_ladder_highlight";
    
    /**
     * 生成指定楼层
     */
    public static void generateFloor(ServerLevel level, int floorNumber) {
        RandomSource random = level.getRandom();

        // 每天只生成一次：同一天内进入不刷新
        MineFloorDataManager manager = MineFloorDataManager.get(level);
        MineFloorData existingData = manager.getFloorData(floorNumber);
        if (!manager.needsGeneration(floorNumber)
            && existingData != null
            && existingData.getGenerationVersion() == GENERATION_VERSION) {
            StardewCraft.LOGGER.info("[MINE] Floor {} already generated today, skipping refresh.", floorNumber);
            return;
        }
        manager.clearFloorData(floorNumber);
        
        // 1. 确定房间尺寸（20-50随机）
        int size = MIN_SIZE + random.nextInt(MAX_SIZE - MIN_SIZE + 1);
        
        // 2. 确定主题
        FloorTheme theme = getThemeForFloor(floorNumber);
        boolean isDark = random.nextDouble() < 0.15; // 15% 概率为 Dark 层
        
    // 3. 计算中心坐标
    int centerX = 0;
    // 第0层特殊处理：Z=0；其他层：Z=floor*100+14
    int centerZ = (floorNumber == 0) ? 0 : (floorNumber * MiningCoordinates.FLOOR_SPACING + 14);
        
        StardewCraft.LOGGER.info("[MINE] Generating floor {} at center ({}, {}) with size {}x{}, theme: {}, dark: {}", 
            floorNumber, centerX, centerZ, size, size, theme, isDark);
        
        // 4. 生成外壳（mine_barrier）
        generateShell(level, centerX, centerZ, size);
        
        // 5. 填充主石头 + 装饰石头 + 原版方块（按频次：80-90% > 5-15% > 1-5%）
        fillInterior(level, random, centerX, centerZ, size, theme, isDark);

    // 5.1 装饰石头斑块（类似原版花岗岩/闪长岩）
    generateDecorPatches(level, random, centerX, centerZ, size, theme);
        
    // 6. 生成洞窟（原版顺序：carver在前）
    generateCaves(level, random, centerX, centerZ, size, theme);

    // 7. 生成矿石（避免被洞窟挖空）
    generateOres(level, random, centerX, centerZ, size, floorNumber, theme);
        
        // 8. 生成中心安全区（清空玩家出生点周围）
        generateSafeZone(level, centerX, centerZ, floorNumber);
        if (floorNumber < 120) {
            generateLevelExit(level, random, centerX, centerZ, size, floorNumber, manager.getOrCreateFloorData(floorNumber, 0));
        }

        // 9. 生成A类矿（洞窟表面）与B类矿（宝石矿石节点）
        generateSurfaceMinerals(level, random, centerX, centerZ, size, floorNumber);
        generateGemOreNodes(level, random, centerX, centerZ, size, floorNumber);
        
    // 10. 统计可计数石头数量并初始化楼层数据
        int stonesLeft = countStones(level, centerX, centerZ, size);
        MineFloorData floorData = manager.getOrCreateFloorData(floorNumber, stonesLeft);
        floorData.setStonesLeft(stonesLeft);
    floorData.setGenerationVersion(GENERATION_VERSION);
        manager.setFloorData(floorNumber, floorData);

    // 记录当日已生成
    manager.markGenerated(floorNumber);
        
        StardewCraft.LOGGER.info("[MINE] Floor {} generation complete, stonesLeft: {}", floorNumber, stonesLeft);
    }
    
    /**
     * 统计楼层中可计数的石头数量
     */
    private static int countStones(ServerLevel level, int centerX, int centerZ, int size) {
        int halfSize = size / 2;
        int count = 0;
        
        for (int x = centerX - halfSize + 1; x < centerX + halfSize; x++) {
            for (int z = centerZ - halfSize + 1; z < centerZ + halfSize; z++) {
                for (int y = FLOOR_Y_START; y <= FLOOR_Y_END; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = level.getBlockState(pos).getBlock();
                    
                    // 检查是否是可计数的主石头
                    if (isCountableStone(block)) {
                        count++;
                    }
                }
            }
        }
        
        return count;
    }
    
    /**
     * 检查方块是否是可计数的主石头
     */
    private static boolean isCountableStone(Block block) {
        // 主石头（6种）
        return block == ModBlocks.EARTH_SHALE.get() ||
               block == ModBlocks.FROST_GNEISS.get() ||
               block == ModBlocks.LAVA_BASALT.get() ||
               // 装饰石头也计数
               block == ModBlocks.BANDED_MARBLE.get() ||
               block == ModBlocks.LIMESTONE.get() ||
               block == ModBlocks.MOSSY_SANDSTONE.get() ||
               block == ModBlocks.CRACKED_SLATE.get() ||
               block == ModBlocks.SCORIA.get() ||
               block == ModBlocks.SALT_ROCK.get();
    }
    
    /**
     * 生成外壳（mine_barrier包裹整个房间）
     */
    @SuppressWarnings("null")
    private static void generateShell(ServerLevel level, int centerX, int centerZ, int size) {
        int halfSize = size / 2;
        Block barrier = ModBlocks.MINE_BARRIER.get();
        
        // 遍历整个房间范围
        for (int x = centerX - halfSize; x <= centerX + halfSize; x++) {
            for (int z = centerZ - halfSize; z <= centerZ + halfSize; z++) {
                // 底层（Y=63，比地板低1格）
                level.setBlock(new BlockPos(x, FLOOR_Y_START - 1, z), barrier.defaultBlockState(), 3);
                
                // 顶层（Y=70，比天花板高1格）
                level.setBlock(new BlockPos(x, FLOOR_Y_END + 1, z), barrier.defaultBlockState(), 3);
                
                // 四周墙壁（只在边缘）
                if (x == centerX - halfSize || x == centerX + halfSize || 
                    z == centerZ - halfSize || z == centerZ + halfSize) {
                    for (int y = FLOOR_Y_START; y <= FLOOR_Y_END; y++) {
                        level.setBlock(new BlockPos(x, y, z), barrier.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
    
    /**
     * 填充内部区域（主石头 + 装饰石头 + 原版方块）
     * 频次：主石头(80-90%) >> 装饰石头(5-15%) > 原版方块(1-5%)
     */
    @SuppressWarnings("null")
    private static void fillInterior(ServerLevel level, RandomSource random, 
                                     int centerX, int centerZ, int size, 
                                     FloorTheme theme, boolean isDark) {
        int halfSize = size / 2;
        
        // 填充内部区域（不包括最外层，那是墙）
        for (int x = centerX - halfSize + 1; x < centerX + halfSize; x++) {
            for (int z = centerZ - halfSize + 1; z < centerZ + halfSize; z++) {
                int dx = Math.abs(x - centerX);
                int dz = Math.abs(z - centerZ);
                
                // 填充 Y=64 到 Y=69（6层）
                for (int y = FLOOR_Y_START; y <= FLOOR_Y_END; y++) {
                    // 仅跳过安全区 3×3×3（65-67三层）
                    if (dx <= SAFE_ZONE_RADIUS && dz <= SAFE_ZONE_RADIUS
                        && y >= SAFE_ZONE_Y_START && y <= SAFE_ZONE_Y_END) {
                        continue;
                    }
                    // 随机选择方块类型
                    float roll = random.nextFloat();
                    Block block;
                    
                    if (roll < 0.87f) {
                        // 80-90% 主石头（可能混入dark变体）
                        block = getMainStone(theme, isDark && random.nextFloat() < 0.3f);
                    } else if (roll < 0.96f) {
                        // 5-15% 装饰石头（按主题和用途选择）
                        block = getDecorativeStone(theme, random);
                    } else {
                        // 1-5% 原版方块（按主题点缀）
                        block = getVanillaBlock(theme, random);
                    }
                    
                    level.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), 3);
                }
            }
        }
    }
    
    /**
     * 生成矿石（精确按照 MINING_IMPLEMENTATION_PLAN.md 的概率）
     * 
     * 核心概率：
     * - 主矿（铜/铁/金）：0.17% 每个石头
     * - 铱矿：0.03% (80-99层) → 0.05% (100-119层)
     * - 煤矿：0.06% 全层通用
     * 
     * 跨段规则：
     * - 铜矿：1-39正常(0.17%)，40-119减半(0.085%)
     * - 铁矿：40-79正常(0.17%)，80-119减半(0.085%)
     * - 金矿：80-119正常(0.17%)
     * - 铱矿：80-99低(0.03%)，100-119高(0.05%)
     */
    private static void generateOres(ServerLevel level, RandomSource random,
                                     int centerX, int centerZ, int size,
                                     int floorNumber, FloorTheme theme) {
        int halfSize = size / 2;
        int totalPlaced = 0;

        // 使用矿脉生成（原版风格）
        int veinCount = getOreVeinCount(floorNumber, size);
        if (veinCount <= 0) {
            return;
        }

        for (int i = 0; i < veinCount; i++) {
            int x = centerX + random.nextInt(size - 10) - (size - 10) / 2;
            int z = centerZ + random.nextInt(size - 10) - (size - 10) / 2;
            int y = FLOOR_Y_START + random.nextInt(FLOOR_HEIGHT);

            if (Math.abs(x - centerX) <= SAFE_ZONE_RADIUS && Math.abs(z - centerZ) <= SAFE_ZONE_RADIUS) {
                continue;
            }

            String oreKey = pickOreKeyForFloor(random, floorNumber);

            Block oreBlock = getOreBlock(theme, oreKey);
            if (oreBlock == null) {
                continue;
            }

            int veinSize = getOreVeinSize(random, oreKey, floorNumber);

            totalPlaced += generateOreVein(level, random, x, y, z, veinSize, oreBlock, centerX, centerZ, halfSize);
        }

        // 兜底：如果本层没有生成任何矿石，强制放置一小簇主矿
        if (totalPlaced == 0) {
            String primaryOreKey = getPrimaryOreKeyForFloor(floorNumber);
            Block primaryOre = getOreBlock(theme, primaryOreKey);
            if (primaryOre != null) {
                placeFallbackOreCluster(level, centerX, centerZ, size, primaryOre, halfSize);
            }
        }
    }

    /**
     * 生成装饰石头斑块（类似原版石头变种分布）
     */
    private static void generateDecorPatches(ServerLevel level, RandomSource random,
                                              int centerX, int centerZ, int size, FloorTheme theme) {
        int halfSize = size / 2;
        int patchCount = 6 + random.nextInt(6); // 6-11 个斑块

        for (int i = 0; i < patchCount; i++) {
            int patchSize = 6 + random.nextInt(9); // 6-14
            int x = centerX + random.nextInt(size - 10) - (size - 10) / 2;
            int z = centerZ + random.nextInt(size - 10) - (size - 10) / 2;
            int y = FLOOR_Y_START + random.nextInt(FLOOR_HEIGHT);

            if (Math.abs(x - centerX) <= SAFE_ZONE_RADIUS && Math.abs(z - centerZ) <= SAFE_ZONE_RADIUS) {
                continue;
            }

            Block decor = getDecorativeStone(theme, random);
            generateBlob(level, random, x, y, z, patchSize, decor, centerX, centerZ, halfSize);
        }
    }

    private static void generateBlob(ServerLevel level, RandomSource random,
                                     int startX, int startY, int startZ, int size,
                                     Block block, int centerX, int centerZ, int halfSize) {
        double x = startX;
        double y = startY;
        double z = startZ;

        for (int i = 0; i < size; i++) {
            double radius = 1.5 + random.nextFloat() * 1.5;
            replaceMainStoneSphere(level, x, y, z, radius, block, centerX, centerZ, halfSize);

            x += random.nextInt(3) - 1;
            y += random.nextInt(3) - 1;
            z += random.nextInt(3) - 1;
        }
    }

    private static int getOreVeinCount(int floor, int size) {
        double stoneEstimate = size * size * FLOOR_HEIGHT * 0.8; // 估算每层石头数量（体积）
        double totalProbability = getTotalOreProbabilityForFloor(floor);
        double expectedOreBlocks = stoneEstimate * totalProbability;
    double averageVeinSize = 4.5 + getThemeProgress(floor) * 1.0;

        int count = (int)Math.round(expectedOreBlocks / averageVeinSize);
        return Math.max(6, count);
    }

    private static double getTotalOreProbabilityForFloor(int floor) {
        if (floor >= 1 && floor <= 39) {
            double depthFactor = getThemeDepthFactor(floor);
            return (0.0164 + 0.0020) * depthFactor * ORE_RATE_MULTIPLIER; // copper + coal
        }
        if (floor >= 40 && floor <= 79) {
            double depthFactor = getThemeDepthFactor(floor);
            return (0.0140 + 0.0090 + 0.0022) * depthFactor * ORE_RATE_MULTIPLIER; // iron + copper + coal
        }
        if (floor >= 80 && floor <= 119) {
            double depthFactor = getThemeDepthFactor(floor);
            double iridium = (floor >= 100) ? 0.004 : 0.0024;
            return (iridium + 0.0136 + 0.0084 + 0.0084 + 0.0026) * depthFactor * ORE_RATE_MULTIPLIER; // iridium + gold + iron + copper + coal
        }
        return 0.0;
    }

    private static String pickOreKeyForFloor(RandomSource random, int floor) {
        double progress = getThemeProgress(floor);
        double commonFactor = 1.0 + progress * 0.5;
        double rareFactor = 1.0 + progress * 1.4;
        double cumulative = 0.0;

        // 基于 MINING_IMPLEMENTATION_PLAN.md 概率 * 4（作为权重），并随深度提升
        if (floor >= 1 && floor <= 39) {
            double copper = 0.0164 * commonFactor * ORE_RATE_MULTIPLIER;
            double coal = 0.0020 * commonFactor * ORE_RATE_MULTIPLIER;
            double total = copper + coal;
            double roll = random.nextDouble() * total;
            cumulative += copper;
            if (roll < cumulative) return "copper";
            return "coal";
        }

        if (floor >= 40 && floor <= 79) {
            double iron = 0.0140 * rareFactor * ORE_RATE_MULTIPLIER;
            double copper = 0.0090 * commonFactor * ORE_RATE_MULTIPLIER;
            double coal = 0.0022 * commonFactor * ORE_RATE_MULTIPLIER;
            double total = iron + copper + coal;
            double roll = random.nextDouble() * total;
            cumulative += iron;
            if (roll < cumulative) return "iron";
            cumulative += copper;
            if (roll < cumulative) return "copper";
            return "coal";
        }

        if (floor >= 80 && floor <= 119) {
            double iridium = (floor >= 100) ? 0.004 : 0.0024;
            double gold = 0.0136;
            double iron = 0.0084;
            double copper = 0.0084;
            double coal = 0.0026;
            iridium *= rareFactor * ORE_RATE_MULTIPLIER;
            gold *= rareFactor * ORE_RATE_MULTIPLIER;
            iron *= commonFactor * ORE_RATE_MULTIPLIER;
            copper *= commonFactor * ORE_RATE_MULTIPLIER;
            coal *= commonFactor * ORE_RATE_MULTIPLIER;
            double total = iridium + gold + iron + copper + coal;
            double roll = random.nextDouble() * total;
            cumulative += iridium;
            if (roll < cumulative) return "iridium";
            cumulative += gold;
            if (roll < cumulative) return "gold";
            cumulative += iron;
            if (roll < cumulative) return "iron";
            cumulative += copper;
            if (roll < cumulative) return "copper";
            return "coal";
        }

        return "coal";
    }

    private static double getThemeDepthFactor(int floor) {
        double progress = getThemeProgress(floor);
        if (floor >= 1 && floor <= 39) {
            return 1.0 + progress * 0.7; // 1.0 -> 1.7
        }
        if (floor >= 40 && floor <= 79) {
            return 1.0 + progress * 0.9; // 1.0 -> 1.9
        }
        if (floor >= 80 && floor <= 119) {
            return 1.0 + progress * 1.1; // 1.0 -> 2.1
        }
        return 1.0;
    }

    private static double getThemeProgress(int floor) {
        if (floor >= 1 && floor <= 39) {
            return (floor - 1) / 38.0;
        }
        if (floor >= 40 && floor <= 79) {
            return (floor - 40) / 39.0;
        }
        if (floor >= 80 && floor <= 119) {
            return (floor - 80) / 39.0;
        }
        return 0.0;
    }

    private static int getOreVeinSize(RandomSource random, String oreKey, int floor) {
        double progress = getThemeProgress(floor);
        int commonBonus = (int)Math.floor(progress * 2.0); // 0-2
        int rareBonus = (int)Math.floor(progress * 1.0);   // 0-1
        return switch (oreKey) {
            case "copper" -> 4 + random.nextInt(4) + commonBonus; // 4-9
            case "iron" -> 4 + random.nextInt(3) + commonBonus; // 4-8
            case "gold" -> 3 + random.nextInt(3) + rareBonus; // 3-6
            case "iridium" -> 2 + random.nextInt(3) + rareBonus; // 2-5
            case "coal" -> 3 + random.nextInt(4) + commonBonus; // 3-8
            default -> 3 + random.nextInt(3) + commonBonus; // 3-7
        };
    }

    private static int generateOreVein(ServerLevel level, RandomSource random,
                                        int startX, int startY, int startZ, int size,
                                        Block oreBlock, int centerX, int centerZ, int halfSize) {
        double angle = random.nextDouble() * Math.PI;
        double spread = size / 8.0;

        double ax = startX + Math.sin(angle) * spread;
        double bx = startX - Math.sin(angle) * spread;
        double az = startZ + Math.cos(angle) * spread;
        double bz = startZ - Math.cos(angle) * spread;
        double ay = startY + random.nextInt(3) - 1;
        double by = startY + random.nextInt(3) - 1;

        double radiusBase = random.nextDouble() * size / 16.0;

        int placed = 0;
        for (int i = 0; i <= size; i++) {
            double t = (double)i / size;
            double x = lerp(t, ax, bx);
            double y = lerp(t, ay, by);
            double z = lerp(t, az, bz);

            double scale = (Math.sin(Math.PI * t) + 1.0) * 0.5; // 0..1
            double radius = (scale * radiusBase + 1.0);

            double rx = radius * (0.85 + random.nextDouble() * 0.3);
            double ry = radius * 0.6;
            double rz = radius * (0.85 + random.nextDouble() * 0.3);

            placed += replaceOreEllipsoid(level, x, y, z, rx, ry, rz, oreBlock, centerX, centerZ, halfSize);
        }

        return placed;
    }

    private static double lerp(double t, double a, double b) {
        return a + (b - a) * t;
    }

    @SuppressWarnings("null")
    private static int replaceOreEllipsoid(ServerLevel level,
                                                  double centerX, double centerY, double centerZ,
                                                  double radiusX, double radiusY, double radiusZ,
                                                  Block replacement,
                                                  int roomCenterX, int roomCenterZ, int halfSize) {
        if (radiusX <= 0.0 || radiusY <= 0.0 || radiusZ <= 0.0) {
            return 0;
        }

        int placed = 0;

        int minX = (int)Math.floor(centerX - radiusX - 1.0);
        int maxX = (int)Math.floor(centerX + radiusX + 1.0);
        int minY = Math.max(FLOOR_Y_START, (int)Math.floor(centerY - radiusY - 1.0));
        int maxY = Math.min(FLOOR_Y_END, (int)Math.floor(centerY + radiusY + 1.0));
        int minZ = (int)Math.floor(centerZ - radiusZ - 1.0);
        int maxZ = (int)Math.floor(centerZ + radiusZ + 1.0);

        for (int x = minX; x <= maxX; x++) {
            if (Math.abs(x - roomCenterX) >= halfSize - 1) continue;
            double dx = (x + 0.5 - centerX) / radiusX;

            for (int z = minZ; z <= maxZ; z++) {
                if (Math.abs(z - roomCenterZ) >= halfSize - 1) continue;
                double dz = (z + 0.5 - centerZ) / radiusZ;

                for (int y = minY; y <= maxY; y++) {
                    double dy = (y + 0.5 - centerY) / radiusY;
                    double distanceSq = dx * dx + dy * dy + dz * dz;

                    if (distanceSq < 1.0) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState currentState = level.getBlockState(pos);
                        if (isOreReplaceable(currentState)) {
                            level.setBlock(pos, replacement.defaultBlockState(), 3);
                            placed++;
                        }
                    }
                }
            }
        }

        return placed;
    }

    private static boolean isOreReplaceable(BlockState state) {
        Block block = state.getBlock();
        return isMainStone(state)
            || block == ModBlocks.BANDED_MARBLE.get()
            || block == ModBlocks.LIMESTONE.get()
            || block == ModBlocks.MOSSY_SANDSTONE.get()
            || block == ModBlocks.CRACKED_SLATE.get()
            || block == ModBlocks.SCORIA.get()
            || block == ModBlocks.SALT_ROCK.get()
            || block == Blocks.ANDESITE
            || block == Blocks.DIRT
                        || block == Blocks.BLUE_ICE
            || block == Blocks.PACKED_ICE
            || block == Blocks.PRISMARINE_BRICKS
            || block == Blocks.MAGMA_BLOCK
            || block == Blocks.NETHERRACK;
    }

    private static String getPrimaryOreKeyForFloor(int floor) {
        if (floor >= 80) return "gold";
        if (floor >= 40) return "iron";
        return "copper";
    }

    @SuppressWarnings("null")
    private static void placeFallbackOreCluster(ServerLevel level,
                                                int centerX, int centerZ, int size,
                                                Block oreBlock, int halfSize) {
        int startX = centerX + 3;
        int startZ = centerZ + 3;
        int placed = 0;

        for (int dx = 0; dx <= 4; dx++) {
            for (int dz = 0; dz <= 4; dz++) {
                int x = startX + dx;
                int z = startZ + dz;
                if (Math.abs(x - centerX) >= halfSize - 1 || Math.abs(z - centerZ) >= halfSize - 1) {
                    continue;
                }
                for (int y = FLOOR_Y_START; y <= FLOOR_Y_END; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState currentState = level.getBlockState(pos);
                    if (isOreReplaceable(currentState)) {
                        level.setBlock(pos, oreBlock.defaultBlockState(), 3);
                        placed++;
                        if (placed >= 6) {
                            return;
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("null")
    private static void replaceMainStoneSphere(ServerLevel level, double centerX, double centerY, double centerZ,
                                               double radius, Block replacement,
                                               int roomCenterX, int roomCenterZ, int halfSize) {
        int minX = (int)Math.floor(centerX - radius - 1.0);
        int maxX = (int)Math.floor(centerX + radius + 1.0);
        int minY = Math.max(FLOOR_Y_START, (int)Math.floor(centerY - radius - 1.0));
        int maxY = Math.min(FLOOR_Y_END, (int)Math.floor(centerY + radius + 1.0));
        int minZ = (int)Math.floor(centerZ - radius - 1.0);
        int maxZ = (int)Math.floor(centerZ + radius + 1.0);

        for (int x = minX; x <= maxX; x++) {
            if (Math.abs(x - roomCenterX) >= halfSize - 1) continue;
            double dx = (x + 0.5 - centerX) / radius;

            for (int z = minZ; z <= maxZ; z++) {
                if (Math.abs(z - roomCenterZ) >= halfSize - 1) continue;
                double dz = (z + 0.5 - centerZ) / radius;

                for (int y = minY; y <= maxY; y++) {
                    double dy = (y + 0.5 - centerY) / radius;
                    double distanceSq = dx * dx + dy * dy * 1.2 + dz * dz;

                    if (distanceSq < 1.0) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState currentState = level.getBlockState(pos);
                        if (isMainStone(currentState)) {
                            level.setBlock(pos, replacement.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 检查是否是主石头方块
     */
    private static boolean isMainStone(BlockState state) {
        Block block = state.getBlock();
        return block == ModBlocks.EARTH_SHALE.get() || 
               block == ModBlocks.DARK_EARTH_SHALE.get() ||
               block == ModBlocks.FROST_GNEISS.get() || 
               block == ModBlocks.DARK_FROST_GNEISS.get() ||
               block == ModBlocks.LAVA_BASALT.get() || 
               block == ModBlocks.DARK_LAVA_BASALT.get();
    }

    private static boolean isDecorStone(BlockState state) {
        Block block = state.getBlock();
        return block == ModBlocks.BANDED_MARBLE.get()
                || block == ModBlocks.LIMESTONE.get()
                || block == ModBlocks.MOSSY_SANDSTONE.get()
                || block == ModBlocks.CRACKED_SLATE.get()
                || block == ModBlocks.SCORIA.get()
                || block == ModBlocks.SALT_ROCK.get();
    }

    private static boolean isStoneForMineral(BlockState state) {
        return isMainStone(state) || isDecorStone(state);
    }

    private static List<BlockPos> collectStonePositions(ServerLevel level, int centerX, int centerZ, int size) {
        int halfSize = size / 2;
        List<BlockPos> positions = new ArrayList<>();

        for (int x = centerX - halfSize + 1; x < centerX + halfSize; x++) {
            for (int z = centerZ - halfSize + 1; z < centerZ + halfSize; z++) {
                for (int y = FLOOR_Y_START; y <= FLOOR_Y_END; y++) {
                    if (isInSafeZone(x, y, z, centerX, centerZ)) {
                        continue;
                    }
                    BlockPos pos = new BlockPos(x, y, z);
                    if (isStoneForMineral(level.getBlockState(pos))) {
                        positions.add(pos);
                    }
                }
            }
        }
        return positions;
    }

    @SuppressWarnings("null")
    private static List<BlockPos> collectSurfaceStonePositions(ServerLevel level, int centerX, int centerZ, int size) {
        int halfSize = size / 2;
        List<BlockPos> positions = new ArrayList<>();

        for (int x = centerX - halfSize + 1; x < centerX + halfSize; x++) {
            for (int z = centerZ - halfSize + 1; z < centerZ + halfSize; z++) {
                for (int y = FLOOR_Y_START; y <= FLOOR_Y_END - 1; y++) {
                    if (isInSafeZone(x, y, z, centerX, centerZ)) continue;

                    BlockPos stonePos = new BlockPos(x, y, z);
                    BlockPos airPos = stonePos.above();

                    BlockState stoneState = level.getBlockState(stonePos);
                    BlockState airState = level.getBlockState(airPos);

                    if (isStoneForMineral(stoneState) && airState.isAir()) {
                        positions.add(airPos); // Store the AIR position where the mineral will go
                    }
                }
            }
        }
        return positions;
    }

    @SuppressWarnings("null")
    
    private static void generateLevelExit(ServerLevel level, RandomSource random, int centerX, int centerZ, int size, int floorNumber, MineFloorData data) {
        List<BlockPos> surfaceAirPositions = collectSurfaceStonePositions(level, centerX, centerZ, size);
        if (surfaceAirPositions.isEmpty()) return;
        
        BlockPos bestPos = surfaceAirPositions.get(random.nextInt(surfaceAirPositions.size()));
        double maxDist = 0;
        
        for (int i = 0; i < 20; i++) {
            BlockPos pos = surfaceAirPositions.get(random.nextInt(surfaceAirPositions.size()));
            double dist = pos.distToCenterSqr(centerX, pos.getY(), centerZ);
            if (dist > maxDist) {
                maxDist = dist;
                bestPos = pos;
            }
        }
        
        BlockPos ladderPos = bestPos.below();
        level.setBlock(ladderPos, ModBlocks.MINE_LADDER.get().defaultBlockState(), 3);
        data.setLadderPos(ladderPos);
        data.setLadderFound(true);

        spawnLadderHighlightDisplay(level, centerX, centerZ, size, floorNumber, ladderPos);
    }

    private static void spawnLadderHighlightDisplay(ServerLevel level, int centerX, int centerZ, int size, int floorNumber, BlockPos ladderPos) {
        int halfSize = size / 2;
        AABB scanBox = new AABB(
            centerX - halfSize, FLOOR_Y_START - 2, centerZ - halfSize,
            centerX + halfSize + 1, FLOOR_Y_END + 3, centerZ + halfSize + 1
        );

        String floorTag = LADDER_HIGHLIGHT_TAG + "_" + floorNumber;
        for (Display.BlockDisplay existing : level.getEntitiesOfClass(Display.BlockDisplay.class, scanBox,
            e -> e.getTags().contains(LADDER_HIGHLIGHT_TAG) && e.getTags().contains(floorTag))) {
            existing.discard();
        }

        Display.BlockDisplay marker = EntityType.BLOCK_DISPLAY.create(level);
        if (marker == null) {
            return;
        }

        marker.setPos(ladderPos.getX() + 0.5D, ladderPos.getY(), ladderPos.getZ() + 0.5D);
        marker.setGlowingTag(true);
        marker.setInvisible(true);
        marker.setNoGravity(true);
        marker.setInvulnerable(true);
        marker.setSilent(true);
        marker.addTag(LADDER_HIGHLIGHT_TAG);
        marker.addTag(floorTag);
        marker.addTag("stardewcraft_mine_ladder_highlight_anchor");
        level.addFreshEntity(marker);
    }

    @SuppressWarnings("null")
    private static void generateSurfaceMinerals(ServerLevel level, RandomSource random, int centerX, int centerZ, int size, int floorNumber) {
        List<BlockPos> surfaceAirPositions = collectSurfaceStonePositions(level, centerX, centerZ, size);
        if (surfaceAirPositions.isEmpty()) {
            return;
        }

        int targetCount = (int)Math.round(surfaceAirPositions.size() * SURFACE_MINERAL_RATE);
        int attempts = Math.max(10, targetCount * 4);
        int placed = 0;

        while (placed < targetCount && attempts-- > 0) {
            BlockPos pos = surfaceAirPositions.get(random.nextInt(surfaceAirPositions.size()));
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) continue;

            Block mineral = pickSurfaceMineralBlock(random, floorNumber);
            if (mineral != null) {
                level.setBlock(pos, mineral.defaultBlockState(), 3);
                placed++;
            }
        }
    }

    private static Block pickSurfaceMineralBlock(RandomSource random, int floorNumber) {
        float quartzWeight = 0.6f;
        float roll = random.nextFloat();
        if (floorNumber >= 80) {
            return roll < quartzWeight ? ModBlocks.QUARTZ.get() : ModBlocks.FIRE_QUARTZ.get();
        }
        if (floorNumber >= 40) {
            return roll < quartzWeight ? ModBlocks.QUARTZ.get() : ModBlocks.FROZEN_TEAR.get();
        }
        if (floorNumber >= 1) {
            return roll < quartzWeight ? ModBlocks.QUARTZ.get() : ModBlocks.EARTH_CRYSTAL.get();
        }
        return ModBlocks.QUARTZ.get();
    }

    @SuppressWarnings("null")
    private static void generateGemOreNodes(ServerLevel level, RandomSource random,
                                            int centerX, int centerZ, int size, int floorNumber) {
        List<BlockPos> stonePositions = collectStonePositions(level, centerX, centerZ, size);
        if (stonePositions.isEmpty()) {
            return;
        }

        List<BlockPos> surfaceStones = collectSurfaceStonePositions(level, centerX, centerZ, size);
        double baseRate = 0.003 * GEM_NODE_RATE_MULTIPLIER;
        int targetCount = (int)Math.round(stonePositions.size() * baseRate);
        if (targetCount <= 0) {
            targetCount = random.nextFloat() < 0.35f ? 1 : 0;
        }

        int attempts = Math.max(10, targetCount * 4);
        int placed = 0;
        while (placed < targetCount && attempts-- > 0) {
            boolean preferSurface = !surfaceStones.isEmpty() && random.nextDouble() < GEM_SURFACE_BIAS;
            BlockPos pos = (preferSurface ? surfaceStones : stonePositions).get(
                    random.nextInt((preferSurface ? surfaceStones : stonePositions).size()));
            @SuppressWarnings("null")
            BlockState state = level.getBlockState(pos);
            if (!isStoneForMineral(state)) {
                continue;
            }

            Block gemOre = pickGemOreBlockForFloor(random, floorNumber);
            if (gemOre != null) {
                level.setBlock(pos, gemOre.defaultBlockState(), 3);
                placed++;
            }
        }
    }

    private static Block pickGemOreBlockForFloor(RandomSource random, int floorNumber) {
        List<Block> candidates = new ArrayList<>();
        candidates.add(ModBlocks.AMETHYST_ORE.get());
        candidates.add(ModBlocks.TOPAZ_ORE.get());

        if (floorNumber >= 40) {
            candidates.add(ModBlocks.AQUAMARINE_ORE.get());
            candidates.add(ModBlocks.JADE_ORE.get());
        }

        if (floorNumber >= 80) {
            candidates.add(ModBlocks.RUBY_ORE.get());
            candidates.add(ModBlocks.EMERALD_ORE.get());
        }

        if (floorNumber >= 100) {
            candidates.add(ModBlocks.DIAMOND_ORE.get());
        }

        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }
    
    /**
     * 根据楼层和概率选择矿石类型
     * 
     * 概率表（按 MINING_IMPLEMENTATION_PLAN.md）：
     * - 铜矿：1-39层(0.17%), 40-119层(0.085%)
     * - 铁矿：40-79层(0.17%), 80-119层(0.085%)
     * - 金矿：80-119层(0.17%)
     * - 铱矿：80-99层(0.03%), 100-119层(0.05%)
     * - 煤矿：1-119层(0.06%)
     * 
     * @param roll 0-100之间的随机数
     */
    
    /**
     * 生成洞窟（类似原版Minecraft的虫蚀洞窟系统）
     * 使用类似原版的"虫蚀"算法，创建自然曲折的洞窟通道
     */
    private static void generateCaves(ServerLevel level, RandomSource random,
                                      int centerX, int centerZ, int size,
                                      FloorTheme theme) {
        int halfSize = size / 2;
        
        // 生成2-5条洞窟通道（类似原版的cave carver）
    int caveCount = (int)((size * size) / 800.0) + random.nextInt(4);
        
        List<Block> decorBlocks = getCaveDecorationBlocks(theme);
        
        for (int i = 0; i < caveCount; i++) {
            // 随机起点（避开中心安全区）
            int startX = centerX + (random.nextInt(size - 10) - (size - 10) / 2);
            int startZ = centerZ + (random.nextInt(size - 10) - (size - 10) / 2);
        int startY = FLOOR_Y_START + CAVE_VERTICAL_PADDING
            + random.nextInt(Math.max(1, FLOOR_HEIGHT - CAVE_VERTICAL_PADDING * 2));
            
            // 确保起点不在中心3×3安全区（±2格范围）
            if (Math.abs(startX - centerX) <= SAFE_ZONE_RADIUS + SAFE_ZONE_BUFFER && 
                Math.abs(startZ - centerZ) <= SAFE_ZONE_RADIUS + SAFE_ZONE_BUFFER) {
                continue;
            }
            
            // 随机方向和长度
            float yaw = random.nextFloat() * (float)Math.PI * 2.0F;
            float pitch = (random.nextFloat() - 0.5F) * 0.5F;
            float baseRadius = 2.5F + random.nextFloat() * 2.5F; // 基础半径
            
            // 洞窟长度（10-25格）
            int caveLength = 30 + random.nextInt(40);
            
            // 类似原版的"虫蚀"生成
            carveCaveTunnel(level, random, centerX, centerZ, halfSize, decorBlocks,
                           startX, startY, startZ, yaw, pitch, baseRadius, caveLength, 0);
        }
        
        // 额外生成1-2个大型洞穴室（类似原版的cave room）
    int roomCount = (int)((size * size) / 800.0) + random.nextInt(5);
        for (int i = 0; i < roomCount; i++) {
            generateCaveRoom(level, random, centerX, centerZ, halfSize, decorBlocks, theme);
        }
    }
    
    /**
     * 雕刻洞窟通道（类似原版的carve方法）
     * 模拟虫蚀效果，创建曲折的自然洞窟
     */
    private static void carveCaveTunnel(ServerLevel level, RandomSource random, 
                                        int centerX, int centerZ, int halfSize, 
                                        List<Block> decorBlocks,
                                        double startX, double startY, double startZ,
                                        float yaw, float pitch, float baseRadius,
                                        int steps, int depth) {
        double x = startX;
        double y = startY;
        double z = startZ;
        
        // 随机变化参数（模拟自然曲折）
        float yawChange = 0.0F;
        float pitchChange = 0.0F;
        
        for (int step = 0; step < steps; step++) {
            // 动态半径（模拟原版的变化）
            double radiusVariation = 0.5 + Math.sin(step * Math.PI / steps) * 0.5;
            double currentRadius = baseRadius * radiusVariation;
            
            // 更新方向（添加随机扰动，模拟自然曲折）
            yaw += yawChange * 0.1F;
            pitch += pitchChange * 0.1F;
            pitch *= 0.9F; // 阻尼，避免过于陡峭
            
            yawChange += (random.nextFloat() - random.nextFloat()) * 4.0F;
            pitchChange += (random.nextFloat() - random.nextFloat()) * 2.0F;
            
            // 移动到下一个位置
            x += Math.sin(yaw) * Math.cos(pitch);
            y += Math.sin(pitch);
            z += Math.cos(yaw) * Math.cos(pitch);
            
            // 严格边界检查（确保不超出房间范围，留2格边界给mine_barrier）
            if (Math.abs(x - centerX) >= halfSize - 2 || Math.abs(z - centerZ) >= halfSize - 2) {
                break; // 到达边界，停止雕刻
            }
            
            // 检查是否在中心安全区
            if (Math.abs(x - centerX) <= SAFE_ZONE_RADIUS + SAFE_ZONE_BUFFER && 
                Math.abs(z - centerZ) <= SAFE_ZONE_RADIUS + SAFE_ZONE_BUFFER) {
                continue; // 跳过中心安全区
            }
            
            // 雕刻球形空间（类似原版）- 传入边界参数确保不超出
            
            if (depth < 1 && step > steps / 3 && random.nextFloat() < 0.05f) {
                carveCaveTunnel(level, random, centerX, centerZ, halfSize, decorBlocks,
                                x, y, z, yaw + (random.nextFloat() - 0.5f) * 2.0f, pitch + (random.nextFloat() - 0.5f), 
                                baseRadius * 0.7f, steps / 2, depth + 1);
            }
            carveSphere(level, x, y, z, currentRadius, decorBlocks, random, centerX, centerZ, halfSize);
        }
    }
    
    /**
     * 雕刻球形空间（带边界检查）
     */
    @SuppressWarnings("null")
    private static void carveSphere(ServerLevel level, double centerX, double centerY, double centerZ,
                                    double radius, List<Block> decorBlocks, RandomSource random,
                                    int roomCenterX, int roomCenterZ, int halfSize) {
        int minX = (int)Math.floor(centerX - radius - 1.0);
        int maxX = (int)Math.floor(centerX + radius + 1.0);
    int minY = Math.max(FLOOR_Y_START + CAVE_VERTICAL_PADDING, (int)Math.floor(centerY - radius - 1.0));
    int maxY = Math.min(FLOOR_Y_END - CAVE_VERTICAL_PADDING, (int)Math.floor(centerY + radius + 1.0));
        int minZ = (int)Math.floor(centerZ - radius - 1.0);
        int maxZ = (int)Math.floor(centerZ + radius + 1.0);
        
        for (int x = minX; x <= maxX; x++) {
            // 严格边界检查：确保不超出房间范围（留2格给barrier）
            if (Math.abs(x - roomCenterX) >= halfSize - 1) continue;
            
            double dx = (x + 0.5 - centerX) / radius;
            
            for (int z = minZ; z <= maxZ; z++) {
                // 严格边界检查
                if (Math.abs(z - roomCenterZ) >= halfSize - 1) continue;
                
                double dz = (z + 0.5 - centerZ) / radius;
                
                for (int y = minY; y <= maxY; y++) {
                    double dy = (y + 0.5 - centerY) / radius;
                    
                    // 椭球距离检测（类似原版）
                    double distanceSq = dx * dx + dy * dy * 2.0 + dz * dz; // Y方向拉伸
                    
                    if (distanceSq < 1.0) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState currentState = level.getBlockState(pos);
                        
                        // 只挖掉石头类方块（不挖空气和barrier）
                        if (isInSafeZone(x, y, z, roomCenterX, roomCenterZ)) {
                            continue;
                        }

                        if (!currentState.isAir() && currentState.getBlock() != ModBlocks.MINE_BARRIER.get()) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            
                            // 边缘装饰（小概率）
                            if (distanceSq > 0.7 && random.nextFloat() < 0.15f) {
                                Block decorBlock = decorBlocks.get(random.nextInt(decorBlocks.size()));
                                level.setBlock(pos, decorBlock.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 生成大型洞穴室（类似原版的cave room）
     */
    @SuppressWarnings("null")
    private static void generateCaveRoom(ServerLevel level, RandomSource random,
                                         int centerX, int centerZ, int halfSize,
                                         List<Block> decorBlocks, FloorTheme theme) {
        // 随机位置
        int roomX = centerX + (random.nextInt(halfSize) - halfSize / 2);
        int roomZ = centerZ + (random.nextInt(halfSize) - halfSize / 2);
    int roomY = FLOOR_Y_START + CAVE_VERTICAL_PADDING + 1;
        
        // 确保不在中心安全区
        if (Math.abs(roomX - centerX) <= SAFE_ZONE_RADIUS + SAFE_ZONE_BUFFER + 3 && 
            Math.abs(roomZ - centerZ) <= SAFE_ZONE_RADIUS + SAFE_ZONE_BUFFER + 3) {
            return;
        }
        
        // 大型椭球形房间（半径4-7格）
        float roomRadius = 6.0F + random.nextFloat() * 6.0F;
        
        // 雕刻大型房间（使用变形的球形）
        for (int dx = -(int)roomRadius - 3; dx <= (int)roomRadius + 3; dx++) {
            for (int dz = -(int)roomRadius - 3; dz <= (int)roomRadius + 3; dz++) {
                for (int dy = -3; dy <= 4; dy++) {
                    int x = roomX + dx;
                    int z = roomZ + dz;
                    int y = roomY + dy;
                    
                    // 严格边界检查
                    if (y < FLOOR_Y_START + CAVE_VERTICAL_PADDING || y > FLOOR_Y_END - CAVE_VERTICAL_PADDING) continue;
                    if (Math.abs(x - centerX) >= halfSize - 1 || Math.abs(z - centerZ) >= halfSize - 1) continue;
                    
                    // 椭球检测（Y方向拉伸，创建高大的房间）
                    double distanceSq = (dx * dx + dz * dz) / (roomRadius * roomRadius) + 
                                       (dy * dy * 0.5) / (roomRadius * roomRadius);
                    
                    if (distanceSq < 1.0) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState currentState = level.getBlockState(pos);
                        
                        if (isInSafeZone(x, y, z, centerX, centerZ)) {
                            continue;
                        }

                        if (!currentState.isAir() && currentState.getBlock() != ModBlocks.MINE_BARRIER.get()) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                    // 房间边缘装饰
                    else if (distanceSq < 1.3 && random.nextFloat() < 0.25f) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState currentState = level.getBlockState(pos);
                        
                        if (isInSafeZone(x, y, z, centerX, centerZ)) {
                            continue;
                        }

                        if (!currentState.isAir() && currentState.getBlock() != ModBlocks.MINE_BARRIER.get()) {
                            Block decorBlock = decorBlocks.get(random.nextInt(decorBlocks.size()));
                            level.setBlock(pos, decorBlock.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        
        // TODO: 后续可以在房间中心放置箱子/木桶
    }

    private static boolean isInSafeZone(int x, int y, int z, int centerX, int centerZ) {
        return Math.abs(x - centerX) <= SAFE_ZONE_RADIUS + 1 && Math.abs(z - centerZ) <= SAFE_ZONE_RADIUS + 1 && y >= FLOOR_Y_START && y <= SAFE_ZONE_Y_END + 2;
    }
    
    /**
     * 生成中心安全区
     * 
     * 结构：
     * - 3×3×3 空气区域（Y=65,66,67三层，X和Z各±1）
     * - 玩家背后（北侧 Z-1）：1×3×1 封印石墙（Y=65..67）
     * - 封印石墙南侧：3个 mine_exit（每格一个）
     * - 玩家面向南（朝向exit）
     */
    @SuppressWarnings("null")
    private static void generateSafeZone(ServerLevel level, int centerX, int centerZ, int floorNumber) {
        // 1. 清空3×3×3区域（Y=65,66,67三层，X和Z各±1）
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int y = SAFE_ZONE_Y_START; y <= SAFE_ZONE_Y_END; y++) {
                    level.setBlock(new BlockPos(centerX + dx, y, centerZ + dz), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        
        // 2. 玩家背后（北侧 Z-1）放置1×3×1封印石墙
        Block barrier = ModBlocks.MINE_BARRIER.get();
        int wallZ = centerZ - 1; // 北侧
        for (int y = SAFE_ZONE_Y_START; y <= SAFE_ZONE_Y_END; y++) {
            level.setBlock(new BlockPos(centerX, y, wallZ), barrier.defaultBlockState(), 3);
        }
        
        // 3. 在封印石墙南侧（玩家面向方向）放置3个mine_exit（每格一个）
        // exit依附在北侧墙上，FACING=SOUTH表示面朝南（墙在exit的北侧）
        Block exit = ModBlocks.MINE_EXIT.get();
        for (int y = SAFE_ZONE_Y_START; y <= SAFE_ZONE_Y_END; y++) {
            BlockPos exitPos = new BlockPos(centerX, y, centerZ);
            @SuppressWarnings("null")
            BlockState exitState = exit.defaultBlockState()
                .setValue(com.stardew.craft.block.mine.MineExitBlock.FACING, Direction.SOUTH); // 面朝南，依附北墙
            level.setBlock(exitPos, exitState, 3);
        }

        // 4. 电梯（每 5 层放一个，0层由结构放置）
        if (floorNumber > 0 && floorNumber % 5 == 0) {
            Block elevator = ModBlocks.ELEVATOR.get();
            BlockPos elevatorPos = new BlockPos(centerX + 1, SAFE_ZONE_Y_START - 1, wallZ); // 封印石旁边一格
            @SuppressWarnings("null")
            BlockState elevatorState = elevator.defaultBlockState()
                .setValue(com.stardew.craft.block.mine.ElevatorBlock.FACING, Direction.SOUTH);
            level.setBlock(elevatorPos, elevatorState, 3);
        }
        
    StardewCraft.LOGGER.info("[MINE] Generated safe zone (3×3×3) with 3 exits at center ({}, {}, {})", centerX, SAFE_ZONE_Y_START, centerZ);
    }
    
    /**
     * 根据楼层确定主题
     */
    private static FloorTheme getThemeForFloor(int floor) {
        if (floor >= 1 && floor <= 39) {
            return FloorTheme.EARTH;
        } else if (floor >= 40 && floor <= 79) {
            return FloorTheme.FROST;
        } else if (floor >= 80 && floor <= 119) {
            return FloorTheme.LAVA;
        } else if (floor == 120) {
            return FloorTheme.SUMMIT;
        }
        return FloorTheme.EARTH; // 默认
    }
    
    /**
     * 获取主题对应的主石头
     */
    private static Block getMainStone(FloorTheme theme, boolean isDark) {
        switch (theme) {
            case EARTH:
                return isDark ? ModBlocks.DARK_EARTH_SHALE.get() : ModBlocks.EARTH_SHALE.get();
            case FROST:
                return isDark ? ModBlocks.DARK_FROST_GNEISS.get() : ModBlocks.FROST_GNEISS.get();
            case LAVA:
                return isDark ? ModBlocks.DARK_LAVA_BASALT.get() : ModBlocks.LAVA_BASALT.get();
            case SUMMIT:
                return ModBlocks.MINE_BARRIER.get();
            default:
                return ModBlocks.EARTH_SHALE.get();
        }
    }
    
    /**
     * 获取装饰石头（按主题和用途选择）
     * 按照 MINING_REGISTRATION_CHECKLIST.md 的用途说明：
     * - banded_marble: 冰段洞窟装饰、宝箱房地面
     * - limestone: 土段洞窟、靠近木桶/矿车区域
     * - mossy_sandstone: 土段过渡、隐藏房间的潮湿角落
     * - cracked_slate: 三段通用，适合边缘/墙面阴影区
     * - scoria: 熔岩段洞窟、火山气息装饰
     * - salt_rock: 冰段、特殊洞窟标志材
     */
    private static Block getDecorativeStone(FloorTheme theme, RandomSource random) {
        switch (theme) {
            case EARTH:
                // 土段: limestone(石灰岩洞窟), mossy_sandstone(苔斑潮湿角落), cracked_slate(通用边缘)
                Block[] earthDecor = {
                    ModBlocks.LIMESTONE.get(),
                    ModBlocks.MOSSY_SANDSTONE.get(),
                    ModBlocks.CRACKED_SLATE.get()
                };
                return earthDecor[random.nextInt(earthDecor.length)];
                
            case FROST:
                // 冰段: banded_marble(洞窟装饰), salt_rock(特殊洞窟), cracked_slate(通用边缘)
                Block[] frostDecor = {
                    ModBlocks.BANDED_MARBLE.get(),
                    ModBlocks.SALT_ROCK.get(),
                    ModBlocks.CRACKED_SLATE.get()
                };
                return frostDecor[random.nextInt(frostDecor.length)];
                
            case LAVA:
                // 熔岩段: scoria(火山渣岩洞窟), cracked_slate(通用边缘)
                Block[] lavaDecor = {
                    ModBlocks.SCORIA.get(),
                    ModBlocks.CRACKED_SLATE.get()
                };
                return lavaDecor[random.nextInt(lavaDecor.length)];
                
            default:
                return ModBlocks.CRACKED_SLATE.get(); // 通用
        }
    }
    
    /**
     * 获取原版方块点缀（按主题选择，占比最低）
     */
    private static Block getVanillaBlock(FloorTheme theme, RandomSource random) {
        switch (theme) {
            case EARTH:
                // 土段: 安山岩、泥土、沙砾
                Block[] earthVanilla = {Blocks.ANDESITE, Blocks.DIRT};
                return earthVanilla[random.nextInt(earthVanilla.length)];
                
            case FROST:
                // 冰段: 蓝冰、浮冰、海晶砖
                Block[] frostVanilla = {Blocks.BLUE_ICE, Blocks.PACKED_ICE, Blocks.PRISMARINE_BRICKS};
                return frostVanilla[random.nextInt(frostVanilla.length)];
                
            case LAVA:
                // 熔岩段: 岩浆块、地狱岩
                Block[] lavaVanilla = {Blocks.MAGMA_BLOCK, Blocks.NETHERRACK};
                return lavaVanilla[random.nextInt(lavaVanilla.length)];
                
            default:
                return Blocks.STONE;
        }
    }
    
    /**
     * 获取洞窟装饰石头（用于洞窟边缘装饰）
     */
    private static List<Block> getCaveDecorationBlocks(FloorTheme theme) {
        List<Block> blocks = new ArrayList<>();
        switch (theme) {
            case EARTH:
                blocks.add(ModBlocks.LIMESTONE.get());        // 石灰岩 - 土段洞窟
                blocks.add(ModBlocks.MOSSY_SANDSTONE.get());  // 苔斑砂岩 - 潮湿角落
                break;
                
            case FROST:
                blocks.add(ModBlocks.BANDED_MARBLE.get());    // 条带大理石 - 冰段洞窟装饰
                blocks.add(ModBlocks.SALT_ROCK.get());        // 盐霜岩 - 冰段特殊洞窟
                break;
                
            case LAVA:
                blocks.add(ModBlocks.SCORIA.get());           // 火山渣岩 - 熔岩段洞窟
                break;
                
            case SUMMIT:
                blocks.add(ModBlocks.MINE_BARRIER.get());     // 顶峰层特殊
                break;
        }
        blocks.add(ModBlocks.CRACKED_SLATE.get());            // 龟裂板岩 - 三段通用
        return blocks;
    }
    
    /**
     * 获取矿石方块（按主题选择）
     */
    private static Block getOreBlock(FloorTheme theme, String oreType) {
        switch (theme) {
            case EARTH:
                switch (oreType) {
                    case "copper": return ModBlocks.EARTH_COPPER_ORE.get();
                    case "iron": return ModBlocks.EARTH_IRON_ORE.get();
                    case "gold": return ModBlocks.EARTH_GOLD_ORE.get();
                    case "iridium": return ModBlocks.EARTH_IRIDIUM_ORE.get();
                    case "coal": return ModBlocks.EARTH_COAL_ORE.get();
                    default: return ModBlocks.EARTH_COPPER_ORE.get();
                }
                
            case FROST:
                switch (oreType) {
                    case "copper": return ModBlocks.FROST_COPPER_ORE.get();
                    case "iron": return ModBlocks.FROST_IRON_ORE.get();
                    case "gold": return ModBlocks.FROST_GOLD_ORE.get();
                    case "iridium": return ModBlocks.FROST_IRIDIUM_ORE.get();
                    case "coal": return ModBlocks.FROST_COAL_ORE.get();
                    default: return ModBlocks.FROST_COPPER_ORE.get();
                }
                
            case LAVA:
                switch (oreType) {
                    case "copper": return ModBlocks.LAVA_COPPER_ORE.get();
                    case "iron": return ModBlocks.LAVA_IRON_ORE.get();
                    case "gold": return ModBlocks.LAVA_GOLD_ORE.get();
                    case "iridium": return ModBlocks.LAVA_IRIDIUM_ORE.get();
                    case "coal": return ModBlocks.LAVA_COAL_ORE.get();
                    default: return ModBlocks.LAVA_COPPER_ORE.get();
                }
                
            default:
                return ModBlocks.EARTH_COPPER_ORE.get();
        }
    }
    
    /**
     * 楼层主题枚举
     */
    public enum FloorTheme {
        EARTH,   // 1-39层：土段
        FROST,   // 40-79层：冰段
        LAVA,    // 80-119层：熔岩段
        SUMMIT   // 120层：顶峰
    }
}
