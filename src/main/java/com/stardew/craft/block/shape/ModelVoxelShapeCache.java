package com.stardew.craft.block.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ModelVoxelShapeCache {
    private static final Map<String, VoxelShape> SHAPE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, VariantModelRef>> BLOCKSTATE_VARIANTS_CACHE = new ConcurrentHashMap<>();

    private ModelVoxelShapeCache() {
    }

    @SuppressWarnings("null")
    public static VoxelShape shapeFromModelId(String modelId) {
        return SHAPE_CACHE.computeIfAbsent(modelId, ModelVoxelShapeCache::loadShapeFromModelId);
    }

    @SuppressWarnings("null")
    public static VoxelShape shape(String modelId) {
        return shapeFromModelId(modelId);
    }

    public static String variantModel(String blockId, String variantKey) {
        VariantModelRef ref = variantRef(blockId, variantKey);
        return ref == null ? null : ref.modelId();
    }

    @SuppressWarnings("null")
    public static VoxelShape variantShape(String blockId, String variantKey) {
        VariantModelRef ref = variantRef(blockId, variantKey);
        if (ref == null || ref.modelId() == null || ref.modelId().isBlank()) {
            return Shapes.empty();
        }

        VoxelShape shape = shapeFromModelId(ref.modelId());
        if (shape.isEmpty()) {
            return shape;
        }

        if (ref.xRotation() % 90 == 0) {
            shape = rotateX(shape, ref.xRotation() / 90);
        }
        if (ref.yRotation() % 90 == 0) {
            shape = rotateY(shape, ref.yRotation() / 90);
        }
        return shape.optimize();
    }

    private static VariantModelRef variantRef(String blockId, String variantKey) {
        Map<String, VariantModelRef> variants = BLOCKSTATE_VARIANTS_CACHE.computeIfAbsent(blockId, ModelVoxelShapeCache::loadBlockstateVariants);
        return variants.get(variantKey);
    }

    @SuppressWarnings("null")
    public static VoxelShape[] horizontalShapes(String modelId, Direction baseFacing) {
        VoxelShape[] out = new VoxelShape[4];
        VoxelShape base = shapeFromModelId(modelId);

        int baseIndex = horizontalIndex(baseFacing);
        out[baseIndex] = base;
        for (int i = 0; i < 4; i++) {
            if (out[i] != null) {
                continue;
            }
            int turns = i - baseIndex;
            if (turns < 0) {
                turns += 4;
            }
            out[i] = rotateY(base, turns).optimize();
        }
        return out;
    }

    public static int horizontalIndex(Direction direction) {
        return switch (direction) {
            case SOUTH -> 0;
            case WEST -> 1;
            case NORTH -> 2;
            case EAST -> 3;
            default -> 0;
        };
    }

    @SuppressWarnings("null")
    public static VoxelShape rotateY(VoxelShape shape, int quarterTurnsClockwise) {
        int turns = ((quarterTurnsClockwise % 4) + 4) % 4;
        if (turns == 0 || shape.isEmpty()) {
            return shape;
        }
        final VoxelShape[] holder = new VoxelShape[] { Shapes.empty() };
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double rMinX = minX;
            double rMinZ = minZ;
            double rMaxX = maxX;
            double rMaxZ = maxZ;
            switch (turns) {
                case 1 -> {
                    rMinX = 1.0 - maxZ;
                    rMaxX = 1.0 - minZ;
                    rMinZ = minX;
                    rMaxZ = maxX;
                }
                case 2 -> {
                    rMinX = 1.0 - maxX;
                    rMaxX = 1.0 - minX;
                    rMinZ = 1.0 - maxZ;
                    rMaxZ = 1.0 - minZ;
                }
                case 3 -> {
                    rMinX = minZ;
                    rMaxX = maxZ;
                    rMinZ = 1.0 - maxX;
                    rMaxZ = 1.0 - minX;
                }
                default -> {
                }
            }
            holder[0] = Shapes.or(holder[0], Shapes.box(rMinX, minY, rMinZ, rMaxX, maxY, rMaxZ));
        });
        return holder[0];
    }

    @SuppressWarnings("null")
    public static VoxelShape rotateX(VoxelShape shape, int quarterTurnsClockwise) {
        int turns = ((quarterTurnsClockwise % 4) + 4) % 4;
        if (turns == 0 || shape.isEmpty()) {
            return shape;
        }
        final VoxelShape[] holder = new VoxelShape[] { Shapes.empty() };
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double rMinY = minY;
            double rMinZ = minZ;
            double rMaxY = maxY;
            double rMaxZ = maxZ;
            switch (turns) {
                case 1 -> {
                    rMinY = 1.0 - maxZ;
                    rMaxY = 1.0 - minZ;
                    rMinZ = minY;
                    rMaxZ = maxY;
                }
                case 2 -> {
                    rMinY = 1.0 - maxY;
                    rMaxY = 1.0 - minY;
                    rMinZ = 1.0 - maxZ;
                    rMaxZ = 1.0 - minZ;
                }
                case 3 -> {
                    rMinY = minZ;
                    rMaxY = maxZ;
                    rMinZ = 1.0 - maxY;
                    rMaxZ = 1.0 - minY;
                }
                default -> {
                }
            }
            holder[0] = Shapes.or(holder[0], Shapes.box(minX, rMinY, rMinZ, maxX, rMaxY, rMaxZ));
        });
        return holder[0];
    }

    @SuppressWarnings("null")
    private static VoxelShape loadShapeFromModelId(String modelId) {
        ModelResolve resolved = normalizeModelIdForShape(modelId);
        String resolvedModelId = resolved.modelId();
        JsonObject model = readModel(resolveModelPath(resolvedModelId));
        if (model == null) {
            return Shapes.empty();
        }

        String parent = model.has("parent") ? model.get("parent").getAsString() : null;
        if (isCrossParent(parent)) {
            VoxelShape crossShape = shapeFromCrossTexture(model);
            if (!crossShape.isEmpty()) {
                if (resolved.shiftDownOneBlock()) {
                    crossShape = offsetY(crossShape, -1.0);
                }
                return crossShape.optimize();
            }
        }

        VoxelShape shape = Shapes.empty();
        JsonArray elements = model.getAsJsonArray("elements");
        if ((elements == null || elements.isEmpty()) && parent != null && !isCrossParent(parent)) {
            VoxelShape parentShape = shapeFromModelId(parent);
            if (resolved.shiftDownOneBlock()) {
                parentShape = offsetY(parentShape, -1.0);
            }
            return parentShape;
        }
        if (elements != null) {
            for (JsonElement element : elements) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject obj = element.getAsJsonObject();
                JsonArray from = obj.getAsJsonArray("from");
                JsonArray to = obj.getAsJsonArray("to");
                if (from == null || to == null || from.size() < 3 || to.size() < 3) {
                    continue;
                }

                double minX = from.get(0).getAsDouble();
                double minY = from.get(1).getAsDouble();
                double minZ = from.get(2).getAsDouble();
                double maxX = to.get(0).getAsDouble();
                double maxY = to.get(1).getAsDouble();
                double maxZ = to.get(2).getAsDouble();

                JsonObject rotation = obj.getAsJsonObject("rotation");
                if (rotation != null) {
                    Bounds rotated = rotatedBounds(minX, minY, minZ, maxX, maxY, maxZ, rotation);
                    minX = rotated.minX;
                    minY = rotated.minY;
                    minZ = rotated.minZ;
                    maxX = rotated.maxX;
                    maxY = rotated.maxY;
                    maxZ = rotated.maxZ;
                }

                if (maxX <= minX) {
                    maxX = minX + 0.0625;
                }
                if (maxY <= minY) {
                    maxY = minY + 0.0625;
                }
                if (maxZ <= minZ) {
                    maxZ = minZ + 0.0625;
                }

                shape = Shapes.or(shape, Block.box(minX, minY, minZ, maxX, maxY, maxZ));
            }
        }

        if (shape.isEmpty()) {
            return Shapes.empty();
        }
        if (resolved.shiftDownOneBlock()) {
            shape = offsetY(shape, -1.0);
        }
        return shape.optimize();
    }

    private static ModelResolve normalizeModelIdForShape(String modelId) {
        JsonObject direct = readModel(resolveModelPath(modelId));
        if (direct != null && modelHasUsableShape(direct)) {
            return new ModelResolve(modelId, false);
        }

        if (modelId.contains("_extension")) {
            String fallback = modelId.replace("_extension", "");
            JsonObject fallbackModel = readModel(resolveModelPath(fallback));
            if (fallbackModel != null && modelHasUsableShape(fallbackModel)) {
                return new ModelResolve(fallback, true);
            }
        }
        return new ModelResolve(modelId, false);
    }

    @SuppressWarnings("null")
    public static VoxelShape offsetY(VoxelShape shape, double blockOffsetY) {
        if (shape.isEmpty() || blockOffsetY == 0.0) {
            return shape;
        }
        final VoxelShape[] holder = new VoxelShape[] { Shapes.empty() };
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            holder[0] = Shapes.or(holder[0], Shapes.box(minX, minY + blockOffsetY, minZ, maxX, maxY + blockOffsetY, maxZ));
        });
        return holder[0].optimize();
    }

    private static boolean modelHasUsableShape(JsonObject model) {
        JsonArray elements = model.getAsJsonArray("elements");
        if (elements != null && !elements.isEmpty()) {
            return true;
        }
        String parent = model.has("parent") ? model.get("parent").getAsString() : null;
        return isCrossParent(parent) && hasRenderableTexture(model);
    }

    private static boolean isCrossParent(String parent) {
        if (parent == null || parent.isBlank()) {
            return false;
        }
        return parent.contains("cross");
    }

    private static boolean hasRenderableTexture(JsonObject model) {
        JsonObject textures = model.getAsJsonObject("textures");
        if (textures == null || textures.entrySet().isEmpty()) {
            return false;
        }
        String textureRef = textureReferenceForCross(model);
        return textureRef != null && !textureRef.isBlank();
    }

    private static VoxelShape shapeFromCrossTexture(JsonObject model) {
        String textureRef = textureReferenceForCross(model);
        if (textureRef == null || textureRef.isBlank()) {
            return Shapes.empty();
        }
        String textureId = resolveTextureReference(model.getAsJsonObject("textures"), textureRef);
        if (textureId == null || textureId.isBlank()) {
            return Shapes.empty();
        }

        BufferedImage image = readTexture(textureId);
        if (image == null) {
            return Shapes.empty();
        }

        PixelBounds bounds = findOpaqueBounds(image);
        if (bounds == null) {
            return Shapes.empty();
        }

        double widthPx = Math.max(1.0, bounds.width());
        double heightPx = Math.max(1.0, bounds.height());
        double side = Math.min(16.0, widthPx / Math.sqrt(2.0));
        double min = 8.0 - side / 2.0;
        double max = 8.0 + side / 2.0;
        double top = Math.min(16.0, heightPx);
        return Block.box(min, 0.0, min, max, top, max);
    }

    private static String textureReferenceForCross(JsonObject model) {
        JsonObject textures = model.getAsJsonObject("textures");
        if (textures == null) {
            return null;
        }
        if (textures.has("cross")) {
            return textures.get("cross").getAsString();
        }
        if (textures.has("plant")) {
            return textures.get("plant").getAsString();
        }
        for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
            String key = entry.getKey();
            if ("particle".equals(key)) {
                continue;
            }
            JsonElement value = entry.getValue();
            if (value != null && value.isJsonPrimitive()) {
                return value.getAsString();
            }
        }
        return null;
    }

    private static String resolveTextureReference(JsonObject textures, String textureRef) {
        if (!textureRef.startsWith("#")) {
            return textureRef;
        }
        if (textures == null) {
            return null;
        }

        String key = textureRef.substring(1);
        Set<String> seen = new HashSet<>();
        while (!key.isBlank() && seen.add(key) && textures.has(key)) {
            String value = textures.get(key).getAsString();
            if (!value.startsWith("#")) {
                return value;
            }
            key = value.substring(1);
        }
        return null;
    }

    private static BufferedImage readTexture(String textureId) {
        String texturePath = resolveTexturePath(textureId);
        try (InputStream input = ModelVoxelShapeCache.class.getClassLoader().getResourceAsStream(texturePath)) {
            if (input == null) {
                return null;
            }
            return ImageIO.read(input);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String resolveTexturePath(String textureId) {
        String[] split = textureId.split(":", 2);
        String namespace = split.length == 2 ? split[0] : "minecraft";
        String path = split.length == 2 ? split[1] : split[0];
        return "assets/" + namespace + "/textures/" + path + ".png";
    }

    private static PixelBounds findOpaqueBounds(BufferedImage image) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        int width = image.getWidth();
        int height = image.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);
                int alpha = (argb >>> 24) & 0xFF;
                if (alpha <= 4) {
                    continue;
                }
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }

        if (maxX < minX || maxY < minY) {
            return null;
        }

        double scaleX = 16.0 / Math.max(1, width);
        double scaleY = 16.0 / Math.max(1, height);
        double scaledWidth = (maxX - minX + 1) * scaleX;
        double scaledHeight = (maxY - minY + 1) * scaleY;
        return new PixelBounds(scaledWidth, scaledHeight);
    }

    private static String resolveModelPath(String modelId) {
        String[] split = modelId.split(":", 2);
        String namespace = split.length == 2 ? split[0] : "minecraft";
        String path = split.length == 2 ? split[1] : split[0];
        return "assets/" + namespace + "/models/" + path + ".json";
    }

    private static String resolveBlockstatePath(String blockId) {
        String[] split = blockId.split(":", 2);
        String namespace = split.length == 2 ? split[0] : "minecraft";
        String path = split.length == 2 ? split[1] : split[0];
        return "assets/" + namespace + "/blockstates/" + path + ".json";
    }

    @SuppressWarnings("null")
    private static JsonObject readModel(String classpathPath) {
        try (InputStream input = ModelVoxelShapeCache.class.getClassLoader().getResourceAsStream(classpathPath)) {
            if (input == null) {
                return null;
            }
            return JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Map<String, VariantModelRef> loadBlockstateVariants(String blockId) {
        Map<String, VariantModelRef> out = new ConcurrentHashMap<>();
        String blockstatePath = resolveBlockstatePath(blockId);
        try (InputStream input = ModelVoxelShapeCache.class.getClassLoader().getResourceAsStream(blockstatePath)) {
            if (input == null) {
                return out;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject variants = root.getAsJsonObject("variants");
            if (variants == null) {
                return out;
            }
            for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                JsonElement value = entry.getValue();
                VariantModelRef ref = parseVariantModelRef(value);
                if (ref == null) {
                    continue;
                }
                out.put(entry.getKey(), ref);
            }
            return out;
        } catch (Exception ignored) {
            return out;
        }
    }

    private static VariantModelRef parseVariantModelRef(JsonElement value) {
        if (value == null) {
            return null;
        }
        JsonObject variantObj = null;
        if (value.isJsonObject()) {
            variantObj = value.getAsJsonObject();
        } else if (value.isJsonArray()) {
            JsonArray array = value.getAsJsonArray();
            for (JsonElement element : array) {
                if (element != null && element.isJsonObject()) {
                    variantObj = element.getAsJsonObject();
                    break;
                }
            }
        }

        if (variantObj == null || !variantObj.has("model")) {
            return null;
        }

        String modelId = variantObj.get("model").getAsString();
        int xRotation = variantObj.has("x") ? variantObj.get("x").getAsInt() : 0;
        int yRotation = variantObj.has("y") ? variantObj.get("y").getAsInt() : 0;
        return new VariantModelRef(modelId, xRotation, yRotation);
    }

    private static Bounds rotatedBounds(double minX, double minY, double minZ,
                                       double maxX, double maxY, double maxZ,
                                       JsonObject rotation) {
        String axis = rotation.get("axis").getAsString();
        double angle = Math.toRadians(rotation.get("angle").getAsDouble());
        JsonArray origin = rotation.getAsJsonArray("origin");
        double ox = origin.get(0).getAsDouble();
        double oy = origin.get(1).getAsDouble();
        double oz = origin.get(2).getAsDouble();

        double[][] corners = new double[][] {
            { minX, minY, minZ }, { minX, minY, maxZ }, { minX, maxY, minZ }, { minX, maxY, maxZ },
            { maxX, minY, minZ }, { maxX, minY, maxZ }, { maxX, maxY, minZ }, { maxX, maxY, maxZ }
        };

        double outMinX = Double.POSITIVE_INFINITY;
        double outMinY = Double.POSITIVE_INFINITY;
        double outMinZ = Double.POSITIVE_INFINITY;
        double outMaxX = Double.NEGATIVE_INFINITY;
        double outMaxY = Double.NEGATIVE_INFINITY;
        double outMaxZ = Double.NEGATIVE_INFINITY;

        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        for (double[] corner : corners) {
            double x = corner[0] - ox;
            double y = corner[1] - oy;
            double z = corner[2] - oz;
            double rx = x;
            double ry = y;
            double rz = z;

            switch (axis) {
                case "x" -> {
                    ry = y * cos - z * sin;
                    rz = y * sin + z * cos;
                }
                case "y" -> {
                    rx = x * cos + z * sin;
                    rz = -x * sin + z * cos;
                }
                case "z" -> {
                    rx = x * cos - y * sin;
                    ry = x * sin + y * cos;
                }
                default -> {
                }
            }

            rx += ox;
            ry += oy;
            rz += oz;

            outMinX = Math.min(outMinX, rx);
            outMinY = Math.min(outMinY, ry);
            outMinZ = Math.min(outMinZ, rz);
            outMaxX = Math.max(outMaxX, rx);
            outMaxY = Math.max(outMaxY, ry);
            outMaxZ = Math.max(outMaxZ, rz);
        }

        return new Bounds(outMinX, outMinY, outMinZ, outMaxX, outMaxY, outMaxZ);
    }

    private record Bounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    }

    private record PixelBounds(double width, double height) {
    }

    private record VariantModelRef(String modelId, int xRotation, int yRotation) {
    }

    private record ModelResolve(String modelId, boolean shiftDownOneBlock) {
    }
}