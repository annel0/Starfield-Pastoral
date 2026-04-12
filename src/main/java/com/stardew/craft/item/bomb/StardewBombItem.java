package com.stardew.craft.item.bomb;

import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.entity.bomb.StardewBombEntity;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * SDV 炸弹物品 — 右键放置在方块表面，生成引信实体。
 */
public class StardewBombItem extends Item implements IStardewItem {

    private final String typeKey;
    private final int sellPrice;
    private final BombType bombType;

    public StardewBombItem(BombType bombType, int sellPrice, Properties properties) {
        super(properties);
        this.typeKey = "stardewcraft.type.resource";
        this.sellPrice = sellPrice;
        this.bombType = bombType;
    }

    public BombType getBombType() { return bombType; }

    @Override
    public String getItemTypeKey() { return typeKey; }

    @Override
    public int getSellPrice(ItemStack stack) { return sellPrice; }

    @SuppressWarnings("null")
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Vec3 clickPos = context.getClickLocation();

        // 生成炸弹实体
        StardewBombEntity bomb = new StardewBombEntity(ModEntities.STARDEW_BOMB.get(), level);
        bomb.setBombType(bombType);
        bomb.setOwner(context.getPlayer());
        // SDV: 放置在瓦片中心
        bomb.setPos(
            Math.floor(clickPos.x) + 0.5,
            Math.floor(clickPos.y),
            Math.floor(clickPos.z) + 0.5
        );
        level.addFreshEntity(bomb);

        // SDV: 放置音效 "thudStep"
        if (context.getPlayer() != null) {
            context.getPlayer().playSound(ModSounds.THUD_STEP.get(), 0.8f, 1.0f);
            // 消耗物品
            if (!context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.CONSUME;
    }
}
