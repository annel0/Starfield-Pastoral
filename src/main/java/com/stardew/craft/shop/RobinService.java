package com.stardew.craft.shop;

import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.network.payload.OpenCarpenterMenuPayload;
import com.stardew.craft.network.payload.OpenRobinMenuPayload;
import com.stardew.craft.network.payload.OpenShopScreenPayload;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Robin's carpenter service. Handles counter detection, choice dialogue,
 * building purchase menu, and material shop.
 * Follows the same multi-option pattern as BlacksmithService.
 */
@SuppressWarnings("null")
public final class RobinService {

    // Counter AABB (world coordinates)
    private static final int COUNTER_MIN_X = 16526, COUNTER_MAX_X = 16528;
    private static final int COUNTER_MIN_Y = 75,    COUNTER_MAX_Y = 77;
    private static final int COUNTER_MIN_Z = 16518, COUNTER_MAX_Z = 16523;

    private static final List<CarpenterBlueprint> BLUEPRINTS = List.of(
        new CarpenterBlueprint(
            "Coop",
            "\u9e21\u820d",     // 鸡舍 (SDV exact)
            "\u53ef\u5bb9\u7eb3 4 \u53ea\u5bb6\u79bd\u3002\u8d2d\u4e70\u540e\u83b7\u5f97\u9e21\u820d\u7ba1\u7406\u5668\uff0c\u653e\u7f6e\u5728\u519c\u573a\u5373\u53ef\u5f00\u59cb\u5efa\u9020\u3002",
            // 可容纳 4 只家禽。购买后获得鸡舍管理器，放置在农场即可开始建造。
            4000,
            List.of(
                new CarpenterBlueprint.MaterialEntry("stardewcraft:wood_normal", 300),
                new CarpenterBlueprint.MaterialEntry("stardewcraft:stone", 100)
            ),
            "stardewcraft:coop_manager",
            false
        ),
        new CarpenterBlueprint(
            "Barn",
            "\u755c\u68da",     // 畜棚 (SDV exact)
            "\u53ef\u5bb9\u7eb3 4 \u53ea\u5bb6\u755c\u3002\u8d2d\u4e70\u540e\u83b7\u5f97\u755c\u68da\u7ba1\u7406\u5668\uff0c\u653e\u7f6e\u5728\u519c\u573a\u5373\u53ef\u5f00\u59cb\u5efa\u9020\u3002",
            // 可容纳 4 只家畜。购买后获得畜棚管理器，放置在农场即可开始建造。
            6000,
            List.of(
                new CarpenterBlueprint.MaterialEntry("stardewcraft:wood_normal", 350),
                new CarpenterBlueprint.MaterialEntry("stardewcraft:stone", 150)
            ),
            "stardewcraft:barn_manager",
            false
        ),
        new CarpenterBlueprint(
            "Silo",
            "\u7b52\u4ed3",     // 筒仓 (SDV exact)
            "\u53ef\u50a8\u5b58\u5e72\u8349\u3002\u8d2d\u4e70\u540e\u83b7\u5f97\u7b52\u4ed3\u7ba1\u7406\u5668\uff0c\u653e\u7f6e\u5728\u519c\u573a\u5373\u53ef\u5f00\u59cb\u5efa\u9020\u3002",
            // 可储存干草。购买后获得筒仓管理器，放置在农场即可开始建造。
            100,
            List.of(
                new CarpenterBlueprint.MaterialEntry("stardewcraft:stone", 100),
                new CarpenterBlueprint.MaterialEntry("stardewcraft:clay", 10),
                new CarpenterBlueprint.MaterialEntry("stardewcraft:copper_bar", 5)
            ),
            "stardewcraft:silo_manager",
            false
        )
    );

    private RobinService() {}

    public static List<CarpenterBlueprint> getBlueprints() {
        return BLUEPRINTS;
    }

    public static boolean isPlayerAtCounter(ServerPlayer player) {
        int px = (int) Math.floor(player.getX());
        int py = (int) Math.floor(player.getY());
        int pz = (int) Math.floor(player.getZ());
        return px >= COUNTER_MIN_X && px <= COUNTER_MAX_X
            && py >= COUNTER_MIN_Y && py <= COUNTER_MAX_Y
            && pz >= COUNTER_MIN_Z && pz <= COUNTER_MAX_Z;
    }

    /**
     * Entry point: called from NpcInteractionService when player interacts
     * with Robin at her counter. Sends choice dialogue (Build / Shop / Leave).
     */
    public static InteractionResult handleCarpenterInteraction(ServerPlayer player, StardewNpcEntity robin) {
        robin.setYRot(0f);
        robin.setYHeadRot(0f);

        // Send choice dialogue to client (like BlacksmithService)
        PacketDistributor.sendToPlayer(player, new OpenRobinMenuPayload());
        return InteractionResult.SUCCESS;
    }

    /**
     * Dispatches the player's menu choice.
     * 0 = Build (open CarpenterMenu), 1 = Shop (open material shop), 2 = Leave
     */
    public static void handleMenuChoice(ServerPlayer player, int choice) {
        switch (choice) {
            case 0 -> openCarpenterMenu(player);
            case 1 -> openCarpenterShop(player);
            // 2 = Leave, do nothing
        }
    }

    // ──── Build (CarpenterMenu) ────

    private static void openCarpenterMenu(ServerPlayer player) {
        int money = PlayerStardewDataAPI.getMoney(player);
        OpenCarpenterMenuPayload payload = new OpenCarpenterMenuPayload(
            money,
            new ArrayList<>(BLUEPRINTS)
        );
        PacketDistributor.sendToPlayer(player, payload);
    }

    // ──── Shop (material purchase via ShopScreen) ────

    private static void openCarpenterShop(ServerPlayer player) {
        ShopRegistry.ShopDefinition shop = ShopRegistry.get("CarpenterShop");
        if (shop == null) return;

        int money = PlayerStardewDataAPI.getMoney(player);
        List<ShopItemEntry> items = ShopRegistry.getFilteredItemsForPlayer("CarpenterShop", shop, player);

        OpenShopScreenPayload payload = new OpenShopScreenPayload(
            "CarpenterShop", money, items,
            shop.ownerNpcId(), shop.ownerDialogue(),
            new ArrayList<>(shop.acceptedSellTypes())
        );
        PacketDistributor.sendToPlayer(player, payload);
    }
}
