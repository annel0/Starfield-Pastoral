package com.stardew.craft.block;

import com.stardew.craft.StardewCraft;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块注册管理器
 */
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(StardewCraft.MODID);

                @SuppressWarnings("null")
                private static Block.Properties stoneProps(MapColor color, SoundType sound, float hardness) {
                return Block.Properties.of()
                                .mapColor(color)
                                .sound(sound)
                                .strength(hardness, 6.0F);
        }

                @SuppressWarnings("null")
                private static DeferredBlock<SlabBlock> slab(String name, Block.Properties props) {
                return BLOCKS.register(name, () -> new SlabBlock(props));
        }

                @SuppressWarnings("null")
                private static DeferredBlock<StairBlock> stairs(String name, DeferredBlock<Block> base, Block.Properties props) {
                return BLOCKS.register(name, () -> new StairBlock(base.get().defaultBlockState(), props));
        }

                @SuppressWarnings("null")
                private static DeferredBlock<WallBlock> wall(String name, Block.Properties props) {
                return BLOCKS.register(name, () -> new WallBlock(props));
        }

        // 自然/杂草
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_WEEDS = BLOCKS.register("wild_weeds",
                        () -> new com.stardew.craft.block.nature.WildWeedsBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noCollission()
                                        .noOcclusion()
                                        .instabreak()
                                        .randomTicks()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> PASTURE_GRASS = BLOCKS.register("pasture_grass",
                        () -> new com.stardew.craft.block.nature.PastureGrassBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noCollission()
                                        .noOcclusion()
                                        .instabreak()
                                        .randomTicks()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BLUE_PASTURE_GRASS = BLOCKS.register("blue_pasture_grass",
                        () -> new com.stardew.craft.block.nature.PastureGrassBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noCollission()
                                        .noOcclusion()
                                        .instabreak()
                                        .randomTicks()));

        // ---- 采集物方块 (Forage blocks with cross model, drop corresponding items) ----
        @SuppressWarnings("null")
        private static Block.Properties forageProps() {
                return Block.Properties.of()
                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                        .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)
                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                        .noCollission()
                        .noOcclusion()
                        .instabreak();
        }
        @SuppressWarnings("null")
        private static DeferredBlock<Block> forage(String name) {
                return BLOCKS.register("forage_" + name,
                        () -> new com.stardew.craft.block.nature.ForageBlock(forageProps())
                                .setDrop(() -> new net.minecraft.world.item.ItemStack(
                                        net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                                                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("stardewcraft", name)))));
        }

        // Spring forage
        public static final DeferredBlock<Block> FORAGE_WILD_HORSERADISH = forage("wild_horseradish");
        public static final DeferredBlock<Block> FORAGE_DAFFODIL          = forage("daffodil");
        public static final DeferredBlock<Block> FORAGE_LEEK              = forage("leek");
        public static final DeferredBlock<Block> FORAGE_DANDELION         = forage("dandelion");
        public static final DeferredBlock<Block> FORAGE_SPRING_ONION      = forage("spring_onion");
        // Summer forage
        public static final DeferredBlock<Block> FORAGE_SPICE_BERRY       = forage("spice_berry");
        public static final DeferredBlock<Block> FORAGE_SWEET_PEA         = forage("sweet_pea");
        public static final DeferredBlock<Block> FORAGE_FIDDLEHEAD_FERN   = forage("fiddlehead_fern");
        // Fall forage
        public static final DeferredBlock<Block> FORAGE_WILD_PLUM         = forage("wild_plum");
        public static final DeferredBlock<Block> FORAGE_HAZELNUT          = forage("hazelnut");
        public static final DeferredBlock<Block> FORAGE_BLACKBERRY        = forage("blackberry");
        // Winter forage
        public static final DeferredBlock<Block> FORAGE_WINTER_ROOT       = forage("winter_root");
        public static final DeferredBlock<Block> FORAGE_CRYSTAL_FRUIT     = forage("crystal_fruit");
        public static final DeferredBlock<Block> FORAGE_CROCUS            = forage("crocus");
        public static final DeferredBlock<Block> FORAGE_HOLLY             = forage("holly");
        // Cave / universal
        public static final DeferredBlock<Block> FORAGE_CAVE_CARROT       = forage("cave_carrot");
        // Beach
        public static final DeferredBlock<Block> FORAGE_NAUTILUS_SHELL    = forage("nautilus_shell");
        public static final DeferredBlock<Block> FORAGE_CORAL             = forage("coral");
        public static final DeferredBlock<Block> FORAGE_RAINBOW_SHELL     = forage("rainbow_shell");
        public static final DeferredBlock<Block> FORAGE_SEA_URCHIN        = forage("sea_urchin");
        // Desert / tropical
        public static final DeferredBlock<Block> FORAGE_COCONUT           = forage("coconut");
        public static final DeferredBlock<Block> FORAGE_CACTUS_FRUIT      = forage("cactus_fruit");
        // Mushrooms (cave)
        public static final DeferredBlock<Block> FORAGE_COMMON_MUSHROOM   = forage("common_mushroom");
        public static final DeferredBlock<Block> FORAGE_RED_MUSHROOM      = forage("red_mushroom");
        public static final DeferredBlock<Block> FORAGE_PURPLE_MUSHROOM   = forage("purple_mushroom");
        public static final DeferredBlock<Block> FORAGE_MOREL             = forage("morel");
        public static final DeferredBlock<Block> FORAGE_CHANTERELLE       = forage("chanterelle");
        public static final DeferredBlock<Block> FORAGE_MAGMA_CAP         = forage("magma_cap");

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> ANIMAL_PRODUCE_SPOT = BLOCKS.register("animal_produce_spot",
                        () -> new com.stardew.craft.block.animal.AnimalProduceSpotBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.NONE)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noCollission()
                                        .noOcclusion()
                                        .instabreak()));

        // 矿井
        // 说明：生存/冒险不可破坏（硬度 -1），创造模式可正常破坏。
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> MINE_BARRIER = BLOCKS.register("mine_barrier",
                        () -> new Block(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_BLACK)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .strength(-1.0F, 3600000.0F)));

        // 矿井：主石头
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> EARTH_SHALE = BLOCKS.register("earth_shale",
                        () -> new Block(stoneProps(MapColor.STONE, SoundType.STONE, 5.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FROST_GNEISS = BLOCKS.register("frost_gneiss",
                        () -> new Block(stoneProps(MapColor.STONE, SoundType.STONE, 7.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LAVA_BASALT = BLOCKS.register("lava_basalt",
                        () -> new Block(stoneProps(MapColor.STONE, SoundType.STONE, 10.0F)));

        // 矿井：Dark 变体（随机修饰器用）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DARK_EARTH_SHALE = BLOCKS.register("dark_earth_shale",
                        () -> new Block(stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 6.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DARK_FROST_GNEISS = BLOCKS.register("dark_frost_gneiss",
                        () -> new Block(stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 8.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DARK_LAVA_BASALT = BLOCKS.register("dark_lava_basalt",
                        () -> new Block(stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 12.0F)));

        // ========== 主石头变体：台阶、楼梯、墙 ==========
        
        // Earth Shale 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> EARTH_SHALE_SLAB = slab("earth_shale_slab",
                        stoneProps(MapColor.STONE, SoundType.STONE, 5.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> EARTH_SHALE_STAIRS = stairs("earth_shale_stairs", EARTH_SHALE,
                        stoneProps(MapColor.STONE, SoundType.STONE, 5.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> EARTH_SHALE_WALL = wall("earth_shale_wall",
                        stoneProps(MapColor.STONE, SoundType.STONE, 5.0F));

        // Frost Gneiss 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> FROST_GNEISS_SLAB = slab("frost_gneiss_slab",
                        stoneProps(MapColor.STONE, SoundType.STONE, 7.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> FROST_GNEISS_STAIRS = stairs("frost_gneiss_stairs", FROST_GNEISS,
                        stoneProps(MapColor.STONE, SoundType.STONE, 7.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> FROST_GNEISS_WALL = wall("frost_gneiss_wall",
                        stoneProps(MapColor.STONE, SoundType.STONE, 7.0F));

        // Lava Basalt 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> LAVA_BASALT_SLAB = slab("lava_basalt_slab",
                        stoneProps(MapColor.STONE, SoundType.STONE, 10.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> LAVA_BASALT_STAIRS = stairs("lava_basalt_stairs", LAVA_BASALT,
                        stoneProps(MapColor.STONE, SoundType.STONE, 10.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> LAVA_BASALT_WALL = wall("lava_basalt_wall",
                        stoneProps(MapColor.STONE, SoundType.STONE, 10.0F));

        // Dark Earth Shale 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> DARK_EARTH_SHALE_SLAB = slab("dark_earth_shale_slab",
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 6.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> DARK_EARTH_SHALE_STAIRS = stairs("dark_earth_shale_stairs", DARK_EARTH_SHALE,
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 6.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> DARK_EARTH_SHALE_WALL = wall("dark_earth_shale_wall",
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 6.0F));

        // Dark Frost Gneiss 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> DARK_FROST_GNEISS_SLAB = slab("dark_frost_gneiss_slab",
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 8.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> DARK_FROST_GNEISS_STAIRS = stairs("dark_frost_gneiss_stairs", DARK_FROST_GNEISS,
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 8.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> DARK_FROST_GNEISS_WALL = wall("dark_frost_gneiss_wall",
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 8.0F));

        // Dark Lava Basalt 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> DARK_LAVA_BASALT_SLAB = slab("dark_lava_basalt_slab",
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 12.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> DARK_LAVA_BASALT_STAIRS = stairs("dark_lava_basalt_stairs", DARK_LAVA_BASALT,
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 12.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> DARK_LAVA_BASALT_WALL = wall("dark_lava_basalt_wall",
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 12.0F));

        // 矿井：装饰石材族
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BANDED_MARBLE = BLOCKS.register("banded_marble",
                        () -> new Block(stoneProps(MapColor.QUARTZ, SoundType.STONE, 4.5F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LIMESTONE = BLOCKS.register("limestone",
                        () -> new Block(stoneProps(MapColor.TERRACOTTA_WHITE, SoundType.STONE, 4.5F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> MOSSY_SANDSTONE = BLOCKS.register("mossy_sandstone",
                        () -> new Block(stoneProps(MapColor.SAND, SoundType.STONE, 4.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CRACKED_SLATE = BLOCKS.register("cracked_slate",
                        () -> new Block(stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 5.5F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCORIA = BLOCKS.register("scoria",
                        () -> new Block(stoneProps(MapColor.COLOR_BLACK, SoundType.BASALT, 5.5F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SALT_ROCK = BLOCKS.register("salt_rock",
                        () -> new Block(stoneProps(MapColor.SNOW, SoundType.STONE, 4.0F)));

        // ========== 装饰石材变体：台阶、楼梯、墙 ==========
        
        // Banded Marble 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> BANDED_MARBLE_SLAB = slab("banded_marble_slab",
                        stoneProps(MapColor.QUARTZ, SoundType.STONE, 4.5F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> BANDED_MARBLE_STAIRS = stairs("banded_marble_stairs", BANDED_MARBLE,
                        stoneProps(MapColor.QUARTZ, SoundType.STONE, 4.5F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> BANDED_MARBLE_WALL = wall("banded_marble_wall",
                        stoneProps(MapColor.QUARTZ, SoundType.STONE, 4.5F));

        // Limestone 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> LIMESTONE_SLAB = slab("limestone_slab",
                        stoneProps(MapColor.TERRACOTTA_WHITE, SoundType.STONE, 4.5F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> LIMESTONE_STAIRS = stairs("limestone_stairs", LIMESTONE,
                        stoneProps(MapColor.TERRACOTTA_WHITE, SoundType.STONE, 4.5F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> LIMESTONE_WALL = wall("limestone_wall",
                        stoneProps(MapColor.TERRACOTTA_WHITE, SoundType.STONE, 4.5F));

        // Mossy Sandstone 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> MOSSY_SANDSTONE_SLAB = slab("mossy_sandstone_slab",
                        stoneProps(MapColor.SAND, SoundType.STONE, 4.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> MOSSY_SANDSTONE_STAIRS = stairs("mossy_sandstone_stairs", MOSSY_SANDSTONE,
                        stoneProps(MapColor.SAND, SoundType.STONE, 4.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> MOSSY_SANDSTONE_WALL = wall("mossy_sandstone_wall",
                        stoneProps(MapColor.SAND, SoundType.STONE, 4.0F));

        // Cracked Slate 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> CRACKED_SLATE_SLAB = slab("cracked_slate_slab",
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 5.5F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> CRACKED_SLATE_STAIRS = stairs("cracked_slate_stairs", CRACKED_SLATE,
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 5.5F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> CRACKED_SLATE_WALL = wall("cracked_slate_wall",
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 5.5F));

        // Scoria 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> SCORIA_SLAB = slab("scoria_slab",
                        stoneProps(MapColor.COLOR_BLACK, SoundType.BASALT, 5.5F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> SCORIA_STAIRS = stairs("scoria_stairs", SCORIA,
                        stoneProps(MapColor.COLOR_BLACK, SoundType.BASALT, 5.5F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> SCORIA_WALL = wall("scoria_wall",
                        stoneProps(MapColor.COLOR_BLACK, SoundType.BASALT, 5.5F));

        // Salt Rock 变体
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> SALT_ROCK_SLAB = slab("salt_rock_slab",
                        stoneProps(MapColor.SNOW, SoundType.STONE, 4.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> SALT_ROCK_STAIRS = stairs("salt_rock_stairs", SALT_ROCK,
                        stoneProps(MapColor.SNOW, SoundType.STONE, 4.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> SALT_ROCK_WALL = wall("salt_rock_wall",
                        stoneProps(MapColor.SNOW, SoundType.STONE, 4.0F));

        // 矿井：电梯（模型由资源包提供，有朝向）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> ELEVATOR = BLOCKS.register("elevator",
                        () -> new com.stardew.craft.block.mine.ElevatorBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .requiresCorrectToolForDrops()
                                        .strength(3.0F, 6.0F)
                                        .noOcclusion()));

        // 矿井：下楼梯子（挖石头出现的传送点）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> MINE_LADDER = BLOCKS.register("mine_ladder",
                        () -> new com.stardew.craft.block.mine.MineLadderBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.LADDER)
                                        .strength(1.0F, 3.0F)
                                        .lightLevel(state -> 15)
                                        .noOcclusion()));

        // 矿井：木桶（仿 SDV BreakableContainer，可挖掘/武器打碎，掉落战利品）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> MINE_BARREL = BLOCKS.register("mine_barrel",
                        () -> new com.stardew.craft.block.mine.MineBarrelBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .strength(0.6F, 2.0F)
                                        .noOcclusion()));

        // 矿井：宝箱（per-player 独立库存，不可破坏）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> MINE_CHEST = BLOCKS.register("mine_chest",
                        () -> new com.stardew.craft.block.mine.MineChestBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .strength(-1.0F, 3600000.0F) // 不可破坏
                                        .noOcclusion()));

        // 矿井：出口（金色梯子，中心安全区，打开GUI）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> MINE_EXIT = BLOCKS.register("mine_exit",
                        () -> new com.stardew.craft.block.mine.MineExitBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.GOLD)
                                        .sound(net.minecraft.world.level.block.SoundType.LADDER)
                                        .strength(-1.0F, 3600000.0F) // 不可破坏
                                        .lightLevel(state -> 15)
                                        .noOcclusion()));

        // 矿井：矿石方块（主题三套外观）
        @SuppressWarnings("null")
        private static Block.Properties oreProps(float hardness) {
                return Block.Properties.of()
                                .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                .sound(net.minecraft.world.level.block.SoundType.STONE)
                                .requiresCorrectToolForDrops()
                                .strength(hardness, 6.0F);
        }

        // 矿井：直接采集矿物方块（洞窟表面）
        @SuppressWarnings("null")
        private static Block.Properties mineralNodeProps(float hardness) {
                return Block.Properties.of()
                                .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                .sound(net.minecraft.world.level.block.SoundType.STONE)
                                .requiresCorrectToolForDrops()
                                .noOcclusion()
                                .strength(hardness, 6.0F);
        }

        public static final DeferredBlock<Block> QUARTZ = BLOCKS.register("quartz",
                        () -> new com.stardew.craft.block.mine.MineralNodeBlock(mineralNodeProps(3.0F)));   // Earth段，tier 0
        public static final DeferredBlock<Block> EARTH_CRYSTAL = BLOCKS.register("earth_crystal",
                        () -> new com.stardew.craft.block.mine.MineralNodeBlock(mineralNodeProps(3.0F)));   // Earth段，tier 0
        public static final DeferredBlock<Block> FROZEN_TEAR = BLOCKS.register("frozen_tear",
                        () -> new com.stardew.craft.block.mine.MineralNodeBlock(mineralNodeProps(4.0F)));   // Frost段，tier 1
        public static final DeferredBlock<Block> FIRE_QUARTZ = BLOCKS.register("fire_quartz",
                        () -> new com.stardew.craft.block.mine.MineralNodeBlock(mineralNodeProps(5.0F)));   // Lava段，tier 2

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> EARTH_COPPER_ORE = BLOCKS.register("earth_copper_ore", () -> new Block(oreProps(4.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FROST_COPPER_ORE = BLOCKS.register("frost_copper_ore", () -> new Block(oreProps(4.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LAVA_COPPER_ORE = BLOCKS.register("lava_copper_ore", () -> new Block(oreProps(4.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> EARTH_IRON_ORE = BLOCKS.register("earth_iron_ore", () -> new Block(oreProps(5.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FROST_IRON_ORE = BLOCKS.register("frost_iron_ore", () -> new Block(oreProps(5.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LAVA_IRON_ORE = BLOCKS.register("lava_iron_ore", () -> new Block(oreProps(5.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> EARTH_GOLD_ORE = BLOCKS.register("earth_gold_ore", () -> new Block(oreProps(7.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FROST_GOLD_ORE = BLOCKS.register("frost_gold_ore", () -> new Block(oreProps(7.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LAVA_GOLD_ORE = BLOCKS.register("lava_gold_ore", () -> new Block(oreProps(7.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> EARTH_IRIDIUM_ORE = BLOCKS.register("earth_iridium_ore", () -> new Block(oreProps(10.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FROST_IRIDIUM_ORE = BLOCKS.register("frost_iridium_ore", () -> new Block(oreProps(10.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LAVA_IRIDIUM_ORE = BLOCKS.register("lava_iridium_ore", () -> new Block(oreProps(10.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> EARTH_COAL_ORE = BLOCKS.register("earth_coal_ore", () -> new Block(oreProps(3.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FROST_COAL_ORE = BLOCKS.register("frost_coal_ore", () -> new Block(oreProps(3.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LAVA_COAL_ORE = BLOCKS.register("lava_coal_ore", () -> new Block(oreProps(3.0F)));

        // ========== 骷髅矿洞（Skull Cavern）方块 ==========

        // 骷髅矿：主石头
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DESERT_BEDROCK = BLOCKS.register("desert_bedrock",
                        () -> new Block(stoneProps(MapColor.SAND, SoundType.STONE, 12.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DARK_DESERT_BEDROCK = BLOCKS.register("dark_desert_bedrock",
                        () -> new Block(stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 14.0F)));

        // 骷髅矿：装饰石
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SULFUR_ROCK = BLOCKS.register("sulfur_rock",
                        () -> new Block(stoneProps(MapColor.COLOR_YELLOW, SoundType.STONE, 6.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WEATHERED_STONE = BLOCKS.register("weathered_stone",
                        () -> new Block(stoneProps(MapColor.TERRACOTTA_WHITE, SoundType.STONE, 5.0F)));

        // 骷髅矿主石头/装饰石的建材变体（slab/stairs/wall）
        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> DESERT_BEDROCK_SLAB = slab("desert_bedrock_slab",
                        stoneProps(MapColor.SAND, SoundType.STONE, 12.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> DESERT_BEDROCK_STAIRS = stairs("desert_bedrock_stairs", DESERT_BEDROCK,
                        stoneProps(MapColor.SAND, SoundType.STONE, 12.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> DESERT_BEDROCK_WALL = wall("desert_bedrock_wall",
                        stoneProps(MapColor.SAND, SoundType.STONE, 12.0F));

        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> DARK_DESERT_BEDROCK_SLAB = slab("dark_desert_bedrock_slab",
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 14.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> DARK_DESERT_BEDROCK_STAIRS = stairs("dark_desert_bedrock_stairs", DARK_DESERT_BEDROCK,
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 14.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> DARK_DESERT_BEDROCK_WALL = wall("dark_desert_bedrock_wall",
                        stoneProps(MapColor.DEEPSLATE, SoundType.DEEPSLATE, 14.0F));

        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> SULFUR_ROCK_SLAB = slab("sulfur_rock_slab",
                        stoneProps(MapColor.COLOR_YELLOW, SoundType.STONE, 6.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> SULFUR_ROCK_STAIRS = stairs("sulfur_rock_stairs", SULFUR_ROCK,
                        stoneProps(MapColor.COLOR_YELLOW, SoundType.STONE, 6.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> SULFUR_ROCK_WALL = wall("sulfur_rock_wall",
                        stoneProps(MapColor.COLOR_YELLOW, SoundType.STONE, 6.0F));

        @SuppressWarnings("null")
        public static final DeferredBlock<SlabBlock> WEATHERED_STONE_SLAB = slab("weathered_stone_slab",
                        stoneProps(MapColor.TERRACOTTA_WHITE, SoundType.STONE, 5.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<StairBlock> WEATHERED_STONE_STAIRS = stairs("weathered_stone_stairs", WEATHERED_STONE,
                        stoneProps(MapColor.TERRACOTTA_WHITE, SoundType.STONE, 5.0F));
        @SuppressWarnings("null")
        public static final DeferredBlock<WallBlock> WEATHERED_STONE_WALL = wall("weathered_stone_wall",
                        stoneProps(MapColor.TERRACOTTA_WHITE, SoundType.STONE, 5.0F));

        // 骷髅矿：矿石（desert 主题外观）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DESERT_COPPER_ORE = BLOCKS.register("desert_copper_ore", () -> new Block(oreProps(4.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DESERT_IRON_ORE = BLOCKS.register("desert_iron_ore", () -> new Block(oreProps(5.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DESERT_GOLD_ORE = BLOCKS.register("desert_gold_ore", () -> new Block(oreProps(7.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DESERT_IRIDIUM_ORE = BLOCKS.register("desert_iridium_ore", () -> new Block(oreProps(10.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DESERT_COAL_ORE = BLOCKS.register("desert_coal_ore", () -> new Block(oreProps(3.0F)));

        // 骷髅矿：功能方块
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> QUICKSAND = BLOCKS.register("quicksand",
                        () -> new com.stardew.craft.block.mine.QuicksandBlock(Block.Properties.of()
                                        .mapColor(MapColor.SAND)
                                        .sound(SoundType.SAND)
                                        .strength(0.5F, 0.5F)
                                        .noOcclusion()
                                        .isViewBlocking((s, g, p) -> true)
                                        .isSuffocating((s, g, p) -> false)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TOXIC_SPORE_BLOCK = BLOCKS.register("toxic_spore_block",
                        () -> new com.stardew.craft.block.mine.ToxicSporeBlock(Block.Properties.of()
                                        .mapColor(MapColor.COLOR_GREEN)
                                        .sound(SoundType.MOSS)
                                        .strength(1.0F, 1.0F)
                                        .lightLevel(state -> 3)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> UNSTABLE_ROCK = BLOCKS.register("unstable_rock",
                        () -> new com.stardew.craft.block.mine.UnstableRockBlock(Block.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .sound(SoundType.STONE)
                                        .strength(3.0F, 3.0F)));

        // 矿井：矿物矿石节点（宝石矿，晶洞产物不做）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> AMETHYST_ORE = BLOCKS.register("amethyst_ore", () -> new Block(oreProps(4.0F)));   // tier 0
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> AQUAMARINE_ORE = BLOCKS.register("aquamarine_ore", () -> new Block(oreProps(5.0F))); // tier 1
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DIAMOND_ORE = BLOCKS.register("diamond_ore", () -> new Block(oreProps(7.0F)));     // tier 2
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> EMERALD_ORE = BLOCKS.register("emerald_ore", () -> new Block(oreProps(6.0F)));     // tier 2
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> JADE_ORE = BLOCKS.register("jade_ore", () -> new Block(oreProps(5.0F)));           // tier 1
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> RUBY_ORE = BLOCKS.register("ruby_ore", () -> new Block(oreProps(6.0F)));           // tier 2
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TOPAZ_ORE = BLOCKS.register("topaz_ore", () -> new Block(oreProps(4.0F)));         // tier 0

    // 作物方块
    public static final DeferredBlock<Block> AMARANTH_CROP = BLOCKS.register("amaranth_crop",
            () -> new com.stardew.craft.block.crop.AmaranthCropBlock());

    public static final DeferredBlock<Block> ANCIENT_FRUIT_CROP = BLOCKS.register("ancient_fruit_crop",
            () -> new com.stardew.craft.block.crop.AncientFruitCropBlock());

    public static final DeferredBlock<Block> ARTICHOKE_CROP = BLOCKS.register("artichoke_crop",
            () -> new com.stardew.craft.block.crop.ArtichokeCropBlock());

    public static final DeferredBlock<Block> BEET_CROP = BLOCKS.register("beet_crop",
            () -> new com.stardew.craft.block.crop.BeetCropBlock());

    public static final DeferredBlock<Block> BLUE_JAZZ_CROP = BLOCKS.register("blue_jazz_crop",
            () -> new com.stardew.craft.block.crop.BlueJazzCropBlock());

    public static final DeferredBlock<Block> BLUEBERRY_CROP = BLOCKS.register("blueberry_crop",
            () -> new com.stardew.craft.block.crop.BlueberryCropBlock());

    public static final DeferredBlock<Block> BOK_CHOY_CROP = BLOCKS.register("bok_choy_crop",
            () -> new com.stardew.craft.block.crop.BokChoyCropBlock());

    public static final DeferredBlock<Block> BROCCOLI_CROP = BLOCKS.register("broccoli_crop",
            () -> new com.stardew.craft.block.crop.BroccoliCropBlock());

    public static final DeferredBlock<Block> CARROT_CROP = BLOCKS.register("carrot_crop",
            () -> new com.stardew.craft.block.crop.CarrotCropBlock());

    public static final DeferredBlock<Block> CAULIFLOWER_CROP = BLOCKS.register("cauliflower_crop",
            () -> new com.stardew.craft.block.crop.CauliflowerCropBlock());

    public static final DeferredBlock<Block> COFFEE_BEAN_CROP = BLOCKS.register("coffee_bean_crop",
            () -> new com.stardew.craft.block.crop.CoffeeBeanCropBlock());

    public static final DeferredBlock<Block> CORN_CROP = BLOCKS.register("corn_crop",
            () -> new com.stardew.craft.block.crop.CornCropBlock());

    public static final DeferredBlock<Block> CRANBERRY_CROP = BLOCKS.register("cranberry_crop",
            () -> new com.stardew.craft.block.crop.CranberryCropBlock());

    public static final DeferredBlock<Block> EGGPLANT_CROP = BLOCKS.register("eggplant_crop",
            () -> new com.stardew.craft.block.crop.EggplantCropBlock());

    public static final DeferredBlock<Block> FAIRY_ROSE_CROP = BLOCKS.register("fairy_rose_crop",
            () -> new com.stardew.craft.block.crop.FairyRoseCropBlock());

    public static final DeferredBlock<Block> GARLIC_CROP = BLOCKS.register("garlic_crop",
            () -> new com.stardew.craft.block.crop.GarlicCropBlock());

    public static final DeferredBlock<Block> GRAPE_CROP = BLOCKS.register("grape_crop",
            () -> new com.stardew.craft.block.crop.GrapeCropBlock());

    public static final DeferredBlock<Block> GREEN_BEAN_CROP = BLOCKS.register("green_bean_crop",
            () -> new com.stardew.craft.block.crop.GreenBeanCropBlock());

    public static final DeferredBlock<Block> HOPS_CROP = BLOCKS.register("hops_crop",
            () -> new com.stardew.craft.block.crop.HopsCropBlock());

    public static final DeferredBlock<Block> HOT_PEPPER_CROP = BLOCKS.register("hot_pepper_crop",
            () -> new com.stardew.craft.block.crop.HotPepperCropBlock());

    public static final DeferredBlock<Block> KALE_CROP = BLOCKS.register("kale_crop",
            () -> new com.stardew.craft.block.crop.KaleCropBlock());

    public static final DeferredBlock<Block> MELON_CROP = BLOCKS.register("melon_crop",
            () -> new com.stardew.craft.block.crop.MelonCropBlock());

    public static final DeferredBlock<Block> PARSNIP_CROP = BLOCKS.register("parsnip_crop",
            () -> new com.stardew.craft.block.crop.ParsnipCropBlock());

    public static final DeferredBlock<Block> POPPY_CROP = BLOCKS.register("poppy_crop",
            () -> new com.stardew.craft.block.crop.PoppyCropBlock());

    public static final DeferredBlock<Block> POTATO_CROP = BLOCKS.register("potato_crop",
            () -> new com.stardew.craft.block.crop.PotatoCropBlock());

    public static final DeferredBlock<Block> POWDER_MELON_CROP = BLOCKS.register("powder_melon_crop",
            () -> new com.stardew.craft.block.crop.PowderMelonCropBlock());

    public static final DeferredBlock<Block> PUMPKIN_CROP = BLOCKS.register("pumpkin_crop",
            () -> new com.stardew.craft.block.crop.PumpkinCropBlock());

    public static final DeferredBlock<Block> RADISH_CROP = BLOCKS.register("radish_crop",
            () -> new com.stardew.craft.block.crop.RadishCropBlock());

    public static final DeferredBlock<Block> RED_CABBAGE_CROP = BLOCKS.register("red_cabbage_crop",
            () -> new com.stardew.craft.block.crop.RedCabbageCropBlock());

    public static final DeferredBlock<Block> RHUBARB_CROP = BLOCKS.register("rhubarb_crop",
            () -> new com.stardew.craft.block.crop.RhubarbCropBlock());

    public static final DeferredBlock<Block> STARFRUIT_CROP = BLOCKS.register("starfruit_crop",
            () -> new com.stardew.craft.block.crop.StarfruitCropBlock());

    public static final DeferredBlock<Block> STRAWBERRY_CROP = BLOCKS.register("strawberry_crop",
            () -> new com.stardew.craft.block.crop.StrawberryCropBlock());

    public static final DeferredBlock<Block> SUMMER_SPANGLE_CROP = BLOCKS.register("summer_spangle_crop",
            () -> new com.stardew.craft.block.crop.SummerSpangleCropBlock());

    public static final DeferredBlock<Block> SUMMER_SQUASH_CROP = BLOCKS.register("summer_squash_crop",
            () -> new com.stardew.craft.block.crop.SummerSquashCropBlock());

    public static final DeferredBlock<Block> SUNFLOWER_CROP = BLOCKS.register("sunflower_crop",
            () -> new com.stardew.craft.block.crop.SunflowerCropBlock());

    public static final DeferredBlock<Block> TOMATO_CROP = BLOCKS.register("tomato_crop",
            () -> new com.stardew.craft.block.crop.TomatoCropBlock());

    public static final DeferredBlock<Block> TULIP_CROP = BLOCKS.register("tulip_crop",
            () -> new com.stardew.craft.block.crop.TulipCropBlock());

    public static final DeferredBlock<Block> WHEAT_CROP = BLOCKS.register("wheat_crop",
            () -> new com.stardew.craft.block.crop.WheatCropBlock());

    public static final DeferredBlock<Block> YAM_CROP = BLOCKS.register("yam_crop",
            () -> new com.stardew.craft.block.crop.YamCropBlock());

    // Wild Seed Crops (grow 7 days then transform into forage blocks)
    public static final DeferredBlock<Block> SPRING_WILD_SEED_CROP = BLOCKS.register("spring_wild_seed_crop",
            () -> new com.stardew.craft.block.crop.WildSeedCropBlock(0, com.stardew.craft.item.ModItems.SPRING_SEEDS));
    public static final DeferredBlock<Block> SUMMER_WILD_SEED_CROP = BLOCKS.register("summer_wild_seed_crop",
            () -> new com.stardew.craft.block.crop.WildSeedCropBlock(1, com.stardew.craft.item.ModItems.SUMMER_SEEDS));
    public static final DeferredBlock<Block> FALL_WILD_SEED_CROP = BLOCKS.register("fall_wild_seed_crop",
            () -> new com.stardew.craft.block.crop.WildSeedCropBlock(2, com.stardew.craft.item.ModItems.FALL_SEEDS));
    public static final DeferredBlock<Block> WINTER_WILD_SEED_CROP = BLOCKS.register("winter_wild_seed_crop",
            () -> new com.stardew.craft.block.crop.WildSeedCropBlock(3, com.stardew.craft.item.ModItems.WINTER_SEEDS));

    @SuppressWarnings("null")
public static final DeferredBlock<Block> DEAD_CROP = BLOCKS.register("dead_crop",
            () -> new com.stardew.craft.block.crop.DeadCropBlock(Block.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                    .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .instabreak()));

        // 野生树（原型：橡树）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_OAK_TRUNK0 = BLOCKS.register("wild_oak_trunk0",
                        () -> new com.stardew.craft.block.tree.WildOakTrunkBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_OAK_TRUNK1 = BLOCKS.register("wild_oak_trunk1",
                        () -> new com.stardew.craft.block.tree.WildOakTrunkBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_OAK_BRANCH1 = BLOCKS.register("wild_oak_branch1",
                        () -> new com.stardew.craft.block.tree.WildOakBranchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_OAK_BRANCH2 = BLOCKS.register("wild_oak_branch2",
                        () -> new com.stardew.craft.block.tree.WildOakBranchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_OAK_LEAVES = BLOCKS.register("wild_oak_leaves",
                        () -> new com.stardew.craft.block.tree.WildOakLeavesBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .strength(0.2F)
                                        .noOcclusion()));

        // 野生树苗（2阶段，28天成熟）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_OAK_SAPLING0 = BLOCKS.register("wild_oak_sapling0",
                        () -> new com.stardew.craft.block.tree.WildTreeSaplingBlock(
                                        com.stardew.craft.tree.WildTrees.OAK,
                                        0,
                                        Block.Properties.of()
                                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()
                                                        .instabreak()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_OAK_SAPLING1 = BLOCKS.register("wild_oak_sapling1",
                        () -> new com.stardew.craft.block.tree.WildTreeSaplingBlock(
                                        com.stardew.craft.tree.WildTrees.OAK,
                                        1,
                                        Block.Properties.of()
                                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()
                                                        .instabreak()));

        // 野生树：枫树
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAPLE_TRUNK0 = BLOCKS.register("wild_maple_trunk0",
                        () -> new com.stardew.craft.block.tree.WildOakTrunkBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAPLE_TRUNK1 = BLOCKS.register("wild_maple_trunk1",
                        () -> new com.stardew.craft.block.tree.WildOakTrunkBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAPLE_BRANCH1 = BLOCKS.register("wild_maple_branch1",
                        () -> new com.stardew.craft.block.tree.WildOakBranchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAPLE_BRANCH2 = BLOCKS.register("wild_maple_branch2",
                        () -> new com.stardew.craft.block.tree.WildOakBranchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAPLE_LEAVES = BLOCKS.register("wild_maple_leaves",
                        () -> new com.stardew.craft.block.tree.WildOakLeavesBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .strength(0.2F)
                                        .noOcclusion()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAPLE_SAPLING0 = BLOCKS.register("wild_maple_sapling0",
                        () -> new com.stardew.craft.block.tree.WildTreeSaplingBlock(
                                        com.stardew.craft.tree.WildTrees.MAPLE,
                                        0,
                                        Block.Properties.of()
                                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()
                                                        .instabreak()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAPLE_SAPLING1 = BLOCKS.register("wild_maple_sapling1",
                        () -> new com.stardew.craft.block.tree.WildTreeSaplingBlock(
                                        com.stardew.craft.tree.WildTrees.MAPLE,
                                        1,
                                        Block.Properties.of()
                                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()
                                                        .instabreak()));

        // 野生树：松树
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_PINE_TRUNK0 = BLOCKS.register("wild_pine_trunk0",
                        () -> new com.stardew.craft.block.tree.WildOakTrunkBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_PINE_TRUNK1 = BLOCKS.register("wild_pine_trunk1",
                        () -> new com.stardew.craft.block.tree.WildOakTrunkBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_PINE_BRANCH1 = BLOCKS.register("wild_pine_branch1",
                        () -> new com.stardew.craft.block.tree.WildOakBranchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_PINE_BRANCH2 = BLOCKS.register("wild_pine_branch2",
                        () -> new com.stardew.craft.block.tree.WildOakBranchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_PINE_LEAVES = BLOCKS.register("wild_pine_leaves",
                        () -> new com.stardew.craft.block.tree.WildOakLeavesBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .strength(0.2F)
                                        .noOcclusion()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_PINE_SAPLING0 = BLOCKS.register("wild_pine_sapling0",
                        () -> new com.stardew.craft.block.tree.WildTreeSaplingBlock(
                                        com.stardew.craft.tree.WildTrees.PINE,
                                        0,
                                        Block.Properties.of()
                                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()
                                                        .instabreak()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_PINE_SAPLING1 = BLOCKS.register("wild_pine_sapling1",
                        () -> new com.stardew.craft.block.tree.WildTreeSaplingBlock(
                                        com.stardew.craft.tree.WildTrees.PINE,
                                        1,
                                        Block.Properties.of()
                                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()
                                                        .instabreak()));

        // 野生树：桃花心木
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAHOGANY_TRUNK0 = BLOCKS.register("wild_mahogany_trunk0",
                        () -> new com.stardew.craft.block.tree.WildOakTrunkBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAHOGANY_TRUNK1 = BLOCKS.register("wild_mahogany_trunk1",
                        () -> new com.stardew.craft.block.tree.WildOakTrunkBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAHOGANY_BRANCH1 = BLOCKS.register("wild_mahogany_branch1",
                        () -> new com.stardew.craft.block.tree.WildOakBranchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAHOGANY_BRANCH2 = BLOCKS.register("wild_mahogany_branch2",
                        () -> new com.stardew.craft.block.tree.WildOakBranchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAHOGANY_LEAVES = BLOCKS.register("wild_mahogany_leaves",
                        () -> new com.stardew.craft.block.tree.WildOakLeavesBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .strength(0.2F)
                                        .noOcclusion()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAHOGANY_SAPLING0 = BLOCKS.register("wild_mahogany_sapling0",
                        () -> new com.stardew.craft.block.tree.WildTreeSaplingBlock(
                                        com.stardew.craft.tree.WildTrees.MAHOGANY,
                                        0,
                                        Block.Properties.of()
                                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()
                                                        .instabreak()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MAHOGANY_SAPLING1 = BLOCKS.register("wild_mahogany_sapling1",
                        () -> new com.stardew.craft.block.tree.WildTreeSaplingBlock(
                                        com.stardew.craft.tree.WildTrees.MAHOGANY,
                                        1,
                                        Block.Properties.of()
                                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()
                                                        .instabreak()));

        // 野生树：神秘树
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MYSTIC_TREE_TRUNK0 = BLOCKS.register("wild_mystic_tree_trunk0",
                        () -> new com.stardew.craft.block.tree.WildOakTrunkBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MYSTIC_TREE_TRUNK1 = BLOCKS.register("wild_mystic_tree_trunk1",
                        () -> new com.stardew.craft.block.tree.WildOakTrunkBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MYSTIC_TREE_BRANCH1 = BLOCKS.register("wild_mystic_tree_branch1",
                        () -> new com.stardew.craft.block.tree.WildOakBranchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MYSTIC_TREE_BRANCH2 = BLOCKS.register("wild_mystic_tree_branch2",
                        () -> new com.stardew.craft.block.tree.WildOakBranchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MYSTIC_TREE_LEAVES = BLOCKS.register("wild_mystic_tree_leaves",
                        () -> new com.stardew.craft.block.tree.WildOakLeavesBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .strength(0.2F)
                                        .noOcclusion()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MYSTIC_TREE_SAPLING0 = BLOCKS.register("wild_mystic_tree_sapling0",
                        () -> new com.stardew.craft.block.tree.WildTreeSaplingBlock(
                                        com.stardew.craft.tree.WildTrees.MYSTIC_TREE,
                                        0,
                                        Block.Properties.of()
                                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()
                                                        .instabreak()));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WILD_MYSTIC_TREE_SAPLING1 = BLOCKS.register("wild_mystic_tree_sapling1",
                        () -> new com.stardew.craft.block.tree.WildTreeSaplingBlock(
                                        com.stardew.craft.tree.WildTrees.MYSTIC_TREE,
                                        1,
                                        Block.Properties.of()
                                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()
                                                        .instabreak()));

        // 实用设施
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TAPPER = BLOCKS.register("tapper",
                        () -> new com.stardew.craft.block.utility.TapperBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> KEG = BLOCKS.register("keg",
                        () -> new com.stardew.craft.block.utility.KegBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> PRESERVES_JAR = BLOCKS.register("preserves_jar",
                        () -> new com.stardew.craft.block.utility.PreservesJarBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DEHYDRATOR = BLOCKS.register("dehydrator",
                        () -> new com.stardew.craft.block.utility.DehydratorBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BAIT_MAKER = BLOCKS.register("bait_maker",
                        () -> new com.stardew.craft.block.utility.BaitMakerBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FISH_SMOKER = BLOCKS.register("fish_smoker",
                        () -> new com.stardew.craft.block.utility.FishSmokerBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .lightLevel(state -> state.getValue(com.stardew.craft.block.utility.FishSmokerBlock.WORKING) ? 13 : 0)
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> RECYCLING_MACHINE = BLOCKS.register("recycling_machine",
                        () -> new com.stardew.craft.block.utility.RecyclingMachineBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> COOKING_POT = BLOCKS.register("cooking_pot",
                        () -> new com.stardew.craft.block.utility.CookingPotBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CRYSTALARIUM = BLOCKS.register("crystalarium",
                        () -> new com.stardew.craft.block.utility.CrystalariumBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SEED_MAKER = BLOCKS.register("seed_maker",
                        () -> new com.stardew.craft.block.utility.SeedMakerBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> FURNACE = BLOCKS.register("furnace",
                                        () -> new com.stardew.craft.block.utility.FurnaceBlock(Block.Properties.of()
                                                                .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                                                .sound(net.minecraft.world.level.block.SoundType.METAL)
                                                                .noOcclusion()
                                                                .lightLevel(state -> state.getValue(com.stardew.craft.block.utility.FurnaceBlock.WORKING) ? 15 : 0)
                                                                .strength(1.5F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> CHARCOAL_KILN = BLOCKS.register("charcoal_kiln",
                                                () -> new com.stardew.craft.block.utility.CharcoalKilnBlock(Block.Properties.of()
                                                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                                                        .noOcclusion()
                                                                        .lightLevel(state -> state.getValue(com.stardew.craft.block.utility.CharcoalKilnBlock.WORKING) ? 15 : 0)
                                                                        .strength(1.5F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> LIGHTNING_ROD = BLOCKS.register("lightning_rod",
                                () -> new com.stardew.craft.block.utility.LightningRodBlock(Block.Properties.of()
                                                .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                                .sound(net.minecraft.world.level.block.SoundType.METAL)
                                                .noOcclusion()
                                                .strength(1.5F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> SPRINKLER = BLOCKS.register("sprinkler",
                                () -> new com.stardew.craft.block.utility.SprinklerBlock(
                                                com.stardew.craft.block.utility.SprinklerTier.BASIC,
                                                Block.Properties.of()
                                                                .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                                                .sound(net.minecraft.world.level.block.SoundType.METAL)
                                                                .noOcclusion()
                                                                .strength(1.0F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> QUALITY_SPRINKLER = BLOCKS.register("quality_sprinkler",
                                () -> new com.stardew.craft.block.utility.SprinklerBlock(
                                                com.stardew.craft.block.utility.SprinklerTier.QUALITY,
                                                Block.Properties.of()
                                                                .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                                                .sound(net.minecraft.world.level.block.SoundType.METAL)
                                                                .noOcclusion()
                                                                .strength(1.0F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> IRIDIUM_SPRINKLER = BLOCKS.register("iridium_sprinkler",
                                () -> new com.stardew.craft.block.utility.SprinklerBlock(
                                                com.stardew.craft.block.utility.SprinklerTier.IRIDIUM,
                                                Block.Properties.of()
                                                                .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                                                .sound(net.minecraft.world.level.block.SoundType.METAL)
                                                                .noOcclusion()
                                                                .strength(1.0F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> SOLAR_PANEL = BLOCKS.register("solar_panel",
                                                () -> new com.stardew.craft.block.utility.SolarPanelBlock(Block.Properties.of()
                                                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                                                        .noOcclusion()
                                                                        .strength(1.5F, 3.0F)));


                @SuppressWarnings("null")
                public static final DeferredBlock<Block> CASK = BLOCKS.register("cask",
                                                () -> new com.stardew.craft.block.utility.CaskBlock(Block.Properties.of()
                                                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                                                        .noOcclusion()
                                                                        .strength(1.5F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> CHEESE_PRESS = BLOCKS.register("cheese_press",
                                                () -> new com.stardew.craft.block.utility.CheesePressBlock(Block.Properties.of()
                                                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                                                        .noOcclusion()
                                                                        .strength(1.5F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> MAYONNAISE_MACHINE = BLOCKS.register("mayonnaise_machine",
                                                () -> new com.stardew.craft.block.utility.MayonnaiseMachineBlock(Block.Properties.of()
                                                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                                                        .noOcclusion()
                                                                        .strength(1.5F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> INCUBATOR = BLOCKS.register("incubator",
                                                () -> new com.stardew.craft.block.utility.IncubatorBlock(Block.Properties.of()
                                                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                                                        .noOcclusion()
                                                                        .strength(1.5F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> OIL_MAKER = BLOCKS.register("oil_maker",
                                () -> new com.stardew.craft.block.utility.OilMakerBlock(Block.Properties.of()
                                                .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                                .noOcclusion()
                                                .strength(1.5F, 3.0F)));

                @SuppressWarnings("null")
                public static final DeferredBlock<Block> LOOM = BLOCKS.register("loom",
                                () -> new com.stardew.craft.block.utility.LoomBlock(Block.Properties.of()
                                                .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                                .noOcclusion()
                                                .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WORM_BIN = BLOCKS.register("worm_bin",
                        () -> new com.stardew.craft.block.utility.WormBinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FEED_TROUGH = BLOCKS.register("feed_trough",
                        () -> new com.stardew.craft.block.utility.FeedTroughBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> AUTOFEED_TROUGH = BLOCKS.register("autofeed_trough",
                        () -> new com.stardew.craft.block.utility.AutoFeedTroughBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> AUTO_GRABBER = BLOCKS.register("auto_grabber",
                        () -> new com.stardew.craft.block.utility.AutoGrabberBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> AUTO_PETTER = BLOCKS.register("auto_petter",
                        () -> new com.stardew.craft.block.utility.AutoPetterBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WOODEN_CHEST = BLOCKS.register("wooden_chest",
                        () -> new com.stardew.craft.block.utility.WoodenChestBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> STONE_CHEST = BLOCKS.register("stone_chest",
                        () -> new com.stardew.craft.block.utility.StoneChestBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FRIDGE = BLOCKS.register("fridge",
                        () -> new com.stardew.craft.block.utility.FridgeBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F), "stardewcraft:decor/common/fridge"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> MAILBOX = BLOCKS.register("mailbox",
                        () -> new com.stardew.craft.block.utility.MailboxBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHIPPING_BIN = BLOCKS.register("shipping_bin",
                        () -> new com.stardew.craft.block.utility.ShippingBinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> HEATER = BLOCKS.register("heater",
                        () -> new com.stardew.craft.block.utility.HeaterBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> HAY_HOPPER = BLOCKS.register("hay_hopper",
                        () -> new com.stardew.craft.block.utility.HayHopperBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        // 硬度=1.5F（木质），未绑定建筑时可正常挖掉；绑定后由 onDestroyedByPlayer 拦截
        public static final DeferredBlock<Block> COOP_MANAGER = BLOCKS.register("coop_manager",
                        () -> new com.stardew.craft.block.utility.CoopManagerBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .strength(1.5F, 3600000.0F)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BARN_MANAGER = BLOCKS.register("barn_manager",
                        () -> new com.stardew.craft.block.utility.BarnManagerBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .strength(1.5F, 3600000.0F)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SILO_MANAGER = BLOCKS.register("silo_manager",
                        () -> new com.stardew.craft.block.utility.SiloManagerBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .strength(1.5F, 3600000.0F)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TRASH_BIN = BLOCKS.register("trash_bin",
                        () -> new com.stardew.craft.block.utility.TrashBinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(-1.0F, 3600000.0F)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DELUXE_WORM_BIN = BLOCKS.register("deluxe_worm_bin",
                        () -> new com.stardew.craft.block.utility.DeluxeWormBinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BEE_HOUSE = BLOCKS.register("bee_house",
                        () -> new com.stardew.craft.block.utility.BeeHouseBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CRAB_POT = BLOCKS.register("crab_pot",
                        () -> new com.stardew.craft.block.utility.CrabPotBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        // 需求：徒手也能很快拆（类似羊毛/更快）
                                        .strength(0.2F, 1.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> MUSEUM_EXHIBIT_STAND = BLOCKS.register("museum_exhibit_stand",
                        () -> new com.stardew.craft.block.utility.MuseumExhibitStandBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BED_1 = BLOCKS.register("bed_1",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOL)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/bed_1"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BED_2 = BLOCKS.register("bed_2",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOL)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/bed_2"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SOFA = BLOCKS.register("sofa",
                        () -> new com.stardew.craft.block.utility.SofaBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOL)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CHAIR_1 = BLOCKS.register("chair_1",
                        () -> new com.stardew.craft.block.utility.ChairBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/chair_1", 7.0D / 16.0D));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CHAIR_2 = BLOCKS.register("chair_2",
                        () -> new com.stardew.craft.block.utility.ChairBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/chair_2", 7.0D / 16.0D));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CHAIR_3 = BLOCKS.register("chair_3",
                        () -> new com.stardew.craft.block.utility.ChairBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/chair_3", 7.0D / 16.0D));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LIGHT_1 = BLOCKS.register("light_1",
                        () -> new com.stardew.craft.block.decor.ToggleableWallLightBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.LANTERN)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/light_1"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LIGHT_2 = BLOCKS.register("light_2",
                        () -> new com.stardew.craft.block.decor.ToggleableWallLightBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.LANTERN)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/light_2"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LIGHT_3 = BLOCKS.register("light_3",
                        () -> new com.stardew.craft.block.decor.ToggleableWallLightBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.LANTERN)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/light_3"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LIGHT_4 = BLOCKS.register("light_4",
                        () -> new com.stardew.craft.block.decor.ToggleableWallLightBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.LANTERN)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/light_4"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LIGHT_5 = BLOCKS.register("light_5",
                        () -> new com.stardew.craft.block.decor.ToggleableCeilingLightBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.LANTERN)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/light_5"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LIGHT_6 = BLOCKS.register("light_6",
                        () -> new com.stardew.craft.block.decor.ToggleableWallLightBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.LANTERN)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/light_6"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LIGHT_7 = BLOCKS.register("light_7",
                        () -> new com.stardew.craft.block.decor.ToggleableWallLightBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.LANTERN)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/light_7"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CUSHION = BLOCKS.register("cushion",
                        () -> new com.stardew.craft.block.utility.CushionBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOL)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/cushion"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> OFFICE_STOOL = BLOCKS.register("office_stool",
                        () -> new com.stardew.craft.block.utility.OfficeStoolBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/office_stool_collision"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> OFFICE_CHAIR_2 = BLOCKS.register("office_chair_2",
                        () -> new com.stardew.craft.block.utility.OfficeChair2Block(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/office_chair_2_collision"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> OFFICE_STOOL_TOP_RENDER = BLOCKS.register("office_stool_top_render",
                        () -> new com.stardew.craft.block.utility.OfficeStoolTopRenderBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.2F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> OFFICE_CHAIR_2_TOP_RENDER = BLOCKS.register("office_chair_2_top_render",
                        () -> new com.stardew.craft.block.utility.OfficeChair2TopRenderBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.2F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SINK_4 = BLOCKS.register("sink_4",
                        () -> new com.stardew.craft.block.utility.MapUtilityStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.3F), "stardewcraft:decor/common/sink_4"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FLOOR_LAMP = BLOCKS.register("floor_lamp",
                        () -> new com.stardew.craft.block.utility.ToggleableDecorLightBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.LANTERN)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/floor_lamp_5"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TABLE_LAMP = BLOCKS.register("table_lamp",
                        () -> new com.stardew.craft.block.utility.ToggleableDecorLightBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.LANTERN)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/table_lamp"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> STOOL = BLOCKS.register("stool",
                        () -> new com.stardew.craft.block.utility.DyeableChairBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/stool", 6.0D / 16.0D));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DINING_CHAIR_WOOD = BLOCKS.register("dining_chair_wood",
                        () -> new com.stardew.craft.block.utility.DyeableChairBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/dining_chair_wood", 8.0D / 16.0D));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> ARCADE_MACHINE = BLOCKS.register("arcade_machine",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/arcade_machine"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DINING_CHAIR_IRON = BLOCKS.register("dining_chair_iron",
                        () -> new com.stardew.craft.block.utility.DyeableChairBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/dining_chair_iron", 11.0D / 16.0D));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> PHOTO_FRAME = BLOCKS.register("photo_frame",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/photo_frame"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> OAK_TABLE = BLOCKS.register("oak_table",
                        () -> new com.stardew.craft.block.utility.OakTableBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SPRUCE_TABLE = BLOCKS.register("spruce_table",
                        () -> new com.stardew.craft.block.utility.OakTableBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F), "spruce_table"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BIRCH_TABLE = BLOCKS.register("birch_table",
                        () -> new com.stardew.craft.block.utility.OakTableBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.2F), "birch_table"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SPRUCE_COUNTER = BLOCKS.register("spruce_counter",
                        () -> new com.stardew.craft.block.utility.SpruceCounterBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> OAK_ROUND_TABLE = BLOCKS.register("oak_round_table",
                        () -> new com.stardew.craft.block.utility.OakRoundTableBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TABLE_LANTERN = BLOCKS.register("table_lantern",
                        () -> new com.stardew.craft.block.utility.ToggleableDecorLightBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.LANTERN)
                                        .noOcclusion()
                                        .strength(0.2F), "stardewcraft:decor/common/table_lantern"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> JUKEBOX = BLOCKS.register("jukebox",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/common/jukebox"));

        // 地图装饰：皮埃尔商店（第一批）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_BASKET = BLOCKS.register("shop_basket",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/2"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_CRATE_FRUIT_1 = BLOCKS.register("shop_crate_fruit_1",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/3_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_CRATE_FRUIT_2 = BLOCKS.register("shop_crate_fruit_2",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/3_2"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_CRATE_FRUIT_3 = BLOCKS.register("shop_crate_fruit_3",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/3_3"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_CRATE_FRUIT_4 = BLOCKS.register("shop_crate_fruit_4",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/3_4"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_CRATE_FRUIT_5 = BLOCKS.register("shop_crate_fruit_5",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/3_5"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_CRATE_FRUIT_6 = BLOCKS.register("shop_crate_fruit_6",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/3_6"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_CRATE_FRUIT_7 = BLOCKS.register("shop_crate_fruit_7",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/3_7"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_CRATE_FRUIT_8 = BLOCKS.register("shop_crate_fruit_8",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/3_8"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_CRATE_FRUIT_9 = BLOCKS.register("shop_crate_fruit_9",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/3_9"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_CRATE_FRUIT_10 = BLOCKS.register("shop_crate_fruit_10",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/3_10"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_PACK_BOX = BLOCKS.register("shop_pack_box",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/4_1"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_COUNTER_1 = BLOCKS.register("shop_counter_1",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(1.0F, 1.0F), "stardewcraft:decor/pierre_shop/5_1", true));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_COUNTER_2 = BLOCKS.register("shop_counter_2",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(1.0F, 1.0F), "stardewcraft:decor/pierre_shop/5_2", true));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_COUNTER_3 = BLOCKS.register("shop_counter_3",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion().strength(1.0F, 1.0F), "stardewcraft:decor/pierre_shop/5_3", true));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SUPERMARKET_SHELF_1 = BLOCKS.register("supermarket_shelf_1",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.0F, 1.0F), "stardewcraft:decor/common/supermarket_shelf_1",
                                        0, 0, -16, 16, 25, 32));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SUPERMARKET_SHELF_2 = BLOCKS.register("supermarket_shelf_2",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.0F, 1.0F), "stardewcraft:decor/common/supermarket_shelf_2",
                                        0, 0, -16, 16, 25, 32));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_SHIPPING_BIN = BLOCKS.register("shop_shipping_bin",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/pierre_shop/7_1"));

        // 地图装饰：地毯（地面）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_1 = BLOCKS.register("carpet_1",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_2 = BLOCKS.register("carpet_2",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_2"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_3 = BLOCKS.register("carpet_3",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_3"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_4 = BLOCKS.register("carpet_4",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_4"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_5 = BLOCKS.register("carpet_5",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_5"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_6 = BLOCKS.register("carpet_6",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_6"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_7 = BLOCKS.register("carpet_7",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_7"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_8 = BLOCKS.register("carpet_8",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_8"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_9 = BLOCKS.register("carpet_9",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_9"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_10 = BLOCKS.register("carpet_10",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_10"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_11 = BLOCKS.register("carpet_11",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_11"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_12 = BLOCKS.register("carpet_12",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_12"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_13 = BLOCKS.register("carpet_13",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_13"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_14 = BLOCKS.register("carpet_14",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_14"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_15 = BLOCKS.register("carpet_15",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_15"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_16 = BLOCKS.register("carpet_16",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_16"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_17 = BLOCKS.register("carpet_17",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_17"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_18 = BLOCKS.register("carpet_18",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_18"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_19 = BLOCKS.register("carpet_19",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_19"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_20 = BLOCKS.register("carpet_20",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_20"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> CARPET_21 = BLOCKS.register("carpet_21",
                        () -> new com.stardew.craft.block.decor.CarpetDecorBlock(Block.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOL).sound(net.minecraft.world.level.block.SoundType.WOOL).noOcclusion().instabreak(), "stardewcraft:decor/carpet/carpet_21"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_HANGING_SMALL_A = BLOCKS.register("wall_hanging_small_a",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/wall_decor/common/wall_hanging_small_a"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_NOTICE_BOARD_SMALL = BLOCKS.register("wall_notice_board_small",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/wall_decor/common/wall_notice_board_small"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_HANGING_SMALL_B = BLOCKS.register("wall_hanging_small_b",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/wall_decor/common/wall_hanging_small_b"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_BULLETIN_NOTES = BLOCKS.register("wall_bulletin_notes",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/wall_decor/common/wall_bulletin_notes"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_HANGING_STRIP = BLOCKS.register("wall_hanging_strip",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_SWITCH_PANEL = BLOCKS.register("wall_switch_panel",
                        () -> new com.stardew.craft.block.decor.MapDecorWallSwitchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_HANGING_TRIPTYCH = BLOCKS.register("wall_hanging_triptych",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/wall_decor/common/wall_hanging_triptych"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_HANGING_ORNAMENT = BLOCKS.register("wall_hanging_ornament",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_NOTICE_BOARD_MEDIUM = BLOCKS.register("wall_notice_board_medium",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_FRAME_WIDE = BLOCKS.register("wall_frame_wide",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/wall_decor/common/wall_frame_wide"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_FRAME_DOUBLE = BLOCKS.register("wall_frame_double",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/wall_decor/common/wall_frame_double"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_STICKY_NOTES = BLOCKS.register("wall_sticky_notes",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_POSTER_GAMEPAD = BLOCKS.register("wall_poster_gamepad",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_POSTER_DOLPHIN = BLOCKS.register("wall_poster_dolphin",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_POSTER_GAME_CHARACTER = BLOCKS.register("wall_poster_game_character",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_OUTLET = BLOCKS.register("wall_outlet",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_PHOTO_WHITE_HALL = BLOCKS.register("wall_photo_white_hall",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/wall_decor/common/wall_photo_white_hall"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_BLACKSMITH_SIGN = BLOCKS.register("wall_blacksmith_sign",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/wall_decor/common/wall_blacksmith_sign"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_BLACKSMITH_HAMMERS = BLOCKS.register("wall_blacksmith_hammers",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/wall_decor/common/wall_blacksmith_hammers"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_WINDOW_1 = BLOCKS.register("shop_window_1",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                        .lightLevel(state -> 15)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/pierre_shop/window_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHOP_WINDOW_2 = BLOCKS.register("shop_window_2",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                        .lightLevel(state -> 15)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/pierre_shop/window_2"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FIREPLACE_LARGE = BLOCKS.register("fireplace_large",
                        () -> new com.stardew.craft.block.decor.LargeFireplaceDecorBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(1.5F, 6.0F), "stardewcraft:decor/common/fireplace_1"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BONSAI_1 = BLOCKS.register("bonsai_1",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/bonsai/bonsai_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BONSAI_2 = BLOCKS.register("bonsai_2",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/bonsai/bonsai_2"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BONSAI_3 = BLOCKS.register("bonsai_3",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/bonsai/bonsai_3"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BONSAI_4 = BLOCKS.register("bonsai_4",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/bonsai/bonsai_4"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BONSAI_5_WALL = BLOCKS.register("bonsai_5_wall",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/bonsai/bonsai_5_wall"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BONSAI_BUSH = BLOCKS.register("bonsai_bush",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/bonsai/bonsai_6"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> COMPUTER = BLOCKS.register("computer",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.8F, 2.0F), "stardewcraft:decor/common/computer_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SAILBOAT = BLOCKS.register("sailboat",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.2F), "stardewcraft:decor/common/sailboat_2"));
        // TODO: Dresser variants will later gain hat/ring/equipment storage behavior.
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DRESSER_1 = BLOCKS.register("dresser_1",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.0F, 2.0F), "stardewcraft:decor/common/dresser_3_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DRESSER_2 = BLOCKS.register("dresser_2",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.0F, 2.0F), "stardewcraft:decor/common/dresser_3_2"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DRESSER_3 = BLOCKS.register("dresser_3",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.0F, 2.0F), "stardewcraft:decor/common/dresser_3_3"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WOOD_BUNDLE = BLOCKS.register("wood_bundle",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.2F), "stardewcraft:decor/common/wood_bundle_4"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BARREL = BLOCKS.register("barrel",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.6F), "stardewcraft:decor/common/barrel_5"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> POTTED_PLANT_1 = BLOCKS.register("potted_plant_1",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/common/bonsai_6_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> POTTED_PLANT_2 = BLOCKS.register("potted_plant_2",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/common/bonsai_6_2"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> POTTED_PLANT_3 = BLOCKS.register("potted_plant_3",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/common/bonsai_6_3"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> POTTED_PLANT_4 = BLOCKS.register("potted_plant_4",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/common/bonsai_6_4"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> POTTED_PLANT_5 = BLOCKS.register("potted_plant_5",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/common/bonsai_6_5"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> POTTED_PLANT_6 = BLOCKS.register("potted_plant_6",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/common/bonsai_6_6"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SHRINE = BLOCKS.register("shrine",
                        () -> new com.stardew.craft.block.decor.ShrineDecorBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(1.5F, 6.0F), "stardewcraft:geo/block/decor/shrine_7.geo.json"));

        // ── 稻草人系列（0=基础 9 格半径，1-8=Rarecrow 8 格半径） ──
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCARECROW_0 = BLOCKS.register("scarecrow_0",
                        () -> new com.stardew.craft.block.decor.ScarecrowBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F), "stardewcraft:block/scarecrow/0", 0, 9));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCARECROW_1 = BLOCKS.register("scarecrow_1",
                        () -> new com.stardew.craft.block.decor.ScarecrowBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F), "stardewcraft:block/scarecrow/1", 1, 8));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCARECROW_2 = BLOCKS.register("scarecrow_2",
                        () -> new com.stardew.craft.block.decor.ScarecrowBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F), "stardewcraft:block/scarecrow/2", 2, 8));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCARECROW_3 = BLOCKS.register("scarecrow_3",
                        () -> new com.stardew.craft.block.decor.ScarecrowBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F), "stardewcraft:block/scarecrow/3", 3, 8));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCARECROW_4 = BLOCKS.register("scarecrow_4",
                        () -> new com.stardew.craft.block.decor.ScarecrowBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F), "stardewcraft:block/scarecrow/4", 4, 8));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCARECROW_5 = BLOCKS.register("scarecrow_5",
                        () -> new com.stardew.craft.block.decor.ScarecrowBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F), "stardewcraft:block/scarecrow/5", 5, 8));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCARECROW_6 = BLOCKS.register("scarecrow_6",
                        () -> new com.stardew.craft.block.decor.ScarecrowBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F), "stardewcraft:block/scarecrow/6", 6, 8));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCARECROW_7 = BLOCKS.register("scarecrow_7",
                        () -> new com.stardew.craft.block.decor.ScarecrowBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F), "stardewcraft:block/scarecrow/7", 7, 8));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCARECROW_8 = BLOCKS.register("scarecrow_8",
                        () -> new com.stardew.craft.block.decor.ScarecrowBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F), "stardewcraft:block/scarecrow/8", 8, 8));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCARECROW_9 = BLOCKS.register("scarecrow_9",
                        () -> new com.stardew.craft.block.decor.ScarecrowBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F), "stardewcraft:block/scarecrow/9", 9, 17));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> JUNIMO_HUT_DECOR = BLOCKS.register("junimo_hut_decor",
                        () -> new com.stardew.craft.block.decor.JunimoHutDecorBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.5F, 6.0F), "stardewcraft:geo/block/decor/junimo_hut_decor.geo.json"));

        // ── Giant Crops (3×3×2 GeckoLib block, spawned via GiantCropSpawner only) ──
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> GIANT_CAULIFLOWER = BLOCKS.register("giant_cauliflower",
                        () -> new com.stardew.craft.block.crop.giant.GiantCauliflowerBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)
                                        .noLootTable()));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> GIANT_MELON = BLOCKS.register("giant_melon",
                        () -> new com.stardew.craft.block.crop.giant.GiantMelonBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)
                                        .noLootTable()));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> GIANT_PUMPKIN = BLOCKS.register("giant_pumpkin",
                        () -> new com.stardew.craft.block.crop.giant.GiantPumpkinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)
                                        .noLootTable()));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> GIANT_POWDERMELON = BLOCKS.register("giant_powdermelon",
                        () -> new com.stardew.craft.block.crop.giant.GiantPowdermelonBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 3.0F)
                                        .noLootTable()));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> PILLAR = BLOCKS.register("pillar",
                        () -> new com.stardew.craft.block.decor.PillarGeoDecorBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(1.5F, 6.0F), "stardewcraft:geo/block/decor/pillar_1.geo.json"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> RADIO = BLOCKS.register("radio",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.8F, 1.6F), "stardewcraft:decor/common/radio_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BOOK_STACK_1 = BLOCKS.register("book_stack_1",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.5F, 1.0F), "stardewcraft:decor/common/book_stack_2_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BOOK_STACK_2 = BLOCKS.register("book_stack_2",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.5F, 1.0F), "stardewcraft:decor/common/book_stack_2_2"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BOOK_STACK_3 = BLOCKS.register("book_stack_3",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.5F, 1.0F), "stardewcraft:decor/common/book_stack_2_3"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BOOKSHELF_WALL = BLOCKS.register("bookshelf_wall",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.6F), "stardewcraft:decor/wall_decor/common/bookshelf_wall_3_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BOOKSHELF_TALL_1 = BLOCKS.register("bookshelf_tall_1",
                        () -> new com.stardew.craft.block.decor.BookshelfGeoDecorBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.0F, 2.0F), "stardewcraft:geo/block/decor/bookshelf_3_2.geo.json"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BOOKSHELF_TALL_2 = BLOCKS.register("bookshelf_tall_2",
                        () -> new com.stardew.craft.block.decor.BookshelfGeoDecorBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.0F, 2.0F), "stardewcraft:geo/block/decor/bookshelf_3_3.geo.json"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALLPAPER_BLOCK = BLOCKS.register("wallpaper_block",
                        () -> new com.stardew.craft.block.utility.WallpaperBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOL)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                        .strength(0.8F, 1.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FLOORING_BLOCK = BLOCKS.register("flooring_block",
                        () -> new com.stardew.craft.block.utility.FlooringBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .strength(1.0F, 1.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TV_1 = BLOCKS.register("tv_1",
                        () -> new com.stardew.craft.block.tv.TVBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.6F), "stardewcraft:decor/common/tv_1"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TV_2 = BLOCKS.register("tv_2",
                        () -> new com.stardew.craft.block.tv.TVBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.8F, 1.6F), "stardewcraft:decor/common/tv_2"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_PHOTO_FRAME = BLOCKS.register("wall_photo_frame",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_BONE_DECOR = BLOCKS.register("wall_bone_decor",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> KITCHEN_COUNTER = BLOCKS.register("kitchen_counter",
                        () -> new com.stardew.craft.block.utility.KitchenCounterBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TABLEWARE_PINK = BLOCKS.register("tableware_pink",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_PINK)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/common/tableware_pink"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TABLEWARE_BLUE = BLOCKS.register("tableware_blue",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_LIGHT_BLUE)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/common/tableware_blue"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_KITCHEN_CABINET = BLOCKS.register("wall_kitchen_cabinet",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F)));

        // ──── Batch 3: 10 new furniture items ────
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> JOJA_VENDING_MACHINE = BLOCKS.register("joja_vending_machine",
                        () -> new com.stardew.craft.block.decor.JojaVendingMachineBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_BLUE)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.0F, 2.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WHITE_TEACUP = BLOCKS.register("white_teacup",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/common/white_teacup"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> POOL_TABLE = BLOCKS.register("pool_table",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_GREEN)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.0F, 2.0F), "stardewcraft:decor/common/pool_table"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> GLOBE = BLOCKS.register("globe",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_BROWN)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.5F, 0.5F), "stardewcraft:decor/common/globe"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TELESCOPE = BLOCKS.register("telescope",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.5F, 0.5F), "stardewcraft:decor/common/telescope"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BEAR_FIGURINE = BLOCKS.register("bear_figurine",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_BROWN)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/common/bear_figurine"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FISH_SHOP_COUNTER = BLOCKS.register("fish_shop_counter",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.0F, 1.0F), "stardewcraft:decor/common/fish_shop_counter"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> HOSPITAL_COUNTER = BLOCKS.register("hospital_counter",
                        () -> new com.stardew.craft.block.utility.HospitalCounterBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> HOSPITAL_POSTER_1 = BLOCKS.register("hospital_poster_1",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> HOSPITAL_POSTER_2 = BLOCKS.register("hospital_poster_2",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> HOSPITAL_POSTER_3 = BLOCKS.register("hospital_poster_3",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> HOSPITAL_POSTER_4 = BLOCKS.register("hospital_poster_4",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> HOSPITAL_POSTER_5 = BLOCKS.register("hospital_poster_5",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> ELECTRIC_PIANO = BLOCKS.register("electric_piano",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_BLACK)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/common/electric_piano"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WIZARD_CAULDRON = BLOCKS.register("wizard_cauldron",
                        () -> new com.stardew.craft.block.decor.WizardCauldronBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_PURPLE)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.0F, 2.0F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> GUITAR = BLOCKS.register("guitar",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.5F, 0.8F), "stardewcraft:decor/common/guitar"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> MICROWAVE = BLOCKS.register("microwave",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/common/microwave"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> GRANDFATHER_CLOCK = BLOCKS.register("grandfather_clock",
                        () -> new com.stardew.craft.block.decor.GrandfatherClockBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/common/grandfather_clock_display"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DRUM_SET = BLOCKS.register("drum_set",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/common/drum_set",
                                        -10, 0, -12, 30, 24, 16));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WINE_CABINET_1 = BLOCKS.register("wine_cabinet_1",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/common/wine_cabinet_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WINE_CABINET_2 = BLOCKS.register("wine_cabinet_2",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/common/wine_cabinet_2"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WINE_CABINET_3 = BLOCKS.register("wine_cabinet_3",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.0F), "stardewcraft:decor/common/wine_cabinet_3"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> ALEX_POSTER_1 = BLOCKS.register("alex_poster_1",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> ALEX_POSTER_2 = BLOCKS.register("alex_poster_2",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> ALEX_POSTER_3 = BLOCKS.register("alex_poster_3",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LEAH_POSTER_1 = BLOCKS.register("leah_poster_1",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LEAH_POSTER_2 = BLOCKS.register("leah_poster_2",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LEAH_POSTER_3 = BLOCKS.register("leah_poster_3",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> PERIODIC_TABLE = BLOCKS.register("periodic_table",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/wall_decor/common/periodic_table"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> MICROSCOPE = BLOCKS.register("microscope",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.5F, 0.8F), "stardewcraft:decor/common/microscope"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BEAKER = BLOCKS.register("beaker",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.NONE)
                                        .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                        .noOcclusion()
                                        .strength(0.3F, 0.3F), "stardewcraft:decor/common/beaker"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TRAIN_PHOTO = BLOCKS.register("train_photo",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/wall_decor/common/train_photo"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_PHOTO_1 = BLOCKS.register("wall_photo_1",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> PAPER_CHECKLIST = BLOCKS.register("paper_checklist",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SINE_WAVE_POSTER = BLOCKS.register("sine_wave_poster",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SCATTERED_PAPERS = BLOCKS.register("scattered_papers",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                        .noOcclusion()
                                        .strength(0.2F, 0.2F), "stardewcraft:decor/common/scattered_papers"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SEBASTIAN_POSTER_1 = BLOCKS.register("sebastian_poster_1",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SEBASTIAN_POSTER_2 = BLOCKS.register("sebastian_poster_2",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SEBASTIAN_POSTER_3 = BLOCKS.register("sebastian_poster_3",
                        () -> new com.stardew.craft.block.decor.MapDecorWallThinBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SNOW)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F)));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BOARD_GAME = BLOCKS.register("board_game",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/common/board_game"));

        // ── 新家具批次 ──────────────────────────────────────────────────────────

        // 墙饰
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_ADVENTURER_MAP = BLOCKS.register("wall_adventurer_map",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/wall_decor/common/wall_adventurer_map"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_BUOY = BLOCKS.register("wall_buoy",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_RED)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/wall_decor/common/wall_buoy"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_FISH_SIGN = BLOCKS.register("wall_fish_sign",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/wall_decor/common/wall_fish_sign"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WALL_ISLAND_MAP = BLOCKS.register("wall_island_map",
                        () -> new com.stardew.craft.block.decor.MapDecorWallStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/wall_decor/common/wall_island_map"));

        // 地面摆件
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BEAR_SKIN_RUG = BLOCKS.register("bear_skin_rug",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.DIRT)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                        .noOcclusion()
                                        .strength(0.4F, 0.6F), "stardewcraft:decor/common/bear_skin_rug"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LEANING_SWORD = BLOCKS.register("leaning_sword",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(0.6F, 2.0F), "stardewcraft:decor/common/leaning_sword"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LEAH_SCULPTURE = BLOCKS.register("leah_sculpture",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(1.0F, 3.0F), "stardewcraft:decor/common/leah_sculpture"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> EASEL = BLOCKS.register("easel",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.5F, 1.0F), "stardewcraft:decor/common/easel"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BLUE_BEAR_PLUSHIE = BLOCKS.register("blue_bear_plushie",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_BLUE)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                        .noOcclusion()
                                        .strength(0.3F, 0.5F), "stardewcraft:decor/common/blue_bear_plushie"));

        // ── 图腾柱 ────────────────────────────────────────────────────────────
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TOTEM_POLE_FARM = BLOCKS.register("totem_pole_farm",
                        () -> new com.stardew.craft.block.utility.totem.TotemPoleBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 6.0F),
                                        com.stardew.craft.block.utility.totem.TotemType.FARM,
                                        "stardewcraft:block/utility/totem_pole_farm"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TOTEM_POLE_MOUNTAIN = BLOCKS.register("totem_pole_mountain",
                        () -> new com.stardew.craft.block.utility.totem.TotemPoleBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 6.0F),
                                        com.stardew.craft.block.utility.totem.TotemType.MOUNTAIN,
                                        "stardewcraft:block/utility/totem_pole_mountain"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TOTEM_POLE_BEACH = BLOCKS.register("totem_pole_beach",
                        () -> new com.stardew.craft.block.utility.totem.TotemPoleBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 6.0F),
                                        com.stardew.craft.block.utility.totem.TotemType.BEACH,
                                        "stardewcraft:block/utility/totem_pole_beach"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> TOTEM_POLE_DESERT = BLOCKS.register("totem_pole_desert",
                        () -> new com.stardew.craft.block.utility.totem.TotemPoleBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(2.0F, 6.0F),
                                        com.stardew.craft.block.utility.totem.TotemType.DESERT,
                                        // Reuse farm model (blockstate also points at totem_pole_farm*)
                                        // — desert-specific art not yet produced. Keep modelId in
                                        // sync with the actual JSON to get a valid collision shape.
                                        "stardewcraft:block/utility/totem_pole_farm"));

        // 黄土
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> YELLOW_DIRT = BLOCKS.register("yellow_dirt",
                        () -> new com.stardew.craft.block.nature.YellowDirtBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SAND)
                                        .sound(net.minecraft.world.level.block.SoundType.GRAVEL)
                                        .strength(0.5F)));

        // 远古斑点黄土（Artifact Spot）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> ARTIFACT_SPOT_DIRT = BLOCKS.register("artifact_spot_dirt",
                        () -> new com.stardew.craft.block.nature.ArtifactSpotBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.SAND)
                                        .sound(net.minecraft.world.level.block.SoundType.GRAVEL)
                                        .strength(0.5F)));

        // 沙漠远古斑点（砂岩变体）— 锄头锄后变回普通砂岩
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> DESERT_ARTIFACT_SPOT = BLOCKS.register("desert_artifact_spot",
                        () -> new com.stardew.craft.block.nature.ArtifactSpotBlock(
                                        Block.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE),
                                        net.minecraft.world.level.block.Blocks.SANDSTONE));

        // 传送触发方块（隐形，无碰撞，不可破坏）— 替代 Interaction 实体
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> PORTAL_TRIGGER = BLOCKS.register("portal_trigger",
                        () -> new com.stardew.craft.block.portal.PortalTriggerBlock(Block.Properties.of()
                                        .noCollission()
                                        .noOcclusion()
                                        .noLootTable()
                                        .strength(-1.0f, 3600000.0f)
                                        .replaceable()
                                        .sound(net.minecraft.world.level.block.SoundType.EMPTY)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK)));

        // 临时模型室内装饰（自动 extension + 自动碰撞）
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> SAFE_BOX = BLOCKS.register("safe_box",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.2F, 4.0F), "stardewcraft:decor/tmp_models/1_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BROKEN_SAFE_BOX = BLOCKS.register("broken_safe_box",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.0F, 3.0F), "stardewcraft:decor/tmp_models/1_2"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LOOM_MACHINE = BLOCKS.register("loom_machine",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.2F), "stardewcraft:decor/tmp_models/2"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BOILER_DECOR = BLOCKS.register("boiler_decor",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_GRAY)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.2F, 3.0F), "stardewcraft:decor/tmp_models/3_1"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BROKEN_BOILER = BLOCKS.register("broken_boiler",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_GRAY)
                                        .sound(net.minecraft.world.level.block.SoundType.METAL)
                                        .noOcclusion()
                                        .strength(1.0F, 2.0F), "stardewcraft:decor/tmp_models/3_2"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> YARN_CABINET = BLOCKS.register("yarn_cabinet",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.8F, 1.2F), "stardewcraft:decor/tmp_models/4"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BROKEN_CHAIR = BLOCKS.register("broken_chair",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.4F, 0.8F), "stardewcraft:decor/tmp_models/5"));
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> COAL_BASKET = BLOCKS.register("coal_basket",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.6F, 1.0F), "stardewcraft:decor/tmp_models/6"));

        // 家具目录 (SDV Furniture Catalogue)
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> FURNITURE_CATALOGUE = BLOCKS.register("furniture_catalogue",
                        () -> new com.stardew.craft.block.decor.FurnitureCatalogueBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(1.0F, 2.0F)));

        // 公告栏 (SDV Bulletin Board)
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> BULLETIN_BOARD = BLOCKS.register("bulletin_board",
                        () -> new com.stardew.craft.block.decor.BulletinBoardBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.COLOR_BROWN)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(-1.0F, 3600000.0F)
                                        .noLootTable(),
                                        "stardewcraft:block/decor/bulletin_board"));

        // 社区中心献祭卷轴 (SDV Junimo Note)
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> JUNIMO_NOTE = BLOCKS.register("junimo_note",
                        () -> new com.stardew.craft.communitycenter.block.JunimoNoteBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.GOLD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(-1.0F, 3600000.0F)
                                        .noLootTable()));

        // 社区中心星盘 (Star Plaque)
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> STAR_PLAQUE = BLOCKS.register("star_plaque",
                        () -> new com.stardew.craft.communitycenter.block.StarPlaqueBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(-1.0F, 3600000.0F)
                                        .noLootTable()));

        // ── 加工台 (Workbenches) ──────────────────────────────────────────
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> WOOD_WORKBENCH = BLOCKS.register("wood_workbench",
                        () -> new com.stardew.craft.block.utility.WoodWorkbenchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .strength(2.5F, 3.0F)));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> STONE_WORKBENCH = BLOCKS.register("stone_workbench",
                        () -> new com.stardew.craft.block.utility.StoneWorkbenchBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .strength(3.5F, 3.0F)));

        // ── 祝尼魔温室符文 (Junimo Greenhouse Rune) ──────────────────────────
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> JUNIMO_GREENHOUSE_RUNE = BLOCKS.register("junimo_greenhouse_rune",
                        () -> new com.stardew.craft.block.utility.JunimoGreenhouseRuneBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.EMERALD)
                                        .sound(net.minecraft.world.level.block.SoundType.AMETHYST)
                                        .strength(-1.0F, 3600000.0F)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK)
                                        .lightLevel(state -> 7)));

        // ── 装饰：农场常用 (Farm Common Decor) ──────────────────────────
        @SuppressWarnings("null")
        public static final DeferredBlock<Block> STANDING_HOE = BLOCKS.register("standing_hoe",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                                        .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                        .noOcclusion()
                                        .strength(0.5F, 1.0F), "stardewcraft:decor/common/standing_hoe"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> EMPTY_TERRACOTTA_POT = BLOCKS.register("empty_terracotta_pot",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_BROWN)
                                        .sound(net.minecraft.world.level.block.SoundType.DECORATED_POT)
                                        .noOcclusion()
                                        .strength(0.5F, 1.0F), "stardewcraft:decor/common/empty_terracotta_pot"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> RESERVOIR = BLOCKS.register("reservoir",
                        () -> new com.stardew.craft.block.decor.ReservoirBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                                        .sound(net.minecraft.world.level.block.SoundType.STONE)
                                        .noOcclusion()
                                        .strength(1.5F, 3.0F), "stardewcraft:decor/common/reservoir"));

        @SuppressWarnings("null")
        public static final DeferredBlock<Block> LONG_POTTED_PLANT = BLOCKS.register("long_potted_plant",
                        () -> new com.stardew.craft.block.decor.MapDecorStaticBlock(Block.Properties.of()
                                        .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                                        .sound(net.minecraft.world.level.block.SoundType.AZALEA)
                                        .noOcclusion()
                                        .strength(0.5F, 1.0F), "stardewcraft:decor/common/long_potted_plant"));

        }
