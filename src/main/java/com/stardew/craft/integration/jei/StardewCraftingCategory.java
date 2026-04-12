package com.stardew.craft.integration.jei;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.player.StardewCraftingRecipeData;
import com.stardew.craft.player.StardewCraftingRecipeData.IngredientEntry;
import com.stardew.craft.player.StardewCraftingRecipeData.RecipeEntry;
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

/**
 * JEI category for Stardew Valley crafting recipes (non-vanilla workbench).
 * Shows ingredients → output with unlock condition.
 */
public class StardewCraftingCategory implements IRecipeCategory<StardewCraftingCategory.DisplayRecipe> {
    public static final RecipeType<DisplayRecipe> RECIPE_TYPE = RecipeType.create(
            StardewCraft.MODID, "stardew_crafting", DisplayRecipe.class);

    private static final int GUI_WIDTH = 166;
    private static final int GUI_HEIGHT = 60;

    private final IDrawable icon;
    private final Component title;

    public record DisplayRecipe(
            String recipeId,
            List<ItemStack> inputs,
            ItemStack output,
            String unlockCondition
    ) {}

    @SuppressWarnings("null")
    public StardewCraftingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                ArtisanRecipeCategory.getItemStack("minecraft:crafting_table"));
        this.title = Component.translatable("stardewcraft.jei.stardew_crafting");
    }

    @Override public RecipeType<DisplayRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public int getWidth() { return GUI_WIDTH; }
    @Override public int getHeight() { return GUI_HEIGHT; }
    @Override public IDrawable getIcon() { return icon; }

    @SuppressWarnings("null")
    @Override
    public void setRecipe(@SuppressWarnings("null") IRecipeLayoutBuilder builder,
                          @SuppressWarnings("null") DisplayRecipe recipe,
                          @SuppressWarnings("null") IFocusGroup focuses) {
        // Input slots — up to 4 inputs arranged in a 2×2 grid
        List<ItemStack> inputs = recipe.inputs();
        for (int i = 0; i < inputs.size() && i < 4; i++) {
            int col = i % 2;
            int row = i / 2;
            builder.addSlot(RecipeIngredientRole.INPUT, 10 + col * 22, 8 + row * 22)
                    .addItemStack(inputs.get(i));
        }

        // Output slot — right side, vertically centred
        builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 18)
                .addItemStack(recipe.output());
    }

    @SuppressWarnings("null")
    @Override
    public void draw(@SuppressWarnings("null") DisplayRecipe recipe,
                     @SuppressWarnings("null") IRecipeSlotsView recipeSlotsView,
                     @SuppressWarnings("null") GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        // Background panel
        JeiDrawHelper.drawPanel(guiGraphics, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        // Input slot backgrounds (2×2 grid)
        List<ItemStack> inputs = recipe.inputs();
        for (int i = 0; i < inputs.size() && i < 4; i++) {
            int col = i % 2;
            int row = i / 2;
            JeiDrawHelper.drawSlotBg(guiGraphics, 9 + col * 22, 7 + row * 22);
        }

        // Output slot — golden highlight
        JeiDrawHelper.drawOutputSlotBg(guiGraphics, 109, 17);

        // Arrow (between grid and output)
        JeiDrawHelper.drawArrow(guiGraphics, 60, 20);

        // Unlock condition — bottom of panel
        if (recipe.unlockCondition() != null && !recipe.unlockCondition().isEmpty()) {
            String condText = recipe.unlockCondition();
            int maxW = GUI_WIDTH - 14;
            if (font.width(condText) > maxW) {
                // Truncate with ellipsis
                while (font.width(condText + "…") > maxW && condText.length() > 1) {
                    condText = condText.substring(0, condText.length() - 1);
                }
                condText += "…";
            }
            guiGraphics.drawString(font, condText, 7, 50, JeiDrawHelper.TEXT_MUTED, false);
        }
    }

    /**
     * Build all display recipes from StardewCraftingRecipeData.
     */
    public static List<DisplayRecipe> buildAllRecipes() {
        List<DisplayRecipe> result = new ArrayList<>();
        for (RecipeEntry entry : StardewCraftingRecipeData.getRecipes()) {
            ItemStack output = StardewCraftingRecipeData.getOutputStack(entry.id());
            if (output.isEmpty()) continue;

            List<ItemStack> inputs = new ArrayList<>();
            for (IngredientEntry ingr : StardewCraftingRecipeData.getIngredientEntries(entry.id())) {
                ItemStack inStack = ArtisanRecipeCategory.getItemStack(ingr.item());
                if (inStack.isEmpty()) continue;
                if (ingr.count() > 1) {
                    inStack.setCount(ingr.count());
                }
                inputs.add(inStack);
            }
            if (inputs.isEmpty()) continue;

            String unlock = StardewCraftingRecipeData.getUnlockCondition(entry.id());
            result.add(new DisplayRecipe(entry.id(), inputs, output, unlock));
        }
        return result;
    }
}
