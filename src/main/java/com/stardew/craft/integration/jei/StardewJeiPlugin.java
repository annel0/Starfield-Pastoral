package com.stardew.craft.integration.jei;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.fishing.data.FishingDataManager;
import com.stardew.craft.fishing.data.SpawnFishRule;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.artisan.ArtisanDrinkItem;
import com.stardew.craft.item.artisan.DehydratorIngredientHelper;
import com.stardew.craft.item.artisan.PreserveType;
import com.stardew.craft.item.artisan.PreservesIngredientDataManager;
import com.stardew.craft.item.artisan.PreservesItem;
import com.stardew.craft.item.artisan.SmokedFishItem;
import com.stardew.craft.item.quality.QualityHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JEI 插件 - 钓鱼信息展示
 * 这是可选依赖，当JEI不存在时不会加载
 */
@JeiPlugin
public class StardewJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(@SuppressWarnings("null") IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FishingInfoCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    @SuppressWarnings("null")
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        for (var holder : ModItems.ITEMS.getEntries()) {
            var item = holder.get();
            if (!isQualityItem(item)) {
                continue;
            }
            registration.registerSubtypeInterpreter(item, new ISubtypeInterpreter<>() {
                @Override
                public Object getSubtypeData(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") UidContext context) {
                    int quality = QualityHelper.getQuality(stack);
                    Integer color = getFlowerColor(stack);
                    if (color != null) {
                        return "q=" + quality + ",c=" + color;
                    }
                    return "q=" + quality;
                }

                @Override
                public String getLegacyStringSubtypeInfo(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") UidContext context) {
                    int quality = QualityHelper.getQuality(stack);
                    Integer color = getFlowerColor(stack);
                    if (color != null) {
                        return "quality=" + quality + ",color=" + color;
                    }
                    return "quality=" + quality;
                }
            });
        }

        registration.registerSubtypeInterpreter(ModItems.JELLY.get(), new PreserveSubtypeInterpreter());
        registration.registerSubtypeInterpreter(ModItems.PICKLES.get(), new PreserveSubtypeInterpreter());
        registration.registerSubtypeInterpreter(ModItems.ROE.get(), new PreserveSubtypeInterpreter());
        registration.registerSubtypeInterpreter(ModItems.AGED_ROE.get(), new PreserveSubtypeInterpreter());
        registration.registerSubtypeInterpreter(ModItems.DRIED_FRUIT.get(), new PreserveSubtypeInterpreter());
        registration.registerSubtypeInterpreter(ModItems.DRIED_MUSHROOMS.get(), new PreserveSubtypeInterpreter());
    }

    @Override
    @SuppressWarnings("null")
    public void registerExtraIngredients(@SuppressWarnings("null") IExtraIngredientRegistration registration) {
        registration.addExtraItemStacks(buildPreserveVariants());
        registration.addExtraItemStacks(buildSmokedFishVariants());
    }

    @SuppressWarnings("null")
    @Override
    public void registerRecipes(@SuppressWarnings("null") IRecipeRegistration registration) {
        List<SpawnFishRule> allRules = FishingDataManager.get().getAllFishRules();
        
        // 按 itemId 去重，只保留第一次出现的规则
        List<SpawnFishRule> uniqueRules = new ArrayList<>();
        Set<String> seenItemIds = new HashSet<>();
        
        for (SpawnFishRule rule : allRules) {
            if (seenItemIds.add(rule.itemId())) {
                uniqueRules.add(rule);
            }
        }
        
        if (!uniqueRules.isEmpty()) {
            registration.addRecipes(FishingInfoCategory.RECIPE_TYPE, uniqueRules);
            StardewCraft.LOGGER.info("Registered {} unique fishing info recipes for JEI (deduplicated from {})", uniqueRules.size(), allRules.size());
        } else {
            StardewCraft.LOGGER.warn("No fishing rules loaded when registering JEI recipes!");
        }
    }

    @SuppressWarnings("null")
    @Override
    public void registerRecipeCatalysts(@SuppressWarnings("null") IRecipeCatalystRegistration registration) {
        // 注册所有钓竿作为催化剂，点击它们可以查看钓鱼信息
        registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.FISHING_ROD.get()), FishingInfoCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.TRAINING_ROD.get()), FishingInfoCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.FIBERGLASS_ROD.get()), FishingInfoCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.IRIDIUM_ROD.get()), FishingInfoCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.ADVANCED_IRIDIUM_ROD.get()), FishingInfoCategory.RECIPE_TYPE);
    }


    private static boolean isQualityItem(net.minecraft.world.item.Item item) {
        if (!(item instanceof IStardewItem stardewItem)) {
            return false;
        }
        String typeKey = stardewItem.getItemTypeKey();
        if (item instanceof ArtisanDrinkItem drink && drink.supportsQuality()) {
            return true;
        }
        if (item instanceof SmokedFishItem) {
            return true;
        }
        return "stardewcraft.type.crop".equals(typeKey)
            || "stardewcraft.type.crop_seed".equals(typeKey)
            || "stardewcraft.type.fish".equals(typeKey)
            || "stardewcraft.type.crabpot".equals(typeKey)
            || "stardewcraft.type.legendary_fish".equals(typeKey)
            || "stardewcraft.type.animal_product".equals(typeKey)
            || "stardewcraft.type.artisan_animal_quality".equals(typeKey)
            || "stardewcraft.type.artifact_quality".equals(typeKey);
    }

    private static List<ItemStack> buildSmokedFishVariants() {
        List<ItemStack> stacks = new ArrayList<>();
        for (var holder : ModItems.ITEMS.getEntries()) {
            var item = holder.get();
            if (!(item instanceof SmokedFishItem)) {
                continue;
            }
            ItemStack base = new ItemStack(item);
            stacks.add(QualityHelper.createWithQuality(base, QualityHelper.NORMAL));
            stacks.add(QualityHelper.createWithQuality(base, QualityHelper.SILVER));
            stacks.add(QualityHelper.createWithQuality(base, QualityHelper.GOLD));
            stacks.add(QualityHelper.createWithQuality(base, QualityHelper.IRIDIUM));
        }
        return stacks;
    }

    private static Integer getFlowerColor(@SuppressWarnings("null") ItemStack stack) {
        @SuppressWarnings("null")
        var customData = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY);
        var tag = customData.copyTag();
        if (tag.contains("FlowerColor")) {
            return tag.getInt("FlowerColor");
        }
        return null;
    }

    private static List<ItemStack> buildPreserveVariants() {
        List<ItemStack> stacks = new ArrayList<>();
        for (var holder : ModItems.ITEMS.getEntries()) {
            var item = holder.get();
            if (!(item instanceof IStardewItem stardewItem)) {
                continue;
            }
            ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
            if (!PreservesIngredientDataManager.hasData(id)) {
                continue;
            }
            String typeKey = stardewItem.getItemTypeKey();
            if (isPreserveCropIngredient(typeKey)) {
                var preserveType = com.stardew.craft.item.artisan.PreservesCropTypeHelper.getCropPreserveType(id);
                if (preserveType != null) {
                    stacks.add(buildPreserveVariant(preserveType, item, getPreserveBaseItem(preserveType)));
                }
            } else if (isPreserveFishIngredient(typeKey)) {
                stacks.add(buildPreserveVariant(PreserveType.ROE, item, ModItems.ROE.get()));
                stacks.add(buildPreserveVariant(PreserveType.AGED_ROE, item, ModItems.AGED_ROE.get()));
                if (isSturgeon(id)) {
                    stacks.add(buildPreserveVariant(PreserveType.CAVIAR, item, ModItems.CAVIAR.get()));
                }
            }

            if (isDehydratorFruitIngredient(typeKey, id)) {
                stacks.add(buildPreserveVariant(PreserveType.DRIED_FRUIT, item, ModItems.DRIED_FRUIT.get()));
            } else if (isDehydratorMushroomIngredient(id)) {
                stacks.add(buildPreserveVariant(PreserveType.DRIED_MUSHROOMS, item, ModItems.DRIED_MUSHROOMS.get()));
            }
        }
        return stacks;
    }

    private static boolean isPreserveCropIngredient(String typeKey) {
        return "stardewcraft.type.crop".equals(typeKey);
    }

    private static boolean isPreserveFishIngredient(String typeKey) {
        return "stardewcraft.type.fish".equals(typeKey)
                || "stardewcraft.type.crabpot".equals(typeKey)
                || "stardewcraft.type.legendary_fish".equals(typeKey);
    }

    private static boolean isDehydratorFruitIngredient(String typeKey, ResourceLocation id) {
        if (!"stardewcraft.type.crop".equals(typeKey)) {
            return false;
        }
        if (isGrape(id)) {
            return false;
        }
        return DehydratorIngredientHelper.isFruitCrop(id);
    }

    private static boolean isDehydratorMushroomIngredient(ResourceLocation id) {
        return DehydratorIngredientHelper.isMushroom(id);
    }

    private static boolean isSturgeon(ResourceLocation id) {
        return id != null && "sturgeon".equals(id.getPath());
    }

    private static boolean isGrape(ResourceLocation id) {
        return id != null && "grape".equals(id.getPath());
    }

    @SuppressWarnings("null")
    private static ItemStack buildPreserveVariant(PreserveType type, net.minecraft.world.item.Item ingredient, net.minecraft.world.item.Item baseItem) {
        ItemStack ingredientStack = new ItemStack(ingredient);
        ItemStack resultStack = new ItemStack(baseItem);
        PreservesItem.createFlavored(type, ingredientStack, resultStack);
        return resultStack;
    }

    private static net.minecraft.world.item.Item getPreserveBaseItem(PreserveType type) {
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

    private static final class PreserveSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
        @Override
        public Object getSubtypeData(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") UidContext context) {
            return PreservesItem.getSubtypeKey(stack);
        }

        @Override
        public String getLegacyStringSubtypeInfo(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") UidContext context) {
            return PreservesItem.getSubtypeKey(stack);
        }
    }
}
