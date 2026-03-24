package com.stardew.craft.entity;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.stardew.craft.entity.projectile.MeowmereProjectileEntity;
import com.stardew.craft.entity.projectile.ElfBladeLeafEntity;
import com.stardew.craft.entity.projectile.TideAnchorProjectileEntity;
import com.stardew.craft.entity.projectile.TemperedBilletProjectileEntity;
import com.stardew.craft.entity.effect.IceSpineEffectEntity;
import com.stardew.craft.entity.animal.BaseCoopAnimalEntity;
import com.stardew.craft.entity.animal.CowEntity;
import com.stardew.craft.entity.animal.DinosaurEntity;
import com.stardew.craft.entity.animal.DuckEntity;
import com.stardew.craft.entity.animal.GoatEntity;
import com.stardew.craft.entity.animal.GoldenChickenEntity;
import com.stardew.craft.entity.animal.OstrichEntity;
import com.stardew.craft.entity.animal.PigEntity;
import com.stardew.craft.entity.animal.RabbitEntity;
import com.stardew.craft.entity.animal.SheepEntity;
import com.stardew.craft.entity.animal.VoidChickenEntity;
import com.stardew.craft.entity.animal.WhiteChickenEntity;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.entity.seat.SofaSeatEntity;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public final class ModEntities {
	private ModEntities() {
	}

	@SuppressWarnings("null")
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, StardewCraft.MODID);

	public static final DeferredHolder<EntityType<?>, EntityType<MeowmereProjectileEntity>> MEOWMERE_PROJECTILE = ENTITY_TYPES.register(
			"meowmere_projectile",
			() -> EntityType.Builder.<MeowmereProjectileEntity>of(MeowmereProjectileEntity::new, MobCategory.MISC)
					.sized(0.3F, 0.3F) // 猫头大小
					.clientTrackingRange(4)
					.updateInterval(1)
					.build("meowmere_projectile")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<TideAnchorProjectileEntity>> TIDE_ANCHOR_PROJECTILE = ENTITY_TYPES.register(
			"tide_anchor_projectile",
			() -> EntityType.Builder.<TideAnchorProjectileEntity>of(TideAnchorProjectileEntity::new, MobCategory.MISC)
					.sized(0.5F, 0.5F)
					.clientTrackingRange(4)
					.updateInterval(1)
					.build("tide_anchor_projectile")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<TemperedBilletProjectileEntity>> TEMPERED_BILLET_PROJECTILE = ENTITY_TYPES.register(
			"tempered_billet_projectile",
			() -> EntityType.Builder.<TemperedBilletProjectileEntity>of(TemperedBilletProjectileEntity::new, MobCategory.MISC)
					.sized(0.35F, 0.35F)
					.clientTrackingRange(4)
					.updateInterval(1)
					.build("tempered_billet_projectile")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<ElfBladeLeafEntity>> ELF_BLADE_LEAF = ENTITY_TYPES.register(
			"elf_blade_leaf",
			() -> EntityType.Builder.<ElfBladeLeafEntity>of(ElfBladeLeafEntity::new, MobCategory.MISC)
					.sized(0.35F, 0.35F)
					.clientTrackingRange(4)
					.updateInterval(1)
					.build("elf_blade_leaf")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<IceSpineEffectEntity>> ICE_SPINE_EFFECT = ENTITY_TYPES.register(
			"ice_spine_effect",
			() -> EntityType.Builder.<IceSpineEffectEntity>of(IceSpineEffectEntity::new, MobCategory.MISC)
					.sized(0.8F, 0.8F)
					.clientTrackingRange(32)
					.updateInterval(1)
					.build("ice_spine_effect")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<FallenOakTreeEntity>> FALLEN_OAK_TREE = ENTITY_TYPES.register(
			"fallen_oak_tree",
			() -> EntityType.Builder.<FallenOakTreeEntity>of(FallenOakTreeEntity::new, MobCategory.MISC)
					// Needs a real size, otherwise frustum culling will make the entity effectively invisible.
					.sized(6.0F, 6.0F)
					.clientTrackingRange(64)
					.updateInterval(1)
					.build("fallen_oak_tree")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<DuckEntity>> DUCK = ENTITY_TYPES.register(
			"duck",
			() -> EntityType.Builder.<DuckEntity>of(DuckEntity::new, MobCategory.CREATURE)
					.sized(0.7F, 0.8F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("duck")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<WhiteChickenEntity>> WHITE_CHICKEN = ENTITY_TYPES.register(
			"white_chicken",
			() -> EntityType.Builder.<WhiteChickenEntity>of(WhiteChickenEntity::new, MobCategory.CREATURE)
					.sized(0.7F, 0.8F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("white_chicken")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<GoldenChickenEntity>> GOLDEN_CHICKEN = ENTITY_TYPES.register(
			"golden_chicken",
			() -> EntityType.Builder.<GoldenChickenEntity>of(GoldenChickenEntity::new, MobCategory.CREATURE)
					.sized(0.7F, 0.8F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("golden_chicken")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<VoidChickenEntity>> VOID_CHICKEN = ENTITY_TYPES.register(
			"void_chicken",
			() -> EntityType.Builder.<VoidChickenEntity>of(VoidChickenEntity::new, MobCategory.CREATURE)
					.sized(0.7F, 0.8F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("void_chicken")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<RabbitEntity>> RABBIT = ENTITY_TYPES.register(
			"rabbit",
			() -> EntityType.Builder.<RabbitEntity>of(RabbitEntity::new, MobCategory.CREATURE)
					.sized(0.7F, 0.8F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("rabbit")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<OstrichEntity>> OSTRICH = ENTITY_TYPES.register(
			"ostrich",
			() -> EntityType.Builder.<OstrichEntity>of(OstrichEntity::new, MobCategory.CREATURE)
					.sized(0.9F, 1.5F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("ostrich")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<DinosaurEntity>> DINOSAUR = ENTITY_TYPES.register(
			"dinosaur",
			() -> EntityType.Builder.<DinosaurEntity>of(DinosaurEntity::new, MobCategory.CREATURE)
					.sized(0.9F, 1.2F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("dinosaur")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<CowEntity>> COW = ENTITY_TYPES.register(
			"cow",
			() -> EntityType.Builder.<CowEntity>of(CowEntity::new, MobCategory.CREATURE)
					.sized(0.9F, 1.3F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("cow")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<GoatEntity>> GOAT = ENTITY_TYPES.register(
			"goat",
			() -> EntityType.Builder.<GoatEntity>of(GoatEntity::new, MobCategory.CREATURE)
					.sized(0.9F, 1.3F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("goat")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<SheepEntity>> SHEEP = ENTITY_TYPES.register(
			"sheep",
			() -> EntityType.Builder.<SheepEntity>of(SheepEntity::new, MobCategory.CREATURE)
					.sized(0.9F, 1.3F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("sheep")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<PigEntity>> PIG = ENTITY_TYPES.register(
			"pig",
			() -> EntityType.Builder.<PigEntity>of(PigEntity::new, MobCategory.CREATURE)
					.sized(0.9F, 1.3F)
					.clientTrackingRange(8)
					.updateInterval(3)
					.build("pig")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<StardewNpcEntity>> STARDEW_NPC = ENTITY_TYPES.register(
			"stardew_npc",
			() -> EntityType.Builder.<StardewNpcEntity>of(StardewNpcEntity::new, MobCategory.CREATURE)
					.sized(0.8F, 1.8F)
					.clientTrackingRange(16)
					.updateInterval(2)
					.build("stardew_npc")
	);

	public static final DeferredHolder<EntityType<?>, EntityType<SofaSeatEntity>> SOFA_SEAT = ENTITY_TYPES.register(
			"sofa_seat",
			() -> EntityType.Builder.<SofaSeatEntity>of(SofaSeatEntity::new, MobCategory.MISC)
					.sized(0.01F, 0.01F)
					.clientTrackingRange(8)
					.updateInterval(1)
					.build("sofa_seat")
	);

	@SuppressWarnings("null")
	public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		event.put(DUCK.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(WHITE_CHICKEN.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(GOLDEN_CHICKEN.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(VOID_CHICKEN.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(RABBIT.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(OSTRICH.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(DINOSAUR.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(COW.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(GOAT.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(SHEEP.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(PIG.get(), BaseCoopAnimalEntity.createAttributes().build());
		event.put(STARDEW_NPC.get(), StardewNpcEntity.createAttributes().build());
	}

}
