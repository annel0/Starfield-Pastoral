package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stardew.craft.blockentity.FruitTreeBlockEntity;
import com.stardew.craft.client.model.block.FruitTreeGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;

@SuppressWarnings("null")
public class FruitTreeBlockEntityRenderer extends StardewGeoBlockRenderer<FruitTreeBlockEntity> {
    public FruitTreeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new FruitTreeGeoModel());
    }

    @Nullable
    @Override
    public RenderType getRenderType(FruitTreeBlockEntity animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, FruitTreeBlockEntity animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        boolean oldHidden = bone.isHidden();
        if (bone.getParent() != null && "fruit".equals(bone.getParent().getName())) {
            bone.setHidden(!shouldRenderFruitBone(animatable.getFruitCount(), bone));
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, renderColor);
        bone.setHidden(oldHidden);
    }

    private static boolean shouldRenderFruitBone(int fruitCount, GeoBone bone) {
        int clampedCount = Math.max(0, Math.min(3, fruitCount));
        if (clampedCount == 0) {
            return false;
        }
        if (clampedCount >= 3) {
            return true;
        }

        GeoBone fruitRoot = bone.getParent();
        if (fruitRoot == null || fruitRoot.getChildBones().isEmpty()) {
            return false;
        }
        int total = fruitRoot.getChildBones().size();
        int visible = Math.max(1, (int) Math.ceil(total * clampedCount / 3.0D));
        int index = fruitRoot.getChildBones().indexOf(bone);
        return index >= 0 && index < visible;
    }
}
