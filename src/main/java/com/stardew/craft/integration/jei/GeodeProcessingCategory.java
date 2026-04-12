package com.stardew.craft.integration.jei;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * JEI category for geode processing — shows each geode type and all possible mineral outputs.
 * Each "recipe" displays one geode → one possible output mineral.
 */
@SuppressWarnings("null")
public class GeodeProcessingCategory implements IRecipeCategory<GeodeProcessingCategory.DisplayEntry> {
    public static final RecipeType<DisplayEntry> RECIPE_TYPE = RecipeType.create(
            StardewCraft.MODID, "geode_processing", DisplayEntry.class);

    private static final int GUI_WIDTH = 160;
    private static final int GUI_HEIGHT = 44;

    private final IDrawable icon;
    private final Component title;

    public record DisplayEntry(ItemStack geode, ItemStack mineral) {}

    @SuppressWarnings("null")
    public GeodeProcessingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModItems.GEODE.get()));
        this.title = Component.translatable("stardewcraft.jei.geode_processing");
        JeiDrawHelper.initGoldIcon(guiHelper);
    }

    @Override public RecipeType<DisplayEntry> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public int getWidth() { return GUI_WIDTH; }
    @Override public int getHeight() { return GUI_HEIGHT; }
    @Override public IDrawable getIcon() { return icon; }

    @SuppressWarnings("null")
    @Override
    public void setRecipe(@SuppressWarnings("null") IRecipeLayoutBuilder builder,
                          @SuppressWarnings("null") DisplayEntry recipe,
                          @SuppressWarnings("null") IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 12, 15)
                .addItemStack(recipe.geode());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 62, 15)
                .addItemStack(recipe.mineral());
    }

    @SuppressWarnings("null")
    @Override
    public void draw(@SuppressWarnings("null") DisplayEntry recipe,
                     @SuppressWarnings("null") IRecipeSlotsView recipeSlotsView,
                     @SuppressWarnings("null") GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        // Background panel
        JeiDrawHelper.drawPanel(guiGraphics, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        // Slots
        JeiDrawHelper.drawSlotBg(guiGraphics, 11, 14);
        JeiDrawHelper.drawOutputSlotBg(guiGraphics, 61, 14);

        // Arrow
        JeiDrawHelper.drawArrow(guiGraphics, 34, 17);

        // Geode name — right side (from item's translated name)
        String geodeName = recipe.geode().getHoverName().getString();
        guiGraphics.drawString(font, geodeName, 86, 10, JeiDrawHelper.TEXT_TITLE, false);

        // Cost with gold icon
        JeiDrawHelper.drawGoldAmount(guiGraphics, font, 86, 24, 25);
    }

    /**
     * Build all display entries — one per geode/mineral pair.
     * Uses reflection-free access to the hardcoded arrays in GeodeLootService.
     * Since the pools are package-private, we duplicate the pool references here.
     */
    public static List<DisplayEntry> buildAllEntries() {
        List<DisplayEntry> result = new ArrayList<>();

        addGeodeEntries(result, ModItems.GEODE,
                ModItems.ALAMITE, ModItems.CALCITE, ModItems.JAMBORITE, ModItems.JAGOITE,
                ModItems.MALACHITE, ModItems.NEKOITE, ModItems.ORPIMENT, ModItems.PETRIFIED_SLIME,
                ModItems.THUNDER_EGG, ModItems.CELESTINE, ModItems.SANDSTONE, ModItems.GRANITE,
                ModItems.LIMESTONE_MINERAL, ModItems.MUDSTONE, ModItems.SLATE, ModItems.DWARVISH_HELM);

        addGeodeEntries(result, ModItems.FROZEN_GEODE,
                ModItems.AERINITE, ModItems.ESPERITE, ModItems.FLUORAPATITE, ModItems.GEMINITE,
                ModItems.KYANITE, ModItems.LUNARITE, ModItems.PYRITE, ModItems.OCEAN_STONE,
                ModItems.GHOST_CRYSTAL, ModItems.OPAL, ModItems.MARBLE, ModItems.SOAPSTONE,
                ModItems.HEMATITE, ModItems.FAIRY_STONE, ModItems.ANCIENT_DRUM);

        addGeodeEntries(result, ModItems.MAGMA_GEODE,
                ModItems.BIXITE, ModItems.BARYTE, ModItems.DOLOMITE, ModItems.HELVITE,
                ModItems.NEPTUNITE, ModItems.LEMON_STONE, ModItems.TIGERSEYE, ModItems.JASPER,
                ModItems.FIRE_OPAL, ModItems.BASALT, ModItems.OBSIDIAN, ModItems.STAR_SHARDS,
                ModItems.DWARF_GADGET);

        // Omni geode shows all minerals from all three pools - can drop any of them
        addGeodeEntries(result, ModItems.OMNI_GEODE,
                ModItems.ALAMITE, ModItems.CALCITE, ModItems.JAMBORITE, ModItems.JAGOITE,
                ModItems.MALACHITE, ModItems.NEKOITE, ModItems.ORPIMENT, ModItems.PETRIFIED_SLIME,
                ModItems.THUNDER_EGG, ModItems.CELESTINE, ModItems.SANDSTONE, ModItems.GRANITE,
                ModItems.LIMESTONE_MINERAL, ModItems.MUDSTONE, ModItems.SLATE, ModItems.DWARVISH_HELM,
                ModItems.AERINITE, ModItems.ESPERITE, ModItems.FLUORAPATITE, ModItems.GEMINITE,
                ModItems.KYANITE, ModItems.LUNARITE, ModItems.PYRITE, ModItems.OCEAN_STONE,
                ModItems.GHOST_CRYSTAL, ModItems.OPAL, ModItems.MARBLE, ModItems.SOAPSTONE,
                ModItems.HEMATITE, ModItems.FAIRY_STONE, ModItems.ANCIENT_DRUM,
                ModItems.BIXITE, ModItems.BARYTE, ModItems.DOLOMITE, ModItems.HELVITE,
                ModItems.NEPTUNITE, ModItems.LEMON_STONE, ModItems.TIGERSEYE, ModItems.JASPER,
                ModItems.FIRE_OPAL, ModItems.BASALT, ModItems.OBSIDIAN, ModItems.STAR_SHARDS,
                ModItems.DWARF_GADGET);

        return result;
    }

    @SafeVarargs
    private static void addGeodeEntries(List<DisplayEntry> result,
                                        Supplier<? extends net.minecraft.world.item.Item> geodeSupplier,
                                        Supplier<? extends net.minecraft.world.item.Item>... minerals) {
        ItemStack geode = new ItemStack(geodeSupplier.get());
        for (var mineralSupplier : minerals) {
            ItemStack mineral = new ItemStack(mineralSupplier.get());
            if (!mineral.isEmpty()) {
                result.add(new DisplayEntry(geode.copy(), mineral));
            }
        }
    }
}
