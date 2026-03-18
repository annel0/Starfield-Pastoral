package com.stardew.craft.blockentity;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
	private ModBlockEntities() {
	}

	@SuppressWarnings("null")
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
			DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, StardewCraft.MODID);

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TapperBlockEntity>> TAPPER =
			BLOCK_ENTITIES.register("tapper", () -> BlockEntityType.Builder.of(TapperBlockEntity::new, ModBlocks.TAPPER.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KegBlockEntity>> KEG =
			BLOCK_ENTITIES.register("keg", () -> BlockEntityType.Builder.of(KegBlockEntity::new, ModBlocks.KEG.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PreservesJarBlockEntity>> PRESERVES_JAR =
			BLOCK_ENTITIES.register("preserves_jar", () -> BlockEntityType.Builder.of(PreservesJarBlockEntity::new, ModBlocks.PRESERVES_JAR.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DehydratorBlockEntity>> DEHYDRATOR =
			BLOCK_ENTITIES.register("dehydrator", () -> BlockEntityType.Builder.of(DehydratorBlockEntity::new, ModBlocks.DEHYDRATOR.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BaitMakerBlockEntity>> BAIT_MAKER =
			BLOCK_ENTITIES.register("bait_maker", () -> BlockEntityType.Builder.of(BaitMakerBlockEntity::new, ModBlocks.BAIT_MAKER.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FishSmokerBlockEntity>> FISH_SMOKER =
			BLOCK_ENTITIES.register("fish_smoker", () -> BlockEntityType.Builder.of(FishSmokerBlockEntity::new, ModBlocks.FISH_SMOKER.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalariumBlockEntity>> CRYSTALARIUM =
			BLOCK_ENTITIES.register("crystalarium", () -> BlockEntityType.Builder.of(CrystalariumBlockEntity::new, ModBlocks.CRYSTALARIUM.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SeedMakerBlockEntity>> SEED_MAKER =
			BLOCK_ENTITIES.register("seed_maker", () -> BlockEntityType.Builder.of(SeedMakerBlockEntity::new, ModBlocks.SEED_MAKER.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FurnaceBlockEntity>> FURNACE =
			BLOCK_ENTITIES.register("furnace", () -> BlockEntityType.Builder.of(FurnaceBlockEntity::new, ModBlocks.FURNACE.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CharcoalKilnBlockEntity>> CHARCOAL_KILN =
			BLOCK_ENTITIES.register("charcoal_kiln", () -> BlockEntityType.Builder.of(CharcoalKilnBlockEntity::new, ModBlocks.CHARCOAL_KILN.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LightningRodBlockEntity>> LIGHTNING_ROD =
			BLOCK_ENTITIES.register("lightning_rod", () -> BlockEntityType.Builder.of(LightningRodBlockEntity::new, ModBlocks.LIGHTNING_ROD.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL =
			BLOCK_ENTITIES.register("solar_panel", () -> BlockEntityType.Builder.of(SolarPanelBlockEntity::new, ModBlocks.SOLAR_PANEL.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CaskBlockEntity>> CASK =
			BLOCK_ENTITIES.register("cask", () -> BlockEntityType.Builder.of(CaskBlockEntity::new, ModBlocks.CASK.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CheesePressBlockEntity>> CHEESE_PRESS =
			BLOCK_ENTITIES.register("cheese_press", () -> BlockEntityType.Builder.of(CheesePressBlockEntity::new, ModBlocks.CHEESE_PRESS.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MayonnaiseMachineBlockEntity>> MAYONNAISE_MACHINE =
			BLOCK_ENTITIES.register("mayonnaise_machine", () -> BlockEntityType.Builder.of(MayonnaiseMachineBlockEntity::new, ModBlocks.MAYONNAISE_MACHINE.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IncubatorBlockEntity>> INCUBATOR =
			BLOCK_ENTITIES.register("incubator", () -> BlockEntityType.Builder.of(IncubatorBlockEntity::new, ModBlocks.INCUBATOR.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OilMakerBlockEntity>> OIL_MAKER =
			BLOCK_ENTITIES.register("oil_maker", () -> BlockEntityType.Builder.of(OilMakerBlockEntity::new, ModBlocks.OIL_MAKER.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LoomBlockEntity>> LOOM =
			BLOCK_ENTITIES.register("loom", () -> BlockEntityType.Builder.of(LoomBlockEntity::new, ModBlocks.LOOM.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BeeHouseBlockEntity>> BEE_HOUSE =
			BLOCK_ENTITIES.register("bee_house", () -> BlockEntityType.Builder.of(BeeHouseBlockEntity::new, ModBlocks.BEE_HOUSE.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrabPotBlockEntity>> CRAB_POT =
			BLOCK_ENTITIES.register("crab_pot", () -> BlockEntityType.Builder.of(CrabPotBlockEntity::new, ModBlocks.CRAB_POT.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WormBinBlockEntity>> WORM_BIN =
			BLOCK_ENTITIES.register("worm_bin", () -> BlockEntityType.Builder.of(WormBinBlockEntity::new, ModBlocks.WORM_BIN.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FeedTroughBlockEntity>> FEED_TROUGH =
			BLOCK_ENTITIES.register("feed_trough", () -> BlockEntityType.Builder.of(FeedTroughBlockEntity::new, ModBlocks.FEED_TROUGH.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoFeedTroughBlockEntity>> AUTOFEED_TROUGH =
			BLOCK_ENTITIES.register("autofeed_trough", () -> BlockEntityType.Builder.of(AutoFeedTroughBlockEntity::new, ModBlocks.AUTOFEED_TROUGH.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoGrabberBlockEntity>> AUTO_GRABBER =
			BLOCK_ENTITIES.register("auto_grabber", () -> BlockEntityType.Builder.of(AutoGrabberBlockEntity::new, ModBlocks.AUTO_GRABBER.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoPetterBlockEntity>> AUTO_PETTER =
			BLOCK_ENTITIES.register("auto_petter", () -> BlockEntityType.Builder.of(AutoPetterBlockEntity::new, ModBlocks.AUTO_PETTER.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WoodenChestBlockEntity>> WOODEN_CHEST =
			BLOCK_ENTITIES.register("wooden_chest", () -> BlockEntityType.Builder.of(WoodenChestBlockEntity::new, ModBlocks.WOODEN_CHEST.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StoneChestBlockEntity>> STONE_CHEST =
			BLOCK_ENTITIES.register("stone_chest", () -> BlockEntityType.Builder.of(StoneChestBlockEntity::new, ModBlocks.STONE_CHEST.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShippingBinBlockEntity>> SHIPPING_BIN =
			BLOCK_ENTITIES.register("shipping_bin", () -> BlockEntityType.Builder.of(ShippingBinBlockEntity::new, ModBlocks.SHIPPING_BIN.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeaterBlockEntity>> HEATER =
			BLOCK_ENTITIES.register("heater", () -> BlockEntityType.Builder.of(HeaterBlockEntity::new, ModBlocks.HEATER.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HayHopperBlockEntity>> HAY_HOPPER =
			BLOCK_ENTITIES.register("hay_hopper", () -> BlockEntityType.Builder.of(HayHopperBlockEntity::new, ModBlocks.HAY_HOPPER.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DeluxeWormBinBlockEntity>> DELUXE_WORM_BIN =
			BLOCK_ENTITIES.register("deluxe_worm_bin", () -> BlockEntityType.Builder.of(DeluxeWormBinBlockEntity::new, ModBlocks.DELUXE_WORM_BIN.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnimalProduceSpotBlockEntity>> ANIMAL_PRODUCE_SPOT =
			BLOCK_ENTITIES.register("animal_produce_spot", () -> BlockEntityType.Builder.of(AnimalProduceSpotBlockEntity::new, ModBlocks.ANIMAL_PRODUCE_SPOT.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecorBlockEntity>> DECOR_BLOCK =
			BLOCK_ENTITIES.register("decor_block", () -> BlockEntityType.Builder.of(DecorBlockEntity::new, ModBlocks.WALLPAPER_BLOCK.get(), ModBlocks.FLOORING_BLOCK.get()).build(null));

	@SuppressWarnings("null")
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecorAnchorBlockEntity>> DECOR_ANCHOR =
			BLOCK_ENTITIES.register("decor_anchor", () -> BlockEntityType.Builder.of(DecorAnchorBlockEntity::new, ModBlocks.DECOR_ANCHOR.get()).build(null));
}
