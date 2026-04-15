package com.stardew.craft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stardew.craft.entity.junimo.JunimoEntity;
import com.stardew.craft.item.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.util.RenderUtil;

/**
 * SDV parity: Renders the correct held item on the Junimo's right_item bone.
 * The item is always rendered upright (no bone rotation applied).
 * Bundle color is applied via MC's ItemColor system (see StardewCraftClient).
 */
@SuppressWarnings("null")
public class JunimoBundleLayer extends BlockAndItemGeoLayer<JunimoEntity> {

    /**
     * Thread-local current bundle color for the ItemColor handler to read.
     * Set before rendering the bundle item, reset after.
     */
    public static volatile int currentRenderBundleColor = 0xFFFFFF;

    private ItemStack bundleItem;
    private ItemStack starItem;

    public JunimoBundleLayer(GeoRenderer<JunimoEntity> renderer) {
        super(renderer);
    }

    private ItemStack getBundleItem() {
        if (bundleItem == null) bundleItem = new ItemStack(ModItems.JUNIMO_BUNDLE.get());
        return bundleItem;
    }

    private ItemStack getStarItem() {
        if (starItem == null) starItem = new ItemStack(ModItems.JUNIMO_STAR.get());
        return starItem;
    }

    @Override
    @Nullable
    protected ItemStack getStackForBone(GeoBone bone, JunimoEntity animatable) {
        if ("right_item".equals(bone.getName())) {
            int type = animatable.getHoldingType();
            if (type == JunimoEntity.HOLDING_BUNDLE) return getBundleItem();
            if (type == JunimoEntity.HOLDING_STAR)   return getStarItem();
        }
        return null;
    }

    @Override
    @Nullable
    protected BlockState getBlockForBone(GeoBone bone, JunimoEntity animatable) {
        return null;
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, JunimoEntity animatable) {
        return ItemDisplayContext.GROUND;
    }

    /**
     * Override to apply only the bone's translation (not rotation),
     * so the held item always faces upright regardless of bone orientation.
     * Bundle color is set via static field for ItemColor to pick up.
     */
    @Override
    public void renderForBone(PoseStack poseStack, JunimoEntity animatable, GeoBone bone,
                              RenderType renderType, MultiBufferSource bufferSource,
                              VertexConsumer buffer, float partialTick,
                              int packedLight, int packedOverlay) {
        ItemStack stack = getStackForBone(bone, animatable);
        if (stack == null) return;

        // Don't render held item during fade (item renderer doesn't support per-entity alpha)
        if (animatable.getAlpha() < 0.5f) return;

        poseStack.pushPose();
        // Apply only bone pivot translation, skip rotation so item stays upright
        RenderUtil.translateToPivotPoint(poseStack, bone);

        // SDV parity: set the bundle color for the ItemColor handler to read
        if (animatable.getHoldingType() == JunimoEntity.HOLDING_BUNDLE) {
            currentRenderBundleColor = animatable.getBundleColor();
        }

        renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);

        // Reset to white (no tint)
        currentRenderBundleColor = 0xFFFFFF;
        poseStack.popPose();
    }
}
