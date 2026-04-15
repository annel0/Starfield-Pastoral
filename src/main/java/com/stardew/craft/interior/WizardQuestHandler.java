package com.stardew.craft.interior;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.communitycenter.network.BundleSyncPayload;
import com.stardew.craft.communitycenter.state.CCStoryFlags;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.entity.junimo.JunimoEntity;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.network.payload.OpenNpcDialogueScreenPayload;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 巫师塔枢纽任务逻辑：
 * - 首次见面剧情对话
 * - 末影之眼交付检测
 * - 解锁星露谷入口
 * - 日常传送 query 分支路由
 */
@SuppressWarnings({"null", "unused"})
@EventBusSubscriber(modid = StardewCraft.MODID)
public final class WizardQuestHandler {

    private static final String NPC_ID = "wizard";

    // 巫师在巫师塔内部的位置（与 default_spawns.json 一致）
    private static final BlockPos WIZARD_POS = new BlockPos(18249, 71, 17095);
    private static final double PROXIMITY_RANGE_SQ = 5.0 * 5.0; // 5 格内自动触发
    private static final int PROXIMITY_CHECK_INTERVAL = 20; // 每秒检测一次
    private static final String PROXIMITY_COOLDOWN_TAG = "stardewcraft_wizard_prox_tick";

    // 特殊 nextDialogueNode 值，用于识别巫师传送指令
    public static final String NODE_GO_STARDEW = "wizard_go_stardew";
    public static final String NODE_GO_OVERWORLD = "wizard_go_overworld";
    public static final String NODE_STAY = "wizard_stay";
    public static final String NODE_ACCEPTED = "wizard_accepted";
    public static final String NODE_FIRST_TELEPORT = "wizard_first_teleport";
    public static final String NODE_GALAXY_CONFIRM = "wizard_galaxy_confirm";
    public static final String NODE_GALAXY_DECLINE = "wizard_galaxy_decline";

    // ── SDV Event 112: 巫师森林魔法药水（多阶段过场） ──
    /** Phase 0→1: 巫师展示 Junimo ("看哪！") */
    public static final String NODE_E112_REVEAL     = "wizard_e112_reveal";
    /** Phase 1→2: Junimo 笼子消失，巫师解释 */
    public static final String NODE_E112_DISMISS     = "wizard_e112_dismiss";
    /** Phase 2→2.5: 巫师说"你留在这，我去看看"→ 消失 → 等待 → 回来 */
    public static final String NODE_E112_WIZARD_LEAVE = "wizard_e112_wizard_leave";
    /** Phase 2.5→3: 巫师回来后翻译卷轴 → 走到大锅 */
    public static final String NODE_E112_CAULDRON    = "wizard_e112_cauldron";
    /** Phase 3→4: 玩家喝药水 → 森林幻视 → 解锁 */
    public static final String NODE_E112_DRINK       = "wizard_e112_drink";

    /** 巫师塔内的 Junimo 展示位置（巫师附近） */
    private static final BlockPos JUNIMO_CAGE_POS = WIZARD_POS.offset(0, 0, 2);
    /** Event 112 过程中生成的临时 Junimo 的 tag */
    private static final String TAG_E112_JUNIMO = "stardewcraft_e112_junimo";
    /** 巫师离开后的等待倒计 tag（存 tick 数） */
    private static final String TAG_E112_WIZARD_AWAY_TICK = "stardewcraft_e112_wizard_away";
    /** 巫师消失到返回的等待时间（tick），约4秒 */
    private static final int WIZARD_AWAY_DURATION = 80;
    /** 巫师消失时传送到的远处 Y（世界外） */
    private static final BlockPos WIZARD_HIDDEN_POS = WIZARD_POS.offset(0, -200, 0);

    private WizardQuestHandler() {}

    /**
     * 玩家靠近巫师时自动触发对话。
     * 仅在首次见面时自动触发（任务发布），其他情况需要右键交互。
     * 同时处理 E112 巫师离开后的等待倒计时。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!ModDimensions.STARDEW_VALLEY.equals(player.level().dimension())) return;

        // ── E112 巫师离开倒计时 ──
        if (player.getPersistentData().contains(TAG_E112_WIZARD_AWAY_TICK)) {
            int tick = player.getPersistentData().getInt(TAG_E112_WIZARD_AWAY_TICK) + 1;
            if (tick >= WIZARD_AWAY_DURATION) {
                player.getPersistentData().remove(TAG_E112_WIZARD_AWAY_TICK);
                handleE112WizardReturn(player);
            } else {
                player.getPersistentData().putInt(TAG_E112_WIZARD_AWAY_TICK, tick);
            }
            return; // 巫师不在时不处理其他接近逻辑
        }

        if (player.tickCount % PROXIMITY_CHECK_INTERVAL != 0) return;

        PlayerStardewData data = PlayerDataManager.getPlayerData(player);

        // 只有首次见面才自动触发对话
        if (data.isWizardFirstMet()) return;

        // 检测是否在巫师附近
        double distSq = player.blockPosition().distSqr(WIZARD_POS);
        if (distSq > PROXIMITY_RANGE_SQ) {
            player.getPersistentData().remove(PROXIMITY_COOLDOWN_TAG);
            return;
        }

        // 冷却检测：进入范围后只触发一次
        if (player.getPersistentData().contains(PROXIMITY_COOLDOWN_TAG)) return;
        player.getPersistentData().putBoolean(PROXIMITY_COOLDOWN_TAG, true);

        // 首次见面剧情对话
        data.setWizardFirstMet(true);
        sendDialogue(player, "stardewcraft.npc.wizard.intro_1", 0);
    }

    /**
     * 在 NPC 交互时调用。如果是巫师，优先处理任务相关对话。
     * @return true 如果已处理（调用方应跳过普通对话），false 走普通对话
     */
    public static boolean handleWizardInteraction(ServerPlayer player) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);

        // ── SDV Event 112 parity: 巫师森林魔法药水 → 解锁 Junimo 文字 ──
        // 玩家看过 JunimoNote（收到巫师邀请信）但尚未解锁 → 触发多阶段过场
        if (CCStoryFlags.hasSeenJunimoNote(player) && !CCStoryFlags.canReadJunimoText(player)) {
            // Phase 0: 巫师自我介绍 + "我要给你看样东西"
            playSound(player, ModSounds.DWOP.get());
            sendDialogue(player, "stardewcraft.npc.wizard.e112_intro", 0);
            return true;
        }

        if (data.isWizardQuestComplete()) {
            // Galaxy Sword: 手持棱彩碎片 + 尚未获取过 Galaxy Sword
            if (!data.hasMailFlag("galaxySword") && findPrismaticShard(player) != null) {
                sendDialogue(player, "stardewcraft.npc.wizard.galaxy_offer", 0);
                return true;
            }
            // 任务已完成 → 走普通 NPC 对话系统（每日1次限制、对话轮转、好感度）
            return false;
        }

        if (!data.isWizardFirstMet()) {
            // 首次见面：触发剧情第1页
            data.setWizardFirstMet(true);
            sendDialogue(player, "stardewcraft.npc.wizard.intro_1", 0);
            return true;
        }

        // 已见过但未完成任务：检查背包是否有末影之眼
        ItemStack eyeSlot = findEyeOfEnder(player);
        if (eyeSlot != null) {
            eyeSlot.shrink(1);
            data.setWizardQuestComplete(true);
            sendDialogue(player, "stardewcraft.npc.wizard.has_eye", 0);
            StardewCraft.LOGGER.info("[WIZARD] {} completed wizard quest (Eye of Ender)", player.getName().getString());

            // 法师任务完成 → 立即触发故事任务（不必等到下一次 onDayStarted）
            com.stardew.craft.time.StardewTimeManager tm = com.stardew.craft.time.StardewTimeManager.get();
            int absDay = (tm.getCurrentYear() - 1) * 112 + tm.getCurrentSeason() * 28 + tm.getCurrentDay();
            data.getQuestManager().triggerStoryQuests(player, absDay);
            return true;
        }

        // 没有末影之眼：提醒（任务进行中，仍拦截）
        sendDialogue(player, "stardewcraft.npc.wizard.daily_locked", 0);
        return true;
    }

    /**
     * 处理 AnswerNpcQuestionPayload 的回调。
     * @return true 如果已处理，false 走普通 handleClientQuestionAnswer 链
     */
    public static boolean handleWizardQuestionAnswer(ServerPlayer player, String nextDialogueNode) {
        if (nextDialogueNode == null) return false;

        return switch (nextDialogueNode) {
            case NODE_GO_STARDEW -> {
                CrossDimensionTeleporter.wizardInteriorToStardewOutdoor(player);
                yield true;
            }
            case NODE_GO_OVERWORLD -> {
                CrossDimensionTeleporter.wizardInteriorToOverworld(player);
                yield true;
            }
            case NODE_STAY -> {
                // 关闭对话，什么也不做
                yield true;
            }
            case NODE_ACCEPTED -> {
                // 玩家接受了任务，显示后续对话
                sendDialogue(player, "stardewcraft.npc.wizard.accepted", 0);
                yield true;
            }
            case NODE_FIRST_TELEPORT -> {
                // 首次解锁后传送到星露谷室外（农场门前）
                CrossDimensionTeleporter.wizardInteriorToStardewOutdoor(player);
                yield true;
            }
            case NODE_GALAXY_CONFIRM -> {
                // 玩家同意交出棱彩碎片 → 获得 Galaxy Sword
                handleGalaxyConfirm(player);
                yield true;
            }
            case NODE_GALAXY_DECLINE -> {
                // 玩家拒绝，关闭对话
                yield true;
            }
            // ── SDV Event 112 多阶段过场 ──
            case NODE_E112_REVEAL -> {
                handleE112Reveal(player);
                yield true;
            }
            case NODE_E112_DISMISS -> {
                handleE112Dismiss(player);
                yield true;
            }
            case NODE_E112_WIZARD_LEAVE -> {
                handleE112WizardLeave(player);
                yield true;
            }
            case NODE_E112_CAULDRON -> {
                // Phase 2→3: 走到大锅
                sendDialogue(player, "stardewcraft.npc.wizard.e112_cauldron", 0);
                yield true;
            }
            case NODE_E112_DRINK -> {
                handleE112Drink(player);
                yield true;
            }
            default -> false;
        };
    }

    // ═══════════════════════════════════════════════════════════
    // SDV Event 112 — Phase handlers
    // ═══════════════════════════════════════════════════════════

    /**
     * Phase 0→1: "看哪！" — screenFlash + playSound wand + 生成笼中 Junimo
     * SDV: showFrame Wizard 19/playSound wand/screenFlash .8/warp Junimo 10 17/specificTemporarySprite junimoCage
     */
    private static void handleE112Reveal(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        // 闪白 + wand 音效
        playSound(player, ModSounds.WAND.get());
        sendScreenFlash(player);
        // 生成临时 Junimo（绿色，笼中展示）
        JunimoEntity junimo = new JunimoEntity(ModEntities.JUNIMO.get(), level);
        junimo.setJunimoColor(0x32CD32); // SDV 默认绿色 (LimeGreen)
        junimo.moveTo(JUNIMO_CAGE_POS.getX() + 0.5, JUNIMO_CAGE_POS.getY(), JUNIMO_CAGE_POS.getZ() + 0.5, 0, 0);
        junimo.setNoAi(true);
        junimo.setNoTimeout(true);
        junimo.addTag(TAG_E112_JUNIMO);
        level.addFreshEntity(junimo);
        // junimoMeep1 音效 (SDV: shake Junimo + junimoMeep1 × 4)
        playSound(player, ModSounds.DWOP.get());
        // Phase 1 对话：Junimo 笼
        sendDialogue(player, "stardewcraft.npc.wizard.e112_cage", 0);
    }

    /**
     * Phase 1→2: Junimo 笼消失 — screenFlash + playSound wand + 消除 Junimo
     * SDV: playSound dwop/playSound wand/screenFlash .8/warp Junimo -3000/specificTemporarySprite junimoCageGone
     */
    private static void handleE112Dismiss(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        // 闪白 + wand 音效
        playSound(player, ModSounds.WAND.get());
        sendScreenFlash(player);
        // 移除笼中 Junimo
        level.getEntitiesOfClass(JunimoEntity.class,
                new net.minecraft.world.phys.AABB(JUNIMO_CAGE_POS).inflate(3.0),
                e -> e.getTags().contains(TAG_E112_JUNIMO)
        ).forEach(net.minecraft.world.entity.Entity::discard);
        // Phase 2 对话：巫师解释 CC + "你留在这，我去看看"
        sendDialogue(player, "stardewcraft.npc.wizard.e112_note", 0);
    }

    /**
     * Phase 2→2.5: 巫师说"你留在这" → 闪白 + 消失 → 启动倒计时
     * SDV: showFrame Wizard 16/playSound wand/warp Wizard -3000/specificTemporarySprite wizardWarp
     */
    private static void handleE112WizardLeave(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        // 闪白 + wand 音效
        playSound(player, ModSounds.WAND.get());
        sendScreenFlash(player);
        // 把巫师传送到远处（隐藏）
        findWizardNpc(level).ifPresent(wizard -> {
            wizard.setInvisible(true);
            wizard.moveTo(WIZARD_HIDDEN_POS.getX() + 0.5,
                    WIZARD_HIDDEN_POS.getY(),
                    WIZARD_HIDDEN_POS.getZ() + 0.5, 0, 0);
        });
        // 启动倒计时（onPlayerTick 中递增）
        player.getPersistentData().putInt(TAG_E112_WIZARD_AWAY_TICK, 0);
        StardewCraft.LOGGER.debug("[WIZARD] E112: wizard leaves, timer started");
    }

    /**
     * Phase 2.5 (自动触发): 巫师回来 → 门声 → 发送翻译卷轴 + 大锅对话
     * SDV: playSound doorClose/warp Wizard 8 24/"I found the note..."
     */
    private static void handleE112WizardReturn(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        // 门声
        player.level().playSound(null, player.blockPosition(),
                net.minecraft.sounds.SoundEvents.WOODEN_DOOR_CLOSE,
                SoundSource.BLOCKS, 1.0f, 1.0f);
        // 巫师回到原位
        findWizardNpc(level).ifPresent(wizard -> {
            wizard.setInvisible(false);
            wizard.moveTo(WIZARD_POS.getX() + 0.5,
                    WIZARD_POS.getY(),
                    WIZARD_POS.getZ() + 0.5,
                    wizard.getYRot(), 0);
        });
        playSound(player, ModSounds.DWOP.get());
        StardewCraft.LOGGER.debug("[WIZARD] E112: wizard returns");
        // 自动弹出对话（无需玩家再次交互）
        sendDialogue(player, "stardewcraft.npc.wizard.e112_return", 0);
    }

    /**
     * Phase 3→4: 玩家喝下森林魔法药水
     * SDV: farmerEat 184/playSound gulp/specificTemporarySprite farmerForestVision/globalFade/playSound reward
     */
    private static void handleE112Drink(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        // gulp 音效 (SDV: playSound gulp)
        player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.GENERIC_DRINK,
                SoundSource.PLAYERS, 1.0f, 1.0f);
        // 森林幻视粒子效果 (SDV: specificTemporarySprite farmerForestVision)
        for (int i = 0; i < 30; i++) {
            double ox = (level.random.nextDouble() - 0.5) * 6;
            double oy = level.random.nextDouble() * 3;
            double oz = (level.random.nextDouble() - 0.5) * 6;
            level.sendParticles(player, ParticleTypes.HAPPY_VILLAGER, true,
                    player.getX() + ox, player.getY() + oy, player.getZ() + oz,
                    1, 0, 0, 0, 0);
        }
        // 闪白
        sendScreenFlash(player);
        // reward 音效 (SDV: playSound reward)
        playSound(player, ModSounds.REWARD.get());
        // 设置 canReadJunimoText 标记
        CCStoryFlags.addFlag(player, CCStoryFlags.CAN_READ_JUNIMO);
        BundleSyncPayload.sendFullSync(player);
        StardewCraft.LOGGER.info("[WIZARD] {} unlocked canReadJunimoText (SDV Event 112)",
                player.getName().getString());
        // 最终消息：你得到了森林的魔力！
        sendDialogue(player, "stardewcraft.npc.wizard.e112_complete", 0);
    }

    /** 在巫师塔附近（含隐藏位置）搜索巫师 NPC 实体 */
    private static java.util.Optional<StardewNpcEntity> findWizardNpc(ServerLevel level) {
        // 搜索 WIZARD_POS 周围大范围（包含 WIZARD_HIDDEN_POS）
        net.minecraft.world.phys.AABB searchBox = new net.minecraft.world.phys.AABB(WIZARD_POS).inflate(250);
        return level.getEntitiesOfClass(StardewNpcEntity.class, searchBox,
                e -> NPC_ID.equals(e.getNpcId())
        ).stream().findFirst();
    }

    /** 向玩家发送屏幕闪白 (SDV: screenFlash .8) */
    private static void sendScreenFlash(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player,
                new com.stardew.craft.communitycenter.cutscene.CutscenePayload(
                        com.stardew.craft.communitycenter.cutscene.CutscenePayload.TYPE_AREA_RESTORE,
                        com.stardew.craft.communitycenter.cutscene.CutscenePayload.PHASE_RESTORE,
                        -1, player.blockPosition()));
    }

    /** 播放音效给附近玩家 */
    private static void playSound(ServerPlayer player, net.minecraft.sounds.SoundEvent sound) {
        player.level().playSound(null, player.blockPosition(), sound, SoundSource.MASTER, 1.0f, 1.0f);
    }

    // ═══════════════════════════════════════════════════════════

    /**
     * 玩家确认交出棱彩碎片 → 消耗碎片 → 给予 Galaxy Sword → 设置 galaxySword 标记。
     */
    private static void handleGalaxyConfirm(ServerPlayer player) {
        ItemStack shard = findPrismaticShard(player);
        if (shard == null) {
            // 碎片已不在背包（可能被丢弃），回退到日常对话
            sendDialogue(player, "stardewcraft.npc.wizard.daily_unlocked", 0);
            return;
        }
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (data.hasMailFlag("galaxySword")) {
            sendDialogue(player, "stardewcraft.npc.wizard.daily_unlocked", 0);
            return;
        }

        // 消耗 1 个棱彩碎片
        shard.shrink(1);

        // 给予 Galaxy Sword
        ItemStack galaxySword = new ItemStack(ModItems.GALAXY_SWORD.get());
        if (!player.getInventory().add(galaxySword)) {
            // 背包满则掉落在脚下
            player.drop(galaxySword, false);
        }

        // 设置标记 → 马龙商店解锁 Galaxy Dagger / Galaxy Hammer
        data.addMailFlag("galaxySword");

        StardewCraft.LOGGER.info("[WIZARD] {} obtained Galaxy Sword via Prismatic Shard", player.getName().getString());
        sendDialogue(player, "stardewcraft.npc.wizard.galaxy_granted", 0);
    }

    private static ItemStack findPrismaticShard(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(ModItems.PRISMATIC_SHARD.get())) {
                return stack;
            }
        }
        return null;
    }

    private static ItemStack findEyeOfEnder(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.ENDER_EYE) && !stack.isEmpty()) {
                return stack;
            }
        }
        return null;
    }

    private static void sendDialogue(ServerPlayer player, String translateKey, int friendshipPoints) {
        PacketDistributor.sendToPlayer(player,
            new OpenNpcDialogueScreenPayload(NPC_ID, translateKey, friendshipPoints));
    }
}
