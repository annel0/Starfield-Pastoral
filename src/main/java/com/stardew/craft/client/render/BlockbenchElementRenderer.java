package com.stardew.craft.client.render;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stardew.craft.StardewCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

final class BlockbenchElementRenderer {
    private static final Gson GSON = new Gson();
    private static final Map<ResourceLocation, Optional<Model>> CACHE = new ConcurrentHashMap<>();

    private BlockbenchElementRenderer() {
    }

    static void renderAll(ResourceLocation modelLocation, PoseStack poseStack, MultiBufferSource buffer,
                          int packedLight, int packedOverlay) {
        render(modelLocation, poseStack, buffer, packedLight, packedOverlay, false);
    }

    static void renderNegativeOnly(ResourceLocation modelLocation, PoseStack poseStack, MultiBufferSource buffer,
                                   int packedLight, int packedOverlay) {
        render(modelLocation, poseStack, buffer, packedLight, packedOverlay, true);
    }

    private static void render(ResourceLocation modelLocation, PoseStack poseStack, MultiBufferSource buffer,
                               int packedLight, int packedOverlay, boolean negativeOnly) {
        Model model = CACHE.computeIfAbsent(modelLocation, BlockbenchElementRenderer::load).orElse(null);
        if (model == null) {
            return;
        }

        Matrix4f matrix = poseStack.last().pose();
        for (JsonElement elementValue : model.elements()) {
            if (!elementValue.isJsonObject()) {
                continue;
            }
            JsonObject element = elementValue.getAsJsonObject();
            float[] from = vector3(element.getAsJsonArray("from"));
            float[] to = vector3(element.getAsJsonArray("to"));
            boolean negative = from[0] > to[0] || from[1] > to[1] || from[2] > to[2];
            if (negativeOnly && !negative) {
                continue;
            }

            JsonObject faces = element.getAsJsonObject("faces");
            if (faces == null) {
                continue;
            }

            Rotation rotation = rotation(element);
            float minX = Math.min(from[0], to[0]) / 16.0F;
            float minY = Math.min(from[1], to[1]) / 16.0F;
            float minZ = Math.min(from[2], to[2]) / 16.0F;
            float maxX = Math.max(from[0], to[0]) / 16.0F;
            float maxY = Math.max(from[1], to[1]) / 16.0F;
            float maxZ = Math.max(from[2], to[2]) / 16.0F;

            for (Map.Entry<String, JsonElement> faceEntry : faces.entrySet()) {
                if (!faceEntry.getValue().isJsonObject()) {
                    continue;
                }
                JsonObject face = faceEntry.getValue().getAsJsonObject();
                ResourceLocation texture = model.texture(textureKey(face));
                if (texture == null) {
                    continue;
                }
                VertexConsumer consumer = buffer.getBuffer(negative
                        ? RenderType.entityCutout(texture)
                        : RenderType.entityCutoutNoCull(texture));
                renderFace(consumer, matrix, rotation, faceEntry.getKey().toLowerCase(Locale.ROOT), face,
                        minX, minY, minZ, maxX, maxY, maxZ, model.textureWidth(), model.textureHeight(),
                        packedLight, packedOverlay, negative);
            }
        }
    }

    private static Optional<Model> load(ResourceLocation modelLocation) {
        try {
            var resource = Minecraft.getInstance().getResourceManager().getResource(modelLocation);
            if (resource.isEmpty()) {
                return Optional.empty();
            }
            try (var reader = resource.get().openAsReader()) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                if (root == null) {
                    return Optional.empty();
                }

                int textureWidth = 16;
                int textureHeight = 16;
                JsonArray textureSize = root.getAsJsonArray("texture_size");
                if (textureSize != null && textureSize.size() >= 2) {
                    textureWidth = textureSize.get(0).getAsInt();
                    textureHeight = textureSize.get(1).getAsInt();
                }

                Map<String, ResourceLocation> textures = new HashMap<>();
                JsonObject textureJson = root.getAsJsonObject("textures");
                if (textureJson != null) {
                    for (Map.Entry<String, JsonElement> entry : textureJson.entrySet()) {
                        if (entry.getValue().isJsonPrimitive()) {
                            textures.put(entry.getKey(), textureLocation(entry.getValue().getAsString()));
                        }
                    }
                }

                JsonArray elements = root.getAsJsonArray("elements");
                return Optional.of(new Model(textureWidth, textureHeight, textures,
                        elements == null ? new JsonArray() : elements));
            }
        } catch (Exception ex) {
            StardewCraft.LOGGER.warn("Failed to load Blockbench element model {}", modelLocation, ex);
            return Optional.empty();
        }
    }

    private static void renderFace(VertexConsumer consumer, Matrix4f matrix, Rotation rotation, String side,
                                   JsonObject face, float minX, float minY, float minZ,
                                   float maxX, float maxY, float maxZ, int textureWidth, int textureHeight,
                                   int packedLight, int packedOverlay, boolean inward) {
        float[] uv = face.has("uv") ? vector4(face.getAsJsonArray("uv")) : new float[] {0, 0, 16, 16};
        float u1 = uv[0] / textureWidth;
        float v1 = uv[1] / textureHeight;
        float u2 = uv[2] / textureWidth;
        float v2 = uv[3] / textureHeight;

        switch (side) {
            case "north" -> quad(consumer, matrix, rotation, packedLight, packedOverlay, inward,
                    minX, minY, minZ, u1, v2, minX, maxY, minZ, u1, v1,
                    maxX, maxY, minZ, u2, v1, maxX, minY, minZ, u2, v2);
            case "south" -> quad(consumer, matrix, rotation, packedLight, packedOverlay, inward,
                    maxX, minY, maxZ, u1, v2, maxX, maxY, maxZ, u1, v1,
                    minX, maxY, maxZ, u2, v1, minX, minY, maxZ, u2, v2);
            case "east" -> quad(consumer, matrix, rotation, packedLight, packedOverlay, inward,
                    maxX, minY, minZ, u1, v2, maxX, maxY, minZ, u1, v1,
                    maxX, maxY, maxZ, u2, v1, maxX, minY, maxZ, u2, v2);
            case "west" -> quad(consumer, matrix, rotation, packedLight, packedOverlay, inward,
                    minX, minY, maxZ, u1, v2, minX, maxY, maxZ, u1, v1,
                    minX, maxY, minZ, u2, v1, minX, minY, minZ, u2, v2);
            case "up" -> quad(consumer, matrix, rotation, packedLight, packedOverlay, inward,
                    minX, maxY, minZ, u1, v2, minX, maxY, maxZ, u1, v1,
                    maxX, maxY, maxZ, u2, v1, maxX, maxY, minZ, u2, v2);
            case "down" -> quad(consumer, matrix, rotation, packedLight, packedOverlay, inward,
                    minX, minY, maxZ, u1, v2, minX, minY, minZ, u1, v1,
                    maxX, minY, minZ, u2, v1, maxX, minY, maxZ, u2, v2);
            default -> {
            }
        }
    }

    private static void quad(VertexConsumer consumer, Matrix4f matrix, Rotation rotation,
                             int packedLight, int packedOverlay, boolean inward,
                             float x1, float y1, float z1, float u1, float v1,
                             float x2, float y2, float z2, float u2, float v2,
                             float x3, float y3, float z3, float u3, float v3,
                             float x4, float y4, float z4, float u4, float v4) {
        if (inward) {
            vertex(consumer, matrix, rotation, packedLight, packedOverlay, x1, y1, z1, u1, v1);
            vertex(consumer, matrix, rotation, packedLight, packedOverlay, x4, y4, z4, u4, v4);
            vertex(consumer, matrix, rotation, packedLight, packedOverlay, x3, y3, z3, u3, v3);
            vertex(consumer, matrix, rotation, packedLight, packedOverlay, x2, y2, z2, u2, v2);
            return;
        }
        vertex(consumer, matrix, rotation, packedLight, packedOverlay, x1, y1, z1, u1, v1);
        vertex(consumer, matrix, rotation, packedLight, packedOverlay, x2, y2, z2, u2, v2);
        vertex(consumer, matrix, rotation, packedLight, packedOverlay, x3, y3, z3, u3, v3);
        vertex(consumer, matrix, rotation, packedLight, packedOverlay, x4, y4, z4, u4, v4);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Rotation rotation,
                               int packedLight, int packedOverlay, float x, float y, float z, float u, float v) {
        float[] rotated = rotate(rotation, x, y, z);
        consumer.addVertex(matrix, rotated[0], rotated[1], rotated[2])
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(packedOverlay == 0 ? OverlayTexture.NO_OVERLAY : packedOverlay)
                .setLight(packedLight)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private static float[] rotate(Rotation rotation, float x, float y, float z) {
        if (rotation == null) {
            return new float[] {x, y, z};
        }
        float ox = rotation.origin()[0] / 16.0F;
        float oy = rotation.origin()[1] / 16.0F;
        float oz = rotation.origin()[2] / 16.0F;
        float rx = x - ox;
        float ry = y - oy;
        float rz = z - oz;
        float[] afterX = rotateAxis(rx, ry, rz, 'x', rotation.x());
        float[] afterY = rotateAxis(afterX[0], afterX[1], afterX[2], 'y', rotation.y());
        float[] afterZ = rotateAxis(afterY[0], afterY[1], afterY[2], 'z', rotation.z());
        return new float[] {afterZ[0] + ox, afterZ[1] + oy, afterZ[2] + oz};
    }

    private static float[] rotateAxis(float x, float y, float z, char axis, float degrees) {
        if (degrees == 0.0F) {
            return new float[] {x, y, z};
        }
        double radians = Math.toRadians(degrees);
        float sin = (float) Math.sin(radians);
        float cos = (float) Math.cos(radians);
        return switch (axis) {
            case 'x' -> new float[] {x, y * cos - z * sin, y * sin + z * cos};
            case 'y' -> new float[] {x * cos + z * sin, y, -x * sin + z * cos};
            case 'z' -> new float[] {x * cos - y * sin, x * sin + y * cos, z};
            default -> new float[] {x, y, z};
        };
    }

    private static Rotation rotation(JsonObject element) {
        float[] origin = element.has("origin") ? vector3(element.getAsJsonArray("origin")) : new float[] {8, 8, 8};
        if (!element.has("rotation")) {
            return null;
        }
        JsonElement rotation = element.get("rotation");
        if (rotation.isJsonArray()) {
            float[] euler = vector3(rotation.getAsJsonArray());
            return new Rotation(euler[0], euler[1], euler[2], origin);
        }
        if (rotation.isJsonObject()) {
            JsonObject object = rotation.getAsJsonObject();
            float angle = object.has("angle") ? object.get("angle").getAsFloat() : 0.0F;
            float[] objectOrigin = object.has("origin") ? vector3(object.getAsJsonArray("origin")) : origin;
            return switch (object.has("axis") ? object.get("axis").getAsString() : "y") {
                case "x" -> new Rotation(angle, 0.0F, 0.0F, objectOrigin);
                case "z" -> new Rotation(0.0F, 0.0F, angle, objectOrigin);
                default -> new Rotation(0.0F, angle, 0.0F, objectOrigin);
            };
        }
        return null;
    }

    private static String textureKey(JsonObject face) {
        if (!face.has("texture")) {
            return "";
        }
        JsonElement texture = face.get("texture");
        String key = texture.isJsonPrimitive() ? texture.getAsString() : "";
        return key.startsWith("#") ? key.substring(1) : key;
    }

    private static ResourceLocation textureLocation(String textureRef) {
        ResourceLocation ref = ResourceLocation.tryParse(textureRef);
        if (ref == null) {
            ref = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, textureRef);
        }
        return ResourceLocation.fromNamespaceAndPath(ref.getNamespace(), "textures/" + ref.getPath() + ".png");
    }

    private static float[] vector3(JsonArray array) {
        if (array == null || array.size() < 3) {
            return new float[] {0.0F, 0.0F, 0.0F};
        }
        return new float[] {array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()};
    }

    private static float[] vector4(JsonArray array) {
        if (array == null || array.size() < 4) {
            return new float[] {0.0F, 0.0F, 16.0F, 16.0F};
        }
        return new float[] {array.get(0).getAsFloat(), array.get(1).getAsFloat(),
                array.get(2).getAsFloat(), array.get(3).getAsFloat()};
    }

    private record Model(int textureWidth, int textureHeight, Map<String, ResourceLocation> textures,
                         JsonArray elements) {
        private ResourceLocation texture(String key) {
            return textures.get(key);
        }
    }

    private record Rotation(float x, float y, float z, float[] origin) {
    }
}
