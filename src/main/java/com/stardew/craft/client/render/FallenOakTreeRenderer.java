package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stardew.craft.entity.FallenOakTreeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FallenOakTreeRenderer extends EntityRenderer<FallenOakTreeEntity> {
	public FallenOakTreeRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.0F;
	}

	@SuppressWarnings({ "null", "deprecation" })
	@Override
	public void render(@SuppressWarnings("null") FallenOakTreeEntity entity, float entityYaw, float partialTicks, @SuppressWarnings("null") PoseStack poseStack, @SuppressWarnings("null") MultiBufferSource buffer, int packedLight) {
		Level level = entity.level();
		BlockPos pivot = entity.blockPosition();

		float t = entity.getProgress(partialTicks);
		// Ease-in: starts slower, accelerates later.
		float eased = t * t;
		float angle = 90.0F * eased;
		Direction dir = entity.getFallDirection();

		poseStack.pushPose();
		// Rotate around the bottom-center of the trunk0 pivot block.
		poseStack.translate(0.5F, 0.0F, 0.5F);
		switch (dir) {
			case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(-angle));
			case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
			case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(angle));
			case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-angle));
			default -> {
			}
		}
		poseStack.translate(-0.5F, 0.0F, -0.5F);

		var blockRenderer = Minecraft.getInstance().getBlockRenderer();
		for (FallenOakTreeEntity.Piece piece : entity.getPieces()) {
			BlockState state = entity.resolveBlockState(piece);
			if (state.isAir()) {
				continue;
			}

			poseStack.pushPose();
			poseStack.translate(piece.dx, piece.dy, piece.dz);

			int light = packedLight;
			if (level != null) {
				BlockPos p = pivot.offset(piece.dx, piece.dy, piece.dz);
				light = LevelRenderer.getLightColor(level, p);
			}

			blockRenderer.renderSingleBlock(state, poseStack, buffer, light, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
			poseStack.popPose();
		}

		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ResourceLocation getTextureLocation(@SuppressWarnings("null") FallenOakTreeEntity entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
