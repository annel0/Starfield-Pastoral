package com.stardew.craft.integration.jade;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.blockentity.MailboxBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MailboxJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
	INSTANCE;

	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "mailbox");

	private static final String NBT_OWNER_NAME = "OwnerName";
	private static final String NBT_HAS_OWNER = "HasOwner";

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@SuppressWarnings("null")
	@Override
	public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
		BlockEntity be = accessor.getBlockEntity();
		if (be instanceof MailboxBlockEntity mailbox) {
			tag.putBoolean(NBT_HAS_OWNER, mailbox.hasOwner());
			if (mailbox.hasOwner()) {
				tag.putString(NBT_OWNER_NAME, mailbox.getOwnerName());
			}
		}
	}

	@SuppressWarnings("null")
	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		CompoundTag tag = accessor.getServerData();
		if (tag.getBoolean(NBT_HAS_OWNER)) {
			String ownerName = tag.getString(NBT_OWNER_NAME);
			tooltip.add(Component.translatable("stardewcraft.jade.mailbox.owner", ownerName)
					.withStyle(ChatFormatting.GRAY));
		}
	}
}
