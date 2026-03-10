package com.stardew.craft.mining;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.io.InputStream;

/**
 * NBT 结构加载器 - 使用原版结构系统
 */
public final class StructureLoader {
    private StructureLoader() {}

    /**
     * 从 resources 加载并放置 NBT 结构文件
     * 
     * @param level 服务端世界
     * @param structurePath 结构文件路径（相对于 resources，如 "data/stardewcraft/structures/mine_entrance.nbt"）
     * @param pos 放置位置（结构的左下角）
     */
    @SuppressWarnings("null")
    public static void loadAndPlace(ServerLevel level, String structurePath, BlockPos pos) {
        try {
            // 从 resources 读取 NBT 文件
            InputStream stream = StructureLoader.class.getClassLoader().getResourceAsStream(structurePath);
            if (stream == null) {
                StardewCraft.LOGGER.error("Structure file not found: {}", structurePath);
                return;
            }

            // 读取 NBT
            @SuppressWarnings("null")
            CompoundTag nbt = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
            stream.close();

            // 使用原版结构模板系统
            StructureTemplate template = new StructureTemplate();
            template.load(level.holderLookup(net.minecraft.core.registries.Registries.BLOCK), nbt);

            // 放置设置
            StructurePlaceSettings settings = new StructurePlaceSettings()
                .setIgnoreEntities(false);  // 包含实体

            // 放置结构
            template.placeInWorld(level, pos, pos, settings, level.random, 3);

            StardewCraft.LOGGER.info("Successfully placed structure {} at {}", structurePath, pos);

        } catch (Exception e) {
            StardewCraft.LOGGER.error("Failed to load/place structure {}: {}", structurePath, e.getMessage(), e);
        }
    }
}
