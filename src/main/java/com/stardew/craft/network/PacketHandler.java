package com.stardew.craft.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import com.stardew.craft.fishing.network.FishingStartPayload;
import com.stardew.craft.fishing.network.FishingResultPayload;
import com.stardew.craft.fishing.network.FishingCatchVisualPayload;
import com.stardew.craft.fishing.network.FishingFailVisualPayload;
import com.stardew.craft.fishing.network.FishingRodCastStatePayload;
import com.stardew.craft.fishing.network.StartMinigamePayload;
import com.stardew.craft.fishing.network.FishingBitePromptPayload;
import com.stardew.craft.fishing.network.FishingHookedAnimPayload;
import com.stardew.craft.combat.network.DamageNumberPayload;
import com.stardew.craft.combat.network.WeaponSkillAnimPayload;
import com.stardew.craft.combat.network.WeaponSkillCounterAnimPayload;
import com.stardew.craft.combat.network.SkillFailFeedbackPayload;
import com.stardew.craft.combat.network.SilverSaberFoldbackPayload;
import com.stardew.craft.combat.network.ForestBlessingPayload;
import com.stardew.craft.combat.network.SteelSpineFuryPayload;
import com.stardew.craft.combat.network.WindSpirePayload;
import com.stardew.craft.combat.network.SteelSpineFuryEnterPayload;
import com.stardew.craft.combat.network.SteelSpineFuryHitPayload;
import com.stardew.craft.combat.network.SteelSpineFuryStrikePayload;
import com.stardew.craft.combat.network.CarvingKnifeThrustStrikePayload;
import com.stardew.craft.combat.network.SkillCooldownSyncPayload;
import com.stardew.craft.combat.network.WeaponSkillUsePayload;
import com.stardew.craft.combat.network.ObsidianCrackPayload;
import com.stardew.craft.combat.network.ObsidianResonanceSyncPayload;
import com.stardew.craft.combat.network.OssifiedExecutionCirclePayload;
import com.stardew.craft.combat.network.OssifiedMarkPayload;
import com.stardew.craft.combat.network.ElfBladePayload;
import com.stardew.craft.combat.network.ElfBladeMarkPayload;
import com.stardew.craft.combat.network.HolyBladeRingPayload;
import com.stardew.craft.combat.network.TemplarJudgementImpactPayload;
import com.stardew.craft.combat.network.TemplarMarkPayload;
import com.stardew.craft.combat.network.TemplarVowPayload;
import com.stardew.craft.combat.network.InsectEyeStancePayload;
import com.stardew.craft.combat.network.TideMarkPayload;
import com.stardew.craft.combat.network.WaterRingEffectPayload;
import com.stardew.craft.combat.network.YetiToothMarkPayload;
import com.stardew.craft.combat.network.YetiFreezePayload;
import com.stardew.craft.combat.network.LavaKatanaMarkPayload;
import com.stardew.craft.combat.network.LavaKatanaReverbPayload;
import com.stardew.craft.combat.network.GalaxyDaggerMarkPayload;
import com.stardew.craft.combat.network.InfinityDaggerMarkPayload;
import com.stardew.craft.combat.network.DwarfDaggerRushPayload;
import com.stardew.craft.combat.network.DragontoothShivBreathPayload;
import com.stardew.craft.combat.network.WickedKrisPoisonStatusPayload;
import com.stardew.craft.combat.network.SteelFalchionLineCreatePayload;
import com.stardew.craft.combat.network.SteelFalchionLinePointPayload;
import com.stardew.craft.combat.network.SteelFalchionLinePulsePayload;
import com.stardew.craft.combat.network.SteelFalchionLineBurstPayload;
import com.stardew.craft.combat.network.SteelFalchionTracePayload;
import com.stardew.craft.combat.network.DarkSwordBloodDebtPayload;
import com.stardew.craft.combat.network.DarkSwordBloodMoonPayload;
import com.stardew.craft.combat.network.SingularityPayload;
import com.stardew.craft.combat.network.StartrailPayload;
import com.stardew.craft.combat.network.ShockwaveRingPayload;
import com.stardew.craft.combat.network.AccretionDiskPayload;
import com.stardew.craft.combat.network.SingularityCorePayload;
import com.stardew.craft.combat.network.SingularityRunePayload;
import com.stardew.craft.combat.network.RiftPathPayload;
import com.stardew.craft.combat.network.StarfallMeteorPayload;
import com.stardew.craft.combat.network.BlackHolePostPayload;
import com.stardew.craft.combat.network.BurglarShankLootPayload;
import com.stardew.craft.combat.network.CrystalDaggerLayerPayload;
import com.stardew.craft.combat.network.CrystalDaggerBurstPayload;
import com.stardew.craft.combat.network.StarfallShockwavePostPayload;
import com.stardew.craft.combat.network.TremorBlockPayload;
import com.stardew.craft.combat.network.BrokenTridentCatchPayload;
import com.stardew.craft.combat.network.BrokenTridentThrustStrikePayload;
import com.stardew.craft.combat.network.IridiumNeedleCritPayload;
import com.stardew.craft.combat.network.IridiumNeedleFrenzyPayload;
import com.stardew.craft.combat.network.IridiumNeedleThrustStrikePayload;
import com.stardew.craft.network.payload.StoneChestColorSelectPayload;
import com.stardew.craft.network.payload.WoodenChestColorSelectPayload;

@SuppressWarnings("null")
public class PacketHandler {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        
        // 客户端 -> 服务端
        registrar.playToServer(
            GrowCropsPayload.TYPE,
            GrowCropsPayload.STREAM_CODEC,
            GrowCropsPayload::handle
        );

        registrar.playToServer(
            GrowTreesPayload.TYPE,
            GrowTreesPayload.STREAM_CODEC,
            GrowTreesPayload::handle
        );

        registrar.playToServer(
            AdvanceUtilitiesPayload.TYPE,
            AdvanceUtilitiesPayload.STREAM_CODEC,
            AdvanceUtilitiesPayload::handle
        );

		registrar.playToServer(
			ScytheSwingPayload.TYPE,
			ScytheSwingPayload.STREAM_CODEC,
			ScytheSwingPayload::handle
		);

        registrar.playToServer(
            FishingResultPayload.TYPE,
            FishingResultPayload.STREAM_CODEC,
            FishingResultPayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.OpenTreasureChestRequestPayload.TYPE,
            com.stardew.craft.network.payload.OpenTreasureChestRequestPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.OpenTreasureChestRequestPayload::handle
        );

        registrar.playToServer(
            WeaponSkillUsePayload.TYPE,
            WeaponSkillUsePayload.STREAM_CODEC,
            WeaponSkillUsePayload::handle
        );

        registrar.playToServer(
            WoodenChestColorSelectPayload.TYPE,
            WoodenChestColorSelectPayload.STREAM_CODEC,
            WoodenChestColorSelectPayload::handle
        );

        registrar.playToServer(
            StoneChestColorSelectPayload.TYPE,
            StoneChestColorSelectPayload.STREAM_CODEC,
            StoneChestColorSelectPayload::handle
        );
        
        // 服务端 -> 客户端
        registrar.playToClient(
            PlayerDataSyncPacket.TYPE,
            PlayerDataSyncPacket.STREAM_CODEC,
            PlayerDataSyncPacket::handle
        );
        
        registrar.playToClient(
            TimeSyncPacket.TYPE,
            TimeSyncPacket.STREAM_CODEC,
            TimeSyncPacket::handle
        );

        registrar.playToClient(
            MiningFloorSyncPacket.TYPE,
            MiningFloorSyncPacket.STREAM_CODEC,
            MiningFloorSyncPacket::handle
        );

        registrar.playToClient(
            MissingItemHudMessagePacket.TYPE,
            MissingItemHudMessagePacket.STREAM_CODEC,
            MissingItemHudMessagePacket::handle
        );

        registrar.playToClient(
            HayHarvestHudMessagePacket.TYPE,
            HayHarvestHudMessagePacket.STREAM_CODEC,
            HayHarvestHudMessagePacket::handle
        );

        registrar.playToClient(
            com.stardew.craft.network.payload.SkillExperienceGainPayload.TYPE,
            com.stardew.craft.network.payload.SkillExperienceGainPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.SkillExperienceGainPayload::handle
        );

        registrar.playToClient(
            MuseumDonationSyncPacket.TYPE,
            MuseumDonationSyncPacket.STREAM_CODEC,
            MuseumDonationSyncPacket::handle
        );

		registrar.playToClient(
			FishingStartPayload.TYPE,
			FishingStartPayload.STREAM_CODEC,
			FishingStartPayload::handle
		);

		registrar.playToClient(
			StartMinigamePayload.TYPE,
			StartMinigamePayload.STREAM_CODEC,
			StartMinigamePayload::handle
		);

        registrar.playToClient(
            FishingBitePromptPayload.TYPE,
            FishingBitePromptPayload.STREAM_CODEC,
            FishingBitePromptPayload::handle
        );

        registrar.playToClient(
            FishingHookedAnimPayload.TYPE,
            FishingHookedAnimPayload.STREAM_CODEC,
            FishingHookedAnimPayload::handle
        );

        registrar.playToClient(
            FishingCatchVisualPayload.TYPE,
            FishingCatchVisualPayload.STREAM_CODEC,
            FishingCatchVisualPayload::handle
        );
        
        registrar.playToClient(
            com.stardew.craft.network.payload.OpenTreasureChestPayload.TYPE,
            com.stardew.craft.network.payload.OpenTreasureChestPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.OpenTreasureChestPayload::handle
        );

		registrar.playToClient(
			FishingFailVisualPayload.TYPE,
			FishingFailVisualPayload.STREAM_CODEC,
			FishingFailVisualPayload::handle
		);

        registrar.playToClient(
            FishingRodCastStatePayload.TYPE,
            FishingRodCastStatePayload.STREAM_CODEC,
            FishingRodCastStatePayload::handle
        );

        registrar.playToClient(
            DamageNumberPayload.TYPE,
            DamageNumberPayload.STREAM_CODEC,
            DamageNumberPayload::handle
        );

        registrar.playToClient(
            BurglarShankLootPayload.TYPE,
            BurglarShankLootPayload.STREAM_CODEC,
            BurglarShankLootPayload::handle
        );

        registrar.playToClient(
            CrystalDaggerLayerPayload.TYPE,
            CrystalDaggerLayerPayload.STREAM_CODEC,
            CrystalDaggerLayerPayload::handle
        );

        registrar.playToClient(
            CrystalDaggerBurstPayload.TYPE,
            CrystalDaggerBurstPayload.STREAM_CODEC,
            CrystalDaggerBurstPayload::handle
        );

        registrar.playToClient(
            BrokenTridentCatchPayload.TYPE,
            BrokenTridentCatchPayload.STREAM_CODEC,
            BrokenTridentCatchPayload::handle
        );

        registrar.playToClient(
            BrokenTridentThrustStrikePayload.TYPE,
            BrokenTridentThrustStrikePayload.STREAM_CODEC,
            BrokenTridentThrustStrikePayload::handle
        );

        registrar.playToClient(
            IridiumNeedleCritPayload.TYPE,
            IridiumNeedleCritPayload.STREAM_CODEC,
            IridiumNeedleCritPayload::handle
        );

        registrar.playToClient(
            IridiumNeedleFrenzyPayload.TYPE,
            IridiumNeedleFrenzyPayload.STREAM_CODEC,
            IridiumNeedleFrenzyPayload::handle
        );

        registrar.playToClient(
            IridiumNeedleThrustStrikePayload.TYPE,
            IridiumNeedleThrustStrikePayload.STREAM_CODEC,
            IridiumNeedleThrustStrikePayload::handle
        );

        registrar.playToClient(
            WeaponSkillAnimPayload.TYPE,
            WeaponSkillAnimPayload.STREAM_CODEC,
            WeaponSkillAnimPayload::handle
        );

        registrar.playToClient(
            WeaponSkillCounterAnimPayload.TYPE,
            WeaponSkillCounterAnimPayload.STREAM_CODEC,
            WeaponSkillCounterAnimPayload::handle
        );

        registrar.playToClient(
            SkillFailFeedbackPayload.TYPE,
            SkillFailFeedbackPayload.STREAM_CODEC,
            SkillFailFeedbackPayload::handle
        );

        registrar.playToClient(
            SilverSaberFoldbackPayload.TYPE,
            SilverSaberFoldbackPayload.STREAM_CODEC,
            SilverSaberFoldbackPayload::handle
        );

        registrar.playToClient(
            ForestBlessingPayload.TYPE,
            ForestBlessingPayload.STREAM_CODEC,
            ForestBlessingPayload::handle
        );

        registrar.playToClient(
            SteelSpineFuryPayload.TYPE,
            SteelSpineFuryPayload.STREAM_CODEC,
            SteelSpineFuryPayload::handle
        );

        registrar.playToClient(
            WindSpirePayload.TYPE,
            WindSpirePayload.STREAM_CODEC,
            WindSpirePayload::handle
        );

        registrar.playToClient(
            SteelSpineFuryEnterPayload.TYPE,
            SteelSpineFuryEnterPayload.STREAM_CODEC,
            SteelSpineFuryEnterPayload::handle
        );

        registrar.playToClient(
            SteelSpineFuryHitPayload.TYPE,
            SteelSpineFuryHitPayload.STREAM_CODEC,
            SteelSpineFuryHitPayload::handle
        );

        registrar.playToClient(
            SteelSpineFuryStrikePayload.TYPE,
            SteelSpineFuryStrikePayload.STREAM_CODEC,
            SteelSpineFuryStrikePayload::handle
        );

        registrar.playToClient(
            CarvingKnifeThrustStrikePayload.TYPE,
            CarvingKnifeThrustStrikePayload.STREAM_CODEC,
            CarvingKnifeThrustStrikePayload::handle
        );

        registrar.playToClient(
            SkillCooldownSyncPayload.TYPE,
            SkillCooldownSyncPayload.STREAM_CODEC,
            SkillCooldownSyncPayload::handle
        );

        registrar.playToClient(
            ObsidianCrackPayload.TYPE,
            ObsidianCrackPayload.STREAM_CODEC,
            ObsidianCrackPayload::handle
        );

        registrar.playToClient(
            ShockwaveRingPayload.TYPE,
            ShockwaveRingPayload.STREAM_CODEC,
            ShockwaveRingPayload::handle
        );

        registrar.playToClient(
            AccretionDiskPayload.TYPE,
            AccretionDiskPayload.STREAM_CODEC,
            AccretionDiskPayload::handle
        );

        registrar.playToClient(
            SingularityCorePayload.TYPE,
            SingularityCorePayload.STREAM_CODEC,
            SingularityCorePayload::handle
        );

        registrar.playToClient(
            SingularityRunePayload.TYPE,
            SingularityRunePayload.STREAM_CODEC,
            SingularityRunePayload::handle
        );

        registrar.playToClient(
            RiftPathPayload.TYPE,
            RiftPathPayload.STREAM_CODEC,
            RiftPathPayload::handle
        );

        registrar.playToClient(
            StarfallMeteorPayload.TYPE,
            StarfallMeteorPayload.STREAM_CODEC,
            StarfallMeteorPayload::handle
        );

        registrar.playToClient(
            BlackHolePostPayload.TYPE,
            BlackHolePostPayload.STREAM_CODEC,
            BlackHolePostPayload::handle
        );

        registrar.playToClient(
            StarfallShockwavePostPayload.TYPE,
            StarfallShockwavePostPayload.STREAM_CODEC,
            StarfallShockwavePostPayload::handle
        );

        registrar.playToClient(
            TremorBlockPayload.TYPE,
            TremorBlockPayload.STREAM_CODEC,
            TremorBlockPayload::handle
        );

        registrar.playToClient(
            ObsidianResonanceSyncPayload.TYPE,
            ObsidianResonanceSyncPayload.STREAM_CODEC,
            ObsidianResonanceSyncPayload::handle
        );

        registrar.playToClient(
            ElfBladePayload.TYPE,
            ElfBladePayload.STREAM_CODEC,
            ElfBladePayload::handle
        );

        registrar.playToClient(
            TideMarkPayload.TYPE,
            TideMarkPayload.STREAM_CODEC,
            TideMarkPayload::handle
        );

        registrar.playToClient(
            GalaxyDaggerMarkPayload.TYPE,
            GalaxyDaggerMarkPayload.STREAM_CODEC,
            GalaxyDaggerMarkPayload::handle
        );

        registrar.playToClient(
            InfinityDaggerMarkPayload.TYPE,
            InfinityDaggerMarkPayload.STREAM_CODEC,
            InfinityDaggerMarkPayload::handle
        );

        registrar.playToClient(
            ElfBladeMarkPayload.TYPE,
            ElfBladeMarkPayload.STREAM_CODEC,
            ElfBladeMarkPayload::handle
        );

        registrar.playToClient(
            LavaKatanaMarkPayload.TYPE,
            LavaKatanaMarkPayload.STREAM_CODEC,
            LavaKatanaMarkPayload::handle
        );

        registrar.playToClient(
            OssifiedMarkPayload.TYPE,
            OssifiedMarkPayload.STREAM_CODEC,
            OssifiedMarkPayload::handle
        );

        registrar.playToClient(
            TemplarVowPayload.TYPE,
            TemplarVowPayload.STREAM_CODEC,
            TemplarVowPayload::handle
        );

        registrar.playToClient(
            InsectEyeStancePayload.TYPE,
            InsectEyeStancePayload.STREAM_CODEC,
            InsectEyeStancePayload::handle
        );

        registrar.playToClient(
            TemplarMarkPayload.TYPE,
            TemplarMarkPayload.STREAM_CODEC,
            TemplarMarkPayload::handle
        );

        registrar.playToClient(
            TemplarJudgementImpactPayload.TYPE,
            TemplarJudgementImpactPayload.STREAM_CODEC,
            TemplarJudgementImpactPayload::handle
        );

        registrar.playToClient(
            WaterRingEffectPayload.TYPE,
            WaterRingEffectPayload.STREAM_CODEC,
            WaterRingEffectPayload::handle
        );

        registrar.playToClient(
            OssifiedExecutionCirclePayload.TYPE,
            OssifiedExecutionCirclePayload.STREAM_CODEC,
            OssifiedExecutionCirclePayload::handle
        );

        registrar.playToClient(
            HolyBladeRingPayload.TYPE,
            HolyBladeRingPayload.STREAM_CODEC,
            HolyBladeRingPayload::handle
        );

        registrar.playToClient(
            YetiToothMarkPayload.TYPE,
            YetiToothMarkPayload.STREAM_CODEC,
            YetiToothMarkPayload::handle
        );

        registrar.playToClient(
            YetiFreezePayload.TYPE,
            YetiFreezePayload.STREAM_CODEC,
            YetiFreezePayload::handle
        );

        registrar.playToClient(
            LavaKatanaReverbPayload.TYPE,
            LavaKatanaReverbPayload.STREAM_CODEC,
            LavaKatanaReverbPayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.combat.network.DashMovementPayload.TYPE,
            com.stardew.craft.combat.network.DashMovementPayload.STREAM_CODEC,
            com.stardew.craft.combat.network.DashMovementPayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.combat.network.DwarfDaggerThrustPayload.TYPE,
            com.stardew.craft.combat.network.DwarfDaggerThrustPayload.STREAM_CODEC,
            com.stardew.craft.combat.network.DwarfDaggerThrustPayload::handle
        );

        registrar.playToClient(
            DwarfDaggerRushPayload.TYPE,
            DwarfDaggerRushPayload.STREAM_CODEC,
            DwarfDaggerRushPayload::handle
        );

        registrar.playToClient(
            DragontoothShivBreathPayload.TYPE,
            DragontoothShivBreathPayload.STREAM_CODEC,
            DragontoothShivBreathPayload::handle
        );

        registrar.playToClient(
            WickedKrisPoisonStatusPayload.TYPE,
            WickedKrisPoisonStatusPayload.STREAM_CODEC,
            WickedKrisPoisonStatusPayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.combat.network.DwarfFortressPayload.TYPE,
            com.stardew.craft.combat.network.DwarfFortressPayload.STREAM_CODEC,
            com.stardew.craft.combat.network.DwarfFortressPayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.combat.network.DragonBreathPayload.TYPE,
            com.stardew.craft.combat.network.DragonBreathPayload.STREAM_CODEC,
            com.stardew.craft.combat.network.DragonBreathPayload::handle
        );

        registrar.playToClient(
            StartrailPayload.TYPE,
            StartrailPayload.STREAM_CODEC,
            StartrailPayload::handle
        );

        registrar.playToClient(
            SingularityPayload.TYPE,
            SingularityPayload.STREAM_CODEC,
            SingularityPayload::handle
        );

        registrar.playToClient(
            SteelFalchionLineCreatePayload.TYPE,
            SteelFalchionLineCreatePayload.STREAM_CODEC,
            SteelFalchionLineCreatePayload::handle
        );

        registrar.playToClient(
            SteelFalchionLinePointPayload.TYPE,
            SteelFalchionLinePointPayload.STREAM_CODEC,
            SteelFalchionLinePointPayload::handle
        );

        registrar.playToClient(
            SteelFalchionLinePulsePayload.TYPE,
            SteelFalchionLinePulsePayload.STREAM_CODEC,
            SteelFalchionLinePulsePayload::handle
        );

        registrar.playToClient(
            SteelFalchionLineBurstPayload.TYPE,
            SteelFalchionLineBurstPayload.STREAM_CODEC,
            SteelFalchionLineBurstPayload::handle
        );

        registrar.playToClient(
            SteelFalchionTracePayload.TYPE,
            SteelFalchionTracePayload.STREAM_CODEC,
            SteelFalchionTracePayload::handle
        );

        registrar.playToClient(
            DarkSwordBloodDebtPayload.TYPE,
            DarkSwordBloodDebtPayload.STREAM_CODEC,
            DarkSwordBloodDebtPayload::handle
        );

        registrar.playToClient(
            DarkSwordBloodMoonPayload.TYPE,
            DarkSwordBloodMoonPayload.STREAM_CODEC,
            DarkSwordBloodMoonPayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.combat.network.FireRingEffectPayload.TYPE,
            com.stardew.craft.combat.network.FireRingEffectPayload.STREAM_CODEC,
            com.stardew.craft.combat.network.FireRingEffectPayload::handle
        );

        registrar.playToClient(
            FertilizerSyncPacket.TYPE,
            FertilizerSyncPacket.STREAM_CODEC,
            (packet, context) -> {
                context.enqueueWork(() -> {
                    if (packet.fertilizerType() != null) {
                        var type = packet.getFertilizerType();
                        if (type != null) {
                            com.stardew.craft.client.ClientFertilizerCache.setFertilizer(packet.pos(), type);
                            com.stardew.craft.StardewCraft.LOGGER.info("Client received fertilizer sync: {} at {}", type.getSerializedName(), packet.pos());
                        }
                    } else {
                        com.stardew.craft.client.ClientFertilizerCache.removeFertilizer(packet.pos());
                        com.stardew.craft.StardewCraft.LOGGER.info("Client removed fertilizer at {}", packet.pos());
                    }
                });
            }
        );

        registrar.playToClient(
            WeatherSyncPacket.TYPE,
            WeatherSyncPacket.STREAM_CODEC,
            (packet, context) -> {
                context.enqueueWork(() -> {
                    try {
                        net.minecraft.resources.ResourceLocation dimLoc = net.minecraft.resources.ResourceLocation.parse(packet.dimension());
                        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(
                            net.minecraft.core.registries.Registries.DIMENSION,
                            dimLoc
                        );
                        com.stardew.craft.weather.ClientWeatherCache.setWeather(dimKey, packet.weatherType(), packet.weatherForTomorrow());
                        com.stardew.craft.StardewCraft.LOGGER.info("Client received weather sync: {} (tomorrow: {})", packet.weatherType(), packet.weatherForTomorrow());
                    } catch (Exception e) {
                        com.stardew.craft.StardewCraft.LOGGER.error("Failed to parse weather sync packet", e);
                    }
                });
            }
        );

        // 矿井出口传送操作
        registrar.playToServer(
            com.stardew.craft.network.payload.MineExitActionPayload.TYPE,
            com.stardew.craft.network.payload.MineExitActionPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.MineExitActionPayload::handle
        );

        // 矿井电梯传送操作
        registrar.playToServer(
            com.stardew.craft.network.payload.ElevatorActionPayload.TYPE,
            com.stardew.craft.network.payload.ElevatorActionPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.ElevatorActionPayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.EmoteUsePayload.TYPE,
            com.stardew.craft.network.payload.EmoteUsePayload.STREAM_CODEC,
            com.stardew.craft.network.payload.EmoteUsePayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.network.payload.EmoteBroadcastPayload.TYPE,
            com.stardew.craft.network.payload.EmoteBroadcastPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.EmoteBroadcastPayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.AnimalQueryActionPayload.TYPE,
            com.stardew.craft.network.payload.AnimalQueryActionPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.AnimalQueryActionPayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.network.payload.OpenAnimalMoveHomeScreenPayload.TYPE,
            com.stardew.craft.network.payload.OpenAnimalMoveHomeScreenPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.OpenAnimalMoveHomeScreenPayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.AnimalMoveHomeSelectPayload.TYPE,
            com.stardew.craft.network.payload.AnimalMoveHomeSelectPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.AnimalMoveHomeSelectPayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.AnimalRenamePayload.TYPE,
            com.stardew.craft.network.payload.AnimalRenamePayload.STREAM_CODEC,
            com.stardew.craft.network.payload.AnimalRenamePayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.network.payload.OpenAnimalPurchaseScreenPayload.TYPE,
            com.stardew.craft.network.payload.OpenAnimalPurchaseScreenPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.OpenAnimalPurchaseScreenPayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.AnimalPurchaseSubmitPayload.TYPE,
            com.stardew.craft.network.payload.AnimalPurchaseSubmitPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.AnimalPurchaseSubmitPayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.IncubatorClaimSubmitPayload.TYPE,
            com.stardew.craft.network.payload.IncubatorClaimSubmitPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.IncubatorClaimSubmitPayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.network.payload.OpenDecorationScreenPayload.TYPE,
            com.stardew.craft.network.payload.OpenDecorationScreenPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.OpenDecorationScreenPayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.ApplyDecorationStylePayload.TYPE,
            com.stardew.craft.network.payload.ApplyDecorationStylePayload.STREAM_CODEC,
            com.stardew.craft.network.payload.ApplyDecorationStylePayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.CookingPotCookSubmitPayload.TYPE,
            com.stardew.craft.network.payload.CookingPotCookSubmitPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.CookingPotCookSubmitPayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.network.payload.OpenSleepConfirmScreenPayload.TYPE,
            com.stardew.craft.network.payload.OpenSleepConfirmScreenPayload.STREAM_CODEC,
            com.stardew.craft.network.payload.OpenSleepConfirmScreenPayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.SleepConfirmChoicePayload.TYPE,
            com.stardew.craft.network.payload.SleepConfirmChoicePayload.STREAM_CODEC,
            com.stardew.craft.network.payload.SleepConfirmChoicePayload::handle
        );

        registrar.playToServer(
            com.stardew.craft.network.payload.OvernightProfessionChoicePayload.TYPE,
            com.stardew.craft.network.payload.OvernightProfessionChoicePayload.STREAM_CODEC,
            com.stardew.craft.network.payload.OvernightProfessionChoicePayload::handle
        );

        registrar.playToClient(
            com.stardew.craft.network.overnight.OvernightSettlementPayload.TYPE,
            com.stardew.craft.network.overnight.OvernightSettlementPayload.STREAM_CODEC,
            com.stardew.craft.network.overnight.OvernightSettlementPayload::handle
        );

    }
}
