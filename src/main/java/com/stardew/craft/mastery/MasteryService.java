package com.stardew.craft.mastery;

import com.stardew.craft.player.PlayerDataEventHandler;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.player.SkillType;
import net.minecraft.server.level.ServerPlayer;

/**
 * 服务端：精通奖励校验与发放。
 * 注意：阶段 0 只标记 claim 状态；具体物品/方块奖励发放在阶段 3 接入 (TODO: deliverItems)。
 */
public final class MasteryService {

    private MasteryService() {}

    /**
     * 尝试为玩家领取某技能的 mastery 奖励。返回是否成功。
     * 失败原因：等级不足 / 已领过。
     */
    public static boolean tryClaim(ServerPlayer player, SkillType skill) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (data == null) return false;
        if (!data.claimMasteryReward(skill)) return false;

        deliverRewards(player, skill);

        // TODO(阶段 6): 若 data.hasClaimedAllMasteryRewards() → 触发完成 cutscene。

        PlayerDataEventHandler.syncPlayerData(player, data);
        return true;
    }

    private static void deliverRewards(ServerPlayer player, SkillType skill) {
        // Trinket 槽奖励：claimMasteryReward(COMBAT) 内部已处理。
        for (MasteryRewardRegistry.RewardEntry entry : MasteryRewardRegistry.rewardsFor(skill)) {
            if (entry.isRecipe()) {
                PlayerStardewData data = PlayerDataManager.getPlayerData(player);
                if (data != null) {
                    data.unlockRecipe(entry.recipeId());
                }
                continue;
            }
            if (!entry.isItem()) continue;
            net.minecraft.world.item.ItemStack stack = entry.stack().get();
            if (stack == null || stack.isEmpty()) continue;
            if (!player.getInventory().add(stack.copy())) {
                player.drop(stack.copy(), false);
            }
        }
    }
}
