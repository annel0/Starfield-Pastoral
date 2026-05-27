package com.stardew.craft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.utility.LuckyPurpleShortsBlock;
import com.stardew.craft.entity.monster.LuckyPurpleShortsMonsterEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("null")
public class LuckyPurpleShortsMonsterRenderer extends EntityRenderer<LuckyPurpleShortsMonsterEntity> {
    public LuckyPurpleShortsMonsterRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.35F;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(LuckyPurpleShortsMonsterEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        float age = entity.tickCount + partialTicks;
        float bob = Mth.sin(age * 0.22F) * 0.12F;
        float jitterX = Mth.sin(age * 1.47F) * 0.025F;
        float jitterZ = Mth.cos(age * 1.31F) * 0.025F;

        poseStack.pushPose();
        poseStack.translate(jitterX, 0.2F + bob, jitterZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        poseStack.scale(0.78F, 0.78F, 0.78F);
        poseStack.translate(-0.5F, 0.0F, -0.5F);

        BlockState state = ModBlocks.LUCKY_PURPLE_SHORTS.get().defaultBlockState()
                .setValue(LuckyPurpleShortsBlock.FACING, Direction.NORTH);
        int light = packedLight;
        Level level = entity.level();
        if (level != null) {
            light = LevelRenderer.getLightColor(level, entity.blockPosition());
        }
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                state,
                poseStack,
                buffer,
                light,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
        );
        poseStack.popPose();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(LuckyPurpleShortsMonsterEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
