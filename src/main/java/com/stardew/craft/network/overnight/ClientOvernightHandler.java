package com.stardew.craft.network.overnight;

import com.stardew.craft.network.payload.PassOutPayload;
import com.stardew.craft.player.PassOutService;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.screens.Screen;
import com.stardew.craft.client.gui.overnight.ShippingMenuScreen;
import com.stardew.craft.client.gui.overnight.PassOutOverlayScreen;
import com.stardew.craft.client.gui.overnight.PassOutSummaryScreen;
import com.stardew.craft.player.ProfessionType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ClientOvernightHandler {
    private static final Set<Integer> LOCAL_OVERNIGHT_PROFESSIONS = new HashSet<>();

    public static void beginSequence() {
        LOCAL_OVERNIGHT_PROFESSIONS.clear();
    }

    public static void recordLocalProfessionChoice(int professionId) {
        LOCAL_OVERNIGHT_PROFESSIONS.add(professionId);
    }

    public static boolean hasLocalProfession(ProfessionType profession) {
        return profession != null && LOCAL_OVERNIGHT_PROFESSIONS.contains(profession.getId());
    }

    public static void startSequence(OvernightSettlementPayload payload) {
        beginSequence();

        // 如果当前正在显示睡眠等待界面，先关闭它（不发取消包）
        if (Minecraft.getInstance().screen instanceof com.stardew.craft.client.gui.overnight.SleepWaitingOverlayScreen waitingScreen) {
            waitingScreen.onDayAdvanced();
        }

        com.stardew.craft.StardewCraft.LOGGER.info("[OVERNIGHT_CLIENT] startSequence: hasPassOut={}, passOutType={}, moneyLost={}, levelUps={}, shippedItems={}",
            payload.hasPassOut(), payload.passOutType(), payload.passOutMoneyLost(),
            payload.levelUps().size(), payload.shippedItems().size());

        List<Screen> screenStack = new java.util.ArrayList<>();

        // 如果是 2AM 晕倒，先展示渐黑 + 惩罚摘要画面，再进入正常结算流程
        if (payload.hasPassOut()) {
            PassOutPayload passOutPayload = new PassOutPayload(
                PassOutService.PassOutType.fromId(payload.passOutType()),
                payload.passOutMoneyLost(),
                payload.passOutLostItems()
            );
            screenStack.add(new PassOutOverlayScreen(passOutPayload, screenStack));
            screenStack.add(new PassOutSummaryScreen(passOutPayload, screenStack));
        }

        // 技能升级画面
        for (OvernightSettlementPayload.LevelUpData levelData : payload.levelUps()) {
            screenStack.add(new com.stardew.craft.client.gui.overnight.LevelUpMenuScreen(levelData, screenStack));
        }

        // 始终添加出货结算画面（即使没有出货物品也要展示夜间过渡动画）
        screenStack.add(new ShippingMenuScreen(payload.shippedItems(), screenStack));
        com.stardew.craft.StardewCraft.LOGGER.info("[OVERNIGHT_CLIENT] Screen chain size={}, opening first screen: {}",
            screenStack.size(), screenStack.get(0).getClass().getSimpleName());
        Minecraft.getInstance().setScreen(screenStack.remove(0));
    }
}
