package com.stardew.craft.client.tooltip;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.tooltip.MaxChargeRangeTooltipComponent;
import com.stardew.craft.tooltip.WaterAmountTooltipComponent;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = StardewCraft.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class StardewTooltipComponents {
    private StardewTooltipComponents() {
    }

    @SubscribeEvent
    public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(WaterAmountTooltipComponent.class, WaterAmountClientTooltipComponent::new);
        event.register(MaxChargeRangeTooltipComponent.class, MaxChargeRangeClientTooltipComponent::new);
    }
}
