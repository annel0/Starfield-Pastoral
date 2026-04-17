package com.stardew.craft.mining;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * NBT 结构加载器 - 使用原版结构系统
 */
@SuppressWarnings("null")
public final class StructureLoader {
    private StructureLoader() {}

    @SuppressWarnings("null")
    public static boolean loadAndPlaceWithResult(ServerLevel level, String structurePath, BlockPos pos) {
        if (structurePath != null && structurePath.toLowerCase(Locale.ROOT).endsWith(".schem")) {
            return loadAndPlaceSchematic(level, structurePath, pos);
        }

        try {
            InputStream stream = StructureLoader.class.getClassLoader().getResourceAsStream(structurePath);
            if (stream == null) {
                StardewCraft.LOGGER.error("Structure file not found: {}", structurePath);
                return false;
            }

            @SuppressWarnings("null")
            CompoundTag nbt = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
            stream.close();

            StructureTemplate template = new StructureTemplate();
            template.load(level.holderLookup(net.minecraft.core.registries.Registries.BLOCK), nbt);

            Vec3i size = template.getSize();
            ensureChunksLoaded(level, pos, Math.max(1, size.getX()), Math.max(1, size.getZ()));

            StructurePlaceSettings settings = new StructurePlaceSettings()
                .setIgnoreEntities(false);

            template.placeInWorld(level, pos, pos, settings, level.random, 3);

            StardewCraft.LOGGER.info("Successfully placed structure {} at {}", structurePath, pos);
            return true;
        } catch (Exception e) {
            StardewCraft.LOGGER.error("Failed to load/place structure {}: {}", structurePath, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 从 resources 加载并放置 NBT 结构文件
     * 
     * @param level 服务端世界
     * @param structurePath 结构文件路径（相对于 resources，如 "data/stardewcraft/structures/mine_entrance.nbt"）
     * @param pos 放置位置（结构的左下角）
     */
    @SuppressWarnings("null")
    public static void loadAndPlace(ServerLevel level, String structurePath, BlockPos pos) {
        loadAndPlaceWithResult(level, structurePath, pos);
    }

    @SuppressWarnings("null")
    private static boolean loadAndPlaceSchematic(ServerLevel level, String structurePath, BlockPos origin) {
        try (InputStream stream = StructureLoader.class.getClassLoader().getResourceAsStream(structurePath)) {
            if (stream == null) {
                StardewCraft.LOGGER.error("Schematic file not found: {}", structurePath);
                return false;
            }

            CompoundTag root = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());

            CompoundTag schematic = root;
            if ((readDimension(root, "Width") == 0 || readDimension(root, "Height") == 0 || readDimension(root, "Length") == 0)
                && root.contains("Schematic", Tag.TAG_COMPOUND)) {
                schematic = root.getCompound("Schematic");
            }

            int width = readDimension(schematic, "Width");
            int height = readDimension(schematic, "Height");
            int length = readDimension(schematic, "Length");
            if (width <= 0 || height <= 0 || length <= 0) {
                StardewCraft.LOGGER.error("Invalid schematic size for {}: {}x{}x{}, rootKeys={}, schematicKeys={}",
                    structurePath, width, height, length, root.getAllKeys(), schematic.getAllKeys());
                return false;
            }

            CompoundTag paletteTag = schematic.getCompound("Palette");
            byte[] blockDataRaw = schematic.getByteArray("BlockData");

            if ((paletteTag.isEmpty() || blockDataRaw.length == 0) && schematic.contains("Blocks", Tag.TAG_COMPOUND)) {
                CompoundTag blocksTag = schematic.getCompound("Blocks");
                if (paletteTag.isEmpty()) {
                    paletteTag = blocksTag.getCompound("Palette");
                }
                if (blockDataRaw.length == 0) {
                    blockDataRaw = blocksTag.getByteArray("Data");
                }
            }

            if (paletteTag.isEmpty()) {
                StardewCraft.LOGGER.error("Schematic {} has empty palette, keys={}", structurePath, schematic.getAllKeys());
                return false;
            }

            int paletteMax = Math.max(1, schematic.getInt("PaletteMax"));
            BlockState[] paletteStates = new BlockState[Math.max(paletteMax, paletteTag.size()) + 1];
            for (String stateString : paletteTag.getAllKeys()) {
                int paletteId = paletteTag.getInt(stateString);
                if (paletteId < 0 || paletteId >= paletteStates.length) {
                    continue;
                }
                paletteStates[paletteId] = parseBlockState(stateString);
            }

            int expected = width * height * length;
            int[] blockIndices = decodeVarIntArray(blockDataRaw, expected);
            if (blockIndices.length < expected) {
                StardewCraft.LOGGER.error("Schematic {} block data too short: {} < {}", structurePath, blockIndices.length, expected);
                return false;
            }

            int index = 0;
                ensureChunksLoaded(level, origin, width, length);
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    for (int x = 0; x < width; x++) {
                        int paletteIndex = blockIndices[index++];
                        if (paletteIndex < 0 || paletteIndex >= paletteStates.length) {
                            continue;
                        }
                        BlockState state = paletteStates[paletteIndex];
                        if (state == null) {
                            continue;
                        }
                        level.setBlock(origin.offset(x, y, z), state, 3);
                    }
                }
            }

            applySchematicBlockEntities(level, origin, root, schematic, width, height, length);

            StardewCraft.LOGGER.info("Successfully placed schematic {} at {} ({}x{}x{})", structurePath, origin, width, height, length);
            return true;
        } catch (Exception e) {
            StardewCraft.LOGGER.error("Failed to load/place schematic {}: {}", structurePath, e.getMessage(), e);
            return false;
        }
    }

    private static BlockState parseBlockState(String stateString) {
        String blockId = stateString;
        String propsPart = null;

        int bracketStart = stateString.indexOf('[');
        if (bracketStart >= 0 && stateString.endsWith("]")) {
            blockId = stateString.substring(0, bracketStart);
            propsPart = stateString.substring(bracketStart + 1, stateString.length() - 1);
        }

        ResourceLocation id = ResourceLocation.tryParse(blockId);
        if (id == null) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }

        Block block = BuiltInRegistries.BLOCK.get(id);
        BlockState state = block.defaultBlockState();
        if (propsPart == null || propsPart.isBlank()) {
            return state;
        }

        Map<String, String> values = new HashMap<>();
        for (String token : propsPart.split(",")) {
            String[] kv = token.split("=", 2);
            if (kv.length == 2) {
                values.put(kv[0].trim(), kv[1].trim());
            }
        }

        for (Property<?> property : state.getProperties()) {
            String value = values.get(property.getName());
            if (value == null) {
                continue;
            }
            state = setPropertySafely(state, property, value);
        }

        return state;
    }

    private static <T extends Comparable<T>> BlockState setPropertySafely(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(v -> state.setValue(property, v)).orElse(state);
    }

    private static int[] decodeVarIntArray(byte[] data, int expectedCount) {
        int[] out = new int[expectedCount];
        int outIndex = 0;
        int byteIndex = 0;

        while (byteIndex < data.length && outIndex < expectedCount) {
            int value = 0;
            int shift = 0;
            while (true) {
                if (byteIndex >= data.length) {
                    return Arrays.copyOf(out, outIndex);
                }
                int current = data[byteIndex++] & 0xFF;
                value |= (current & 0x7F) << shift;
                if ((current & 0x80) == 0) {
                    break;
                }
                shift += 7;
                if (shift > 35) {
                    return Arrays.copyOf(out, outIndex);
                }
            }
            out[outIndex++] = value;
        }

        return outIndex == expectedCount ? out : Arrays.copyOf(out, outIndex);
    }

    private static int readDimension(CompoundTag tag, String name) {
        if (tag.contains(name, Tag.TAG_SHORT)) {
            return tag.getShort(name) & 0xFFFF;
        }
        if (tag.contains(name, Tag.TAG_INT)) {
            return tag.getInt(name);
        }

        String lower = name.toLowerCase(Locale.ROOT);
        if (tag.contains(lower, Tag.TAG_SHORT)) {
            return tag.getShort(lower) & 0xFFFF;
        }
        if (tag.contains(lower, Tag.TAG_INT)) {
            return tag.getInt(lower);
        }
        return 0;
    }

    private static void applySchematicBlockEntities(ServerLevel level,
                                                    BlockPos origin,
                                                    CompoundTag root,
                                                    CompoundTag schematic,
                                                    int width,
                                                    int height,
                                                    int length) {
        ListTag blockEntities = findBlockEntityList(root, schematic);
        if (blockEntities == null || blockEntities.isEmpty()) {
            return;
        }

        int restored = 0;
        for (int i = 0; i < blockEntities.size(); i++) {
            CompoundTag raw = blockEntities.getCompound(i);
            int[] relative = resolveRelativePos(raw);
            if (relative == null || relative.length < 3) {
                continue;
            }

            int rx = relative[0];
            int ry = relative[1];
            int rz = relative[2];
            if (rx < 0 || ry < 0 || rz < 0 || rx >= width || ry >= height || rz >= length) {
                continue;
            }

            BlockPos worldPos = origin.offset(rx, ry, rz);
            BlockState state = level.getBlockState(worldPos);

            CompoundTag normalized = raw.copy();
            if (!normalized.contains("id", Tag.TAG_STRING) && normalized.contains("Id", Tag.TAG_STRING)) {
                normalized.putString("id", normalized.getString("Id"));
            }
            if (!normalized.contains("x", Tag.TAG_INT)) {
                normalized.putInt("x", worldPos.getX());
            }
            if (!normalized.contains("y", Tag.TAG_INT)) {
                normalized.putInt("y", worldPos.getY());
            }
            if (!normalized.contains("z", Tag.TAG_INT)) {
                normalized.putInt("z", worldPos.getZ());
            }

            if (!normalized.contains("id", Tag.TAG_STRING)) {
                continue;
            }

            BlockEntity blockEntity = BlockEntity.loadStatic(worldPos, state, normalized, level.registryAccess());
            if (blockEntity == null) {
                continue;
            }

            level.setBlockEntity(blockEntity);
            blockEntity.setChanged();
            restored++;
        }

        StardewCraft.LOGGER.info("Restored {} schematic block entities at {}", restored, origin);
    }

    private static ListTag findBlockEntityList(CompoundTag root, CompoundTag schematic) {
        if (schematic.contains("BlockEntities", Tag.TAG_LIST)) {
            return schematic.getList("BlockEntities", Tag.TAG_COMPOUND);
        }

        if (schematic.contains("Blocks", Tag.TAG_COMPOUND)) {
            CompoundTag blocksTag = schematic.getCompound("Blocks");
            if (blocksTag.contains("BlockEntities", Tag.TAG_LIST)) {
                return blocksTag.getList("BlockEntities", Tag.TAG_COMPOUND);
            }
        }

        if (root.contains("BlockEntities", Tag.TAG_LIST)) {
            return root.getList("BlockEntities", Tag.TAG_COMPOUND);
        }

        return null;
    }

    private static int[] resolveRelativePos(CompoundTag tag) {
        if (tag.contains("Pos", Tag.TAG_INT_ARRAY)) {
            return tag.getIntArray("Pos");
        }

        if (tag.contains("x", Tag.TAG_INT) && tag.contains("y", Tag.TAG_INT) && tag.contains("z", Tag.TAG_INT)) {
            return new int[] { tag.getInt("x"), tag.getInt("y"), tag.getInt("z") };
        }

        if (tag.contains("X", Tag.TAG_INT) && tag.contains("Y", Tag.TAG_INT) && tag.contains("Z", Tag.TAG_INT)) {
            return new int[] { tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z") };
        }

        return null;
    }

    private static void ensureChunksLoaded(ServerLevel level, BlockPos origin, int sizeX, int sizeZ) {
        int minChunkX = origin.getX() >> 4;
        int minChunkZ = origin.getZ() >> 4;
        int maxChunkX = (origin.getX() + Math.max(0, sizeX - 1)) >> 4;
        int maxChunkZ = (origin.getZ() + Math.max(0, sizeZ - 1)) >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                level.getChunk(chunkX, chunkZ);
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  顺时针 90° 旋转放置 (俯视视角)
    // ═══════════════════════════════════════════════════════════════

    /**
     * 加载 .schem 文件并以俯视顺时针 90° 旋转放置。
     * <p>
     * 坐标映射：原始 (x, y, z) → (length-1-z, y, x)
     * 旋转后实际占地：宽=oldLength, 深=oldWidth
     * 方块朝向也会旋转（NORTH→EAST, EAST→SOUTH, SOUTH→WEST, WEST→NORTH, axis X↔Z）。
     */
    public static boolean loadAndPlaceCW90(ServerLevel level, String structurePath, BlockPos origin) {
        if (structurePath == null || !structurePath.toLowerCase(Locale.ROOT).endsWith(".schem")) {
            StardewCraft.LOGGER.error("loadAndPlaceCW90 only supports .schem files: {}", structurePath);
            return false;
        }
        try (InputStream stream = StructureLoader.class.getClassLoader().getResourceAsStream(structurePath)) {
            if (stream == null) {
                StardewCraft.LOGGER.error("Schematic file not found: {}", structurePath);
                return false;
            }

            CompoundTag root = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
            CompoundTag schematic = root;
            if ((readDimension(root, "Width") == 0 || readDimension(root, "Height") == 0 || readDimension(root, "Length") == 0)
                && root.contains("Schematic", Tag.TAG_COMPOUND)) {
                schematic = root.getCompound("Schematic");
            }

            int width  = readDimension(schematic, "Width");
            int height = readDimension(schematic, "Height");
            int length = readDimension(schematic, "Length");
            if (width <= 0 || height <= 0 || length <= 0) {
                StardewCraft.LOGGER.error("Invalid schematic size for {}: {}x{}x{}", structurePath, width, height, length);
                return false;
            }

            CompoundTag paletteTag = schematic.getCompound("Palette");
            byte[] blockDataRaw = schematic.getByteArray("BlockData");
            if ((paletteTag.isEmpty() || blockDataRaw.length == 0) && schematic.contains("Blocks", Tag.TAG_COMPOUND)) {
                CompoundTag blocksTag = schematic.getCompound("Blocks");
                if (paletteTag.isEmpty()) paletteTag = blocksTag.getCompound("Palette");
                if (blockDataRaw.length == 0) blockDataRaw = blocksTag.getByteArray("Data");
            }
            if (paletteTag.isEmpty()) {
                StardewCraft.LOGGER.error("Schematic {} has empty palette", structurePath);
                return false;
            }

            int paletteMax = Math.max(1, schematic.getInt("PaletteMax"));
            BlockState[] paletteStates = new BlockState[Math.max(paletteMax, paletteTag.size()) + 1];
            for (String stateString : paletteTag.getAllKeys()) {
                int paletteId = paletteTag.getInt(stateString);
                if (paletteId >= 0 && paletteId < paletteStates.length) {
                    paletteStates[paletteId] = parseBlockState(stateString);
                }
            }

            int expected = width * height * length;
            int[] blockIndices = decodeVarIntArray(blockDataRaw, expected);
            if (blockIndices.length < expected) {
                StardewCraft.LOGGER.error("Schematic {} block data too short: {} < {}", structurePath, blockIndices.length, expected);
                return false;
            }

            // 旋转后占地：宽=length, 深=width
            ensureChunksLoaded(level, origin, length, width);

            int index = 0;
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    for (int x = 0; x < width; x++) {
                        int paletteIndex = blockIndices[index++];
                        if (paletteIndex < 0 || paletteIndex >= paletteStates.length) continue;
                        BlockState state = paletteStates[paletteIndex];
                        if (state == null) continue;

                        // CW 90°: (x,y,z) → (length-1-z, y, x)
                        int rx = length - 1 - z;
                        int rz = x;
                        level.setBlock(origin.offset(rx, y, rz), rotateCW90(state), 3);
                    }
                }
            }

            applySchematicBlockEntitiesCW90(level, origin, root, schematic, width, height, length);

            StardewCraft.LOGGER.info("Successfully placed schematic {} at {} rotated CW90 ({}x{}x{} → {}x{}x{})",
                structurePath, origin, width, height, length, length, height, width);
            return true;
        } catch (Exception e) {
            StardewCraft.LOGGER.error("Failed to load/place schematic CW90 {}: {}", structurePath, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 顺时针 90° 旋转方块状态的朝向属性。
     */
    private static BlockState rotateCW90(BlockState state) {
        // HORIZONTAL_FACING / FACING
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING,
                rotateDirCW90(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
        } else if (state.hasProperty(BlockStateProperties.FACING)) {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            if (dir.getAxis().isHorizontal()) {
                state = state.setValue(BlockStateProperties.FACING, rotateDirCW90(dir));
            }
        }
        // AXIS
        if (state.hasProperty(BlockStateProperties.AXIS)) {
            Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
            if (axis == Direction.Axis.X) {
                state = state.setValue(BlockStateProperties.AXIS, Direction.Axis.Z);
            } else if (axis == Direction.Axis.Z) {
                state = state.setValue(BlockStateProperties.AXIS, Direction.Axis.X);
            }
        }
        return state;
    }

    private static Direction rotateDirCW90(Direction dir) {
        return switch (dir) {
            case NORTH -> Direction.EAST;
            case EAST  -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST  -> Direction.NORTH;
            default    -> dir;
        };
    }

    /**
     * 对方块实体也执行 CW90 坐标旋转。
     */
    private static void applySchematicBlockEntitiesCW90(ServerLevel level,
                                                        BlockPos origin,
                                                        CompoundTag root,
                                                        CompoundTag schematic,
                                                        int width,
                                                        int height,
                                                        int length) {
        ListTag blockEntities = findBlockEntityList(root, schematic);
        if (blockEntities == null || blockEntities.isEmpty()) return;

        int restored = 0;
        for (int i = 0; i < blockEntities.size(); i++) {
            CompoundTag raw = blockEntities.getCompound(i);
            int[] relative = resolveRelativePos(raw);
            if (relative == null || relative.length < 3) continue;

            int rx = relative[0], ry = relative[1], rz = relative[2];
            if (rx < 0 || ry < 0 || rz < 0 || rx >= width || ry >= height || rz >= length) continue;

            // CW 90°: (rx,ry,rz) → (length-1-rz, ry, rx)
            int newRx = length - 1 - rz;
            int newRz = rx;
            BlockPos worldPos = origin.offset(newRx, ry, newRz);
            BlockState state = level.getBlockState(worldPos);

            CompoundTag normalized = raw.copy();
            if (!normalized.contains("id", Tag.TAG_STRING) && normalized.contains("Id", Tag.TAG_STRING)) {
                normalized.putString("id", normalized.getString("Id"));
            }
            normalized.putInt("x", worldPos.getX());
            normalized.putInt("y", worldPos.getY());
            normalized.putInt("z", worldPos.getZ());

            if (!normalized.contains("id", Tag.TAG_STRING)) continue;

            BlockEntity blockEntity = BlockEntity.loadStatic(worldPos, state, normalized, level.registryAccess());
            if (blockEntity == null) continue;

            level.setBlockEntity(blockEntity);
            blockEntity.setChanged();
            restored++;
        }

        StardewCraft.LOGGER.info("Restored {} schematic block entities (CW90) at {}", restored, origin);
    }
}
