package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.blockentity.IncubatorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class IncubatorBlockEntityRenderer implements BlockEntityRenderer<IncubatorBlockEntity> {
    private static final ResourceLocation BUBBLE_TEX = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/bubble.png");
    private static final float PX = 1.0f / 32.0f;

    private static final Map<String, ResourceLocation> ICONS = Map.of(
        "white_chicken", ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_white_chicken.png"),
        "golden_chicken", ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_golden_chicken.png"),
        "duck", ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_duck.png"),
        "void_chicken", ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_void_chicken.png"),
        "dinosaur", ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_dinosaur.png")
    );

    public IncubatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @SuppressWarnings("null")
    @Override
    public void render(@SuppressWarnings("null") IncubatorBlockEntity be, float partialTick, @SuppressWarnings("null") PoseStack poseStack, @SuppressWarnings("null") MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be == null || !be.isReady()) {
            return;
        }

        String animalTypeId = be.getReadyAnimalTypeId();
        if (animalTypeId == null) {
            return;
        }
        ResourceLocation iconTex = ICONS.get(animalTypeId);
        if (iconTex == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5f, 1.22f, 0.5f);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

        float w = 20 * PX;
        float h = 24 * PX;
        float x0 = -w / 2.0f;
        float x1 = w / 2.0f;
        float y0 = 0.0f;
        float y1 = h;

        @SuppressWarnings("null")
        VertexConsumer bubble = buffer.getBuffer(RenderType.entityTranslucent(BUBBLE_TEX));
        bubble.addVertex(poseStack.last().pose(), x0, y1, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        bubble.addVertex(poseStack.last().pose(), x1, y1, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        bubble.addVertex(poseStack.last().pose(), x1, y0, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        bubble.addVertex(poseStack.last().pose(), x0, y0, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);

        float iconW = 14 * PX;
        float iconH = 14 * PX;
        float ix0 = x0 + (3 * PX);
        float ix1 = ix0 + iconW;
        float iy1 = y1 - (3 * PX);
        float iy0 = iy1 - iconH;

        VertexConsumer icon = buffer.getBuffer(RenderType.entityCutoutNoCull(iconTex));
        icon.addVertex(poseStack.last().pose(), ix0, iy1, 0.001f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        icon.addVertex(poseStack.last().pose(), ix1, iy1, 0.001f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        icon.addVertex(poseStack.last().pose(), ix1, iy0, 0.001f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        icon.addVertex(poseStack.last().pose(), ix0, iy0, 0.001f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);

        poseStack.popPose();
    }
}
