package com.stardew.craft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.entity.projectile.MeowmereProjectileEntity;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class MeowmereProjectileRenderer extends EntityRenderer<MeowmereProjectileEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/weapon_skill/meowmere_head.png");
    private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/white.png");
    private static final float TRAIL_WIDTH = 0.12f;
    private static final Map<Integer, Vec3> LAST_RIGHT = new HashMap<>();
    private static final double TRAIL_BACKWARD_SHIFT = 0.0;

    public MeowmereProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(@SuppressWarnings("null") MeowmereProjectileEntity entity) {
        return TEXTURE;
    }

    @SuppressWarnings("null")
    @Override
    public void render(@SuppressWarnings("null") MeowmereProjectileEntity entity, float entityYaw, float partialTicks, @SuppressWarnings("null") PoseStack poseStack, @SuppressWarnings("null") MultiBufferSource buffer, int packedLight) {
        renderTrail(entity, partialTicks, poseStack, buffer, packedLight);

        poseStack.pushPose();
        
        // 缩放和定位
        poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        PoseStack.Pose last = poseStack.last();
        Matrix4f matrix4f = last.pose();
        Matrix3f matrix3f = last.normal();
        @SuppressWarnings("null")
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
        @SuppressWarnings("null")
        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity)));
        
        // 绘制 -0.5 到 0.5 的矩形 (32x32 像素看起来大概是这么大)
        float size = 0.5f;
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, -size, -size, 0, 1);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, size, -size, 1, 1);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, size, size, 1, 0);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, -size, size, 0, 0);

        int glowLight = 0xF000F0;
        vertex(glowConsumer, matrix4f, matrix3f, glowLight, -size, -size, 0, 1);
        vertex(glowConsumer, matrix4f, matrix3f, glowLight, size, -size, 1, 1);
        vertex(glowConsumer, matrix4f, matrix3f, glowLight, size, size, 1, 0);
        vertex(glowConsumer, matrix4f, matrix3f, glowLight, -size, size, 0, 0);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderTrail(MeowmereProjectileEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isRemoved()) {
            return;
        }

        java.util.Deque<MeowmereProjectileEntity.TrailPoint> points = entity.getTrailPoints();
        if (points.size() < 2) {
            return;
        }

        @SuppressWarnings("null")
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE));
        @SuppressWarnings("null")
        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TRAIL_TEXTURE));
        Vec3 entityPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        Vec3 worldUp = new Vec3(0, 1, 0);
        Vec3 cameraPos = getViewerPos(entity, partialTicks);

        MeowmereProjectileEntity.TrailPoint[] pts = points.toArray(new MeowmereProjectileEntity.TrailPoint[0]);
        int n = pts.length;

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        Vec3 lastRight = LAST_RIGHT.get(entity.getId());
        for (int i = 0; i < n - 1; i++) {
            MeowmereProjectileEntity.TrailPoint a = pts[i];
            MeowmereProjectileEntity.TrailPoint b = pts[i + 1];

            Vec3 aPos = applyBackwardShift(a.position, cameraPos);
            Vec3 bPos = applyBackwardShift(b.position, cameraPos);
            @SuppressWarnings("null")
            Vec3 dir = bPos.subtract(aPos);
            if (dir.lengthSqr() < 1.0E-6) {
                continue;
            }
            dir = dir.normalize();
            Vec3 right;
            @SuppressWarnings("null")
            Vec3 viewDir = aPos.subtract(cameraPos);
            if (viewDir.lengthSqr() > 1.0E-6) {
                viewDir = viewDir.normalize();
            } else {
                viewDir = new Vec3(0, 0, -1);
            }
            if (lastRight != null && lastRight.lengthSqr() > 1.0E-6) {
                @SuppressWarnings("null")
                Vec3 projected = dir.cross(viewDir);
                if (projected.lengthSqr() < 1.0E-6) {
                    projected = dir.cross(worldUp);
                }
                @SuppressWarnings("null")
                Vec3 blended = lastRight.scale(0.65).add(projected.scale(0.35));
                if (blended.lengthSqr() < 1.0E-6) {
                    blended = projected;
                }
                right = blended.normalize();
            } else {
                @SuppressWarnings("null")
                Vec3 computed = dir.cross(viewDir);
                right = computed;
                if (right.lengthSqr() < 1.0E-6) {
                    right = dir.cross(new Vec3(1, 0, 0));
                }
                right = right.normalize();
            }
            lastRight = right;

            float tGlobal = (float) i / (float) (n - 1);
            float tNextGlobal = (float) (i + 1) / (float) (n - 1);

            float taper = smoothstep(tGlobal);
            float width = TRAIL_WIDTH * (0.18f + taper * 0.9f);
            Vec3 rightScaled = right.scale(width);
            Vec3 rightOuter = right.scale(width * 2.2f);
            Vec3 rightCore = right.scale(width * 0.65f);
            Vec3 rightInner = right.scale(width * 0.4f);

            float ageNorm = a.age / (float) MeowmereProjectileEntity.getTrailMaxAge();
            float fade = (float) Math.pow(1.0f - Math.min(1.0f, ageNorm), 1.5f);
            float lengthFade = 0.2f + 0.8f * taper;
            float alpha = 1.15f * fade * lengthFade;

            float time = (entity.tickCount + partialTicks) * 0.06f;
            float hue = (time % 1.0f + 1.0f) % 1.0f;
            float[] rgb = hsvToRgb(hue, 1.0f, 1.0f);
            addTrailQuad(consumer, matrix, packedLight, alpha * 0.5f, tGlobal, tNextGlobal, entityPos, aPos, bPos, rightOuter, rgb[0], rgb[1], rgb[2]);
            addTrailQuad(consumer, matrix, packedLight, alpha * 1.0f, tGlobal, tNextGlobal, entityPos, aPos, bPos, rightScaled, rgb[0], rgb[1], rgb[2]);
            addTrailQuad(glowConsumer, matrix, packedLight, alpha * 1.3f, tGlobal, tNextGlobal, entityPos, aPos, bPos, rightScaled, rgb[0], rgb[1], rgb[2]);
            addTrailQuad(glowConsumer, matrix, packedLight, alpha * 1.35f, tGlobal, tNextGlobal, entityPos, aPos, bPos, rightCore, rgb[0], rgb[1], rgb[2]);
            addTrailQuad(glowConsumer, matrix, packedLight, alpha * 1.15f, tGlobal, tNextGlobal, entityPos, aPos, bPos, rightInner, rgb[0], rgb[1], rgb[2]);
        }

        if (lastRight != null) {
            LAST_RIGHT.put(entity.getId(), lastRight);
        }
    }

    @SuppressWarnings("null")
    private static void addTrailQuad(VertexConsumer consumer, Matrix4f matrix, int packedLight, float alpha,
                                     float t, float tNext, Vec3 entityPos, Vec3 p0, Vec3 p1, Vec3 right,
                                     float r, float g, float b) {
        @SuppressWarnings("null")
        Vec3 p0r = p0.add(right).subtract(entityPos);
        @SuppressWarnings("null")
        Vec3 p0l = p0.subtract(right).subtract(entityPos);
        @SuppressWarnings("null")
        Vec3 p1r = p1.add(right).subtract(entityPos);
        @SuppressWarnings("null")
        Vec3 p1l = p1.subtract(right).subtract(entityPos);

        int glowLight = 0xF000F0;
        consumer.addVertex(matrix, (float) p0r.x, (float) p0r.y, (float) p0r.z)
            .setColor(r, g, b, alpha)
            .setUv(1.0f, t)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(glowLight)
            .setNormal(0.0f, 1.0f, 0.0f);

        consumer.addVertex(matrix, (float) p0l.x, (float) p0l.y, (float) p0l.z)
            .setColor(r, g, b, alpha)
            .setUv(0.0f, t)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(glowLight)
            .setNormal(0.0f, 1.0f, 0.0f);

        consumer.addVertex(matrix, (float) p1l.x, (float) p1l.y, (float) p1l.z)
            .setColor(r, g, b, alpha)
            .setUv(0.0f, tNext)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(glowLight)
            .setNormal(0.0f, 1.0f, 0.0f);

        consumer.addVertex(matrix, (float) p1r.x, (float) p1r.y, (float) p1r.z)
            .setColor(r, g, b, alpha)
            .setUv(1.0f, tNext)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(glowLight)
            .setNormal(0.0f, 1.0f, 0.0f);
    }

    private static float[] hsvToRgb(float h, float s, float v) {
        float hh = (h % 1.0f + 1.0f) % 1.0f;
        float c = v * s;
        float x = c * (1.0f - Math.abs((hh * 6.0f) % 2.0f - 1.0f));
        float m = v - c;
        float r;
        float g;
        float b;
        if (hh < 1.0f / 6.0f) {
            r = c; g = x; b = 0.0f;
        } else if (hh < 2.0f / 6.0f) {
            r = x; g = c; b = 0.0f;
        } else if (hh < 3.0f / 6.0f) {
            r = 0.0f; g = c; b = x;
        } else if (hh < 4.0f / 6.0f) {
            r = 0.0f; g = x; b = c;
        } else if (hh < 5.0f / 6.0f) {
            r = x; g = 0.0f; b = c;
        } else {
            r = c; g = 0.0f; b = x;
        }
        return new float[] { r + m, g + m, b + m };
    }


    private static float smoothstep(float t) {
        float x = Math.max(0.0f, Math.min(1.0f, t));
        return x * x * (3.0f - 2.0f * x);
    }

    @SuppressWarnings("null")
    private static Vec3 getViewerPos(MeowmereProjectileEntity entity, float partialTicks) {
        if (entity.level() == null) {
            return Vec3.ZERO;
        }
        var player = entity.level().getNearestPlayer(entity, 64.0);
        if (player != null) {
            return player.getEyePosition(partialTicks);
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return Vec3.ZERO;
        }
        return mc.player.getEyePosition(partialTicks);
    }

    @SuppressWarnings("null")
    private static Vec3 applyBackwardShift(Vec3 pos, Vec3 cameraPos) {
        if (cameraPos == null) {
            return pos;
        }
        Vec3 dir = pos.subtract(cameraPos);
        if (dir.lengthSqr() < 1.0E-6) {
            return pos;
        }
        return pos.add(dir.normalize().scale(TRAIL_BACKWARD_SHIFT));
    }
    
    @SuppressWarnings("null")
    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int lightmapUV, float x, float y, float u, float v) {
        consumer.addVertex(pose, x, y, 0.0F)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(lightmapUV)
            .setNormal(0.0F, 0.0F, 1.0F);
    }
}
