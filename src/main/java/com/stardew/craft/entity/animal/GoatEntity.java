package com.stardew.craft.entity.animal;

import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.animal.model.FarmAnimalRecord;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.quality.QualityHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

@SuppressWarnings("null")
public class GoatEntity extends BaseCoopAnimalEntity {
    private static final Ingredient BREED_INGREDIENT = Ingredient.of(Items.WHEAT);

    public GoatEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public CoopAnimalVariant getVariant() {
        return CoopAnimalVariant.GOAT;
    }

    @Override
    protected Ingredient getBreedIngredient() {
        return BREED_INGREDIENT;
    }

    @Override
    protected EntityType<? extends Animal> getOffspringType() {
        return ModEntities.GOAT.get();
    }

    @Override
    public @Nonnull InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && !this.level().isClientSide) {
            ItemStack held = player.getItemInHand(hand);
            if (held.is(ModItems.MILK_PAIL.get()) && this.level() instanceof ServerLevel serverLevel && getManagedAnimalId() > 0L) {
                AnimalWorldData data = AnimalWorldData.get(serverLevel);
                FarmAnimalRecord record = data.getAnimal(getManagedAnimalId()).orElse(null);
                if (record != null && !record.isBaby() && !record.currentProduceId().isBlank()) {
                    ItemStack produce = resolveProduceStack(record);
                    if (!produce.isEmpty()) {
                        if (!player.addItem(produce.copy())) {
                            player.spawnAtLocation(produce, 0.0F);
                        }
                        record.setCurrentProduceId("");
                        record.setProduceQuality(0);
                        data.markChanged();
                        serverLevel.playSound(null, this.blockPosition(), SoundEvents.COW_MILK, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    private ItemStack resolveProduceStack(FarmAnimalRecord record) {
        ResourceLocation id = ResourceLocation.tryParse(record.currentProduceId());
        if (id == null) {
            return ItemStack.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(item);
        QualityHelper.setQuality(stack, record.produceQuality());
        return stack;
    }
}
