package com.stardew.craft.client.hud;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.particle.GoldStarParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 金币数字滚动显示器
 * 完全按照星露谷物语原版实现
 */
public class MoneyDial {
    
    private static final ResourceLocation DIGIT_TEXTURE = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/money_dial_digits.png");
    private static final int DIGIT_WIDTH = 5;
    private static final int DIGIT_HEIGHT = 8;
    private static final int DIGIT_TEXTURE_WIDTH = 5;
    private static final int DIGIT_TEXTURE_HEIGHT = 80;
    private static final int DIGIT_SPACING = 6;
    
    private final int numDigits;
    private int currentValue;
    private int previousTargetValue;
    private int speed;
    private int soundTimer;
    private int moneyMadeAccumulator;
    private int moneyShineTimer;
    private final boolean playSounds;
    private final List<GoldStarParticle> particles = new ArrayList<>();
    private final Random random = new Random();
    
    public MoneyDial(int numDigits, boolean playSound) {
        this.numDigits = numDigits;
        this.playSounds = playSound;
        this.currentValue = 0;
        this.previousTargetValue = 0;
    }
    
    public MoneyDial(int numDigits) {
        this(numDigits, true);
    }
    
    /**
     * 立即设置金币值（无动画）
     */
    public void setValueImmediately(int value) {
        this.currentValue = value;
        this.previousTargetValue = value;
        this.speed = 0;
    }
    
    /**
     * 渲染金币数字 - 完全按原版实现
     */
    public void draw(GuiGraphics graphics, float x, float y, int targetMoney) {
        Minecraft mc = Minecraft.getInstance();
        
        // 检测目标值变化 - 原版逻辑
        if (previousTargetValue != targetMoney) {
            speed = (targetMoney - currentValue) / 100;
            previousTargetValue = targetMoney;
            soundTimer = Math.max(6, 100 / (Math.abs(speed) + 1));
        }
        
        // 更新闪光计时器 - 原版每帧减少
        if (moneyShineTimer > 0 && currentValue == targetMoney) {
            moneyShineTimer--;
        }
        
        // 累积金币变化
        if (moneyMadeAccumulator > 0) {
            moneyMadeAccumulator -= (Math.abs(speed / 2) + 1);
            if (moneyMadeAccumulator <= 0) {
                moneyShineTimer = numDigits * 60;
            }
        }
        
        // 触发抖动效果
        if (moneyMadeAccumulator > 2000) {
            StardewTimeHud.triggerMoneyShake();
        }
        
        // 更新当前值（滚动效果）- 原版逻辑
        if (currentValue != targetMoney) {
            currentValue += speed + ((currentValue < targetMoney) ? 1 : -1);
            
            if (currentValue < targetMoney) {
                moneyMadeAccumulator += Math.abs(speed);
            }
            
            soundTimer--;  // 原版：每帧减1
            
            // 播放音效和生成粒子 - 必须在检查到达目标之前！
            if (soundTimer <= 0) {
                if (playSounds && mc.player != null) {
                    int direction = Integer.signum(targetMoney - currentValue);
                    playMoneySound(direction);
                    
                    // 原版：40%概率生成金星粒子（只在加钱时）
                    if (direction > 0 && random.nextDouble() < 0.4) {
                        // 金币框宽度约47px，限制粒子在框内及附近
                        float particleX = x + random.nextInt(40); // 0到40范围内
                        float particleY = y + random.nextInt(16) - 8; // 上下8像素范围
                        particles.add(new GoldStarParticle(particleX, particleY));
                    }
                }
                soundTimer = Math.max(6, 100 / (Math.abs(speed) + 1));
            }
            
            // 检查是否到达目标
            if (Math.abs(targetMoney - currentValue) <= Math.abs(speed) + 1 
                || (speed != 0 && Integer.signum(targetMoney - currentValue) != Integer.signum(speed))) {
                currentValue = targetMoney;
            }
        }
        
        // 更新粒子（使用游戏tick时间）
        float deltaTime = mc.getTimer().getRealtimeDeltaTicks() * 50.0f; // tick转毫秒
        particles.removeIf(particle -> particle.update(deltaTime));
        
        // 先渲染粒子（在数字后面）
        for (GoldStarParticle particle : particles) {
            particle.render(graphics);
        }
        
        // 渲染数字
        drawDigits(graphics, x, y);
    }
    
    /**
     * 渲染每个数字 - 右对齐8个框
     */
    @SuppressWarnings("null")
    private void drawDigits(GuiGraphics graphics, float x, float y) {
        // 右对齐：从右边开始填充
        String moneyStr = String.valueOf(currentValue);
        int numDigitsToDraw = Math.min(moneyStr.length(), numDigits);
        int startSlot = numDigits - numDigitsToDraw; // 右对齐起始槽位
        
        for (int i = 0; i < numDigitsToDraw; i++) {
            int currentDigit = Character.getNumericValue(moneyStr.charAt(i));
            int slotIndex = startSlot + i;
            float digitX = x + slotIndex * DIGIT_SPACING; // 每个槽位6px间距
            int digitY = 72 - currentDigit * DIGIT_HEIGHT; // 材质Y坐标（与原版 MoneyDial 一致）
            
            // 计算缩放 - 闪光动画（从右到左依次闪光）
            float scale = 1.0f;
            if (moneyShineTimer > 0) {
                // 每60帧闪一个数字（1秒=20ticks=60帧在60fps下）
                int shineDigitIndex = moneyShineTimer / 60;
                if (shineDigitIndex == (numDigits - slotIndex - 1)) {
                    scale = 1.075f; // 放大7.5%
                }
            }
            
            // 只在需要缩放时应用变换
            if (scale != 1.0f) {
                graphics.pose().pushPose();
                // 以数字中心为缩放原点
                graphics.pose().translate(digitX + DIGIT_WIDTH * 0.5f, y + DIGIT_HEIGHT * 0.5f, 0);
                graphics.pose().scale(scale, scale, 1.0f);
                graphics.pose().translate(-DIGIT_WIDTH * 0.5f, -DIGIT_HEIGHT * 0.5f, 0);
                
                // 渲染数字
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.502f, 0.0f, 0.0f, 1.0f);
                graphics.blit(DIGIT_TEXTURE, 0, 0, 0, digitY, DIGIT_WIDTH, DIGIT_HEIGHT, DIGIT_TEXTURE_WIDTH, DIGIT_TEXTURE_HEIGHT);
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                
                graphics.pose().popPose();
            } else {
                // 无缩放直接渲染
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.502f, 0.0f, 0.0f, 1.0f);
                graphics.blit(DIGIT_TEXTURE, (int)digitX, (int)y, 0, digitY, DIGIT_WIDTH, DIGIT_HEIGHT, DIGIT_TEXTURE_WIDTH, DIGIT_TEXTURE_HEIGHT);
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }
    
    /**
     * 播放金币音效
     */
    @SuppressWarnings("null")
    private void playMoneySound(int direction) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && direction > 0) {  // 原版：只有加钱时播放！
            ResourceLocation soundLocation = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "money_dial");
            @SuppressWarnings("null")
            SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundLocation);
            mc.player.playSound(soundEvent, 0.5f, 1.0f);
        }
    }
    
    /**
     * 获取当前显示的金额
     */
    public int getCurrentValue() {
        return currentValue;
    }
    
    /**
     * 重置为指定值（无动画）
     */
    public void reset(int value) {
        this.currentValue = value;
        this.previousTargetValue = value;
        this.speed = 0;
        this.soundTimer = 0;
        this.moneyMadeAccumulator = 0;
        this.moneyShineTimer = 0;
        this.particles.clear();
    }
}
