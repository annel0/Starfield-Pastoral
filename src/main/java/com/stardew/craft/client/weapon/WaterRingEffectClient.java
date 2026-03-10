package com.stardew.craft.client.weapon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import java.util.Iterator;
import java.util.List;

public final class WaterRingEffectClient {

    private static final ResourceLocation RING_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/water_ring.png"
    );

    private static final List<Ring> RINGS = new ArrayList<>();

    private WaterRingEffectClient() {}

    public static void add(float x, float y, float z, float maxRadius, int durationTicks) {
        RINGS.add(new Ring(new Vec3(x, y, z), maxRadius, durationTicks));
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (RINGS.isEmpty()) {
            return;
        }
        Iterator<Ring> it = RINGS.iterator();
        while (it.hasNext()) {
            Ring ring = it.next();
            ring.age++;
            if (ring.age > ring.durationTicks) {
                it.remove();
            }
        }
    }

    @SuppressWarnings("null")
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        if (RINGS.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        RenderType ringType = RenderType.entityTranslucent(RING_TEXTURE);
        VertexConsumer consumer = buffer.getBuffer(ringType);

        for (Ring ring : RINGS) {
            float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            float age = ring.age + partial;
            float t = Math.min(1.0f, Math.max(0.0f, age / ring.durationTicks));
            float radius = ring.maxRadius * (0.2f + 0.8f * t);
            float alpha = t < 0.2f ? (t / 0.2f) : (1.0f - t);
            int a = Math.min(255, Math.max(0, (int) (alpha * 255)));

            double x = ring.pos.x - camPos.x;
            double y = ring.pos.y - camPos.y + 0.02;
            double z = ring.pos.z - camPos.z;

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.scale(radius, radius, radius);

            PoseStack.Pose last = poseStack.last();
            Matrix4f pose = last.pose();

            float size = 0.5f;
            vertex(consumer, pose, 0xF000F0, a, -size, -size, 0, 1);
            vertex(consumer, pose, 0xF000F0, a, size, -size, 1, 1);
            vertex(consumer, pose, 0xF000F0, a, size, size, 1, 0);
            vertex(consumer, pose, 0xF000F0, a, -size, size, 0, 0);

            poseStack.popPose();
        }

        buffer.endBatch(ringType);
    }

    @SuppressWarnings("null")
    private static void vertex(VertexConsumer consumer, Matrix4f pose, int light, int alpha,
                               float x, float y, float u, float v) {
        consumer.addVertex(pose, x, 0.0f, y)
            .setColor(255, 255, 255, alpha)
            .setUv(u, v)
            .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(0.0f, 1.0f, 0.0f);
    }

    private static final class Ring {
        private final Vec3 pos;
        private final float maxRadius;
        private final int durationTicks;
        private int age = 0;

        private Ring(Vec3 pos, float maxRadius, int durationTicks) {
            this.pos = pos;
            this.maxRadius = maxRadius;
            this.durationTicks = durationTicks;
        }
    }
}
