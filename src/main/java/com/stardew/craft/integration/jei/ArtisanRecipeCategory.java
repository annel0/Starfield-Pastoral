package com.stardew.craft.integration.jei;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.artisan.ArtisanRecipeDataManager;
import com.stardew.craft.item.artisan.ArtisanRecipeDataManager.InputMode;
import com.stardew.craft.item.artisan.ArtisanRecipeDataManager.OutputMode;
import com.stardew.craft.item.artisan.PreserveType;
import com.stardew.craft.item.artisan.PreservesCropTypeHelper;
import com.stardew.craft.item.artisan.PreservesItem;
import com.stardew.craft.item.artisan.SmokedFishItem;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * JEI category for artisan machine processing recipes.
 * Each machine gets its own instance with a unique RecipeType.
 * Shows: [input item] → [machine icon] → [output item] with processing time.
 */
@SuppressWarnings("null")
public class ArtisanRecipeCategory implements IRecipeCategory<ArtisanRecipeCategory.DisplayRecipe> {

    // ─── Per-machine RecipeType registry ───────────────────────────────
    private static final java.util.Map<String, RecipeType<DisplayRecipe>> RECIPE_TYPES = new java.util.HashMap<>();

    /**
     * Get (or create) the RecipeType for a specific machine.
     * The type ID is "stardewcraft:machine/{machineKey}".
     */
    public static RecipeType<DisplayRecipe> getRecipeType(String machineKey) {
        return RECIPE_TYPES.computeIfAbsent(machineKey, k ->
                RecipeType.create(StardewCraft.MODID, "machine/" + k, DisplayRecipe.class));
    }

    /** @deprecated Use {@link #getRecipeType(String)} instead. Kept for backward compat during transition. */
    @Deprecated
    public static final RecipeType<DisplayRecipe> RECIPE_TYPE = RecipeType.create(
            StardewCraft.MODID, "artisan_recipe", DisplayRecipe.class);

    private static final int GUI_WIDTH = 160;
    private static final int GUI_HEIGHT = 50;

    private final RecipeType<DisplayRecipe> recipeType;
    private final IDrawable icon;
    private final Component title;

    public record DisplayRecipe(
            String machineKey,
            ItemStack input,
            int consumeCount,
            ItemStack output,
            ItemStack machineIcon,
            int minutes
    ) {}

    /**
     * Create a category instance for a specific machine.
     *
     * @param machineKey  e.g. "keg", "preserves_jar"
     * @param machineIcon the machine's ItemStack for the tab icon
     * @param recipeType  the per-machine RecipeType from {@link #getRecipeType(String)}
     */
    @SuppressWarnings("null")
    public ArtisanRecipeCategory(IGuiHelper guiHelper, String machineKey, ItemStack machineIcon, RecipeType<DisplayRecipe> recipeType) {
        this.recipeType = recipeType;
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, machineIcon);
        this.title = Component.translatable("stardewcraft.jei.machine." + machineKey);
        JeiDrawHelper.initGoldIcon(guiHelper);
    }

    @Override
    public RecipeType<DisplayRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return GUI_WIDTH;
    }

    @Override
    public int getHeight() {
        return GUI_HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @SuppressWarnings("null")
    @Override
    public void setRecipe(@SuppressWarnings("null") IRecipeLayoutBuilder builder,
                          @SuppressWarnings("null") DisplayRecipe recipe,
                          @SuppressWarnings("null") IFocusGroup focuses) {
        // Input slot — left side
        builder.addSlot(RecipeIngredientRole.INPUT, 12, 18)
                .addItemStack(recipe.input());

        // Machine as catalyst — centre
        builder.addSlot(RecipeIngredientRole.CATALYST, 65, 18)
                .addItemStack(recipe.machineIcon());

        // Output slot — right side
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 18)
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

        // Slot backgrounds
        JeiDrawHelper.drawSlotBg(guiGraphics, 11, 17);
        JeiDrawHelper.drawSlotBg(guiGraphics, 64, 17);
        JeiDrawHelper.drawOutputSlotBg(guiGraphics, 119, 17);

        // Arrows (input → machine → output)
        JeiDrawHelper.drawArrow(guiGraphics, 34, 19);
        JeiDrawHelper.drawArrow(guiGraphics, 88, 19);

        // Processing time — centred above machine slot
        String timeText = JeiDrawHelper.formatTime(recipe.minutes());
        int timeWidth = font.width(timeText);
        guiGraphics.drawString(font, timeText, (GUI_WIDTH - timeWidth) / 2, 5, JeiDrawHelper.TEXT_MUTED, false);

        // Consume count (if > 1) — below input slot
        if (recipe.consumeCount() > 1) {
            String countText = "×" + recipe.consumeCount();
            guiGraphics.drawString(font, countText, 12, 38, JeiDrawHelper.TEXT_BODY, false);
        }
    }

    @SuppressWarnings("null")
    static ItemStack getItemStack(String id) {
        ResourceLocation loc = ResourceLocation.tryParse(id);
        if (loc == null) return ItemStack.EMPTY;
        Item item = BuiltInRegistries.ITEM.get(loc);
        if (item == null || item == Items.AIR) return ItemStack.EMPTY;
        return new ItemStack(item);
    }

    /**
     * Build display recipes for a single machine.
     * Handles both static (inputId) and dynamic (inputMode) recipes.
     *
     * @param machineKey the machine to build recipes for (e.g. "keg")
     */
    public static java.util.List<DisplayRecipe> buildRecipesForMachine(String machineKey) {
        java.util.List<DisplayRecipe> result = new java.util.ArrayList<>();
        ItemStack machineIcon = getItemStack("stardewcraft:" + machineKey);
        if (machineIcon.isEmpty()) return result;

        for (ArtisanRecipeDataManager.Recipe recipe : ArtisanRecipeDataManager.getRecipes(machineKey)) {
            if (recipe.inputMode() != InputMode.DEFAULT) {
                expandDynamicRecipes(result, machineKey, machineIcon, recipe);
                continue;
            }

            ItemStack input;
            if (recipe.inputId() != null) {
                input = getItemStack(recipe.inputId().toString());
            } else {
                continue;
            }
            if (input.isEmpty()) continue;

            ItemStack output;
            if (recipe.outputId() != null) {
                output = getItemStack(recipe.outputId().toString());
            } else {
                continue;
            }
            if (output.isEmpty()) continue;

            int outputCount = recipe.outputCount();
            if (outputCount > 1) {
                output.setCount(outputCount);
            }

            int consumeCount = recipe.consumeCount();
            if (consumeCount > 1) {
                input.setCount(consumeCount);
            }

            result.add(new DisplayRecipe(machineKey, input, consumeCount, output, machineIcon.copy(), recipe.minutes()));
        }
        return result;
    }

    /**
     * Build all display recipes across all machines (legacy helper).
     * @deprecated Use {@link #buildRecipesForMachine(String)} per machine instead.
     */
    @Deprecated
    public static java.util.List<DisplayRecipe> buildAllRecipes() {
        java.util.List<DisplayRecipe> result = new java.util.ArrayList<>();
        for (String machineKey : ArtisanRecipeDataManager.getAllMachineKeys()) {
            result.addAll(buildRecipesForMachine(machineKey));
        }
        return result;
    }

    /**
     * Expand a dynamic recipe (CROP_TYPE/FISH_TYPE/MINERAL_TYPE) into concrete
     * per-item display recipes by iterating all matching registered items.
     */
    @SuppressWarnings("null")
    private static void expandDynamicRecipes(java.util.List<DisplayRecipe> result,
                                              String machineKey,
                                              ItemStack machineIcon,
                                              ArtisanRecipeDataManager.Recipe recipe) {
        for (var holder : ModItems.ITEMS.getEntries()) {
            Item item = holder.get();
            if (!(item instanceof IStardewItem stardewItem)) continue;

            String typeKey = stardewItem.getItemTypeKey();
            boolean matches = switch (recipe.inputMode()) {
                case CROP_TYPE -> "stardewcraft.type.crop".equals(typeKey);
                case FISH_TYPE -> "stardewcraft.type.fish".equals(typeKey)
                        || "stardewcraft.type.crabpot".equals(typeKey)
                        || "stardewcraft.type.legendary_fish".equals(typeKey);
                case MINERAL_TYPE -> "stardewcraft.type.mineral".equals(typeKey);
                default -> false;
            };
            if (!matches) continue;

            ItemStack input = new ItemStack(item);
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);

            ItemStack output = resolveOutputForDynamic(recipe, item, itemId, input);
            if (output == null || output.isEmpty()) continue;

            int consumeCount = recipe.consumeCount();
            if (consumeCount > 1) {
                input.setCount(consumeCount);
            }

            result.add(new DisplayRecipe(machineKey, input, consumeCount, output, machineIcon.copy(), recipe.minutes()));
        }
    }

    /**
     * Resolve the output ItemStack for a dynamic recipe given a matched input item.
     */
    @SuppressWarnings("null")
    private static ItemStack resolveOutputForDynamic(ArtisanRecipeDataManager.Recipe recipe,
                                                      Item inputItem,
                                                      ResourceLocation inputId,
                                                      ItemStack inputStack) {
        // If the recipe has a preserve type, build a flavored preserve
        if (recipe.preserveType() != null) {
            PreserveType pType = recipe.preserveType();
            // For CROP_TYPE preserves, check if this crop matches the expected preserve type
            if (recipe.inputMode() == InputMode.CROP_TYPE) {
                PreserveType cropType = PreservesCropTypeHelper.getCropPreserveType(inputId);
                if (cropType == null || cropType != pType) return null;
            }
            Item baseItem = getPreserveBaseItem(pType);
            if (baseItem == null) return null;
            ItemStack resultStack = new ItemStack(baseItem);
            PreservesItem.createFlavored(pType, inputStack, resultStack);
            return resultStack;
        }

        // SMOKED output mode — find matching smoked fish item
        if (recipe.outputMode() == OutputMode.SMOKED) {
            for (var smokedHolder : ModItems.ITEMS.getEntries()) {
                Item smokedItem = smokedHolder.get();
                if (smokedItem instanceof SmokedFishItem smoked) {
                    if (smoked.getSourceItem() == inputItem) {
                        return new ItemStack(smokedItem);
                    }
                }
            }
            return null;
        }

        // Fixed output — use outputId
        if (recipe.outputId() != null) {
            ItemStack out = getItemStack(recipe.outputId().toString());
            if (recipe.outputCount() > 1) {
                out.setCount(recipe.outputCount());
            }
            return out;
        }

        return null;
    }

    private static Item getPreserveBaseItem(PreserveType type) {
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
}
