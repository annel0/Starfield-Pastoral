package com.stardew.craft;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.SpecificBaitItem;
import com.stardew.craft.item.artisan.PreservesItem;
import com.stardew.craft.client.render.FallenOakTreeRenderer;
import com.stardew.craft.client.ModItemProperties;
import com.stardew.craft.client.ModRenderLayers;
import com.stardew.craft.client.render.FertilizerOverlayRenderer;
import com.stardew.craft.client.render.TVScreenOverlayRenderer;
import com.stardew.craft.client.renderer.SprinklerOverlayRenderer;
import com.stardew.craft.client.DebugKeybindsTick;
import com.stardew.craft.client.renderer.entity.SofaSeatEntityRenderer;
import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.block.utility.CushionBlock;
import com.stardew.craft.block.utility.OfficeChair2Block;
import com.stardew.craft.block.utility.DyeableChairBlock;
import com.stardew.craft.block.utility.OfficeChair2TopRenderBlock;
import com.stardew.craft.block.utility.OfficeStoolBlock;
import com.stardew.craft.block.utility.OfficeStoolTopRenderBlock;
import com.stardew.craft.block.utility.SofaBlock;
import com.stardew.craft.block.utility.WoodenChestColorPalette;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = StardewCraft.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = StardewCraft.MODID, value = Dist.CLIENT)
public class StardewCraftClient {
    public StardewCraftClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    NeoForge.EVENT_BUS.register(FertilizerOverlayRenderer.class);
    NeoForge.EVENT_BUS.register(TVScreenOverlayRenderer.class);
    NeoForge.EVENT_BUS.register(SprinklerOverlayRenderer.class);
    NeoForge.EVENT_BUS.register(DebugKeybindsTick.class);
    }

    @SubscribeEvent
    @SuppressWarnings({ "deprecation", "null" })
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        StardewCraft.LOGGER.info("HELLO FROM CLIENT SETUP");
        StardewCraft.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        event.enqueueWork(() -> {
            ModRenderLayers.registerCutout(List.of(
                ModBlocks.WILD_WEEDS.get(),
                ModBlocks.PASTURE_GRASS.get(),
                ModBlocks.BLUE_PASTURE_GRASS.get(),
                ModBlocks.WILD_OAK_TRUNK0.get(),
                ModBlocks.WILD_OAK_TRUNK1.get(),
                ModBlocks.WILD_OAK_BRANCH1.get(),
                ModBlocks.WILD_OAK_BRANCH2.get(),
                ModBlocks.WILD_OAK_LEAVES.get(),
                ModBlocks.WILD_MAPLE_TRUNK0.get(),
                ModBlocks.WILD_MAPLE_TRUNK1.get(),
                ModBlocks.WILD_MAPLE_BRANCH1.get(),
                ModBlocks.WILD_MAPLE_BRANCH2.get(),
                ModBlocks.WILD_MAPLE_LEAVES.get(),
                ModBlocks.WILD_PINE_TRUNK0.get(),
                ModBlocks.WILD_PINE_TRUNK1.get(),
                ModBlocks.WILD_PINE_BRANCH1.get(),
                ModBlocks.WILD_PINE_BRANCH2.get(),
                ModBlocks.WILD_PINE_LEAVES.get(),
                ModBlocks.WILD_MAHOGANY_TRUNK0.get(),
                ModBlocks.WILD_MAHOGANY_TRUNK1.get(),
                ModBlocks.WILD_MAHOGANY_BRANCH1.get(),
                ModBlocks.WILD_MAHOGANY_BRANCH2.get(),
                ModBlocks.WILD_MAHOGANY_LEAVES.get(),
                ModBlocks.WILD_MYSTIC_TREE_TRUNK0.get(),
                ModBlocks.WILD_MYSTIC_TREE_TRUNK1.get(),
                ModBlocks.WILD_MYSTIC_TREE_BRANCH1.get(),
                ModBlocks.WILD_MYSTIC_TREE_BRANCH2.get(),
                ModBlocks.WILD_MYSTIC_TREE_LEAVES.get(),
                ModBlocks.QUARTZ.get(),
                ModBlocks.EARTH_CRYSTAL.get(),
                ModBlocks.FROZEN_TEAR.get(),
                ModBlocks.FIRE_QUARTZ.get(),
                ModBlocks.WILD_OAK_SAPLING0.get(),
                ModBlocks.WILD_OAK_SAPLING1.get(),
                ModBlocks.WILD_MAPLE_SAPLING0.get(),
                ModBlocks.WILD_MAPLE_SAPLING1.get(),
                ModBlocks.WILD_PINE_SAPLING0.get(),
                ModBlocks.WILD_PINE_SAPLING1.get(),
                ModBlocks.WILD_MAHOGANY_SAPLING0.get(),
                ModBlocks.WILD_MAHOGANY_SAPLING1.get(),
                ModBlocks.WILD_MYSTIC_TREE_SAPLING0.get(),
                ModBlocks.WILD_MYSTIC_TREE_SAPLING1.get(),
                ModBlocks.AMARANTH_CROP.get(),
                ModBlocks.ANCIENT_FRUIT_CROP.get(),
                ModBlocks.ARTICHOKE_CROP.get(),
                ModBlocks.BEET_CROP.get(),
                ModBlocks.BLUE_JAZZ_CROP.get(),
                ModBlocks.BLUEBERRY_CROP.get(),
                ModBlocks.BOK_CHOY_CROP.get(),
                ModBlocks.BROCCOLI_CROP.get(),
                ModBlocks.CARROT_CROP.get(),
                ModBlocks.CAULIFLOWER_CROP.get(),
                ModBlocks.COFFEE_BEAN_CROP.get(),
                ModBlocks.CORN_CROP.get(),
                ModBlocks.CRANBERRY_CROP.get(),
                ModBlocks.EGGPLANT_CROP.get(),
                ModBlocks.FAIRY_ROSE_CROP.get(),
                ModBlocks.GARLIC_CROP.get(),
                ModBlocks.GRAPE_CROP.get(),
                ModBlocks.GREEN_BEAN_CROP.get(),
                ModBlocks.HOPS_CROP.get(),
                ModBlocks.HOT_PEPPER_CROP.get(),
                ModBlocks.KALE_CROP.get(),
                ModBlocks.MELON_CROP.get(),
                ModBlocks.PARSNIP_CROP.get(),
                ModBlocks.POPPY_CROP.get(),
                ModBlocks.POTATO_CROP.get(),
                ModBlocks.POWDER_MELON_CROP.get(),
                ModBlocks.PUMPKIN_CROP.get(),
                ModBlocks.RADISH_CROP.get(),
                ModBlocks.RED_CABBAGE_CROP.get(),
                ModBlocks.RHUBARB_CROP.get(),
                ModBlocks.STARFRUIT_CROP.get(),
                ModBlocks.STRAWBERRY_CROP.get(),
                ModBlocks.SUMMER_SPANGLE_CROP.get(),
                ModBlocks.SUMMER_SQUASH_CROP.get(),
                ModBlocks.SUNFLOWER_CROP.get(),
                ModBlocks.TOMATO_CROP.get(),
                ModBlocks.TULIP_CROP.get(),
                ModBlocks.WHEAT_CROP.get(),
                ModBlocks.YAM_CROP.get(),
                ModBlocks.DEAD_CROP.get(),
                ModBlocks.WALL_HANGING_SMALL_A.get(),
                ModBlocks.WALL_NOTICE_BOARD_SMALL.get(),
                ModBlocks.WALL_HANGING_SMALL_B.get(),
                ModBlocks.WALL_BULLETIN_NOTES.get(),
                ModBlocks.WALL_HANGING_STRIP.get(),
                ModBlocks.WALL_SWITCH_PANEL.get(),
                ModBlocks.WALL_HANGING_TRIPTYCH.get(),
                ModBlocks.WALL_HANGING_ORNAMENT.get(),
                ModBlocks.WALL_NOTICE_BOARD_MEDIUM.get(),
                ModBlocks.WALL_FRAME_WIDE.get(),
                ModBlocks.WALL_FRAME_DOUBLE.get(),
                ModBlocks.WALL_STICKY_NOTES.get(),
                ModBlocks.WALL_POSTER_GAMEPAD.get(),
                ModBlocks.WALL_POSTER_DOLPHIN.get(),
                ModBlocks.WALL_POSTER_GAME_CHARACTER.get(),
                ModBlocks.WALL_OUTLET.get(),
                ModBlocks.WALL_PHOTO_WHITE_HALL.get(),
                ModBlocks.SHOP_WINDOW_1.get(),
                ModBlocks.SHOP_WINDOW_2.get(),
                ModBlocks.BONSAI_1.get(),
                ModBlocks.BONSAI_2.get(),
                ModBlocks.BONSAI_3.get(),
                ModBlocks.BONSAI_4.get(),
                ModBlocks.BONSAI_5_WALL.get(),
                ModBlocks.BONSAI_BUSH.get(),
                ModBlocks.COMPUTER.get(),
                ModBlocks.SAILBOAT.get(),
                ModBlocks.POTTED_PLANT_1.get(),
                ModBlocks.POTTED_PLANT_2.get(),
                ModBlocks.POTTED_PLANT_3.get(),
                ModBlocks.POTTED_PLANT_4.get(),
                ModBlocks.POTTED_PLANT_5.get(),
                ModBlocks.POTTED_PLANT_6.get(),
                ModBlocks.RADIO.get(),
                ModBlocks.BOOK_STACK_1.get(),
                ModBlocks.BOOK_STACK_2.get(),
                ModBlocks.BOOK_STACK_3.get(),
                ModBlocks.BOOKSHELF_WALL.get(),
                ModBlocks.SOFA.get(),
                ModBlocks.STOOL.get(),
                ModBlocks.DINING_CHAIR_WOOD.get(),
                ModBlocks.DINING_CHAIR_IRON.get(),
                ModBlocks.CHAIR_1.get(),
                ModBlocks.CHAIR_2.get(),
                ModBlocks.CHAIR_3.get(),
                ModBlocks.CUSHION.get(),
                ModBlocks.OFFICE_STOOL.get(),
                ModBlocks.OFFICE_CHAIR_2.get(),
                ModBlocks.LIGHT_1.get(),
                ModBlocks.LIGHT_2.get(),
                ModBlocks.LIGHT_3.get(),
                ModBlocks.LIGHT_4.get(),
                ModBlocks.LIGHT_5.get(),
                ModBlocks.LIGHT_6.get(),
                ModBlocks.LIGHT_7.get(),
                ModBlocks.OAK_TABLE.get(),
                ModBlocks.SPRUCE_TABLE.get(),
                ModBlocks.BIRCH_TABLE.get(),
                ModBlocks.OAK_ROUND_TABLE.get(),
                ModBlocks.ARCADE_MACHINE.get(),
                ModBlocks.WALL_PHOTO_FRAME.get(),
                ModBlocks.WALL_BONE_DECOR.get(),
                ModBlocks.KITCHEN_COUNTER.get(),
                ModBlocks.TABLEWARE_PINK.get(),
                ModBlocks.TABLEWARE_BLUE.get(),
                ModBlocks.WALL_KITCHEN_CABINET.get(),
                ModBlocks.JOJA_VENDING_MACHINE.get(),
                ModBlocks.WHITE_TEACUP.get(),
                ModBlocks.POOL_TABLE.get(),
                ModBlocks.HOSPITAL_COUNTER.get(),
                ModBlocks.HOSPITAL_POSTER_1.get(),
                ModBlocks.HOSPITAL_POSTER_2.get(),
                ModBlocks.HOSPITAL_POSTER_3.get(),
                ModBlocks.HOSPITAL_POSTER_4.get(),
                ModBlocks.HOSPITAL_POSTER_5.get(),
                ModBlocks.ELECTRIC_PIANO.get(),
                ModBlocks.WIZARD_CAULDRON.get(),
                ModBlocks.GUITAR.get(),
                ModBlocks.MICROWAVE.get(),
                ModBlocks.DRUM_SET.get(),
                ModBlocks.WINE_CABINET_1.get(),
                ModBlocks.WINE_CABINET_2.get(),
                ModBlocks.WINE_CABINET_3.get(),
                ModBlocks.ALEX_POSTER_1.get(),
                ModBlocks.ALEX_POSTER_2.get(),
                ModBlocks.ALEX_POSTER_3.get(),
                ModBlocks.LEAH_POSTER_1.get(),
                ModBlocks.LEAH_POSTER_2.get(),
                ModBlocks.LEAH_POSTER_3.get(),
                ModBlocks.PERIODIC_TABLE.get(),
                ModBlocks.MICROSCOPE.get(),
                ModBlocks.TRAIN_PHOTO.get(),
                ModBlocks.WALL_PHOTO_1.get(),
                ModBlocks.PAPER_CHECKLIST.get(),
                ModBlocks.SINE_WAVE_POSTER.get(),
                ModBlocks.SCATTERED_PAPERS.get(),
                ModBlocks.SEBASTIAN_POSTER_1.get(),
                ModBlocks.SEBASTIAN_POSTER_2.get(),
                ModBlocks.SEBASTIAN_POSTER_3.get(),
                ModBlocks.BOARD_GAME.get(),
                ModBlocks.WALL_ADVENTURER_MAP.get(),
                ModBlocks.WALL_BUOY.get(),
                ModBlocks.WALL_FISH_SIGN.get(),
                ModBlocks.WALL_ISLAND_MAP.get(),
                ModBlocks.BEAR_SKIN_RUG.get(),
                ModBlocks.LEANING_SWORD.get(),
                ModBlocks.LEAH_SCULPTURE.get(),
                ModBlocks.EASEL.get(),
                ModBlocks.BLUE_BEAR_PLUSHIE.get()
            ));

            // Negative-volume models with mixed opaque/transparent faces.
            net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                ModBlocks.OIL_MAKER.get(),
                net.minecraft.client.renderer.RenderType.translucent()
            );
            net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                ModBlocks.MUSEUM_EXHIBIT_STAND.get(),
                net.minecraft.client.renderer.RenderType.translucent()
            );
            net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                ModBlocks.TABLE_LANTERN.get(),
                net.minecraft.client.renderer.RenderType.translucent()
            );
            net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                ModBlocks.JUKEBOX.get(),
                net.minecraft.client.renderer.RenderType.translucent()
            );
            net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                ModBlocks.BEAKER.get(),
                net.minecraft.client.renderer.RenderType.translucent()
            );
            
			ModItemProperties.register();
        });
    }

    @SuppressWarnings("null")
    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FALLEN_OAK_TREE.get(), FallenOakTreeRenderer::new);
        event.registerEntityRenderer(ModEntities.SOFA_SEAT.get(), SofaSeatEntityRenderer::new);
    }

    @SuppressWarnings("null")
    @SubscribeEvent
    static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0 || state == null) {
                return 0xFFFFFFFF;
            }
            Integer indexValue = null;
            if (state.hasProperty(SofaBlock.COLOR)) {
                indexValue = state.getValue(SofaBlock.COLOR);
            } else if (state.hasProperty(CushionBlock.COLOR)) {
                indexValue = state.getValue(CushionBlock.COLOR);
            } else if (state.hasProperty(OfficeStoolBlock.COLOR)) {
                indexValue = state.getValue(OfficeStoolBlock.COLOR);
            } else if (state.hasProperty(OfficeStoolTopRenderBlock.COLOR)) {
                indexValue = state.getValue(OfficeStoolTopRenderBlock.COLOR);
            } else if (state.hasProperty(OfficeChair2Block.COLOR)) {
                indexValue = state.getValue(OfficeChair2Block.COLOR);
            } else if (state.hasProperty(OfficeChair2TopRenderBlock.COLOR)) {
                indexValue = state.getValue(OfficeChair2TopRenderBlock.COLOR);
            } else if (state.hasProperty(DyeableChairBlock.COLOR)) {
                indexValue = state.getValue(DyeableChairBlock.COLOR);
            }
            if (indexValue == null) {
                return 0xFFFFFFFF;
            }
            int index = WoodenChestColorPalette.clampIndex(indexValue);
            if (index < 0) {
                index = 0;
            }
            return 0xFF000000 | (WoodenChestColorPalette.rgbAt(index) & 0xFFFFFF);
        }, ModBlocks.SOFA.get(), ModBlocks.CUSHION.get(), ModBlocks.OFFICE_STOOL.get(), ModBlocks.OFFICE_STOOL_TOP_RENDER.get(), ModBlocks.OFFICE_CHAIR_2.get(), ModBlocks.OFFICE_CHAIR_2_TOP_RENDER.get(), ModBlocks.STOOL.get(), ModBlocks.DINING_CHAIR_WOOD.get(), ModBlocks.DINING_CHAIR_IRON.get());
    }

    @SubscribeEvent
    static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) {
                return 0xFFFFFFFF;
            }
            return 0xFF000000 | (WoodenChestColorPalette.rgbAt(0) & 0xFFFFFF);
        }, ModItems.SOFA.get(), ModItems.CUSHION.get(), ModItems.OFFICE_STOOL.get(), ModItems.OFFICE_CHAIR_2.get(), ModItems.STOOL.get(), ModItems.DINING_CHAIR_WOOD.get(), ModItems.DINING_CHAIR_IRON.get());

        event.register((stack, tintIndex) -> {
            if (!(stack.getItem() instanceof PreservesItem preservesItem)) {
                return 0xFFFFFFFF;
            }
            if (tintIndex != 1) {
                return 0xFFFFFFFF;
            }
            int color = preservesItem.getColor(stack);
            int rgb = color >= 0 ? color : 0xFFFFFF;
            return 0xFF000000 | (rgb & 0xFFFFFF);
        },
            ModItems.JELLY.get(),
            ModItems.PICKLES.get(),
            ModItems.ROE.get(),
            ModItems.AGED_ROE.get(),
            ModItems.DRIED_FRUIT.get(),
            ModItems.DRIED_MUSHROOMS.get()
        );
            event.register((stack, tintIndex) -> {
                if (!(stack.getItem() instanceof SpecificBaitItem specificBaitItem)) {
                    return 0xFFFFFFFF;
                }
                if (tintIndex != 1) {
                    return 0xFFFFFFFF;
                }
                int color = specificBaitItem.getColor(stack);
                int rgb = color >= 0 ? color : 0xFFFFFF;
                return 0xFF000000 | (rgb & 0xFFFFFF);
            }, ModItems.TARGETED_BAIT.get());
    }

    @SubscribeEvent
    static void onTextureStitch(TextureAtlasStitchedEvent event) {
        if (!event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) {
            return;
        }
        
        StardewCraft.LOGGER.info("Block atlas stitched - fertilizer textures should be available");
    }

}

