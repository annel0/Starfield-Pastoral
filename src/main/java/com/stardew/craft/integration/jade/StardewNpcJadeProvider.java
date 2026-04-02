package com.stardew.craft.integration.jade;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@SuppressWarnings("null")
public enum StardewNpcJadeProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "stardew_npc");
    private static final String NBT_NPC_ID = "npcId";

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (!(accessor.getEntity() instanceof StardewNpcEntity npc)) {
            return;
        }
        String npcId = npc.getNpcId();
        if (npcId != null && !npcId.isBlank()) {
            tag.putString(NBT_NPC_ID, npcId);
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data == null || !data.contains(NBT_NPC_ID)) {
            return;
        }
        String npcId = data.getString(NBT_NPC_ID);
        if (npcId.isBlank()) {
            return;
        }
        Component name = Component.translatable("entity.stardewcraft.npc." + npcId);
        tooltip.add(name.copy().withStyle(ChatFormatting.WHITE));
    }
}
