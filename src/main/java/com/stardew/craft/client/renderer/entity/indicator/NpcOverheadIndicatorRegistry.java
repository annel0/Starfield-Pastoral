package com.stardew.craft.client.renderer.entity.indicator;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.NpcFriendshipClientCache;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.network.payload.RequestNpcFriendshipOverviewPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NpcOverheadIndicatorRegistry {
    private static final long OVERVIEW_REQUEST_INTERVAL_MS = 1500L;
    private static final NpcOverheadIndicator CHAT_AVAILABLE_INDICATOR = new NpcOverheadIndicator(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/common/social_talk_icon.png"),
        0,
        0,
        13,
        11,
        13,
        11,
        0.065f,
        0.75f
    );

    private static final List<NpcOverheadIndicatorProvider> PROVIDERS = new CopyOnWriteArrayList<>();
    private static long lastOverviewRequestMs;

    static {
        register((npc, localPlayer) -> {
            String npcId = npc.getNpcId();
            if (npcId == null || npcId.isBlank()) {
                return null;
            }
            NpcFriendshipClientCache.Entry entry = NpcFriendshipClientCache.findByNpcId(npcId.toLowerCase(Locale.ROOT));
            if (entry == null) {
                requestOverviewIfNeeded();
                return null;
            }
            return entry.talkedToday() ? null : CHAT_AVAILABLE_INDICATOR;
        });
    }

    private NpcOverheadIndicatorRegistry() {
    }

    public static void register(NpcOverheadIndicatorProvider provider) {
        if (provider != null) {
            PROVIDERS.add(provider);
        }
    }

    public static NpcOverheadIndicator resolve(StardewNpcEntity npc, LocalPlayer localPlayer) {
        for (NpcOverheadIndicatorProvider provider : PROVIDERS) {
            NpcOverheadIndicator indicator = provider.resolve(npc, localPlayer);
            if (indicator != null) {
                return indicator;
            }
        }
        return null;
    }

    private static void requestOverviewIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastOverviewRequestMs < OVERVIEW_REQUEST_INTERVAL_MS) {
            return;
        }
        lastOverviewRequestMs = now;
        PacketDistributor.sendToServer(new RequestNpcFriendshipOverviewPayload());
    }
}
