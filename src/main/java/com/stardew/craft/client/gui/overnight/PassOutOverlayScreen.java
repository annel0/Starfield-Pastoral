package com.stardew.craft.client.gui.overnight;

import com.stardew.craft.client.gui.common.GuiText;
import com.stardew.craft.network.payload.PassOutAckPayload;
import com.stardew.craft.network.payload.PassOutPayload;
import com.stardew.craft.player.PassOutService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 晕倒渐黑过渡屏幕。
 * <p>
 * 0→1s: 屏幕从正常到全黑<br>
 * 1→2s: 黑屏 + 显示文字「你倒下了……」<br>
 * 2→3s: 自动关闭 → 发送 ACK → 进入隔夜结算
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("null")
public class PassOutOverlayScreen extends Screen {

    private static final int FADE_IN_TICKS = 20;   // 1s
    private static final int HOLD_TICKS = 20;       // 1s
    private static final int TOTAL_TICKS = FADE_IN_TICKS + HOLD_TICKS + 20; // 3s

    private final PassOutPayload payload;
    /** null = 独立模式（战斗死亡/体力耗尽），发 ACK 再开摘要画面；
     *  非 null = 链式模式（2AM 夜间结算），不发 ACK，推进到下一个画面 */
    @javax.annotation.Nullable
    private final java.util.List<Screen> siblingScreens;
    private int ticksOpen;
    private boolean finished;

    /** 独立模式构造（战斗死亡、体力耗尽 mid-day） */
    public PassOutOverlayScreen(PassOutPayload payload) {
        this(payload, null);
    }

    /** 链式模式构造（2AM 晕倒 → 接入夜间结算画面链） */
    public PassOutOverlayScreen(PassOutPayload payload, @javax.annotation.Nullable java.util.List<Screen> siblingScreens) {
        super(Component.empty());
        this.payload = payload;
        this.siblingScreens = siblingScreens;
    }

    public static void show(PassOutPayload payload) {
        // 防止重复 PassOutPayload 不断替换屏幕导致计时器重置（2AM 重复触发防护）
        if (Minecraft.getInstance().screen instanceof PassOutOverlayScreen) {
            return;
        }
        Minecraft.getInstance().setScreen(new PassOutOverlayScreen(payload));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        ticksOpen++;
        if (ticksOpen >= TOTAL_TICKS) {
            finish();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        float alpha;
        if (ticksOpen < FADE_IN_TICKS) {
            // 渐入黑屏
            alpha = (ticksOpen + partialTick) / FADE_IN_TICKS;
        } else {
            alpha = 1.0f;
        }

        // 黑色遮罩
        int a = (int)(alpha * 255) << 24;
        graphics.fill(0, 0, width, height, a);

        // 文字（黑屏保持阶段显示）
        if (ticksOpen >= FADE_IN_TICKS) {
            String messageKey;
            if (payload.passOutType() == PassOutService.PassOutType.COMBAT_MINE
                    || payload.passOutType() == PassOutService.PassOutType.COMBAT_OVERWORLD) {
                messageKey = "stardewcraft.passout.combat";
            } else {
                messageKey = "stardewcraft.passout.exhaustion";
            }
            Component text = Component.translatable(messageKey);
            GuiText.drawCenteredClamped(graphics, font, text, width / 2, height / 2 - 4,
                Math.max(1, width - 32), 0xFFFFFFFF, false);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 阻止 ESC 关闭
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 点击可以跳过
        if (ticksOpen >= FADE_IN_TICKS) {
            finish();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void finish() {
        if (finished) return;
        finished = true;

        com.stardew.craft.StardewCraft.LOGGER.info("[OVERNIGHT_CLIENT] PassOutOverlayScreen.finish() chainMode={}",
            siblingScreens != null);

        if (siblingScreens != null) {
            // 链式模式：推进到结算画面链中的下一个（不发 ACK，服务端已完成日推进）
            if (!siblingScreens.isEmpty()) {
                Minecraft.getInstance().setScreen(siblingScreens.remove(0));
            } else {
                Minecraft.getInstance().setScreen(null);
            }
        } else {
            // 独立模式：发送 ACK 告诉服务端可以传送了，然后显示惩罚摘要
            PacketDistributor.sendToServer(new PassOutAckPayload());
            Minecraft.getInstance().setScreen(new PassOutSummaryScreen(payload));
        }
    }
}
