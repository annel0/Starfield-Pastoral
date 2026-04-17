package com.stardew.craft.item.tool;

import com.stardew.craft.item.IStardewItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 传送魔杖 — 右键打开传送轮盘 UI，选择目的地进行传送。
 * 对应 SDV Return Scepter，扩展为多目的地版本。
 */
public class WarpWandItem extends Item implements IStardewItem {

    public WarpWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.magic";
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@javax.annotation.Nonnull Level level, @javax.annotation.Nonnull Player player, @javax.annotation.Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            openWarpWheel();
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /** 客户端侧打开传送轮盘。单独方法避免服务端加载客户端类。 */
    private void openWarpWheel() {
        com.stardew.craft.client.gui.WarpWheelScreen.open();
    }
}
