package com.stardew.craft.client.weapon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stardew.craft.StardewCraft;
import com.mojang.math.Axis;
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

public final class ObsidianCrackEffectClient {

    private static final ResourceLocation CRACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/obsidian_crack.png"
    );

    private static final List<Crack> CRACKS = new ArrayList<>();

    private ObsidianCrackEffectClient() {}

    public static void add(float x, float y, float z, float yaw, float length, int durationTicks) {
        CRACKS.add(new Crack(new Vec3(x, y, z), yaw, length, durationTicks));
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (CRACKS.isEmpty()) {
            return;
        }
        Iterator<Crack> it = CRACKS.iterator();
        while (it.hasNext()) {
            Crack crack = it.next();
            crack.age++;
            if (crack.age > crack.durationTicks) {
                it.remove();
            }
        }
    }

    @SuppressWarnings("null")
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        if (CRACKS.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        RenderType crackType = RenderType.entityTranslucent(CRACK_TEXTURE);
        VertexConsumer consumer = buffer.getBuffer(crackType);

        for (Crack crack : CRACKS) {
            float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            float age = crack.age + partial;
            float t = Math.min(1.0f, Math.max(0.0f, age / crack.durationTicks));

            float alpha = 0.85f;
            if (t < 0.15f) {
                alpha *= (t / 0.15f);
            } else if (t > 0.75f) {
                alpha *= ((1.0f - t) / 0.25f);
            }
            if (t > 0.45f && t < 0.55f) {
                alpha = Math.min(1.0f, alpha + 0.4f); // 闪一下
            }
            int a = Math.min(255, Math.max(0, (int) (alpha * 255)));

            double x = crack.pos.x - camPos.x;
            double y = crack.pos.y - camPos.y + 0.04;
            double z = crack.pos.z - camPos.z;

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(Axis.YP.rotationDegrees(crack.yaw));

            float halfLength = crack.length * 0.5f;
            float halfWidth = 0.22f;

            PoseStack.Pose last = poseStack.last();
            Matrix4f pose = last.pose();
            vertex(consumer, pose, 0xF000F0, a, -halfLength, -halfWidth, 0, 1);
            vertex(consumer, pose, 0xF000F0, a, halfLength, -halfWidth, 1, 1);
            vertex(consumer, pose, 0xF000F0, a, halfLength, halfWidth, 1, 0);
            vertex(consumer, pose, 0xF000F0, a, -halfLength, halfWidth, 0, 0);

            poseStack.popPose();
        }

        buffer.endBatch(crackType);
    }

    @SuppressWarnings("null")
    private static void vertex(VertexConsumer consumer, Matrix4f pose, int light, int alpha,
                               float x, float z, float u, float v) {
        consumer.addVertex(pose, x, 0.0f, z)
            .setColor(255, 255, 255, alpha)
            .setUv(u, v)
            .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(0.0f, 1.0f, 0.0f);
    }

    private static final class Crack {
        private final Vec3 pos;
        private final float yaw;
        private final float length;
        private final int durationTicks;
        private int age = 0;

        private Crack(Vec3 pos, float yaw, float length, int durationTicks) {
            this.pos = pos;
            this.yaw = yaw;
            this.length = length;
            this.durationTicks = durationTicks;
        }
    }
}