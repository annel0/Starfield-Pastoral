package com.stardew.craft.client.weapon;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.stardew.craft.StardewCraft;
import com.mojang.math.Axis;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.List;

@SuppressWarnings("unused")
public final class LavaKatanaMarkRenderer {

    private static final ResourceLocation MARK_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/lava_katana_marker.png"
    );

    @SuppressWarnings("null")
    private static final RenderType MARK_RENDER_TYPE = RenderType.create(
        "stardew_lava_katana_mark",
        DefaultVertexFormat.POSITION_TEX_COLOR,
        VertexFormat.Mode.QUADS,
        256,
        false,
        true,
        RenderType.CompositeState.builder()
            .setShaderState(new RenderType.ShaderStateShard(GameRenderer::getPositionTexColorShader))
            .setTextureState(new RenderType.TextureStateShard(MARK_TEXTURE, false, false))
            .setTransparencyState(new RenderType.TransparencyStateShard("translucent_transparency", () -> {
                com.mojang.blaze3d.systems.RenderSystem.enableBlend();
                com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            }, () -> {
                com.mojang.blaze3d.systems.RenderSystem.disableBlend();
            }))
            .setWriteMaskState(new RenderType.WriteMaskStateShard(true, false))
            .setCullState(new RenderType.CullStateShard(false))
            .setDepthTestState(new RenderType.DepthTestStateShard("always", 519))
            .createCompositeState(false)
    );

    private LavaKatanaMarkRenderer() {}

    @SuppressWarnings("null")
    public static void onRenderLevel(RenderLevelStageEvent event) {
        // visuals disabled by request
    }

    @SuppressWarnings("null")
    private static void vertex(VertexConsumer consumer, Matrix4f pose, int light,
                               float x, float y, float u, float v) {
        consumer.addVertex(pose, x, y, 0.0f)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(0.0f, 1.0f, 0.0f);
    }
}
