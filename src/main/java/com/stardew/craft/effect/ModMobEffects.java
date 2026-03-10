package com.stardew.craft.effect;

import com.stardew.craft.StardewCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义 Buff（状态效果）。
 *
 * 图标放置：assets/stardewcraft/textures/mob_effect/<effect_id>.png
 */
@SuppressWarnings("null")
public final class ModMobEffects {
    private ModMobEffects() {}

    private static final class SimpleBeneficialEffect extends MobEffect {
        private SimpleBeneficialEffect(int color) {
            super(MobEffectCategory.BENEFICIAL, color);
        }
    }

    private static final class SimpleHarmfulEffect extends MobEffect {
        private SimpleHarmfulEffect(int color) {
            super(MobEffectCategory.HARMFUL, color);
        }
    }

    /**
     * 自定义“速度”效果：使用我们自己的 mob_effect/speed.png 图标，但实际加速按 MC 的移动速度属性修饰实现。
     */
    private static final class SpeedEffect extends MobEffect {
        private SpeedEffect(int color) {
            super(MobEffectCategory.BENEFICIAL, color);
            // 与原版 Speed 类似：每级 +20% 移速（乘算，随 amplifier 递增）。
            this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "effect.speed"),
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                amplifier -> 0.2D * (amplifier + 1)
            );
        }
    }

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(net.minecraft.core.registries.Registries.MOB_EFFECT, StardewCraft.MODID);

    /**
     * 活力充沛：用于“最大能量”类增益（每级 +30 Max Energy，持续时间由食物决定）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> VIGOROUS = MOB_EFFECTS.register(
            "vigorous",
            () -> new SimpleBeneficialEffect(0x5CCB6A)
    );

    /**
     * 海王祝福：用于“钓鱼等级”增益（每级 +1 Fishing Level）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> SEA_KING_BLESSING = MOB_EFFECTS.register(
            "sea_king_blessing",
            () -> new SimpleBeneficialEffect(0x4DA6FF)
    );

    /**
     * 精灵赐福：用于“运气等级”增益（每级 +1 Luck Level）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> SPIRIT_BLESSING = MOB_EFFECTS.register(
            "spirit_blessing",
            () -> new SimpleBeneficialEffect(0xE6D36E)
    );

        /**
         * 庇护：每级降低受到伤害 10%。
         */
        public static final DeferredHolder<MobEffect, MobEffect> SHELTER = MOB_EFFECTS.register(
            "shelter",
            () -> new SimpleBeneficialEffect(0x8FBF6A)
        );

        /**
         * 易伤：提高目标受到的伤害。
         */
        public static final DeferredHolder<MobEffect, MobEffect> VULNERABLE = MOB_EFFECTS.register(
            "vulnerable",
            () -> new SimpleHarmfulEffect(0xC43A3A)
        );

        /**
         * 弱点：提高目标被攻击时的暴击率。
         */
        public static final DeferredHolder<MobEffect, MobEffect> WEAK_POINT = MOB_EFFECTS.register(
            "weak_point",
            () -> new SimpleHarmfulEffect(0xB08C4A)
        );

        /**
         * 速度：用于可乐等短时移速增益（图标来自 mob_effect/speed.png）。
         */
        public static final DeferredHolder<MobEffect, MobEffect> SPEED = MOB_EFFECTS.register(
            "speed",
            () -> new SpeedEffect(0x7CAFC6)
        );

        /**
         * 愤怒：攻击伤害+10%（每层）
         */
        public static final DeferredHolder<MobEffect, MobEffect> FURY = MOB_EFFECTS.register(
            "fury",
            () -> {
                MobEffect effect = new SimpleBeneficialEffect(0xD4AF37); // 金色
                effect.addAttributeModifier(
                    Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "effect.fury"),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                    amplifier -> 0.10D * (amplifier + 1)
                );
                return effect;
            }
        );

    // ====== 数值规则（统一放这里，后续扩展更方便） ======

    public static int vigorousMaxEnergyBonus(int amplifier) {
        return 30 * (amplifier + 1);
    }

    public static int seaKingFishingLevelBonus(int amplifier) {
        return amplifier + 1;
    }

    public static int spiritLuckLevelBonus(int amplifier) {
        return amplifier + 1;
    }

    public static float shelterDamageMultiplier(int amplifier) {
        float reduction = 0.10f * (amplifier + 1);
        return Math.max(0.0f, 1.0f - reduction);
    }
}
