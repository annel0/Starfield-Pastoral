package com.stardew.craft.client;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.tool.FishingRodItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class ModItemProperties {
	private static final ResourceLocation CAST = ResourceLocation.fromNamespaceAndPath("minecraft", "cast");

	private ModItemProperties() {
	}

	@SuppressWarnings("unused")
	private static int debugTick = 0;

	@SuppressWarnings("null")
	public static void register() {
		StardewCraft.LOGGER.info("Registering item properties");

		var castProperty = (net.minecraft.client.renderer.item.ClampedItemPropertyFunction) (stack, level, entity, seed) -> {
			if (!(entity instanceof Player player)) {
				return 0.0f;
			}

			// Prefer our own cast flag (drives first-person animation and can flip immediately client-side).
			if (stack.getItem() instanceof FishingRodItem) {
				boolean castActive = FishingRodItem.isCastActive(stack);
				if (castActive) {
					return 1.0f;
				}
			}

			// Fallback: derive from bobber state.
			return FishingRodItem.isBobberOut(player) ? 1.0f : 0.0f;
		};

		ItemProperties.register(ModItems.FISHING_ROD.get(), CAST, castProperty);
		ItemProperties.register(ModItems.TRAINING_ROD.get(), CAST, castProperty);
		ItemProperties.register(ModItems.FIBERGLASS_ROD.get(), CAST, castProperty);
		ItemProperties.register(ModItems.IRIDIUM_ROD.get(), CAST, castProperty);
		ItemProperties.register(ModItems.ADVANCED_IRIDIUM_ROD.get(), CAST, castProperty);
	}
}
