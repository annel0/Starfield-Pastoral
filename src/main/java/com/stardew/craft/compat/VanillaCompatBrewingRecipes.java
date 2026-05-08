package com.stardew.craft.compat;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class VanillaCompatBrewingRecipes {

    private static final TagKey<Item> RABBIT_FEET = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "vanilla_compat/rabbit_feet"));

    private VanillaCompatBrewingRecipes() {}

    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        Ingredient rabbitFeet = Ingredient.of(RABBIT_FEET);
        event.getBuilder().addRecipe(new TaggedPotionRecipe(Potions.WATER, rabbitFeet, Potions.MUNDANE));
        event.getBuilder().addRecipe(new TaggedPotionRecipe(Potions.AWKWARD, rabbitFeet, Potions.LEAPING));
    }

    private record TaggedPotionRecipe(
            Holder<Potion> inputPotion,
            Ingredient ingredient,
            Holder<Potion> outputPotion
    ) implements IBrewingRecipe {

        @Override
        public boolean isInput(@Nonnull ItemStack stack) {
            return getPotion(stack).map(inputPotion::equals).orElse(false);
        }

        @Override
        public boolean isIngredient(@Nonnull ItemStack stack) {
            return ingredient.test(stack);
        }

        @Override
        public ItemStack getOutput(@Nonnull ItemStack input, @Nonnull ItemStack ingredientStack) {
            if (!isInput(input) || !isIngredient(ingredientStack)) {
                return ItemStack.EMPTY;
            }
            return PotionContents.createItemStack(input.getItem(), outputPotion);
        }

        private static Optional<Holder<Potion>> getPotion(ItemStack stack) {
            return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
        }
    }
}