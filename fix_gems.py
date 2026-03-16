import re
import io

with io.open('src/main/java/com/stardew/craft/mining/MineFloorGenerator.java', 'r', encoding='utf-8') as f:
    text = f.read()

pattern = r'private static void generateSurfaceMinerals.*?return candidates\.get\(random\.nextInt\(candidates\.size\(\)\)\);\s*\}'

replacement = u'''private static void generateSurfaceMinerals(ServerLevel level, RandomSource random,
                                                int centerX, int centerZ, int size, int floorNumber) {
        List<BlockPos> surfaceStones = collectSurfaceStonePositions(level, centerX, centerZ, size);
        if (surfaceStones.isEmpty()) {
            return;
        }

        // 以面积计算避免三维泛滥，100x100的地图约产出20-25个表面伴生物
        double area = size * size;
        int targetCount = (int)Math.round((area / 10000.0) * 25.0);
        int attempts = Math.max(10, targetCount * 4);
        int placed = 0;

        while (placed < targetCount && attempts-- > 0) {
            BlockPos pos = surfaceStones.get(random.nextInt(surfaceStones.size()));
            @SuppressWarnings(" null\)
 BlockState state = level.getBlockState(pos);
 if (!isStoneForMineral(state)) {
 continue;
 }
 Block mineral = pickSurfaceMineralBlock(random, floorNumber);
 if (mineral != null ; level.getBlockState(pos.above()).isAir()) {
 level.setBlock(pos.above(), mineral.defaultBlockState(), 3);
 placed++;
 }
 }
 }

 private static Block pickSurfaceMineralBlock(RandomSource random, int floorNumber) {
 float roll = random.nextFloat();
 // 星露谷规划：石英无处不在，特有晶体分层
 if (floorNumber >= 80) {
 return roll < 0.6f ? ModBlocks.QUARTZ.get() : ModBlocks.FIRE_QUARTZ.get();
 }
 if (floorNumber >= 40) {
 return roll < 0.6f ? ModBlocks.QUARTZ.get() : ModBlocks.FROZEN_TEAR.get();
 }
 return roll < 0.6f ? ModBlocks.QUARTZ.get() : ModBlocks.EARTH_CRYSTAL.get();
 }

 @SuppressWarnings(\null\)
 private static void generateGemOreNodes(ServerLevel level, RandomSource random,
 int centerX, int centerZ, int size, int floorNumber) {
 List<BlockPos> stonePositions = collectStonePositions(level, centerX, centerZ, size);
 if (stonePositions.isEmpty()) {
 return;
 }

 List<BlockPos> surfaceStones = collectSurfaceStonePositions(level, centerX, centerZ, size);
 
 // 按照规划：宝石矿发生率约 0.05%，钻石 0.003%。
 // 我们通过面积控制生成总量，100x100 面积矿洞，宝石块预期约 6 块左右就足够。
 double area = size * size;
 int targetCount = (int)Math.round((area / 10000.0) * 6.0);
 if (targetCount <= 0) {
 targetCount = random.nextFloat() < 0.35f ? 1 : 0;
 }

 int attempts = Math.max(10, targetCount * 4);
 int placed = 0;
 while (placed < targetCount ; attempts-- > 0) {
 boolean preferSurface = !surfaceStones.isEmpty() ; random.nextDouble() < GEM_SURFACE_BIAS;
 BlockPos pos = (preferSurface ? surfaceStones : stonePositions).get(
 random.nextInt((preferSurface ? surfaceStones : stonePositions).size()));
 @SuppressWarnings(\null\)
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
 double roll = random.nextDouble();
 
 // 钻石稀有度调整，按照原版设定仅高层罕见出现
 if (floorNumber >= 100 ; roll < 0.08) {
 return ModBlocks.DIAMOND_ORE.get();
 } else if (floorNumber >= 50 ; roll < 0.03) {
 return ModBlocks.DIAMOND_ORE.get(); 
 }

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

 if (candidates.isEmpty()) {
 return null;
 }
 return candidates.get(random.nextInt(candidates.size()));
 }'''

text = re.sub(pattern, replacement, text, flags=re.DOTALL)

with io.open('src/main/java/com/stardew/craft/mining/MineFloorGenerator.java', 'w', encoding='utf-8') as f:
 f.write(text)

print('Success')
