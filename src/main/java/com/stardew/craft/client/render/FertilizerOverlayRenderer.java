package com.stardew.craft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.FertilizerType;
import com.stardew.craft.client.ClientFertilizerCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 肥料叠加层渲染器 - 使用原始纹理渲染
 */
public class FertilizerOverlayRenderer {

    // 耕地顶部高度是 15/16 = 0.9375
    private static final float FARMLAND_TOP = 0.9375f;
    private static final float OFFSET = 0.001f; // 小偏移避免z-fighting

    // entity RenderType 必须按 texture 缓存；否则 endBatch flush 不到同一个实例
    private static final Map<ResourceLocation, RenderType> OVERLAY_RENDER_TYPES = new ConcurrentHashMap<>();

    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        // 作为“贴在方块表面”的覆盖层：在 cutout 阶段之后渲染，能被正常遮挡
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null || mc.player == null) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        // 不写深度，避免覆盖层反过来遮挡作物/其它透明物
        RenderSystem.depthMask(false);

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        Set<RenderType> usedTypes = new HashSet<>();

        @SuppressWarnings("null")
        BlockPos playerPos = mc.player.blockPosition();
        int renderDistance = 16;
        int renderCount = 0;

        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -renderDistance; z <= renderDistance; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    @SuppressWarnings("null")
                    BlockState state = level.getBlockState(pos);

                    if (!(state.getBlock() instanceof FarmBlock)) {
                        continue;
                    }

                    FertilizerType type = ClientFertilizerCache.getFertilizer(pos);
                    
                    if (type != null) {
                        ResourceLocation textureLoc = getTexture(type);
                        RenderType rt = getOverlayRenderType(textureLoc);
                        usedTypes.add(rt);

                        @SuppressWarnings("null")
                        VertexConsumer vc = buffers.getBuffer(rt);
                        renderFertilizerOverlay(poseStack, vc, level, pos);
                        renderCount++;
                    }
                }
            }
        }

        poseStack.popPose();

        // 只 flush 我们用到的 RenderType，避免干扰其它管线
        for (RenderType rt : usedTypes) {
            buffers.endBatch(rt);
        }
        
        if (renderCount > 0) {
            StardewCraft.LOGGER.debug("Rendered {} fertilizer overlays", renderCount);
        }
        
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    @SuppressWarnings("null")
    private static void renderFertilizerOverlay(PoseStack poseStack, VertexConsumer consumer, Level level, BlockPos pos) {
        float x = pos.getX();
        float y = pos.getY() + FARMLAND_TOP + OFFSET;
        float z = pos.getZ();

        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.0f;
        float maxV = 1.0f;

        int r = 255;
        int g = 255;
        int b = 255;
        int a = 220;

        @SuppressWarnings("null")
        int packedLight = LevelRenderer.getLightColor(level, pos);
        int lightU = packedLight & 0xFFFF;
        int lightV = (packedLight >> 16) & 0xFFFF;

        PoseStack.Pose last = poseStack.last();
        consumer.addVertex(last, x, y, z)
            .setUv(minU, minV)
            .setColor(r, g, b, a)
            .setUv1(0, 0)
            .setUv2(lightU, lightV)
            .setNormal(0, 1, 0);

        consumer.addVertex(last, x, y, z + 1)
            .setUv(minU, maxV)
            .setColor(r, g, b, a)
            .setUv1(0, 0)
            .setUv2(lightU, lightV)
            .setNormal(0, 1, 0);

        consumer.addVertex(last, x + 1, y, z + 1)
            .setUv(maxU, maxV)
            .setColor(r, g, b, a)
            .setUv1(0, 0)
            .setUv2(lightU, lightV)
            .setNormal(0, 1, 0);

        consumer.addVertex(last, x + 1, y, z)
            .setUv(maxU, minV)
            .setColor(r, g, b, a)
            .setUv1(0, 0)
            .setUv2(lightU, lightV)
            .setNormal(0, 1, 0);
    }

    private static RenderType getOverlayRenderType(ResourceLocation texture) {
        return OVERLAY_RENDER_TYPES.computeIfAbsent(texture, RenderType::entityTranslucent);
    }

    private static ResourceLocation getTexture(FertilizerType type) {
        return ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID,
                "textures/block/fertilizer/" + type.getSerializedName() + ".png");
    }
}
