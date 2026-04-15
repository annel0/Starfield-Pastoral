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
@SuppressWarnings("null")
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

            // 同步社区中心 bundle 数据到客户端 (星盘渲染等需要)
            com.stardew.craft.communitycenter.network.BundleSyncPayload.sendFullSync(player);

            // 同步任务日志到客户端
            com.stardew.craft.quest.QuestManager qm = data.getQuestManager();
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                com.stardew.craft.quest.network.QuestLogSyncPayload.fromQuests(
                    qm.getQuestLog(), qm.getBillboardQuestsDone(), qm.getDailyQuestCompletedDays()));

            // 如果玩家登录时已在星露谷维度，确保农场初始化
            // （PlayerChangedDimensionEvent 在这种情况下不会触发）
            if (player.serverLevel().dimension() == com.stardew.craft.core.ModDimensions.STARDEW_VALLEY) {
                com.stardew.craft.dimension.FarmInitializer.ensureInitialized(player.serverLevel());
            }

            // 首次登录/每次登录时触发 fireDayStarted 以补偿新存档第1天没有过夜结算的情况
            // （advanceDay 只在过夜时调用，新存档春1没有过夜，quest trigger 不会触发）
            com.stardew.craft.time.StardewTimeManager tm = com.stardew.craft.time.StardewTimeManager.get();
            int absDay = (tm.getCurrentYear() - 1) * 112 + tm.getCurrentSeason() * 28 + tm.getCurrentDay();
            com.stardew.craft.quest.StardewQuestEvents.fireDayStarted(player, absDay);
        }
    }
    
    /**
     * 玩家退出时保存数据
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 清理动态光源
            PlayerGlowHandler.onPlayerLeave(player);

            // Clean up any pending geode treasure (prevents memory leak + item loss)
            com.stardew.craft.shop.GeodeLootService.onPlayerLogout(player);

            // Clean up all combat tracker static maps (prevents memory leak)
            com.stardew.craft.combat.CombatTrackerCleanup.onPlayerLogout(player.getUUID());

            // Clean up fishing session
            com.stardew.craft.fishing.server.FishingSessionManager.onPlayerLogout(player);

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
                // 击倒状态下不再重复处理
                if (PassOutService.isKnockedOut(player)) {
                    player.setHealth(player.getMaxHealth());
                    return;
                }
                // 兜底触发：若死因绕过了 onPlayerHurt（例如虚空、/kill），
                // 由 PassOutService 的防重入机制保证不会和 onPlayerHurt 重复执行。
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

        // 击倒状态：完全免疫所有伤害（等待传送中）
        if (PassOutService.isKnockedOut(player)) {
            event.setAmount(0.0f);
            return;
        }

        // 取消 MC 原版扣血（我们用星露谷血条承载伤害）。
        event.setAmount(0.0f);

        // 环境伤害无敌帧检查：invulnerableTime > 0 时跳过伤害
        // NeoForge 的 LivingIncomingDamageEvent 在原版 invulnerableTime 检查之前触发，
        // 因此需要在这里手动判断
        if (player.invulnerableTime > 0 && event.getSource().getEntity() == null) {
            player.setHealth(player.getMaxHealth());
            return;
        }

        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        int sdMax = Math.max(1, data.getMaxHealth());
        float mcMax = Math.max(1.0f, player.getMaxHealth());

        // 怪物攻击的 ATTACK_DAMAGE 已经是 SDV 数值，直接使用；
        // 环境伤害（摔落、火焰等无攻击实体）是 MC 数值，需要按 HP 比例映射到 SDV。
        net.minecraft.world.entity.Entity dmgSourceEntity = event.getSource().getEntity();
        float sdDamageFloat = (dmgSourceEntity != null)
                ? amount
                : amount * (sdMax / mcMax);

        // 武器防御（握持生效）
        WeaponStats weaponStats = WeaponStats.fromItemStack(player.getMainHandItem());
        float weaponDefense = weaponStats.getDefense();
        float foodDefense = data.getTempDefenseBonus();
        // 装备防御（戒指+靴子）
        com.stardew.craft.combat.equipment.EquipmentStats eqStats = com.stardew.craft.combat.equipment.EquipmentResolver.getMergedStats(player);
        float equipDefense = eqStats.getDefense();
        float totalDefense = weaponDefense + foodDefense + equipDefense;
        if (totalDefense > 0) {
            float reduction = DamageCalculator.calculateDefenseReductionFromDefense(sdDamageFloat, totalDefense);
            sdDamageFloat = Math.max(0.0f, sdDamageFloat - reduction);
        }

        // ── 戒指被动效果 ──

        // 史莱姆克星戒指：免疫史莱姆伤害
        net.minecraft.world.entity.Entity sourceEntity = event.getSource().getEntity();
        if (eqStats.hasSlimeCharmer() && sourceEntity != null) {
            net.minecraft.resources.ResourceLocation entityTypeId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                    .getKey(sourceEntity.getType());
            String entityType = entityTypeId.toString();
            if (entityType.contains("slime") || entityType.contains("green_slime")
                    || entityType.contains("frost_jelly") || entityType.contains("sludge")) {
                player.setHealth(player.getMaxHealth());
                player.getFoodData().setFoodLevel(20);
                player.getFoodData().setSaturation(5.0f);
                return; // 完全免疫史莱姆伤害
            }
        }

        // 约巴之戒：受伤时 50% 概率触发护盾（完全抵消本次伤害）
        if (eqStats.hasYobaProtection() && player.getRandom().nextFloat() < 0.50f) {
            player.setHealth(player.getMaxHealth());
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(5.0f);
            return; // 护盾挡住了伤害
        }

        // 保护戒指：10% 概率减少伤害
        if (eqStats.hasProtection() && player.getRandom().nextFloat() < 0.10f) {
            sdDamageFloat *= 0.5f;
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

        // 荆棘戒指：受伤时反射伤害给攻击者
        if (eqStats.hasThorns() && sourceEntity instanceof net.minecraft.world.entity.LivingEntity attacker) {
            net.minecraft.world.damagesource.DamageSource thornsDmg = player.damageSources().thorns(player);
            attacker.hurt(thornsDmg, sdDamage * 0.5f);
        }

        int oldSdHealth = data.getHealth();
        int newSdHealth = Math.max(0, oldSdHealth - sdDamage);
        data.setHealth(newSdHealth);
        syncPlayerData(player, data);

        // 凤凰戒指：生命值归零时复活（每天一次）
        if (newSdHealth == 0 && eqStats.hasPhoenix()) {
            long lastPhoenixDay = data.getLastPhoenixReviveDay();
            long currentDay = player.level().getGameTime() / 24000L;
            if (lastPhoenixDay < currentDay) {
                data.setLastPhoenixReviveDay(currentDay);
                int reviveHealth = Math.max(1, data.getMaxHealth() / 2);
                data.setHealth(reviveHealth);
                syncPlayerData(player, data);
                player.setHealth(player.getMaxHealth());
                player.getFoodData().setFoodLevel(20);
                player.getFoodData().setSaturation(5.0f);
                return; // 复活成功，跳过晕倒
            }
        }

        // 生命值清零：不要死，走接口（后续接"晕倒"等）。
        if (newSdHealth == 0) {
            DamageSource source = event.getSource();
            StardewDamageHooks.onHealthDepleted(player, source);
        }

        // 环境伤害（无攻击者）：设置无敌帧，防止每 tick 都造成伤害
        // 原版 setAmount(0) 不会触发 MC 无敌帧，需手动补偿
        if (dmgSourceEntity == null && player.invulnerableTime < 10) {
            player.invulnerableTime = 10; // 0.5 秒无敌帧
        }

        // 维持原版血/饱食度满，避免被其他机制"补刀"。
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

        // 发光戒指：动态光源
        PlayerGlowHandler.tick(player);

        // 法师塔指南针：服务端查找最近结构
        if (player.getMainHandItem().getItem() instanceof com.stardew.craft.item.tool.WizardTowerCompassItem
            || player.getOffhandItem().getItem() instanceof com.stardew.craft.item.tool.WizardTowerCompassItem) {
            com.stardew.craft.item.tool.WizardTowerCompassItem.serverTick(player);
        }

        if (player.level().dimension() != ModDimensions.STARDEW_VALLEY
            && player.level().dimension() != ModMiningDimensions.STARDEW_MINING) {
            return;
        }

        // 创造/旁观不强制。
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        // SDV 原版：体力 ≤ -15 时触发体力耗尽晕倒
        {
            PlayerStardewData tickData = PlayerDataManager.getPlayerData(player);
            if (tickData.getEnergy() <= -15f) {
                // 重置能量到 0（防止反复触发）
                tickData.setEnergy(0f);
                PassOutService.onExhaustionPassOut(player);
            }
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
        // sync equipment slots
        PacketDistributor.sendToPlayer(player, new com.stardew.craft.network.payload.EquipmentSyncPayload(
                data.getEquippedLeftRing(),
                data.getEquippedRightRing(),
                data.getEquippedBoots()
        ));
    }

    /**
     * 吸附掉落物（仅 motion 推进，不做瞬移）。
     */
    @SuppressWarnings("null")
    private static void applyMagneticPull(ServerPlayer player, PlayerStardewData data) {
        int radiusBonus = data.getTempMagneticRadiusBonus();
        // Add equipment magnetic radius (rings/boots)
        com.stardew.craft.combat.equipment.EquipmentStats eqStats = com.stardew.craft.combat.equipment.EquipmentResolver.getMergedStats(player);
        // magneticRadius is in SDV units (64=1 tile), convert to blocks: /64 → but in RingType we use raw SDV values (64, 128)
        // Small Magnet = +64 (1 block), Magnet Ring = +128 (2 blocks), Iridium Band = +128, Glowstone = +128
        radiusBonus += (int) Math.round(eqStats.getMagneticRadius() / 64.0);
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
