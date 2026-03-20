package com.stardew.craft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.entity.projectile.ElfBladeLeafEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class ElfBladeLeafRenderer extends EntityRenderer<ElfBladeLeafEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID, "textures/gui/weapon_skill/elf_blade_leaf.png"
    );
    private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "minecraft", "textures/misc/white.png"
    );
    private static final float TRAIL_WIDTH = 0.12f;
    private static final Map<Integer, Vec3> LAST_RIGHT = new HashMap<>();

    private static final int TRAIL_FADE_IN = 0xB7FF8A;
    private static final int TRAIL_FADE_OUT = 0x58D0A6;
    private static final double TRAIL_BACKWARD_SHIFT = 0.08;

    private static final float BASE_R = 0.55f;
    private static final float BASE_G = 1.0f;
    private static final float BASE_B = 0.62f;

    public ElfBladeLeafRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(@SuppressWarnings("null") ElfBladeLeafEntity entity) {
        return TEXTURE;
    }

    @SuppressWarnings("null")
    @Override
    public void render(@SuppressWarnings("null") ElfBladeLeafEntity entity, float entityYaw, float partialTicks,
                       @SuppressWarnings("null") PoseStack poseStack,
                       @SuppressWarnings("null") MultiBufferSource buffer,
                       int packedLight) {
        renderTrail(entity, partialTicks, poseStack, buffer, packedLight);

        poseStack.pushPose();
        poseStack.scale(0.55f, 0.55f, 0.55f);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        PoseStack.Pose last = poseStack.last();
        Matrix4f matrix4f = last.pose();
        Matrix3f matrix3f = last.normal();
        VertexConsumer baseConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));

        float size = 0.45f;
        int glowLight = 0xF000F0;
        vertex(baseConsumer, matrix4f, matrix3f, packedLight, -size, -size, 0, 1, BASE_R, BASE_G, BASE_B, 0.9f);
        vertex(baseConsumer, matrix4f, matrix3f, packedLight, size, -size, 1, 1, BASE_R, BASE_G, BASE_B, 0.9f);
        vertex(baseConsumer, matrix4f, matrix3f, packedLight, size, size, 1, 0, BASE_R, BASE_G, BASE_B, 0.9f);
        vertex(baseConsumer, matrix4f, matrix3f, packedLight, -size, size, 0, 0, BASE_R, BASE_G, BASE_B, 0.9f);

        vertex(glowConsumer, matrix4f, matrix3f, glowLight, -size, -size, 0, 1, BASE_R, BASE_G, BASE_B, 0.9f);
        vertex(glowConsumer, matrix4f, matrix3f, glowLight, size, -size, 1, 1, BASE_R, BASE_G, BASE_B, 0.9f);
        vertex(glowConsumer, matrix4f, matrix3f, glowLight, size, size, 1, 0, BASE_R, BASE_G, BASE_B, 0.9f);
        vertex(glowConsumer, matrix4f, matrix3f, glowLight, -size, size, 0, 0, BASE_R, BASE_G, BASE_B, 0.9f);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @SuppressWarnings("null")
    private void renderTrail(ElfBladeLeafEntity entity, float partialTicks, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight) {
        if (entity.isRemoved()) {
            return;
        }

        java.util.Deque<ElfBladeLeafEntity.TrailPoint> points = entity.getTrailPoints();
        if (points.size() < 2) {
            return;
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE));
        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE));
        Vec3 entityPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        Vec3 worldUp = new Vec3(0, 1, 0);
        Vec3 cameraPos = getCameraPos();

        ElfBladeLeafEntity.TrailPoint[] pts = points.toArray(new ElfBladeLeafEntity.TrailPoint[0]);
        int n = pts.length;

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float totalTex = pts[n - 1].texcoord;
        if (totalTex <= 1.0E-4f) {
            return;
        }

        Vec3 lastRight = LAST_RIGHT.get(entity.getId());
        for (int i = 0; i < n - 1; i++) {
            ElfBladeLeafEntity.TrailPoint a = pts[i];
            ElfBladeLeafEntity.TrailPoint b = pts[i + 1];

            Vec3 aPos = applyBackwardShift(a.position, cameraPos);
            Vec3 bPos = applyBackwardShift(b.position, cameraPos);
            Vec3 dir = bPos.subtract(aPos);
            if (dir.lengthSqr() < 1.0E-6) {
                continue;
            }
            dir = dir.normalize();
            Vec3 right;
            Vec3 viewDir = aPos.subtract(cameraPos);
            if (viewDir.lengthSqr() > 1.0E-6) {
                viewDir = viewDir.normalize();
            } else {
                viewDir = new Vec3(0, 0, -1);
            }
            if (lastRight != null && lastRight.lengthSqr() > 1.0E-6) {
                Vec3 projected = dir.cross(viewDir);
                if (projected.lengthSqr() < 1.0E-6) {
                    projected = dir.cross(worldUp);
                }
                Vec3 blended = lastRight.scale(0.65).add(projected.scale(0.35));
                if (blended.lengthSqr() < 1.0E-6) {
                    blended = projected;
                }
                right = blended.normalize();
            } else {
                right = dir.cross(viewDir);
                if (right.lengthSqr() < 1.0E-6) {
                    right = dir.cross(new Vec3(1, 0, 0));
                }
                right = right.normalize();
            }
            lastRight = right;

            float tGlobal = (float) i / (float) (n - 1);
            float tNextGlobal = (float) (i + 1) / (float) (n - 1);

            float taper = smoothstep(tGlobal);
            float orbitTaper = entity.isOrbiting() ? (0.7f * taper) : taper;
            float widthScale = entity.isOrbiting() ? 0.95f : 1.1f;
            float alphaScale = entity.isOrbiting() ? 0.8f : 1.1f;
            float width = TRAIL_WIDTH * (0.06f + orbitTaper * 1.25f) * widthScale;
            Vec3 rightScaled = right.scale(width);
            Vec3 rightOuter = right.scale(width * 1.9f);
            Vec3 rightCore = right.scale(width * 0.55f);

            float ageNorm = a.age / (float) entity.getTrailMaxAgeValue();
            float fade = (float) Math.pow(1.0f - Math.min(1.0f, ageNorm), 1.6f);
            float lengthFade = 0.18f + 0.82f * taper;
            float alpha = 1.15f * fade * lengthFade * alphaScale;

            float[] rgb = lerpColor(TRAIL_FADE_IN, TRAIL_FADE_OUT, tGlobal);

            addTrailQuad(consumer, matrix, packedLight, alpha * 0.35f, tGlobal, tNextGlobal,
                entityPos, aPos, bPos, rightOuter, rgb[0], rgb[1], rgb[2]);
            addTrailQuad(consumer, matrix, packedLight, alpha * 0.7f, tGlobal, tNextGlobal,
                entityPos, aPos, bPos, rightScaled, rgb[0], rgb[1], rgb[2]);
            addTrailQuad(glowConsumer, matrix, packedLight, alpha, tGlobal, tNextGlobal,
                entityPos, aPos, bPos, rightScaled, rgb[0], rgb[1], rgb[2]);
            addTrailQuad(glowConsumer, matrix, packedLight, alpha, tGlobal, tNextGlobal,
                entityPos, aPos, bPos, rightCore, rgb[0], rgb[1], rgb[2]);
        }

        if (lastRight != null) {
            LAST_RIGHT.put(entity.getId(), lastRight);
        }
    }

    @SuppressWarnings("null")
    private static void addTrailQuad(VertexConsumer consumer, Matrix4f matrix, int packedLight, float alpha,
                                     float t, float tNext, Vec3 entityPos, Vec3 p0, Vec3 p1, Vec3 right,
                                     float r, float g, float b) {
        Vec3 p0r = p0.add(right).subtract(entityPos);
        Vec3 p0l = p0.subtract(right).subtract(entityPos);
        Vec3 p1r = p1.add(right).subtract(entityPos);
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

    @SuppressWarnings("null")
    private static Vec3 getCameraPos() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return Vec3.ZERO;
        }
        return mc.player.getEyePosition(1.0f);
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

    private static float[] lerpColor(int start, int end, float t) {
        float clamped = Math.max(0.0f, Math.min(1.0f, t));
        float sr = ((start >> 16) & 0xFF) / 255.0f;
        float sg = ((start >> 8) & 0xFF) / 255.0f;
        float sb = (start & 0xFF) / 255.0f;
        float er = ((end >> 16) & 0xFF) / 255.0f;
        float eg = ((end >> 8) & 0xFF) / 255.0f;
        float eb = (end & 0xFF) / 255.0f;
        return new float[] {
            sr + (er - sr) * clamped,
            sg + (eg - sg) * clamped,
            sb + (eb - sb) * clamped
        };
    }

    private static float smoothstep(float t) {
        float x = Math.max(0.0f, Math.min(1.0f, t));
        return x * x * (3.0f - 2.0f * x);
    }

    @SuppressWarnings("null")
    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int lightmapUV,
                               float x, float y, float u, float v,
                               float r, float g, float b, float a) {
        consumer.addVertex(pose, x, y, 0.0F)
            .setColor(r, g, b, a)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(lightmapUV)
            .setNormal(0.0F, 0.0F, 1.0F);
    }
}
