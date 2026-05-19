package com.stardew.craft.item.mastery;

import com.stardew.craft.item.IStardewItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Treasure Totem — 采集精通奖励道具。
 * 占位：右键消耗 1 个，给玩家一个采集宝物加成的状态效果（阶段 6b 接入实际 MobEffect）。
 */
@SuppressWarnings("null")
public class TreasureTotemItem extends Item implements IStardewItem {

    private final String itemTypeKey;
    private final int sellPrice;

    public TreasureTotemItem(String itemTypeKey, int sellPrice, Properties properties) {
        super(properties);
        this.itemTypeKey = itemTypeKey;
        this.sellPrice = sellPrice;
    }

    @Override
    public String getItemTypeKey() { return itemTypeKey; }

    @Override
    public int getSellPrice(ItemStack stack) { return sellPrice; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        // 不在采集区域 → 不消耗
        if (!com.stardew.craft.mastery.effect.TreasureTotemService.canActivateAt(player)) {
            com.stardew.craft.mastery.effect.TreasureTotemService.activate(player); // 仅播放 cancel 音
            return InteractionResultHolder.fail(stack);
        }
        com.stardew.craft.mastery.effect.TreasureTotemService.activate(player);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.success(stack);
    }
}
