package com.stardew.craft.item;

import com.stardew.craft.effect.ModMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MonsterMuskItem extends SimpleStardewItem {
    private static final int DURATION_TICKS = 10 * 60 * 20;

    public MonsterMuskItem(Properties properties) {
        super("stardewcraft.type.monster_loot", 50, properties);
    }

    @SuppressWarnings("null")
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.addEffect(new MobEffectInstance(ModMobEffects.MONSTER_MUSK, DURATION_TICKS, 0, false, true, true));
            level.playSound(null, player.blockPosition(), SoundEvents.SNIFFER_SCENTING, SoundSource.PLAYERS, 0.8F, 0.8F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.WITCH,
                    player.getX(), player.getY() + 1.0D, player.getZ(),
                    16, 0.35D, 0.6D, 0.35D, 0.02D);
            }
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getCooldowns().addCooldown(this, 20);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
