package com.stardew.craft.communitycenter.state;

import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.server.level.ServerPlayer;

/**
 * 社区中心故事进度标记。
 * 使用 {@link PlayerStardewData#hasMailFlag} / {@link PlayerStardewData#addMailFlag} 持久化。
 */
public final class CCStoryFlags {

    private CCStoryFlags() {}

    // ── 主线进度 ──
    /** CC 大门已解锁（Lewis 带路后设置） */
    public static final String CC_DOOR_UNLOCKED   = "ccDoorUnlock";
    /** 首次看到 JunimoNote（触发巫师邀请信） */
    public static final String SEEN_JUNIMO_NOTE    = "seenJunimoNote";
    /** 能读懂 Junimo 文字（巫师解锁后设置） */
    public static final String CAN_READ_JUNIMO     = "canReadJunimoText";
    /** 全部区域完成 */
    public static final String CC_IS_COMPLETE      = "ccIsComplete";
    /** 玩家已获得骷髅钥匙（SDV mailReceived "HasSkullKey"）——骷髅矿洞通行证 */
    public static final String HAS_SKULL_KEY       = "HasSkullKey";

    // ── 巫师邀请信 ──
    /** 巫师邀请信邮件 ID */
    public static final String WIZARD_JUNIMO_NOTE  = "wizardJunimoNote";

    // ── 区域完成标记（与 CommunityCenterProgress.getAreaRewardMailFlag() 一致） ──
    public static final String CC_PANTRY      = "ccPantry";
    public static final String CC_CRAFTS_ROOM = "ccCraftsRoom";
    public static final String CC_FISH_TANK   = "ccFishTank";
    public static final String CC_BOILER_ROOM = "ccBoilerRoom";
    public static final String CC_VAULT       = "ccVault";
    public static final String CC_BULLETIN    = "ccBulletin";

    /** 根据 areaId 获取区域完成标记 */
    public static String areaFlag(int areaId) {
        return switch (areaId) {
            case 0 -> CC_PANTRY;
            case 1 -> CC_CRAFTS_ROOM;
            case 2 -> CC_FISH_TANK;
            case 3 -> CC_BOILER_ROOM;
            case 4 -> CC_VAULT;
            case 5 -> CC_BULLETIN;
            default -> "";
        };
    }

    // ── 便捷查询 ──

    public static boolean hasFlag(ServerPlayer player, String flag) {
        return PlayerDataManager.getPlayerData(player).hasMailFlag(flag);
    }

    public static void addFlag(ServerPlayer player, String flag) {
        PlayerDataManager.getPlayerData(player).addMailFlag(flag);
    }

    public static boolean canReadJunimoText(ServerPlayer player) {
        return hasFlag(player, CAN_READ_JUNIMO);
    }

    public static boolean hasSeenJunimoNote(ServerPlayer player) {
        return hasFlag(player, SEEN_JUNIMO_NOTE);
    }
}
