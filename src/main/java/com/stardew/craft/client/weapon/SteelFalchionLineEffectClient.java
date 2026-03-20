package com.stardew.craft.client.weapon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.stardew.craft.StardewCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("null")
public final class SteelFalchionLineEffectClient {

    private static final ResourceLocation HEAD_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/steel_falchion_head.png"
    );
    private static final ResourceLocation MID_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/steel_falchion_mid.png"
    );
    private static final ResourceLocation TAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/steel_falchion_tail.png"
    );

    private static final Map<Integer, Line> LINES = new HashMap<>();

    private SteelFalchionLineEffectClient() {}

    public static void create(int lineId, float x, float y, float z, int durationTicks, float width) {
        Line line = new Line(durationTicks, width);
        line.points.add(new Vec3(x, y, z));
        LINES.put(lineId, line);
    }

    public static void addPoint(int lineId, float x, float y, float z) {
        Line line = LINES.get(lineId);
        if (line == null) {
            return;
        }
        line.points.add(new Vec3(x, y, z));
    }

    public static void pulse(int lineId, int durationTicks) {
        Line line = LINES.get(lineId);
        if (line == null) {
            return;
        }
        line.pulseTicks = Math.max(line.pulseTicks, durationTicks);
    }

    public static void burst(int lineId) {
        Line line = LINES.get(lineId);
        if (line == null) {
            return;
        }
        line.burstTicks = Math.max(line.burstTicks, 10);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (LINES.isEmpty()) {
            return;
        }
        Iterator<Line> it = LINES.values().iterator();
        while (it.hasNext()) {
            Line line = it.next();
            line.age++;
            if (line.age > line.durationTicks) {
                it.remove();
                continue;
            }
            if (line.pulseTicks > 0) {
                line.pulseTicks--;
            }
            if (line.burstTicks > 0) {
                line.burstTicks--;
            }
        }
    }

    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        if (LINES.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        RenderType headType = RenderType.entityTranslucent(HEAD_TEXTURE);
        RenderType midType = RenderType.entityTranslucent(MID_TEXTURE);
        RenderType tailType = RenderType.entityTranslucent(TAIL_TEXTURE);

        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        VertexConsumer headConsumer = buffer.getBuffer(headType);
        for (Line line : LINES.values()) {
            if (line.points.size() < 2) {
                continue;
            }
            float[] alphaWidth = computeAlphaAndWidth(line, partial);
            renderLineHead(line, poseStack, camPos, headConsumer, alphaWidth[0], alphaWidth[1]);
        }
        buffer.endBatch(headType);

        VertexConsumer midConsumer = buffer.getBuffer(midType);
        for (Line line : LINES.values()) {
            if (line.points.size() < 2) {
                continue;
            }
            float[] alphaWidth = computeAlphaAndWidth(line, partial);
            renderLineMid(line, poseStack, camPos, midConsumer, alphaWidth[0], alphaWidth[1]);
        }
        buffer.endBatch(midType);

        VertexConsumer tailConsumer = buffer.getBuffer(tailType);
        for (Line line : LINES.values()) {
            if (line.points.size() < 2) {
                continue;
            }
            float[] alphaWidth = computeAlphaAndWidth(line, partial);
            renderLineTail(line, poseStack, camPos, tailConsumer, alphaWidth[0], alphaWidth[1]);
        }
        buffer.endBatch(tailType);
    }

    private static float[] computeAlphaAndWidth(Line line, float partial) {
        float age = line.age + partial;
        float t = Math.min(1.0f, Math.max(0.0f, age / line.durationTicks));

        float alpha = 0.9f;
        if (t < 0.15f) {
            alpha *= (t / 0.15f);
        } else if (t > 0.75f) {
            alpha *= ((1.0f - t) / 0.25f);
        }

        float width = line.width;
        if (line.pulseTicks > 0) {
            alpha = Math.min(1.0f, alpha + 0.25f);
            width *= 1.15f;
        }
        if (line.burstTicks > 0) {
            alpha = Math.min(1.0f, alpha + 0.45f);
            width *= 1.35f;
        }
        return new float[] { alpha, width };
    }

    private static void renderLineHead(Line line, PoseStack poseStack, Vec3 camPos,
                                   VertexConsumer headConsumer, float alpha, float width) {
        List<Vec3> points = line.points;
        int count = points.size();
        if (count < 2) {
            return;
        }

        Vec3 first = points.get(0);
        Vec3 second = points.get(1);
        Vec3 firstDir = normalize2D(second.subtract(first));

        float headLen = (float) Math.min(0.6, second.subtract(first).horizontalDistance());
        float halfWidth = width * 0.5f;

        if (headLen > 0.01f) {
            Vec3 headCenter = first.add(firstDir.scale(headLen * 0.5));
            float yaw = yawFromDir(firstDir);
            renderQuad(headConsumer, poseStack, camPos, headCenter, yaw, headLen, halfWidth, alpha);
        }
    }

    private static void renderLineTail(Line line, PoseStack poseStack, Vec3 camPos,
                                   VertexConsumer tailConsumer, float alpha, float width) {
        List<Vec3> points = line.points;
        int count = points.size();
        if (count < 2) {
            return;
        }

        Vec3 last = points.get(count - 1);
        Vec3 prev = points.get(count - 2);
        Vec3 lastDir = normalize2D(last.subtract(prev));
        float tailLen = (float) Math.min(0.6, last.subtract(prev).horizontalDistance());
        float halfWidth = width * 0.5f;

        if (tailLen > 0.01f) {
            Vec3 tailCenter = last.subtract(lastDir.scale(tailLen * 0.5));
            float yaw = yawFromDir(lastDir);
            renderQuad(tailConsumer, poseStack, camPos, tailCenter, yaw, tailLen, halfWidth, alpha);
        }
    }

    private static void renderLineMid(Line line, PoseStack poseStack, Vec3 camPos,
                                   VertexConsumer midConsumer, float alpha, float width) {
        List<Vec3> points = line.points;
        int count = points.size();
        if (count < 2) {
            return;
        }

        Vec3 first = points.get(0);
        Vec3 second = points.get(1);
        Vec3 last = points.get(count - 1);
        Vec3 prev = points.get(count - 2);
        float headLen = (float) Math.min(0.6, second.subtract(first).horizontalDistance());
        float tailLen = (float) Math.min(0.6, last.subtract(prev).horizontalDistance());
        float halfWidth = width * 0.5f;

        for (int i = 0; i < count - 1; i++) {
            Vec3 a = points.get(i);
            Vec3 b = points.get(i + 1);
            Vec3 dir = normalize2D(b.subtract(a));
            double segLen = a.subtract(b).horizontalDistance();
            if (segLen < 0.02) {
                continue;
            }

            double startOffset = (i == 0) ? headLen : 0.0;
            double endOffset = (i == count - 2) ? tailLen : 0.0;
            double usable = segLen - startOffset - endOffset;
            if (usable <= 0.01) {
                continue;
            }

            Vec3 segStart = a.add(dir.scale(startOffset));
            Vec3 segEnd = b.subtract(dir.scale(endOffset));
            renderTiledMid(midConsumer, poseStack, camPos, segStart, segEnd, halfWidth, alpha);
        }
    }

    private static void renderTiledMid(VertexConsumer consumer, PoseStack poseStack, Vec3 camPos,
                                       Vec3 start, Vec3 end, float halfWidth, float alpha) {
        Vec3 dir = normalize2D(end.subtract(start));
        double length = start.subtract(end).horizontalDistance();
        if (length < 0.02) {
            return;
        }

        double tileLen = 0.5;
        int tiles = Math.max(1, (int) Math.ceil(length / tileLen));
        double actualLen = length / tiles;
        float yaw = yawFromDir(dir);

        for (int i = 0; i < tiles; i++) {
            double centerDist = (i + 0.5) * actualLen;
            Vec3 center = start.add(dir.scale(centerDist));
            renderQuad(consumer, poseStack, camPos, center, yaw, (float) actualLen, halfWidth, alpha);
        }
    }

    private static void renderQuad(VertexConsumer consumer, PoseStack poseStack, Vec3 camPos,
                                   Vec3 center, float yaw, float halfLength, float halfWidth, float alpha) {
        double x = center.x - camPos.x;
        double y = center.y - camPos.y + 0.02;
        double z = center.z - camPos.z;

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        PoseStack.Pose last = poseStack.last();
        Matrix4f pose = last.pose();
        int a = Math.min(255, Math.max(0, (int) (alpha * 255)));

        vertex(consumer, pose, a, -halfLength, -halfWidth, 0, 1);
        vertex(consumer, pose, a, halfLength, -halfWidth, 1, 1);
        vertex(consumer, pose, a, halfLength, halfWidth, 1, 0);
        vertex(consumer, pose, a, -halfLength, halfWidth, 0, 0);

        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, int alpha,
                               float x, float z, float u, float v) {
        consumer.addVertex(pose, x, 0.0f, z)
            .setColor(255, 255, 255, alpha)
            .setUv(u, v)
            .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
            .setLight(0xF000F0)
            .setNormal(0.0f, 1.0f, 0.0f);
    }

    private static Vec3 normalize2D(Vec3 v) {
        Vec3 flat = new Vec3(v.x, 0.0, v.z);
        double len = flat.length();
        if (len < 1.0E-6) {
            return new Vec3(0.0, 0.0, 0.0);
        }
        return flat.scale(1.0 / len);
    }

    private static float yawFromDir(Vec3 dir) {
        if (dir.lengthSqr() < 1.0E-6) {
            return 0.0f;
        }
        return (float) Math.toDegrees(Math.atan2(-dir.z, dir.x));
    }

    private static final class Line {
        private final List<Vec3> points = new ArrayList<>();
        private final int durationTicks;
        private final float width;
        private int age = 0;
        private int pulseTicks = 0;
        private int burstTicks = 0;

        private Line(int durationTicks, float width) {
            this.durationTicks = Math.max(1, durationTicks);
            this.width = width;
        }
    }
}
