package com.stardew.craft;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.SpecificBaitItem;
import com.stardew.craft.item.artisan.PreservesItem;
import com.stardew.craft.client.render.FallenOakTreeRenderer;
import com.stardew.craft.client.ModItemProperties;
import com.stardew.craft.client.ModRenderLayers;
import com.stardew.craft.client.render.FertilizerOverlayRenderer;
import com.stardew.craft.client.renderer.SprinklerOverlayRenderer;
import com.stardew.craft.client.DebugKeybindsTick;
import com.stardew.craft.entity.ModEntities;

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
                ModBlocks.DEAD_CROP.get()
            ));
            
			ModItemProperties.register();
        });
    }

    @SuppressWarnings("null")
    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FALLEN_OAK_TREE.get(), FallenOakTreeRenderer::new);
    }

    @SubscribeEvent
    static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
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

