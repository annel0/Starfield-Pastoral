package com.stardew.craft;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.capability.UtilityAutomationCapabilities;
import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.effect.ModMobEffects;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.SpecificBaitItem;
import com.stardew.craft.item.StardewQualityItem;
import com.stardew.craft.sound.ModSounds;
import com.stardew.craft.item.artisan.ArtisanDrinkItem;
import com.stardew.craft.item.artisan.DehydratorIngredientHelper;
import com.stardew.craft.item.artisan.SmokedFishItem;
import com.stardew.craft.client.weapon.WeaponShaderRegistry;
import com.stardew.craft.blockentity.ModBlockEntities;
import com.stardew.craft.network.PacketHandler;
import com.stardew.craft.event.WildTreeChopEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(StardewCraft.MODID)
public class StardewCraft {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "stardewcraft";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "stardewcraft" namespace
    @SuppressWarnings("null")
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Stardew Valley专用创造标签
    @SuppressWarnings("null")
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STARDEW_TAB = CREATIVE_MODE_TABS.register("stardew_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.stardewcraft.stardew"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.PARSNIP_SEEDS.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                // 杂草统一物品：放置时自动按季节选择外观，同季节内随机变体。
                output.accept(ModItems.WILD_WEEDS.get());
                output.accept(ModItems.PASTURE_GRASS.get());
                output.accept(ModItems.BLUE_PASTURE_GRASS.get());
                output.accept(ModItems.YELLOW_DIRT.get());

                // 矿井
                output.accept(ModItems.MINE_BARRIER.get());

                output.accept(ModItems.EARTH_SHALE.get());
                output.accept(ModItems.FROST_GNEISS.get());
                output.accept(ModItems.LAVA_BASALT.get());

                output.accept(ModItems.DARK_EARTH_SHALE.get());
                output.accept(ModItems.DARK_FROST_GNEISS.get());
                output.accept(ModItems.DARK_LAVA_BASALT.get());

                // 主石头变体
                output.accept(ModItems.EARTH_SHALE_SLAB.get());
                output.accept(ModItems.EARTH_SHALE_STAIRS.get());
                output.accept(ModItems.EARTH_SHALE_WALL.get());
                output.accept(ModItems.FROST_GNEISS_SLAB.get());
                output.accept(ModItems.FROST_GNEISS_STAIRS.get());
                output.accept(ModItems.FROST_GNEISS_WALL.get());
                output.accept(ModItems.LAVA_BASALT_SLAB.get());
                output.accept(ModItems.LAVA_BASALT_STAIRS.get());
                output.accept(ModItems.LAVA_BASALT_WALL.get());
                output.accept(ModItems.DARK_EARTH_SHALE_SLAB.get());
                output.accept(ModItems.DARK_EARTH_SHALE_STAIRS.get());
                output.accept(ModItems.DARK_EARTH_SHALE_WALL.get());
                output.accept(ModItems.DARK_FROST_GNEISS_SLAB.get());
                output.accept(ModItems.DARK_FROST_GNEISS_STAIRS.get());
                output.accept(ModItems.DARK_FROST_GNEISS_WALL.get());
                output.accept(ModItems.DARK_LAVA_BASALT_SLAB.get());
                output.accept(ModItems.DARK_LAVA_BASALT_STAIRS.get());
                output.accept(ModItems.DARK_LAVA_BASALT_WALL.get());

                output.accept(ModItems.BANDED_MARBLE.get());
                output.accept(ModItems.LIMESTONE.get());
                output.accept(ModItems.MOSSY_SANDSTONE.get());
                output.accept(ModItems.CRACKED_SLATE.get());
                output.accept(ModItems.SCORIA.get());
                output.accept(ModItems.SALT_ROCK.get());

                // 装饰石材变体
                output.accept(ModItems.BANDED_MARBLE_SLAB.get());
                output.accept(ModItems.BANDED_MARBLE_STAIRS.get());
                output.accept(ModItems.BANDED_MARBLE_WALL.get());
                output.accept(ModItems.LIMESTONE_SLAB.get());
                output.accept(ModItems.LIMESTONE_STAIRS.get());
                output.accept(ModItems.LIMESTONE_WALL.get());
                output.accept(ModItems.MOSSY_SANDSTONE_SLAB.get());
                output.accept(ModItems.MOSSY_SANDSTONE_STAIRS.get());
                output.accept(ModItems.MOSSY_SANDSTONE_WALL.get());
                output.accept(ModItems.CRACKED_SLATE_SLAB.get());
                output.accept(ModItems.CRACKED_SLATE_STAIRS.get());
                output.accept(ModItems.CRACKED_SLATE_WALL.get());
                output.accept(ModItems.SCORIA_SLAB.get());
                output.accept(ModItems.SCORIA_STAIRS.get());
                output.accept(ModItems.SCORIA_WALL.get());
                output.accept(ModItems.SALT_ROCK_SLAB.get());
                output.accept(ModItems.SALT_ROCK_STAIRS.get());
                output.accept(ModItems.SALT_ROCK_WALL.get());

                output.accept(ModItems.ELEVATOR.get());
                output.accept(ModItems.MINE_LADDER.get());

                output.accept(ModItems.EARTH_COPPER_ORE.get());
                output.accept(ModItems.FROST_COPPER_ORE.get());
                output.accept(ModItems.LAVA_COPPER_ORE.get());

                output.accept(ModItems.EARTH_IRON_ORE.get());
                output.accept(ModItems.FROST_IRON_ORE.get());
                output.accept(ModItems.LAVA_IRON_ORE.get());

                output.accept(ModItems.EARTH_GOLD_ORE.get());
                output.accept(ModItems.FROST_GOLD_ORE.get());
                output.accept(ModItems.LAVA_GOLD_ORE.get());

                output.accept(ModItems.EARTH_IRIDIUM_ORE.get());
                output.accept(ModItems.FROST_IRIDIUM_ORE.get());
                output.accept(ModItems.LAVA_IRIDIUM_ORE.get());

                output.accept(ModItems.EARTH_COAL_ORE.get());
                output.accept(ModItems.FROST_COAL_ORE.get());
                output.accept(ModItems.LAVA_COAL_ORE.get());

                // 直接采集矿物（放置为方块）
                output.accept(ModItems.QUARTZ.get());
                output.accept(ModItems.EARTH_CRYSTAL.get());
                output.accept(ModItems.FROZEN_TEAR.get());
                output.accept(ModItems.FIRE_QUARTZ.get());

                // 宝石矿石（挖掉掉宝石）
                output.accept(ModItems.AMETHYST_ORE.get());
                output.accept(ModItems.AQUAMARINE_ORE.get());
                output.accept(ModItems.DIAMOND_ORE.get());
                output.accept(ModItems.EMERALD_ORE.get());
                output.accept(ModItems.JADE_ORE.get());
                output.accept(ModItems.RUBY_ORE.get());
                output.accept(ModItems.TOPAZ_ORE.get());

                // 野生树（橡树原型）建筑组件
                output.accept(ModBlocks.WILD_OAK_TRUNK0.get());
                output.accept(ModBlocks.WILD_OAK_TRUNK1.get());
                output.accept(ModBlocks.WILD_OAK_BRANCH1.get());
                output.accept(ModBlocks.WILD_OAK_BRANCH2.get());
                output.accept(ModBlocks.WILD_OAK_LEAVES.get());

                // 野生树（枫树）建筑组件
                output.accept(ModBlocks.WILD_MAPLE_TRUNK0.get());
                output.accept(ModBlocks.WILD_MAPLE_TRUNK1.get());
                output.accept(ModBlocks.WILD_MAPLE_BRANCH1.get());
                output.accept(ModBlocks.WILD_MAPLE_BRANCH2.get());
                output.accept(ModBlocks.WILD_MAPLE_LEAVES.get());

                // 野生树（松树）建筑组件
                output.accept(ModBlocks.WILD_PINE_TRUNK0.get());
                output.accept(ModBlocks.WILD_PINE_TRUNK1.get());
                output.accept(ModBlocks.WILD_PINE_BRANCH1.get());
                output.accept(ModBlocks.WILD_PINE_BRANCH2.get());
                output.accept(ModBlocks.WILD_PINE_LEAVES.get());

                // 野生树（桃花心木）建筑组件
                output.accept(ModBlocks.WILD_MAHOGANY_TRUNK0.get());
                output.accept(ModBlocks.WILD_MAHOGANY_TRUNK1.get());
                output.accept(ModBlocks.WILD_MAHOGANY_BRANCH1.get());
                output.accept(ModBlocks.WILD_MAHOGANY_BRANCH2.get());
                output.accept(ModBlocks.WILD_MAHOGANY_LEAVES.get());

                // 野生树（神秘树）建筑组件
                output.accept(ModBlocks.WILD_MYSTIC_TREE_TRUNK0.get());
                output.accept(ModBlocks.WILD_MYSTIC_TREE_TRUNK1.get());
                output.accept(ModBlocks.WILD_MYSTIC_TREE_BRANCH1.get());
                output.accept(ModBlocks.WILD_MYSTIC_TREE_BRANCH2.get());
                output.accept(ModBlocks.WILD_MYSTIC_TREE_LEAVES.get());

				// 树种子
				output.accept(ModItems.ACORN.get());
				output.accept(ModItems.MAPLE_SEED.get());
				output.accept(ModItems.PINE_CONE.get());
				output.accept(ModItems.MAHOGANY_SEED.get());
				output.accept(ModItems.MYSTIC_TREE_SEED.get());

                // 水壶
                output.accept(ModItems.WATERING_CAN.get());
                output.accept(ModItems.COPPER_WATERING_CAN.get());
                output.accept(ModItems.STEEL_WATERING_CAN.get());
                output.accept(ModItems.GOLD_WATERING_CAN.get());
                output.accept(ModItems.IRIDIUM_WATERING_CAN.get());

                // 洒水器
                output.accept(ModItems.SPRINKLER.get());
                output.accept(ModItems.QUALITY_SPRINKLER.get());
                output.accept(ModItems.IRIDIUM_SPRINKLER.get());

                // 锄头
                output.accept(ModItems.HOE.get());
                output.accept(ModItems.COPPER_HOE.get());
                output.accept(ModItems.STEEL_HOE.get());
                output.accept(ModItems.GOLD_HOE.get());
                output.accept(ModItems.IRIDIUM_HOE.get());

                // 斧头
                output.accept(ModItems.AXE.get());
                output.accept(ModItems.COPPER_AXE.get());
                output.accept(ModItems.STEEL_AXE.get());
                output.accept(ModItems.GOLD_AXE.get());
                output.accept(ModItems.IRIDIUM_AXE.get());

                // 镐子
                output.accept(ModItems.PICKAXE.get());
                output.accept(ModItems.COPPER_PICKAXE.get());
                output.accept(ModItems.STEEL_PICKAXE.get());
                output.accept(ModItems.GOLD_PICKAXE.get());
                output.accept(ModItems.IRIDIUM_PICKAXE.get());

				// 镰刀
				output.accept(ModItems.SCYTHE.get());
				output.accept(ModItems.GOLDEN_SCYTHE.get());
				output.accept(ModItems.IRIDIUM_SCYTHE.get());
                output.accept(ModItems.MILK_PAIL.get());
                output.accept(ModItems.SHEARS.get());

                // 钓鱼竿
                output.accept(ModItems.FISHING_ROD.get());
                output.accept(ModItems.TRAINING_ROD.get());
                output.accept(ModItems.FIBERGLASS_ROD.get());
                output.accept(ModItems.IRIDIUM_ROD.get());
                output.accept(ModItems.ADVANCED_IRIDIUM_ROD.get());

                // 法师塔指南针
                output.accept(ModItems.WIZARD_TOWER_COMPASS.get());

                // 武器 - 剑类
                output.accept(ModItems.RUSTY_SWORD.get());
                output.accept(ModItems.STEEL_SMALLSWORD.get());
                output.accept(ModItems.CARVING_KNIFE.get());
                output.accept(ModItems.IRON_DIRK.get());
                output.accept(ModItems.WIND_SPIRE.get());
                output.accept(ModItems.ELF_BLADE.get());
                output.accept(ModItems.BURGLARS_SHANK.get());
                output.accept(ModItems.CRYSTAL_DAGGER.get());
                output.accept(ModItems.SHADOW_DAGGER.get());
                output.accept(ModItems.WICKED_KRIS.get());
                output.accept(ModItems.GALAXY_DAGGER.get());
                output.accept(ModItems.DWARF_DAGGER.get());
                output.accept(ModItems.IRIDIUM_NEEDLE.get());
                output.accept(ModItems.INFINITY_DAGGER.get());
                output.accept(ModItems.BROKEN_TRIDENT.get());
                output.accept(ModItems.FEMUR.get());
                output.accept(ModItems.WOODEN_BLADE.get());
                output.accept(ModItems.PIRATE_SWORD.get());
                output.accept(ModItems.SILVER_SABER.get());
                output.accept(ModItems.CUTLASS.get());
                output.accept(ModItems.FOREST_SWORD.get());
                output.accept(ModItems.IRON_EDGE.get());
                output.accept(ModItems.MEOWMERE.get());
                output.accept(ModItems.BONE_SWORD.get());
                output.accept(ModItems.CLAYMORE.get());
                output.accept(ModItems.NEPTUNES_GLAIVE.get());
                output.accept(ModItems.TEMPLARS_BLADE.get());
                output.accept(ModItems.INSECT_HEAD.get());
                output.accept(ModItems.OBSIDIAN_EDGE.get());
                output.accept(ModItems.OSSIFIED_BLADE.get());
                output.accept(ModItems.HOLY_BLADE.get());
                output.accept(ModItems.TEMPERED_BROADSWORD.get());
                output.accept(ModItems.YETI_TOOTH.get());
                output.accept(ModItems.STEEL_FALCHION.get());
                output.accept(ModItems.DARK_SWORD.get());
                output.accept(ModItems.LAVA_KATANA.get());
                output.accept(ModItems.DRAGONTOOTH_CUTLASS.get());
                output.accept(ModItems.DWARF_SWORD.get());
                output.accept(ModItems.GALAXY_SWORD.get());
                output.accept(ModItems.INFINITY_BLADE.get());

                // ── 家具/装饰 ──────────────────────────────────────────────────
                // 床 / 沙发 / 坐具
                output.accept(ModItems.BED_1.get());
                output.accept(ModItems.BED_2.get());
                output.accept(ModItems.SOFA.get());
                output.accept(ModItems.CHAIR_1.get());
                output.accept(ModItems.CHAIR_2.get());
                output.accept(ModItems.CHAIR_3.get());
                output.accept(ModItems.DINING_CHAIR_WOOD.get());
                output.accept(ModItems.DINING_CHAIR_IRON.get());
                output.accept(ModItems.CUSHION.get());
                output.accept(ModItems.STOOL.get());
                output.accept(ModItems.OFFICE_STOOL.get());
                output.accept(ModItems.OFFICE_CHAIR_2.get());
                // 桌子 / 台面
                output.accept(ModItems.OAK_TABLE.get());
                output.accept(ModItems.SPRUCE_TABLE.get());
                output.accept(ModItems.BIRCH_TABLE.get());
                output.accept(ModItems.OAK_ROUND_TABLE.get());
                output.accept(ModItems.SPRUCE_COUNTER.get());
                output.accept(ModItems.KITCHEN_COUNTER.get());
                output.accept(ModItems.TABLEWARE_PINK.get());
                output.accept(ModItems.TABLEWARE_BLUE.get());
                output.accept(ModItems.PINK_TABLECLOTH.get());
                output.accept(ModItems.SKY_BLUE_TABLECLOTH.get());
                // 灯具
                output.accept(ModItems.LIGHT_1.get());
                output.accept(ModItems.LIGHT_2.get());
                output.accept(ModItems.LIGHT_3.get());
                output.accept(ModItems.LIGHT_4.get());
                output.accept(ModItems.LIGHT_5.get());
                output.accept(ModItems.LIGHT_6.get());
                output.accept(ModItems.LIGHT_7.get());
                output.accept(ModItems.FLOOR_LAMP.get());
                output.accept(ModItems.TABLE_LAMP.get());
                output.accept(ModItems.TABLE_LANTERN.get());
                // 收纳 / 储物
                output.accept(ModItems.DRESSER_1.get());
                output.accept(ModItems.DRESSER_2.get());
                output.accept(ModItems.DRESSER_3.get());
                output.accept(ModItems.BARREL.get());
                output.accept(ModItems.WOOD_BUNDLE.get());
                output.accept(ModItems.WALL_KITCHEN_CABINET.get());
                output.accept(ModItems.SINK_4.get());
                output.accept(ModItems.WINE_CABINET_1.get());
                output.accept(ModItems.WINE_CABINET_2.get());
                output.accept(ModItems.WINE_CABINET_3.get());
                // 书架 / 书堆
                output.accept(ModItems.BOOKSHELF_TALL_1.get());
                output.accept(ModItems.BOOKSHELF_TALL_2.get());
                output.accept(ModItems.BOOKSHELF_WALL.get());
                output.accept(ModItems.BOOK_STACK_1.get());
                output.accept(ModItems.BOOK_STACK_2.get());
                output.accept(ModItems.BOOK_STACK_3.get());
                // 电器 / 娱乐
                output.accept(ModItems.TV_1.get());
                output.accept(ModItems.TV_2.get());
                output.accept(ModItems.COMPUTER.get());
                output.accept(ModItems.RADIO.get());
                output.accept(ModItems.ARCADE_MACHINE.get());
                output.accept(ModItems.JUKEBOX.get());
                output.accept(ModItems.MICROWAVE.get());
                output.accept(ModItems.GRANDFATHER_CLOCK.get());
                output.accept(ModItems.ELECTRIC_PIANO.get());
                output.accept(ModItems.GUITAR.get());
                output.accept(ModItems.DRUM_SET.get());
                output.accept(ModItems.WIZARD_CAULDRON.get());
                // 装饰物
                output.accept(ModItems.FIREPLACE_LARGE.get());
                output.accept(ModItems.COOKING_POT.get());
                output.accept(ModItems.PILLAR.get());
                output.accept(ModItems.SHRINE.get());
                output.accept(ModItems.SAILBOAT.get());
                output.accept(ModItems.PHOTO_FRAME.get());
                output.accept(ModItems.WHITE_TEACUP.get());
                output.accept(ModItems.POOL_TABLE.get());
                output.accept(ModItems.GLOBE.get());
                output.accept(ModItems.TELESCOPE.get());
                output.accept(ModItems.BEAR_FIGURINE.get());
                output.accept(ModItems.FISH_SHOP_COUNTER.get());
                output.accept(ModItems.HOSPITAL_COUNTER.get());
                output.accept(ModItems.JOJA_VENDING_MACHINE.get());
                output.accept(ModItems.FURNITURE_CATALOGUE.get());
                output.accept(ModItems.BULLETIN_BOARD.get());
                // 植物
                output.accept(ModItems.BONSAI_1.get());
                output.accept(ModItems.BONSAI_2.get());
                output.accept(ModItems.BONSAI_3.get());
                output.accept(ModItems.BONSAI_4.get());
                output.accept(ModItems.BONSAI_5_WALL.get());
                output.accept(ModItems.BONSAI_BUSH.get());
                output.accept(ModItems.POTTED_PLANT_1.get());
                output.accept(ModItems.POTTED_PLANT_2.get());
                output.accept(ModItems.POTTED_PLANT_3.get());
                output.accept(ModItems.POTTED_PLANT_4.get());
                output.accept(ModItems.POTTED_PLANT_5.get());
                output.accept(ModItems.POTTED_PLANT_6.get());
                // 地毯 / 毛皮
                output.accept(ModItems.CARPET_1.get());
                output.accept(ModItems.CARPET_2.get());
                output.accept(ModItems.CARPET_3.get());
                output.accept(ModItems.CARPET_4.get());
                output.accept(ModItems.CARPET_5.get());
                output.accept(ModItems.CARPET_6.get());
                output.accept(ModItems.CARPET_7.get());
                output.accept(ModItems.CARPET_8.get());
                output.accept(ModItems.CARPET_9.get());
                output.accept(ModItems.CARPET_10.get());
                output.accept(ModItems.CARPET_11.get());
                output.accept(ModItems.CARPET_12.get());
                output.accept(ModItems.CARPET_13.get());
                output.accept(ModItems.CARPET_14.get());
                output.accept(ModItems.CARPET_15.get());
                output.accept(ModItems.CARPET_16.get());
                output.accept(ModItems.CARPET_17.get());
                output.accept(ModItems.CARPET_18.get());
                output.accept(ModItems.CARPET_19.get());
                output.accept(ModItems.CARPET_20.get());
                output.accept(ModItems.CARPET_21.get());
                output.accept(ModItems.BEAR_SKIN_RUG.get());
                // 壁挂装饰
                output.accept(ModItems.WALL_PHOTO_FRAME.get());
                output.accept(ModItems.WALL_BONE_DECOR.get());
                output.accept(ModItems.WALL_PHOTO_WHITE_HALL.get());
                output.accept(ModItems.WALL_PHOTO_1.get());
                output.accept(ModItems.TRAIN_PHOTO.get());
                output.accept(ModItems.WALL_BLACKSMITH_SIGN.get());
                output.accept(ModItems.WALL_BLACKSMITH_HAMMERS.get());
                output.accept(ModItems.WALL_HANGING_SMALL_A.get());
                output.accept(ModItems.WALL_HANGING_SMALL_B.get());
                output.accept(ModItems.WALL_HANGING_STRIP.get());
                output.accept(ModItems.WALL_HANGING_TRIPTYCH.get());
                output.accept(ModItems.WALL_HANGING_ORNAMENT.get());
                output.accept(ModItems.WALL_NOTICE_BOARD_SMALL.get());
                output.accept(ModItems.WALL_NOTICE_BOARD_MEDIUM.get());
                output.accept(ModItems.WALL_FRAME_WIDE.get());
                output.accept(ModItems.WALL_FRAME_DOUBLE.get());
                output.accept(ModItems.WALL_BULLETIN_NOTES.get());
                output.accept(ModItems.WALL_STICKY_NOTES.get());
                output.accept(ModItems.PAPER_CHECKLIST.get());
                output.accept(ModItems.SCATTERED_PAPERS.get());
                output.accept(ModItems.WALL_POSTER_GAMEPAD.get());
                output.accept(ModItems.WALL_POSTER_DOLPHIN.get());
                output.accept(ModItems.WALL_POSTER_GAME_CHARACTER.get());
                output.accept(ModItems.SINE_WAVE_POSTER.get());
                output.accept(ModItems.PERIODIC_TABLE.get());
                output.accept(ModItems.HOSPITAL_POSTER_1.get());
                output.accept(ModItems.HOSPITAL_POSTER_2.get());
                output.accept(ModItems.HOSPITAL_POSTER_3.get());
                output.accept(ModItems.HOSPITAL_POSTER_4.get());
                output.accept(ModItems.HOSPITAL_POSTER_5.get());
                output.accept(ModItems.ALEX_POSTER_1.get());
                output.accept(ModItems.ALEX_POSTER_2.get());
                output.accept(ModItems.ALEX_POSTER_3.get());
                output.accept(ModItems.SEBASTIAN_POSTER_1.get());
                output.accept(ModItems.SEBASTIAN_POSTER_2.get());
                output.accept(ModItems.SEBASTIAN_POSTER_3.get());
                output.accept(ModItems.LEAH_POSTER_1.get());
                output.accept(ModItems.LEAH_POSTER_2.get());
                output.accept(ModItems.LEAH_POSTER_3.get());
                output.accept(ModItems.WALL_OUTLET.get());
                output.accept(ModItems.WALL_SWITCH_PANEL.get());
                output.accept(ModItems.WALL_ADVENTURER_MAP.get());
                output.accept(ModItems.WALL_FISH_SIGN.get());
                output.accept(ModItems.WALL_ISLAND_MAP.get());
                output.accept(ModItems.WALL_BUOY.get());
                // 商店装饰
                output.accept(ModItems.SHOP_COUNTER_1.get());
                output.accept(ModItems.SHOP_COUNTER_2.get());
                output.accept(ModItems.SHOP_COUNTER_3.get());
                output.accept(ModItems.SUPERMARKET_SHELF_1.get());
                output.accept(ModItems.SUPERMARKET_SHELF_2.get());
                output.accept(ModItems.SHOP_BASKET.get());
                output.accept(ModItems.SHOP_CRATE_FRUIT_1.get());
                output.accept(ModItems.SHOP_CRATE_FRUIT_2.get());
                output.accept(ModItems.SHOP_CRATE_FRUIT_3.get());
                output.accept(ModItems.SHOP_CRATE_FRUIT_4.get());
                output.accept(ModItems.SHOP_CRATE_FRUIT_5.get());
                output.accept(ModItems.SHOP_CRATE_FRUIT_6.get());
                output.accept(ModItems.SHOP_CRATE_FRUIT_7.get());
                output.accept(ModItems.SHOP_CRATE_FRUIT_8.get());
                output.accept(ModItems.SHOP_CRATE_FRUIT_9.get());
                output.accept(ModItems.SHOP_CRATE_FRUIT_10.get());
                output.accept(ModItems.SHOP_PACK_BOX.get());
                output.accept(ModItems.SHOP_SHIPPING_BIN.get());
                output.accept(ModItems.SHOP_WINDOW_1.get());
                output.accept(ModItems.SHOP_WINDOW_2.get());
                // 艺术品 / 特制
                output.accept(ModItems.LEAH_SCULPTURE.get());
                output.accept(ModItems.EASEL.get());
                output.accept(ModItems.LEANING_SWORD.get());
                output.accept(ModItems.BLUE_BEAR_PLUSHIE.get());
                output.accept(ModItems.MICROSCOPE.get());
                output.accept(ModItems.BEAKER.get());
                output.accept(ModItems.BOARD_GAME.get());

                // 种子/作物：自动把所有已注册作物系统物品加入标签
                java.util.List<Item> fruitItems = new java.util.ArrayList<>();
                java.util.List<Item> forageItems = new java.util.ArrayList<>();
                java.util.List<Item> cookingIngredientItems = new java.util.ArrayList<>();
                for (var holder : ModItems.ITEMS.getEntries()) {
                    Item item = holder.get();
                    if (!(item instanceof IStardewItem stardewItem)) {
                        continue;
                    }

                    if (isHiddenTintedBaseItem(item)) {
                        // Keep creative tab focused on flavored/targeted variants, not empty placeholders.
                        continue;
                    }

                    String typeKey = stardewItem.getItemTypeKey();
                    if ("stardewcraft.type.seed".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.crop_seed".equals(typeKey)) {
                        // 咖啡豆等既是作物又是种子的物品，显示所有品质变体
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.resource".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.mineral".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.artifact".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.artifact_quality".equals(typeKey)) {
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.artisan_goods".equals(typeKey)) {
                        if (item instanceof ArtisanDrinkItem drink && drink.supportsQuality()) {
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                        } else if (item instanceof SmokedFishItem) {
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                        } else {
                            output.accept(item);
                        }
                    } else if ("stardewcraft.type.artisan_animal_quality".equals(typeKey)) {
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.fishing".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.utility".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.furniture".equals(typeKey)) {
                        // 家具已在前面按类别显式排序，跳过自动循环
                    } else if ("stardewcraft.type.cooking".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.fruit".equals(typeKey)) {
                        fruitItems.add(item);
                    } else if ("stardewcraft.type.forage".equals(typeKey)) {
                        forageItems.add(item);
                    } else if ("stardewcraft.type.cooking_ingredient".equals(typeKey)) {
                        cookingIngredientItems.add(item);
                    } else if ("stardewcraft.type.tool".equals(typeKey) || typeKey.startsWith("stardewcraft.tool.")) {
                        output.accept(item);
                    } else if ("stardewcraft.type.craftable".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.fertilizer".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.animal_product".equals(typeKey)) {
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.crop".equals(typeKey)) {
                        Integer colorCount = getFlowerColorVariantCount(item);
                        if (colorCount != null && colorCount > 0) {
                            for (int color = 0; color < colorCount; color++) {
                                output.accept(createFlowerVariantItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL, color));
                                output.accept(createFlowerVariantItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER, color));
                                output.accept(createFlowerVariantItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD, color));
                                output.accept(createFlowerVariantItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM, color));
                            }
                        } else {
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                        }
                    } else if ("stardewcraft.type.fish".equals(typeKey)) {
                        // 鱼类也支持品质变体
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.crabpot".equals(typeKey)) {
                        // 蟹笼物品也支持品质变体
                        // 普通蟹笼: 普通品质
                        // 高级鱼饵: 可获得银星
                        // 采集贝类(蛤蜊、鸟蛤、贻贝、牡蛎): 可获得金星和铱星
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.legendary_fish".equals(typeKey)) {
                        // 传说鱼类也支持品质变体
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.misc".equals(typeKey)) {
                        // 杂项物品（藻类等）不支持品质
                        output.accept(item);
                    } else if ("stardewcraft.type.trash".equals(typeKey)) {
                        // 垃圾物品不支持品质
                        output.accept(item);
                    } else if ("stardewcraft.type.ring".equals(typeKey)) {
                        // 戒指：不支持品质
                        output.accept(item);
                    } else if ("stardewcraft.type.boots".equals(typeKey)) {
                        // 靴子：不支持品质
                        output.accept(item);
                    }
                }

                addGroupedCategoryItems(output, cookingIngredientItems, false);
                addGroupedCategoryItems(output, fruitItems, true);
                addGroupedCategoryItems(output, forageItems, true);

                addPreserveVariants(output);
                addSpecificBaitVariants(output);
            }).build());

    private static boolean isHiddenTintedBaseItem(Item item) {
        return item == ModItems.TARGETED_BAIT.get()
                || item == ModItems.JELLY.get()
                || item == ModItems.PICKLES.get()
                || item == ModItems.ROE.get()
                || item == ModItems.AGED_ROE.get()
                || item == ModItems.CAVIAR.get()
                || item == ModItems.DRIED_FRUIT.get()
                || item == ModItems.DRIED_MUSHROOMS.get();
    }
    
    /**
     * 创建带品质的物品
     */
    @SuppressWarnings("null")
    private static ItemStack createQualityItem(Item item, int quality) {
        @SuppressWarnings("null")
        ItemStack stack = new ItemStack(item);
        com.stardew.craft.item.quality.QualityHelper.setQuality(stack, quality);
        return stack;
    }

    @SuppressWarnings("null")
    private static void addGroupedCategoryItems(CreativeModeTab.Output output, java.util.List<Item> items, boolean forceQualityVariants) {
        items.sort(java.util.Comparator.comparing(i -> BuiltInRegistries.ITEM.getKey(i).getPath()));
        for (Item item : items) {
            boolean supportsQuality = item instanceof StardewQualityItem qualityItem && qualityItem.supportsQuality();
            if (forceQualityVariants || supportsQuality) {
                output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
            } else {
                output.accept(item);
            }
        }
    }

    @SuppressWarnings("null")
    private static ItemStack createFlowerVariantItem(Item item, int quality, int color) {
        @SuppressWarnings("null")
        ItemStack stack = new ItemStack(item);
        com.stardew.craft.item.quality.QualityHelper.setQuality(stack, quality);
        @SuppressWarnings("null")
        var customData = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY);
        var tag = customData.copyTag();
        tag.putInt("FlowerColor", Math.max(0, color));
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        int cmd = 100 + (quality * 10) + Math.max(0, color);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA,
                new net.minecraft.world.item.component.CustomModelData(cmd));
        return stack;
    }

    private static Integer getFlowerColorVariantCount(Item item) {
        if (item == ModItems.BLUE_JAZZ.get()) return 6;
        if (item == ModItems.POPPY.get()) return 3;
        if (item == ModItems.TULIP.get()) return 5;
        if (item == ModItems.SUMMER_SPANGLE.get()) return 6;
        if (item == ModItems.FAIRY_ROSE.get()) return 6;
        return null;
    }

    private static void addPreserveVariants(CreativeModeTab.Output output) {
        for (var holder : ModItems.ITEMS.getEntries()) {
            Item item = holder.get();
            if (!(item instanceof IStardewItem stardewItem)) {
                continue;
            }
            var id = BuiltInRegistries.ITEM.getKey(item);
            if (!com.stardew.craft.item.artisan.PreservesIngredientDataManager.hasData(id)) {
                continue;
            }

            String typeKey = stardewItem.getItemTypeKey();
            if (isPreserveCropIngredient(typeKey)) {
                var preserveType = com.stardew.craft.item.artisan.PreservesCropTypeHelper.getCropPreserveType(id);
                if (preserveType != null) {
                    addPreserveVariant(output, preserveType, item, getPreserveBaseItem(preserveType));
                }
            } else if (isPreserveFishIngredient(typeKey)) {
                addPreserveVariant(output, com.stardew.craft.item.artisan.PreserveType.ROE, item, ModItems.ROE.get());
                addPreserveVariant(output, com.stardew.craft.item.artisan.PreserveType.AGED_ROE, item, ModItems.AGED_ROE.get());
                if (isSturgeon(id)) {
                    addPreserveVariant(output, com.stardew.craft.item.artisan.PreserveType.CAVIAR, item, ModItems.CAVIAR.get());
                }
            }

            if (isDehydratorFruitIngredient(typeKey, id)) {
                addPreserveVariant(output, com.stardew.craft.item.artisan.PreserveType.DRIED_FRUIT, item, ModItems.DRIED_FRUIT.get());
            } else if (isDehydratorMushroomIngredient(id)) {
                addPreserveVariant(output, com.stardew.craft.item.artisan.PreserveType.DRIED_MUSHROOMS, item, ModItems.DRIED_MUSHROOMS.get());
            }
        }
    }

    @SuppressWarnings("null")
    private static void addSpecificBaitVariants(CreativeModeTab.Output output) {
        int variantIndex = 0;
        for (var holder : ModItems.ITEMS.getEntries()) {
            Item item = holder.get();
            if (!(item instanceof IStardewItem stardewItem)) {
                continue;
            }

            String typeKey = stardewItem.getItemTypeKey();
            if (!isPreserveFishIngredient(typeKey)) {
                continue;
            }

            // Show pre-configured Specific Bait entries in creative tab so they are discoverable.
            ItemStack fishStack = new ItemStack(item);
            if (fishStack.isEmpty()) {
                continue;
            }
            ItemStack baitStack = SpecificBaitItem.createForFish(fishStack, 1);
            SpecificBaitItem.applyCreativeVariantMarker(baitStack, variantIndex);
            output.accept(baitStack);
            variantIndex++;
        }
    }

    private static boolean isPreserveCropIngredient(String typeKey) {
        return "stardewcraft.type.crop".equals(typeKey);
    }

    private static boolean isPreserveFishIngredient(String typeKey) {
        return "stardewcraft.type.fish".equals(typeKey)
                || "stardewcraft.type.crabpot".equals(typeKey)
                || "stardewcraft.type.legendary_fish".equals(typeKey);
    }

    private static boolean isDehydratorFruitIngredient(String typeKey, net.minecraft.resources.ResourceLocation id) {
        if (!"stardewcraft.type.crop".equals(typeKey)) {
            return false;
        }
        if (isGrape(id)) {
            return false;
        }
        return DehydratorIngredientHelper.isFruitCrop(id);
    }

    private static boolean isDehydratorMushroomIngredient(net.minecraft.resources.ResourceLocation id) {
        return DehydratorIngredientHelper.isMushroom(id);
    }

    private static boolean isSturgeon(net.minecraft.resources.ResourceLocation id) {
        return id != null && "sturgeon".equals(id.getPath());
    }

    private static boolean isGrape(net.minecraft.resources.ResourceLocation id) {
        return id != null && "grape".equals(id.getPath());
    }

    @SuppressWarnings("null")
    private static void addPreserveVariant(CreativeModeTab.Output output,
                                           com.stardew.craft.item.artisan.PreserveType type,
                                           Item ingredient,
                                           Item baseItem) {
        ItemStack ingredientStack = new ItemStack(ingredient);
        ItemStack resultStack = new ItemStack(baseItem);
        com.stardew.craft.item.artisan.PreservesItem.createFlavored(type, ingredientStack, resultStack);
        output.accept(resultStack);
    }

    private static Item getPreserveBaseItem(com.stardew.craft.item.artisan.PreserveType type) {
        return switch (type) {
            case JELLY -> ModItems.JELLY.get();
            case PICKLES -> ModItems.PICKLES.get();
            case ROE -> ModItems.ROE.get();
            case AGED_ROE -> ModItems.AGED_ROE.get();
            case CAVIAR -> ModItems.CAVIAR.get();
            case DRIED_FRUIT -> ModItems.DRIED_FRUIT.get();
            case DRIED_MUSHROOMS -> ModItems.DRIED_MUSHROOMS.get();
        };
    }

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public StardewCraft(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(PacketHandler::register);
        modEventBus.addListener(WeaponShaderRegistry::onRegisterShadersSafe);
        modEventBus.addListener(UtilityAutomationCapabilities::registerCapabilities);
        modEventBus.addListener(ModEntities::onEntityAttributeCreation);

        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        
        // 注册作物系统的物品和方块
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
		ModEntities.ENTITY_TYPES.register(modEventBus);
		ModSounds.SOUND_EVENTS.register(modEventBus);

		// 注册自定义 Buff（状态效果）
		ModMobEffects.MOB_EFFECTS.register(modEventBus);
		
		// 注册自定义粒子
		com.stardew.craft.weather.ModParticles.PARTICLES.register(modEventBus);
		
		// 注册菜单类型
		com.stardew.craft.menu.ModMenuTypes.MENU_TYPES.register(modEventBus);


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (StardewCraft) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        
        // 手动注册事件（确保事件被正确注册）
        NeoForge.EVENT_BUS.register(com.stardew.craft.event.MinePickaxeEvents.class);
        NeoForge.EVENT_BUS.register(WildTreeChopEvents.class);
        // WeaponCombatEvents 已有 @EventBusSubscriber 自动注册，不需要手动注册

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(com.stardew.craft.client.ModClientEvents.class);
        }

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("Initializing StardewCraft systems...");
        
        // 初始化维度和作物系统
        event.enqueueWork(() -> {
            com.stardew.craft.core.ModDimensions.register();

            // 注入晕倒/死亡系统
            com.stardew.craft.player.StardewDamageHooks.setKnockoutHandler(
                    com.stardew.craft.player.PassOutService::onCombatDeath);
        });

        // Curios 可选兼容：如果安装了 Curios，注册戒指/靴子到 Curios 槽位
        event.enqueueWork(() -> {
            if (com.stardew.craft.compat.CuriosCompatBridge.isCuriosLoaded()) {
                LOGGER.info("[Curios] Curios detected, registering ring/boots items as curio-compatible");
                com.stardew.craft.item.ModItems.ITEMS.getEntries().forEach(entry -> {
                    net.minecraft.world.item.Item item = entry.get();
                    if (item instanceof com.stardew.craft.item.equipment.StardewRingItem
                            || item instanceof com.stardew.craft.item.equipment.StardewBootsItem) {
                        com.stardew.craft.compat.CuriosCompatBridge.registerItem(item);
                    }
                });
            }
        });
    }

    /**
     * 在 createLevels() 之前安装预烘焙区域文件。
     * ServerAboutToStartEvent 在 initServer() 早期触发，早于 LevelEvent.Load，
     * 确保 .mca 文件在维度首次加载前就位。
     */
    @SuppressWarnings("null")
    @SubscribeEvent
    public void onServerAboutToStart(net.neoforged.neoforge.event.server.ServerAboutToStartEvent event) {
        var server = event.getServer();
        LOGGER.info("[VALLEY_MAP] Startup: trying prebuilt region install (ServerAboutToStart)");
        var result = com.stardew.craft.dimension.StardewValleyPrebuiltRegionInstaller.installIfAvailable(server);
        if (result == com.stardew.craft.dimension.StardewValleyPrebuiltRegionInstaller.InstallResult.INSTALLED
            || result == com.stardew.craft.dimension.StardewValleyPrebuiltRegionInstaller.InstallResult.ALREADY_PRESENT) {
            LOGGER.info("[VALLEY_MAP] Prebuilt regions ready ({}).", result);
        } else {
            LOGGER.error("[VALLEY_MAP] Prebuilt region package missing or invalid. Stardew Valley travel will be blocked until fixed.");
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SuppressWarnings("null")
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");

        // 加载任务数据
        com.stardew.craft.quest.QuestDataLoader.load();

        var server = event.getServer();
        // markAsPreGenerated 需要在 level 可用之后执行
        if (com.stardew.craft.dimension.StardewValleyPrebuiltRegionInstaller.hasInstalledPrebuilt(server)) {
            var stardewLevel = server.getLevel(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY);
            if (stardewLevel != null) {
                com.stardew.craft.dimension.StardewValleyMapBootstrap.markAsPreGenerated(stardewLevel);
            } else {
                LOGGER.info("[VALLEY_MAP] Stardew level not loaded at startup, will mark pre-generated on first travel.");
            }
        }
    }
}
