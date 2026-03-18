package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.stardew.craft.blockentity.DecorAnchorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

@SuppressWarnings("null")
public class DecorAnchorBlockEntityRenderer implements BlockEntityRenderer<DecorAnchorBlockEntity> {
    public DecorAnchorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(DecorAnchorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5 + be.getOffsetX(), 0.5 + be.getOffsetY(), 0.5 + be.getOffsetZ());
        poseStack.mulPose(Axis.XP.rotationDegrees(be.getRotX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(be.getRotY()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(be.getRotZ()));

        if (!renderDecorModel(be, poseStack, buffer, packedLight, packedOverlay)) {
            int color = styleColor(be.getStyleId());
            int red = (color >> 16) & 0xFF;
            int green = (color >> 8) & 0xFF;
            int blue = color & 0xFF;

            VertexConsumer lines = buffer.getBuffer(RenderType.lines());
            Matrix4f m = poseStack.last().pose();

            float h = 0.35f;
            line(lines, m, -h, -h, -h, h, -h, -h, red, green, blue);
            line(lines, m, h, -h, -h, h, -h, h, red, green, blue);
            line(lines, m, h, -h, h, -h, -h, h, red, green, blue);
            line(lines, m, -h, -h, h, -h, -h, -h, red, green, blue);

            line(lines, m, -h, h, -h, h, h, -h, red, green, blue);
            line(lines, m, h, h, -h, h, h, h, red, green, blue);
            line(lines, m, h, h, h, -h, h, h, red, green, blue);
            line(lines, m, -h, h, h, -h, h, -h, red, green, blue);

            line(lines, m, -h, -h, -h, -h, h, -h, red, green, blue);
            line(lines, m, h, -h, -h, h, h, -h, red, green, blue);
            line(lines, m, h, -h, h, h, h, h, red, green, blue);
            line(lines, m, -h, -h, h, -h, h, h, red, green, blue);
        }

        poseStack.popPose();
    }

    @SuppressWarnings({"deprecation"})
    private static boolean renderDecorModel(DecorAnchorBlockEntity be, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be.getLevel() == null) {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();
        BlockState state = be.getBlockState();
        BakedModel model = mc.getBlockRenderer().getBlockModel(state);
        if (model == mc.getModelManager().getMissingModel()) {
            return false;
        }

        ModelBlockRenderer renderer = mc.getBlockRenderer().getModelRenderer();
        RenderType renderType = ItemBlockRenderTypes.getRenderType(state, false);
        renderer.tesselateBlock(
            be.getLevel(),
            model,
            state,
            be.getBlockPos(),
            poseStack,
            buffer.getBuffer(renderType),
            false,
            RandomSource.create(0L),
            0L,
            packedOverlay
        );
        return true;
    }

    private static void line(VertexConsumer vc, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b) {
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, 255).setNormal(0, 1, 0);
        vc.addVertex(m, x2, y2, z2).setColor(r, g, b, 255).setNormal(0, 1, 0);
    }

    private static int styleColor(String styleId) {
        if ("pine".equals(styleId)) {
            return 0x4D7F3C;
        }
        if ("stone".equals(styleId)) {
            return 0x9A9A9A;
        }
        if ("iridium".equals(styleId)) {
            return 0x7A6BAE;
        }
        return 0x8B5A2B;
    }
}
