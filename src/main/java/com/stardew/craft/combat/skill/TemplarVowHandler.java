package com.stardew.craft.combat.skill;

import com.stardew.craft.StardewCraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class TemplarVowHandler {

    private TemplarVowHandler() {}

    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) {
            return;
        }

        Level level = player.level();
        if (level.isClientSide) {
            return;
        }

        long nowTick = level.getGameTime();
        if (!TemplarVowTracker.isActive(player, nowTick)) {
            return;
        }

        event.setAmount(0.0f);
        level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 0.9f, 1.1f);

        player.swing(InteractionHand.MAIN_HAND, true);

        Entity src = event.getSource().getEntity();
        if (src instanceof LivingEntity attacker && attacker.isAlive()) {
            SkillContext context = SkillContext.builder()
                .skillId("templar_vow")
                .tier(SkillContext.SkillTier.MINOR)
                .damageMultiplier(1.1f)
                .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 5);
            player.attack(attacker);
        }

        TemplarVowTracker.endNow(player, nowTick);
    }
}
