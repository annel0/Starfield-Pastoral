package com.stardew.craft.entity.animal;

import com.stardew.craft.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class RabbitEntity extends BaseCoopAnimalEntity {
	private static final Ingredient BREED_INGREDIENT = Ingredient.of(Items.CARROT, Items.DANDELION);

	public RabbitEntity(EntityType<? extends Animal> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public CoopAnimalVariant getVariant() {
		return CoopAnimalVariant.RABBIT;
	}

	@Override
	protected Ingredient getBreedIngredient() {
		return BREED_INGREDIENT;
	}

	@Override
	protected EntityType<? extends Animal> getOffspringType() {
		return ModEntities.RABBIT.get();
	}
}
