package com.stardew.craft.client;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.blockentity.ModBlockEntities;
import com.stardew.craft.client.gui.ElevatorScreen;
import com.stardew.craft.client.gui.MineExitScreen;
import com.stardew.craft.client.gui.CoopManagerScreen;
import com.stardew.craft.client.gui.BarnManagerScreen;
import com.stardew.craft.client.gui.AnimalQueryScreen;
import com.stardew.craft.client.gui.ShippingBinScreen;
import com.stardew.craft.client.gui.StoneChestScreen;
import com.stardew.craft.client.gui.WoodenChestScreen;
import com.stardew.craft.client.render.BeeHouseBlockEntityRenderer;
import com.stardew.craft.client.render.CrabPotBlockEntityRenderer;
import com.stardew.craft.client.render.CharcoalKilnBlockEntityRenderer;
import com.stardew.craft.client.render.CheesePressBlockEntityRenderer;
import com.stardew.craft.client.render.FurnaceBlockEntityRenderer;
import com.stardew.craft.client.render.FishSmokerBlockEntityRenderer;
import com.stardew.craft.client.render.BaitMakerBlockEntityRenderer;
import com.stardew.craft.client.render.KegBlockEntityRenderer;
import com.stardew.craft.client.render.LightningRodBlockEntityRenderer;
import com.stardew.craft.client.render.MayonnaiseMachineBlockEntityRenderer;
import com.stardew.craft.client.render.OilMakerBlockEntityRenderer;
import com.stardew.craft.client.render.IncubatorBlockEntityRenderer;
import com.stardew.craft.client.render.PreservesJarBlockEntityRenderer;
import com.stardew.craft.client.render.CrystalariumBlockEntityRenderer;
import com.stardew.craft.client.render.SeedMakerBlockEntityRenderer;
import com.stardew.craft.client.render.CaskBlockEntityRenderer;
import com.stardew.craft.client.render.DehydratorBlockEntityRenderer;
import com.stardew.craft.client.render.DeluxeWormBinBlockEntityRenderer;
import com.stardew.craft.client.render.AutoPetterBlockEntityRenderer;
import com.stardew.craft.client.render.HeaterBlockEntityRenderer;
import com.stardew.craft.client.render.ShippingBinBlockEntityRenderer;
import com.stardew.craft.client.render.StoneChestBlockEntityRenderer;
import com.stardew.craft.client.render.WoodenChestBlockEntityRenderer;
import com.stardew.craft.client.render.SolarPanelBlockEntityRenderer;
import com.stardew.craft.client.render.TapperBlockEntityRenderer;
import com.stardew.craft.client.render.LoomBlockEntityRenderer;
import com.stardew.craft.client.render.WormBinBlockEntityRenderer;
import com.stardew.craft.client.render.FeedTroughBlockEntityRenderer;
import com.stardew.craft.client.render.AutoFeedTroughBlockEntityRenderer;
import com.stardew.craft.client.render.AnimalProduceSpotBlockEntityRenderer;
import com.stardew.craft.menu.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.client.renderer.entity.MeowmereProjectileRenderer;
import com.stardew.craft.client.renderer.entity.ElfBladeLeafRenderer;
import com.stardew.craft.client.renderer.entity.TideAnchorProjectileRenderer;
import com.stardew.craft.client.renderer.entity.TemperedBilletProjectileRenderer;
import com.stardew.craft.client.renderer.entity.IceSpineEffectRenderer;
import com.stardew.craft.client.renderer.entity.CoopAnimalGeoRenderer;
import com.stardew.craft.client.renderer.layer.YetiFreezeLayer;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = StardewCraft.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClientSetup {
	private ModClientSetup() {
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(ModItemProperties::register);
	}

	@SuppressWarnings("null")
	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.MEOWMERE_PROJECTILE.get(), MeowmereProjectileRenderer::new);
		event.registerEntityRenderer(ModEntities.TIDE_ANCHOR_PROJECTILE.get(), TideAnchorProjectileRenderer::new);
		event.registerEntityRenderer(ModEntities.TEMPERED_BILLET_PROJECTILE.get(), TemperedBilletProjectileRenderer::new);
		event.registerEntityRenderer(ModEntities.ELF_BLADE_LEAF.get(), ElfBladeLeafRenderer::new);
		event.registerEntityRenderer(ModEntities.ICE_SPINE_EFFECT.get(), IceSpineEffectRenderer::new);
		event.registerEntityRenderer(ModEntities.DUCK.get(), CoopAnimalGeoRenderer::new);
		event.registerEntityRenderer(ModEntities.WHITE_CHICKEN.get(), CoopAnimalGeoRenderer::new);
		event.registerEntityRenderer(ModEntities.GOLDEN_CHICKEN.get(), CoopAnimalGeoRenderer::new);
		event.registerEntityRenderer(ModEntities.VOID_CHICKEN.get(), CoopAnimalGeoRenderer::new);
		event.registerEntityRenderer(ModEntities.RABBIT.get(), CoopAnimalGeoRenderer::new);
		event.registerEntityRenderer(ModEntities.OSTRICH.get(), CoopAnimalGeoRenderer::new);
		event.registerEntityRenderer(ModEntities.DINOSAUR.get(), CoopAnimalGeoRenderer::new);
		event.registerEntityRenderer(ModEntities.COW.get(), CoopAnimalGeoRenderer::new);
		event.registerEntityRenderer(ModEntities.GOAT.get(), CoopAnimalGeoRenderer::new);
		event.registerEntityRenderer(ModEntities.SHEEP.get(), CoopAnimalGeoRenderer::new);
		event.registerEntityRenderer(ModEntities.PIG.get(), CoopAnimalGeoRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.TAPPER.get(), TapperBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.KEG.get(), KegBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.PRESERVES_JAR.get(), PreservesJarBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.DEHYDRATOR.get(), DehydratorBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.BAIT_MAKER.get(), BaitMakerBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.CASK.get(), CaskBlockEntityRenderer::new);
				event.registerBlockEntityRenderer(ModBlockEntities.CHEESE_PRESS.get(), CheesePressBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.LOOM.get(), LoomBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.BEE_HOUSE.get(), BeeHouseBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.CRAB_POT.get(), CrabPotBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.FISH_SMOKER.get(), FishSmokerBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.CRYSTALARIUM.get(), CrystalariumBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.SEED_MAKER.get(), SeedMakerBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.FURNACE.get(), FurnaceBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.CHARCOAL_KILN.get(), CharcoalKilnBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.LIGHTNING_ROD.get(), LightningRodBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.SOLAR_PANEL.get(), SolarPanelBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.MAYONNAISE_MACHINE.get(), MayonnaiseMachineBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.INCUBATOR.get(), IncubatorBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.OIL_MAKER.get(), OilMakerBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.WORM_BIN.get(), WormBinBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.FEED_TROUGH.get(), FeedTroughBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.AUTOFEED_TROUGH.get(), AutoFeedTroughBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.AUTO_PETTER.get(), AutoPetterBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.WOODEN_CHEST.get(), WoodenChestBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.STONE_CHEST.get(), StoneChestBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.SHIPPING_BIN.get(), ShippingBinBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.HEATER.get(), HeaterBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.DELUXE_WORM_BIN.get(), DeluxeWormBinBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.ANIMAL_PRODUCE_SPOT.get(), AnimalProduceSpotBlockEntityRenderer::new);
	}

	@SuppressWarnings("null")
	@SubscribeEvent
	public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
		event.register(ModMenuTypes.MINE_EXIT.get(), MineExitScreen::new);
		event.register(ModMenuTypes.ELEVATOR.get(), ElevatorScreen::new);
		event.register(ModMenuTypes.COOP_MANAGER.get(), CoopManagerScreen::new);
		event.register(ModMenuTypes.BARN_MANAGER.get(), BarnManagerScreen::new);
		event.register(ModMenuTypes.ANIMAL_QUERY.get(), AnimalQueryScreen::new);
		event.register(ModMenuTypes.TREASURE_CHEST.get(), com.stardew.craft.client.fishing.TreasureChestScreen::new);
		event.register(ModMenuTypes.COOKING_POT.get(), com.stardew.craft.client.gui.CookingPotScreen::new);
		event.register(ModMenuTypes.WOODEN_CHEST.get(), WoodenChestScreen::new);
		event.register(ModMenuTypes.STONE_CHEST.get(), StoneChestScreen::new);
		event.register(ModMenuTypes.SHIPPING_BIN.get(), ShippingBinScreen::new);
	}

	@SuppressWarnings("null")
	@SubscribeEvent
	public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
		for (net.minecraft.world.entity.EntityType<?> type : event.getEntityTypes()) {
			net.minecraft.client.renderer.entity.EntityRenderer<?> renderer = event.getRenderer(type);
			addFreezeLayer(renderer);
		}

		for (net.minecraft.client.resources.PlayerSkin.Model skin : event.getSkins()) {
			net.minecraft.client.renderer.entity.EntityRenderer<? extends net.minecraft.world.entity.player.Player> renderer = event.getSkin(skin);
			addFreezeLayer(renderer);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void addFreezeLayer(net.minecraft.client.renderer.entity.EntityRenderer<?> renderer) {
		if (renderer instanceof net.minecraft.client.renderer.entity.LivingEntityRenderer<?, ?> livingRenderer) {
			livingRenderer.addLayer(new YetiFreezeLayer((net.minecraft.client.renderer.entity.LivingEntityRenderer) livingRenderer));
		}
	}
}
