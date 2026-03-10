package com.stardew.craft.combat.skill;

import com.stardew.craft.StardewCraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class LightCounterParryHandler {

    private LightCounterParryHandler() {}

    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Level level = player.level();
        if (level.isClientSide) {
            return;
        }

        long nowTick = level.getGameTime();
        if (!LightCounterParryState.isActive(player, nowTick)) {
            return;
        }

        String weaponId = LightCounterParryState.getWeaponId(player);
        if (weaponId == null || weaponId.isEmpty()) {
            return;
        }

        // Consume the parry window
        LightCounterParryState.clear(player);

        // Reduce incoming damage to 40%
        event.setAmount(event.getAmount() * 0.4f);

        level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0f, 1.0f);

        Entity src = event.getSource().getEntity();
        if (src instanceof LivingEntity attacker && attacker.isAlive()) {
            SkillContext context = SkillContext.builder()
                    .skillId("light_counter")
                    .tier(SkillContext.SkillTier.MINOR)
                    .damageMultiplier(1.2f)
                    .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 5);
            player.attack(attacker);
        }

        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            WeaponSkillAnimationDispatcher.sendCounterAnim(
                    serverPlayer,
                    weaponId,
                    "light_counter_counter",
                    LightCounterParryState.COUNTER_ANIM_TICKS
            );
        }
    }
}
