package com.stardew.craft.item;

import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.shop.DwarfService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * Dwarvish Translation Guide — a consumable quest item.
 * Right-click to learn Dwarvish (sets the HasDwarvishTranslationGuide mail flag
 * and consumes the item).  NOT an artifact; cannot be donated to the museum.
 */
public class DwarvishTranslationGuideItem extends Item implements IStardewItem {

    public DwarvishTranslationGuideItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.misc";
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return -1; // not sellable
    }

    @SuppressWarnings("null")
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            PlayerStardewData data = PlayerDataManager.getPlayerData(sp);
            if (!data.hasMailFlag(DwarfService.MAIL_FLAG)) {
                data.addMailFlag(DwarfService.MAIL_FLAG);
                sp.playSound(net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, 1.0f, 1.0f);
                sp.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                        "stardewcraft.item.dwarvish_translation_guide.learned"));
            } else {
                // Already learned — no effect, still consume
                sp.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                        "stardewcraft.item.dwarvish_translation_guide.already_learned"));
            }
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
