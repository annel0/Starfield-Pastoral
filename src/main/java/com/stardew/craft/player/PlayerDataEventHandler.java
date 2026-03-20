package com.stardew.craft.player;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.combat.DamageCalculator;
import com.stardew.craft.combat.WeaponStats;
import com.stardew.craft.effect.ModMobEffects;
import com.stardew.craft.network.PlayerDataSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 玩家数据事件处理器
 * 负责数据的自动保存和同步
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public class PlayerDataEventHandler {
    
    private static int tickCounter = 0;
    private static final int AUTO_SAVE_INTERVAL = 6000; // 5分钟 (6000 ticks)
    
    /**
     * 玩家登录时初始化数据
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 获取或创建玩家数据（会自动从NBT加载）
            PlayerStardewData data = PlayerDataManager.getPlayerData(player);
            PlayerStardewDataAPI.applyStardewCraftingConditionUnlocks(player);
            StardewCraft.LOGGER.info("Player {} logged in, loaded Stardew data", player.getName().getString());
            
            // 同步数据到客户端
            syncPlayerData(player, data);
        }
    }
    
    /**
     * 玩家退出时保存数据
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerStardewData data = PlayerDataManager.getPlayerData(player);
            if (data.isDirty()) {
                data.markClean();
                PlayerDataManager.get().setDirty();
                StardewCraft.LOGGER.info("Player {} logged out, saved Stardew data", player.getName().getString());
            }
        }
    }
    
    /**
     * 玩家死亡时的处理
     */
    @SubscribeEvent
    public static void onPlayerDeath(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 星露谷维度：不走 MC 原版死亡（后续要接“晕倒/结算”流程）。
            if (player.level().dimension() == ModDimensions.STARDEW_VALLEY
                || player.level().dimension() == ModMiningDimensions.STARDEW_MINING) {
                event.setCanceled(true);
                StardewDamageHooks.onHealthDepleted(player, event.getSource());
                // 保险：防止已进入 dying 状态
                player.setHealth(player.getMaxHealth());
                player.getFoodData().setFoodLevel(20);
                player.getFoodData().setSaturation(5.0f);
                return;
            }

            PlayerStardewData data = PlayerDataManager.getPlayerData(player);
            
            // 星露谷死亡机制：损失10%金币（最多1000）
            int moneyLoss = Math.min(data.getMoney() / 10, 1000);
            if (moneyLoss > 0) {
                data.removeMoney(moneyLoss);
            }
            
            // 重置生命值为满
            data.setHealth(data.getMaxHealth());
            
            // TODO: 可能还需要掉落一些物品
        }
    }

    /**
     * 星露谷维度：拦截原版受伤，并映射到星露谷生命值。
     */
    @SubscribeEvent
    public static void onPlayerHurt(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        float amount = event.getAmount();
        if (amount <= 0.0f) {
            return;
        }

        @SuppressWarnings("null")
        MobEffectInstance shelter = player.getEffect(ModMobEffects.SHELTER);
        if (shelter != null) {
            amount *= ModMobEffects.shelterDamageMultiplier(shelter.getAmplifier());
        }

        if (player.level().dimension() != ModDimensions.STARDEW_VALLEY
            && player.level().dimension() != ModMiningDimensions.STARDEW_MINING) {
            event.setAmount(amount);
            return;
        }

        // 取消 MC 原版扣血（我们用星露谷血条承载伤害）。
        event.setAmount(0.0f);

        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        int sdMax = Math.max(1, data.getMaxHealth());
        float mcMax = Math.max(1.0f, player.getMaxHealth());
        float sdDamageFloat = amount * (sdMax / mcMax);

        // 武器防御（握持生效）
        WeaponStats weaponStats = WeaponStats.fromItemStack(player.getMainHandItem());
        float weaponDefense = weaponStats.getDefense();
        float foodDefense = data.getTempDefenseBonus();
        float totalDefense = weaponDefense + foodDefense;
        if (totalDefense > 0) {
            float reduction = DamageCalculator.calculateDefenseReductionFromDefense(sdDamageFloat, totalDefense);
            sdDamageFloat = Math.max(0.0f, sdDamageFloat - reduction);
        }

        int sdDamage = (int) Math.ceil(sdDamageFloat);
        if (sdDamage < 1) {
            sdDamage = 1;
        }

        long nowTick = player.level().getGameTime();

        // 钢脊之怒：4秒内首次受击充能
        com.stardew.craft.combat.skill.SteelSpineFuryState.onDamageTaken(player, nowTick, sdDamage);
        // 矮人剑：堡垒态受击触发地脉震波
        com.stardew.craft.combat.skill.DwarfFortressTracker.onDamageTaken(player, nowTick);

        int oldSdHealth = data.getHealth();
        int newSdHealth = Math.max(0, oldSdHealth - sdDamage);
        data.setHealth(newSdHealth);
        syncPlayerData(player, data);

        // 生命值清零：不要死，走接口（后续接“晕倒”等）。
        if (newSdHealth == 0) {
            DamageSource source = event.getSource();
            StardewDamageHooks.onHealthDepleted(player, source);
        }

        // 维持原版血/饱食度满，避免被其他机制“补刀”。
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(5.0f);
    }

    /**
     * 星露谷维度：维持原版血量/饱食度为满值（取消原版生命/饱食机制）。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Buff同步/过期驱动（不依赖维度）：
        // - MobEffect 负责 UI/持续时间（也支持 /effect 指令）
        // - PlayerStardewData 负责把加成落到星露谷数值体系里
        try {
            PlayerStardewData data = PlayerDataManager.getPlayerData(player);
            long now = player.level().getGameTime();
            boolean changed = false;

            @SuppressWarnings("null")
            MobEffectInstance vigorous = player.getEffect(ModMobEffects.VIGOROUS);
            if (vigorous != null) {
                int bonus = ModMobEffects.vigorousMaxEnergyBonus(vigorous.getAmplifier());
                long endTick = now + vigorous.getDuration();
                changed |= data.setTempMaxEnergyBonusDirect(bonus, endTick);
            } else {
                changed |= data.clearTempMaxEnergyBonus();
            }

            @SuppressWarnings("null")
            MobEffectInstance seaKing = player.getEffect(ModMobEffects.SEA_KING_BLESSING);
            if (seaKing != null) {
                int bonus = ModMobEffects.seaKingFishingLevelBonus(seaKing.getAmplifier());
                long endTick = now + seaKing.getDuration();
                changed |= data.setTempFishingLevelBonusDirect(bonus, endTick);
            } else {
                changed |= data.clearTempFishingLevelBonus();
            }

            @SuppressWarnings("null")
            MobEffectInstance spirit = player.getEffect(ModMobEffects.SPIRIT_BLESSING);
            if (spirit != null) {
                int bonus = ModMobEffects.spiritLuckLevelBonus(spirit.getAmplifier());
                long endTick = now + spirit.getDuration();
                changed |= data.setTempLuckBonusDirect(bonus, endTick);
            } else {
                changed |= data.clearTempLuckBonus();
            }

            @SuppressWarnings("null")
            MobEffectInstance farmerBlessing = player.getEffect(ModMobEffects.FARMER_BLESSING);
            if (farmerBlessing != null) {
                int bonus = ModMobEffects.farmerFarmingLevelBonus(farmerBlessing.getAmplifier());
                long endTick = now + farmerBlessing.getDuration();
                changed |= data.setTempFarmingLevelBonusDirect(bonus, endTick);
            } else {
                changed |= data.clearTempFarmingLevelBonus();
            }

            @SuppressWarnings("null")
            MobEffectInstance foragerBlessing = player.getEffect(ModMobEffects.FORAGER_BLESSING);
            if (foragerBlessing != null) {
                int bonus = ModMobEffects.foragerForagingLevelBonus(foragerBlessing.getAmplifier());
                long endTick = now + foragerBlessing.getDuration();
                changed |= data.setTempForagingLevelBonusDirect(bonus, endTick);
            } else {
                changed |= data.clearTempForagingLevelBonus();
            }

            @SuppressWarnings("null")
            MobEffectInstance minerBlessing = player.getEffect(ModMobEffects.MINER_BLESSING);
            if (minerBlessing != null) {
                int bonus = ModMobEffects.minerMiningLevelBonus(minerBlessing.getAmplifier());
                long endTick = now + minerBlessing.getDuration();
                changed |= data.setTempMiningLevelBonusDirect(bonus, endTick);
            } else {
                changed |= data.clearTempMiningLevelBonus();
            }

            @SuppressWarnings("null")
            MobEffectInstance warriorBlessing = player.getEffect(ModMobEffects.WARRIOR_BLESSING);
            if (warriorBlessing != null) {
                int bonus = ModMobEffects.warriorAttackBonus(warriorBlessing.getAmplifier());
                long endTick = now + warriorBlessing.getDuration();
                changed |= data.setTempAttackBonusDirect(bonus, endTick);
            } else {
                changed |= data.clearTempAttackBonus();
            }

            @SuppressWarnings("null")
            MobEffectInstance guardianBlessing = player.getEffect(ModMobEffects.GUARDIAN_BLESSING);
            if (guardianBlessing != null) {
                int bonus = ModMobEffects.guardianDefenseBonus(guardianBlessing.getAmplifier());
                long endTick = now + guardianBlessing.getDuration();
                changed |= data.setTempDefenseBonusDirect(bonus, endTick);
            } else {
                changed |= data.clearTempDefenseBonus();
            }

            @SuppressWarnings("null")
            MobEffectInstance magnetism = player.getEffect(ModMobEffects.MAGNETISM);
            if (magnetism != null) {
                int bonus = ModMobEffects.magnetismRadiusBonus(magnetism.getAmplifier());
                long endTick = now + magnetism.getDuration();
                changed |= data.setTempMagneticRadiusBonusDirect(bonus, endTick);
            } else {
                changed |= data.clearTempMagneticRadiusBonus();
            }

            // 兼容：若存在非 MobEffect 驱动的 timed buff，这里负责过期清理。
            changed |= data.tickTimedBuffs(now);

            if (changed) {
                syncPlayerData(player, data);
            }
        } catch (Exception e) {
            StardewCraft.LOGGER.error("Error ticking player buffs", e);
        }

        // 森林赐福：持续治疗
        com.stardew.craft.combat.skill.ForestBlessingTracker.tick(player, player.level().getGameTime());
        // 钢脊之怒：姿态过期转为弱势命中
        com.stardew.craft.combat.skill.SteelSpineFuryState.tick(player, player.level().getGameTime());
        // 双刃大剑：回刃折返二段斩击
        com.stardew.craft.combat.skill.ClaymoreFoldbackTracker.tick(player, player.level().getGameTime());
        // 股骨：震骨横砸蓄力
        com.stardew.craft.combat.skill.FemurSlamTracker.tick(player, player.level().getGameTime());
        // 圣堂之刃：誓约架势与裁决结算
        com.stardew.craft.combat.skill.TemplarVowTracker.tick(player, player.level().getGameTime());
        com.stardew.craft.combat.skill.TemplarJudgementTracker.tick(player, player.level().getGameTime());
        // 刻刀：刻痕连刺
        com.stardew.craft.combat.skill.CarvingKnifeThrustTracker.tick(player, player.level().getGameTime());
        // 铱针：三针连斩
        com.stardew.craft.combat.skill.IridiumNeedleThrustTracker.tick(player, player.level().getGameTime());
        // 银河匕首：星轨裂刺
        com.stardew.craft.combat.skill.GalaxyDaggerThrustTracker.tick(player, player.level().getGameTime());
        // 无限匕首：奇点连刺
        com.stardew.craft.combat.skill.InfinityDaggerThrustTracker.tick(player, player.level().getGameTime());
        // 残破的三叉戟：鱼获试刺 + 鱼获状态
        com.stardew.craft.combat.skill.BrokenTridentThrustTracker.tick(player, player.level().getGameTime());
        com.stardew.craft.combat.skill.BrokenTridentCatchTracker.tick(player, player.level().getGameTime());
        // 水晶匕首：晶层持续
        com.stardew.craft.combat.skill.CrystalDaggerLayerTracker.tick(player, player.level().getGameTime());
        // 精灵之刃：月露萤刃
        com.stardew.craft.combat.skill.ElfBladeTracker.tick(player, player.level().getGameTime());
        // 昆虫头部：复眼架势
        com.stardew.craft.combat.skill.InsectEyeStanceTracker.tick(player, player.level().getGameTime());
        // 黑曜石之刃：玄刃共鸣 + 裂界一线
        com.stardew.craft.combat.skill.ObsidianResonanceTracker.tick(player, player.level().getGameTime());
        com.stardew.craft.combat.skill.ObsidianCrackTracker.tick(player, player.level().getGameTime());
        // 骨化剑：白骨行刑
        com.stardew.craft.combat.skill.OssifiedExecutionTracker.tick(player, player.level().getGameTime());
        // 圣剑：晨曦圣域
        com.stardew.craft.combat.skill.HolyBladeSanctuaryTracker.tick(player, player.level().getGameTime());
        // 淬火阔剑：回炉淬火延迟爆鸣 + 熔锻飞坯火环
        com.stardew.craft.combat.skill.TemperedQuenchTracker.tick(player, player.level().getGameTime());
        com.stardew.craft.combat.skill.TemperedFireRingTracker.tick(player, player.level().getGameTime());
        // 钢刀：疾锋刻线 / 斩迹回响
        com.stardew.craft.combat.skill.SteelFalchionLineTracker.tick(player, player.level().getGameTime());
        // 黑暗剑：祭血斩 / 血月收割
        com.stardew.craft.combat.skill.DarkSwordBloodDebtTracker.tick(player, player.level().getGameTime());
        com.stardew.craft.combat.skill.DarkSwordBloodMoonTracker.tick(player, player.level().getGameTime());
        // 熔岩武士刀：熔潮回鸣
        com.stardew.craft.combat.skill.LavaKatanaReverbTracker.tick(player, player.level().getGameTime());
        // 矮人剑：地脉堡垒
        com.stardew.craft.combat.skill.DwarfFortressTracker.tick(player, player.level().getGameTime());
        // 银河剑：星落打击
        com.stardew.craft.combat.skill.StarfallTracker.tick(player, player.level().getGameTime());
        // 无限之刃：奇点进化 / 永恒坍缩
        com.stardew.craft.combat.skill.SingularityEvolveTracker.tick(player, player.level().getGameTime());
        com.stardew.craft.combat.skill.EternalCollapseTracker.tick(player, player.level().getGameTime());
            com.stardew.craft.combat.skill.RiftPathDamageTracker.tick(player, player.level().getGameTime());

        applyMagneticPull(player, PlayerDataManager.getPlayerData(player));

        if (player.level().dimension() != ModDimensions.STARDEW_VALLEY
            && player.level().dimension() != ModMiningDimensions.STARDEW_MINING) {
            return;
        }

        // 创造/旁观不强制。
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (player.getHealth() < player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        var food = player.getFoodData();
        if (food.getFoodLevel() != 20) {
            food.setFoodLevel(20);
        }
        if (food.getSaturationLevel() < 5.0f) {
            food.setSaturation(5.0f);
        }
    }
    
    /**
     * 服务器tick - 定时保存数据
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        
        if (tickCounter >= AUTO_SAVE_INTERVAL) {
            tickCounter = 0;
            
            try {
                PlayerDataManager manager = PlayerDataManager.get();
                manager.tickAndSaveDirty();
            } catch (Exception e) {
                StardewCraft.LOGGER.error("Error during player data auto-save", e);
            }
        }
    }
    
    /**
     * 服务器关闭时强制保存所有数据
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        try {
            PlayerDataManager manager = PlayerDataManager.get();
            manager.setDirty();
            StardewCraft.LOGGER.info("Server stopping, saved all player data");
        } catch (Exception e) {
            StardewCraft.LOGGER.error("Error saving player data on server stop", e);
        }
    }
    
    /**
     * 同步玩家数据到客户端
     */
    @SuppressWarnings("null")
    public static void syncPlayerData(ServerPlayer player, PlayerStardewData data) {
        PlayerDataSyncPacket packet = PlayerDataSyncPacket.fromPlayerData(data);
        PacketDistributor.sendToPlayer(player, packet);
    }

    /**
     * 吸附掉落物（仅 motion 推进，不做瞬移）。
     */
    @SuppressWarnings("null")
    private static void applyMagneticPull(ServerPlayer player, PlayerStardewData data) {
        int radiusBonus = data.getTempMagneticRadiusBonus();
        if (radiusBonus <= 0) {
            return;
        }
        if (player.isSpectator()) {
            return;
        }

        // Radius is configured directly in blocks (e.g. +3 => pull items within 3 blocks).
        double radius = Math.max(1.0, radiusBonus);
        AABB playerBox = player.getBoundingBox();
        AABB range = playerBox.inflate(radius, radius * 0.75, radius);
        Vec3 target = player.position().add(0.0, 0.6, 0.0);

        for (ItemEntity item : player.level().getEntitiesOfClass(ItemEntity.class, range, ItemEntity::isAlive)) {
            Vec3 itemPos = item.position().add(0.0, 0.1, 0.0);
            Vec3 delta = target.subtract(itemPos);
            double dist = delta.length();
            if (dist < 0.05 || dist > radius) {
                continue;
            }

            Vec3 dir = delta.scale(1.0 / dist);
            double t = 1.0 - (dist / radius);
            double accel = 0.04 + t * 0.12;
            Vec3 pull = dir.scale(accel);

            Vec3 nextMotion = item.getDeltaMovement().scale(0.82).add(pull);
            double maxSpeed = 0.45;
            if (nextMotion.lengthSqr() > maxSpeed * maxSpeed) {
                nextMotion = nextMotion.normalize().scale(maxSpeed);
            }

            item.setDeltaMovement(nextMotion);
            item.hasImpulse = true;
            item.hurtMarked = true;
        }
    }
}
