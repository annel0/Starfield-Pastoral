package com.stardew.craft.client.weapon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.stardew.craft.Config;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.combat.VfxColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

public final class EvolvedAuraEffectClient {

    private static final ResourceLocation RUNE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/special_effect/1_f.png"
    );

    private static final ResourceLocation CORE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/special_effect/1_b.png"
    );

    private EvolvedAuraEffectClient() {}

    @SuppressWarnings("null")
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        if (!Config.ENABLE_WEAPON_SPECIAL_EFFECTS.getAsBoolean()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }

        int stacks = SingularityClientState.getStacks(player);
        if (stacks < 12) {
            return;
        }

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        RenderType runeType = RenderType.entityTranslucent(RUNE_TEXTURE);
        RenderType coreType = RenderType.entityTranslucent(CORE_TEXTURE);

        float age = player.tickCount + event.getPartialTick().getGameTimeDeltaPartialTick(false);
        float pulse = 0.85f + 0.15f * (float) Math.sin(age * 0.2f);
        float radius = 1.1f * pulse;

        int color = VfxColors.INFINITY_GOLD;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int alpha = 180;

        Vec3 pos = player.position();
        double x = pos.x - camPos.x;
        double y = pos.y - camPos.y + 0.02;
        double z = pos.z - camPos.z;

        VertexConsumer runeConsumer = buffer.getBuffer(runeType);
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(age * 2.5f));
        poseStack.scale(radius * 2.0f, radius * 2.0f, radius * 2.0f);

        PoseStack.Pose last = poseStack.last();
        Matrix4f pose = last.pose();
        float size = 0.5f;
        vertex(runeConsumer, pose, 0xF000F0, r, g, b, alpha, -size, -size, 0, 1);
        vertex(runeConsumer, pose, 0xF000F0, r, g, b, alpha, size, -size, 1, 1);
        vertex(runeConsumer, pose, 0xF000F0, r, g, b, alpha, size, size, 1, 0);
        vertex(runeConsumer, pose, 0xF000F0, r, g, b, alpha, -size, size, 0, 0);
        poseStack.popPose();

        VertexConsumer coreConsumer = buffer.getBuffer(coreType);
        poseStack.pushPose();
        poseStack.translate(x, y + 0.8, z);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(0.9f, 1.6f, 0.9f);

        PoseStack.Pose column = poseStack.last();
        Matrix4f columnPose = column.pose();
        int colAlpha = 140;
        vertex(coreConsumer, columnPose, 0xF000F0, r, g, b, colAlpha, -size, -size, 0, 1);
        vertex(coreConsumer, columnPose, 0xF000F0, r, g, b, colAlpha, size, -size, 1, 1);
        vertex(coreConsumer, columnPose, 0xF000F0, r, g, b, colAlpha, size, size, 1, 0);
        vertex(coreConsumer, columnPose, 0xF000F0, r, g, b, colAlpha, -size, size, 0, 0);
        poseStack.popPose();

        buffer.endBatch(runeType);
        buffer.endBatch(coreType);
    }

    @SuppressWarnings("null")
    private static void vertex(VertexConsumer consumer, Matrix4f pose, int light,
                               int r, int g, int b, int alpha,
                               float x, float y, float u, float v) {
        consumer.addVertex(pose, x, 0.0f, y)
            .setColor(r, g, b, alpha)
            .setUv(u, v)
            .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(0.0f, 1.0f, 0.0f);
    }

}
