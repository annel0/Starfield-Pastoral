package com.stardew.craft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stardew.craft.cutscene.runtime.EventPlayerActorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * Renders {@link EventPlayerActorEntity} using the local player's skin
 * and the vanilla {@link PlayerModel}.
 */
public class EventPlayerActorRenderer extends MobRenderer<EventPlayerActorEntity, PlayerModel<EventPlayerActorEntity>> {

    private static final ResourceLocation STEVE_SKIN =
            ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");

    public EventPlayerActorRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        // Armor layer
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));
        // Held items layer
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(@javax.annotation.Nonnull EventPlayerActorEntity entity) {
        // Use the local player's skin if available
        Minecraft mc = Minecraft.getInstance();
        if (mc.player instanceof AbstractClientPlayer clientPlayer) {
            return clientPlayer.getSkin().texture();
        }
        return STEVE_SKIN;
    }

    @Override
    public void render(@javax.annotation.Nonnull EventPlayerActorEntity entity, float entityYaw, float partialTicks,
                       @javax.annotation.Nonnull PoseStack poseStack, @javax.annotation.Nonnull MultiBufferSource buffer, int packedLight) {
        // Apply item-above-head arm pose before rendering
        if (entity.isHoldingItemAboveHead()) {
            PlayerModel<EventPlayerActorEntity> model = this.getModel();
            // Raise both arms straight up (90 degrees from horizontal = pointing at sky)
            model.leftArm.xRot = (float) Math.toRadians(-180);
            model.rightArm.xRot = (float) Math.toRadians(-180);
            model.leftArm.yRot = 0;
            model.rightArm.yRot = 0;
            model.leftArm.zRot = (float) Math.toRadians(10);  // slight outward angle
            model.rightArm.zRot = (float) Math.toRadians(-10);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
