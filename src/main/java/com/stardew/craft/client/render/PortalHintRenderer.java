package com.stardew.craft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.interior.PortalHintPositions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders visual hints near portal interaction entities:
 * - A soft glowing outline around the door area
 * - A floating Stardew-style bubble with icon + text (billboard, always faces camera)
 *
 * Enter portals: warm golden glow, "▶ 进入" bubble
 * Exit portals:  cool blue-white glow, "◀ 离开" bubble
 */
@SuppressWarnings("unused")
public final class PortalHintRenderer {

    private static final double HINT_RANGE = 5.0;
    private static final double HINT_RANGE_SQ = HINT_RANGE * HINT_RANGE;

    // Edge half-width for the glow outline (world units)
    private static final float EDGE_HALF = 0.02f;

    // Enter style — warm amber/gold
    private static final int ENTER_R = 255, ENTER_G = 200, ENTER_B = 60;
    private static final int ENTER_EDGE_A = 180;
    private static final int ENTER_FACE_A = 25;
    // Bubble background
    private static final int ENTER_BG_R = 80, ENTER_BG_G = 55, ENTER_BG_B = 20, ENTER_BG_A = 180;

    // Exit style — cool blue-white
    private static final int EXIT_R = 140, EXIT_G = 200, EXIT_B = 255;
    private static final int EXIT_EDGE_A = 160;
    private static final int EXIT_FACE_A = 20;
    private static final int EXIT_BG_R = 30, EXIT_BG_G = 50, EXIT_BG_B = 80, EXIT_BG_A = 180;

    // Return-to-overworld style — green
    private static final int RET_R = 80, RET_G = 230, RET_B = 100;
    private static final int RET_EDGE_A = 170;
    private static final int RET_FACE_A = 22;
    private static final int RET_BG_R = 25, RET_BG_G = 60, RET_BG_B = 30, RET_BG_A = 180;

    // Bubble text — translatable keys
    private static final String ENTER_KEY = "stardewcraft.portal.hint.enter";
    private static final String EXIT_KEY = "stardewcraft.portal.hint.exit";
    private static final String RETURN_KEY = "stardewcraft.portal.hint.return";

    @SuppressWarnings("null")
    private static final RenderType QUAD_TYPE = makeQuadType("stardew_portal_hint", false);
    @SuppressWarnings("null")
    private static final RenderType QUAD_XRAY = makeQuadType("stardew_portal_hint_xr", true);

    private PortalHintRenderer() {}

    // ======================== main render ========================

    @SuppressWarnings("null")
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        // Only in Stardew Valley, Overworld (wizard tower), or Mining dimension
        boolean inStardew = ModDimensions.STARDEW_VALLEY.equals(mc.level.dimension());
        boolean inOverworld = Level.OVERWORLD.equals(mc.level.dimension());
        boolean inMine = ModMiningDimensions.STARDEW_MINING.equals(mc.level.dimension());

        if (!inStardew && !inOverworld && !inMine) return;

        List<PortalHint> hints;
        if (inStardew || inMine) {
            hints = findNearbyPortals(player);
        } else {
            hints = findOverworldWizardPortals(player);
        }

        if (hints.isEmpty()) return;

        PoseStack ps = event.getPoseStack();
        Vec3 cam = event.getCamera().getPosition();
        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        // ---- Phase 1: Glow outlines (world-space, single translate(-cam)) ----
        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        for (PortalHint hint : hints) {
            float alpha = calcFadeAlpha(player, hint.pos);
            if (alpha < 0.01f) continue;
            renderGlowOutline(buf, ps, cam, hint, alpha);
        }

        ps.popPose();

        // ---- Phase 2: Floating bubbles with text (billboard per hint) ----
        for (PortalHint hint : hints) {
            float alpha = calcFadeAlpha(player, hint.pos);
            if (alpha < 0.01f) continue;
            renderBubble(event, ps, buf, cam, hint, alpha, mc.font);
        }

        buf.endBatch();
    }

    // ======================== find nearby portals ========================

    @SuppressWarnings("null")
    private static List<PortalHint> findNearbyPortals(Player player) {
        List<PortalHint> result = new ArrayList<>();
        Vec3 playerPos = player.position();
        for (PortalHintPositions.HintInfo info : PortalHintPositions.all()) {
            if (playerPos.distanceToSqr(info.pos()) <= HINT_RANGE_SQ) {
                result.add(new PortalHint(info.pos(), info.isEnter(),
                    info.xBlocks(), info.heightBlocks(), info.zBlocks(), info.hintStyle(),
                    info.destinationKey()));
            }
        }
        return result;
    }

    /**
     * 主世界：扫描附近的 Interaction 实体来生成 Portal Hint。
     */
    @SuppressWarnings("null")
    private static List<PortalHint> findOverworldWizardPortals(Player player) {
        List<PortalHint> result = new ArrayList<>();
        if (player.level() == null) return result;
        AABB scan = player.getBoundingBox().inflate(HINT_RANGE);
        List<Interaction> entities = player.level().getEntitiesOfClass(Interaction.class, scan);
        if (entities.isEmpty()) return result;
        Interaction lowest = entities.stream().min((a, b) -> Double.compare(a.getY(), b.getY())).orElse(null);
        if (lowest == null) return result;
        Vec3 pos = new Vec3(lowest.getX(), lowest.getY(), lowest.getZ());
        result.add(new PortalHint(pos, true, 1, 2, 1, PortalHintPositions.HintStyle.ENTER, "wizard_tower"));
        return result;
    }

    // ======================== fade based on distance ========================

    @SuppressWarnings("null")
    private static float calcFadeAlpha(Player player, Vec3 portalPos) {
        double distSq = player.position().distanceToSqr(portalPos);
        if (distSq > HINT_RANGE_SQ) return 0.0f;

        double dist = Math.sqrt(distSq);
        if (dist > HINT_RANGE - 1.0) {
            return (float) ((HINT_RANGE - dist));
        }
        return 1.0f;
    }

    // ======================== glow outline ========================

    @SuppressWarnings("null")
    private static void renderGlowOutline(MultiBufferSource.BufferSource buf, PoseStack ps, Vec3 cam,
                                           PortalHint hint, float alpha) {
        double x = hint.pos.x - 0.5;
        double y = hint.pos.y;
        double z = hint.pos.z - 0.5;
        AABB box = new AABB(x, y, z,
                            x + hint.xBlocks, y + hint.heightBlocks, z + hint.zBlocks).inflate(0.02);

        int r, g, b, edgeA, faceA;
        if (hint.hintStyle == PortalHintPositions.HintStyle.RETURN_OVERWORLD) {
            r = RET_R; g = RET_G; b = RET_B;
            edgeA = (int) (RET_EDGE_A * alpha);
            faceA = (int) (RET_FACE_A * alpha);
        } else if (hint.isEnter) {
            r = ENTER_R; g = ENTER_G; b = ENTER_B;
            edgeA = (int) (ENTER_EDGE_A * alpha);
            faceA = (int) (ENTER_FACE_A * alpha);
        } else {
            r = EXIT_R; g = EXIT_G; b = EXIT_B;
            edgeA = (int) (EXIT_EDGE_A * alpha);
            faceA = (int) (EXIT_FACE_A * alpha);
        }

        // X-ray layer
        VertexConsumer xr = buf.getBuffer(QUAD_XRAY);
        renderFaces(ps, xr, box, r, g, b, faceA / 2);
        renderEdgeQuads(ps, xr, box, cam, r, g, b, edgeA / 3, EDGE_HALF * 0.6f);
        buf.endBatch(QUAD_XRAY);

        // Depth-tested layer
        VertexConsumer vc = buf.getBuffer(QUAD_TYPE);
        renderFaces(ps, vc, box, r, g, b, faceA);
        renderEdgeQuads(ps, vc, box, cam, r, g, b, edgeA, EDGE_HALF);
        buf.endBatch(QUAD_TYPE);
    }

    // ======================== floating bubble ========================

    @SuppressWarnings("null")
    private static void renderBubble(RenderLevelStageEvent event,
                                      PoseStack ps, MultiBufferSource.BufferSource buf, Vec3 cam,
                                      PortalHint hint, float alpha, Font font) {
        // Position above the door — center of the interaction area, above the top
        double bx = hint.pos.x - 0.5 + hint.xBlocks / 2.0;
        double by = hint.pos.y + hint.heightBlocks + 0.4;
        double bz = hint.pos.z - 0.5 + hint.zBlocks / 2.0;

        // Gentle bob animation
        float time = (System.currentTimeMillis() % 4000) / 4000.0f;
        by += Math.sin(time * Math.PI * 2) * 0.06;

        double x = bx - cam.x;
        double y = by - cam.y;
        double z = bz - cam.z;

        ps.pushPose();
        ps.translate(x, y, z);

        // Billboard: use full camera rotation quaternion (same as DamageNumberClient)
        ps.mulPose(event.getCamera().rotation());

        // Scale down to world-size text (must match DamageNumberClient: +X, -Y, +Z)
        // Negative X would reverse text quad winding → back-face culled by font renderer
        float scale = 0.025f;
        ps.scale(scale, -scale, scale);

        // Two-line layout: action text + destination name
        String hintKey;
        if (hint.hintStyle == PortalHintPositions.HintStyle.RETURN_OVERWORLD) {
            hintKey = RETURN_KEY;
        } else if (hint.isEnter) {
            hintKey = ENTER_KEY;
        } else {
            hintKey = EXIT_KEY;
        }
        Component line1 = Component.translatable(hintKey);
        Component line2 = Component.translatable("stardewcraft.location." + hint.destinationKey);
        int line1Width = font.width(line1);
        int line2Width = font.width(line2);
        int maxLineWidth = Math.max(line1Width, line2Width);
        int lineHeight = 9;
        int lineSpacing = 2;

        // Bubble dimensions (two lines)
        int padX = 6, padY = 4;
        int bubbleW = maxLineWidth + padX * 2;
        int bubbleH = lineHeight * 2 + lineSpacing + padY * 2;
        float left = -bubbleW / 2.0f;
        float top = -bubbleH / 2.0f;

        int bgR, bgG, bgB, bgA;
        int borderR, borderG, borderB;
        if (hint.hintStyle == PortalHintPositions.HintStyle.RETURN_OVERWORLD) {
            bgR = RET_BG_R; bgG = RET_BG_G; bgB = RET_BG_B; bgA = (int) (RET_BG_A * alpha);
            borderR = RET_R; borderG = RET_G; borderB = RET_B;
        } else if (hint.isEnter) {
            bgR = ENTER_BG_R; bgG = ENTER_BG_G; bgB = ENTER_BG_B; bgA = (int) (ENTER_BG_A * alpha);
            borderR = ENTER_R; borderG = ENTER_G; borderB = ENTER_B;
        } else {
            bgR = EXIT_BG_R; bgG = EXIT_BG_G; bgB = EXIT_BG_B; bgA = (int) (EXIT_BG_A * alpha);
            borderR = EXIT_R; borderG = EXIT_G; borderB = EXIT_B;
        }
        int borderA = (int) (220 * alpha);

        // Render bubble background as quads
        VertexConsumer vc = buf.getBuffer(QUAD_TYPE);
        PoseStack.Pose pose = ps.last();

        float zz = 0.0f;

        // Background fill
        fillRect(pose, vc, left + 1, top + 1, left + bubbleW - 1, top + bubbleH - 1, zz, bgR, bgG, bgB, bgA);
        // Rounded corners
        fillRect(pose, vc, left, top + 1, left + 1, top + bubbleH - 1, zz, bgR, bgG, bgB, bgA);
        fillRect(pose, vc, left + bubbleW - 1, top + 1, left + bubbleW, top + bubbleH - 1, zz, bgR, bgG, bgB, bgA);
        fillRect(pose, vc, left + 1, top, left + bubbleW - 1, top + 1, zz, bgR, bgG, bgB, bgA);
        fillRect(pose, vc, left + 1, top + bubbleH - 1, left + bubbleW - 1, top + bubbleH, zz, bgR, bgG, bgB, bgA);

        // Border lines
        fillRect(pose, vc, left + 1, top - 0.5f, left + bubbleW - 1, top + 0.5f, zz, borderR, borderG, borderB, borderA);
        fillRect(pose, vc, left + 1, top + bubbleH - 0.5f, left + bubbleW - 1, top + bubbleH + 0.5f, zz, borderR, borderG, borderB, borderA);
        fillRect(pose, vc, left - 0.5f, top + 1, left + 0.5f, top + bubbleH - 1, zz, borderR, borderG, borderB, borderA);
        fillRect(pose, vc, left + bubbleW - 0.5f, top + 1, left + bubbleW + 0.5f, top + bubbleH - 1, zz, borderR, borderG, borderB, borderA);

        // Small triangle pointer at bottom center
        float triW = 3.0f, triH = 3.0f;
        float cx = 0.0f;
        triQuad(pose, vc, cx - triW, top + bubbleH, cx + triW, top + bubbleH, cx, top + bubbleH + triH, zz,
                borderR, borderG, borderB, borderA);

        buf.endBatch(QUAD_TYPE);

        // Text — two centered lines
        int textAlpha = (int) (255 * alpha);
        int textColor;
        if (hint.hintStyle == PortalHintPositions.HintStyle.RETURN_OVERWORLD) {
            textColor = (textAlpha << 24) | (RET_R << 16) | (RET_G << 8) | RET_B;
        } else if (hint.isEnter) {
            textColor = (textAlpha << 24) | (ENTER_R << 16) | (ENTER_G << 8) | ENTER_B;
        } else {
            textColor = (textAlpha << 24) | (EXIT_R << 16) | (EXIT_G << 8) | EXIT_B;
        }

        // Line 1: action text (centered)
        float line1X = -line1Width / 2.0f;
        float line1Y = top + padY;
        font.drawInBatch(
            line1,
            line1X, line1Y,
            textColor,
            true,
            ps.last().pose(),
            buf,
            Font.DisplayMode.NORMAL,
            0,
            0xF000F0
        );

        // Line 2: destination name (centered)
        float line2X = -line2Width / 2.0f;
        float line2Y = line1Y + lineHeight + lineSpacing;
        font.drawInBatch(
            line2,
            line2X, line2Y,
            textColor,
            true,
            ps.last().pose(),
            buf,
            Font.DisplayMode.NORMAL,
            0,
            0xF000F0
        );

        ps.popPose();
    }

    // ======================== geometry helpers ========================

    @SuppressWarnings("null")
    private static void fillRect(PoseStack.Pose pose, VertexConsumer vc,
                                  float x0, float y0, float x1, float y1, float z,
                                  int r, int g, int b, int a) {
        vc.addVertex(pose, x0, y0, z).setColor(r, g, b, a);
        vc.addVertex(pose, x0, y1, z).setColor(r, g, b, a);
        vc.addVertex(pose, x1, y1, z).setColor(r, g, b, a);
        vc.addVertex(pose, x1, y0, z).setColor(r, g, b, a);
    }

    /** Render a triangle as a degenerate quad (last two vertices at the tip). */
    @SuppressWarnings("null")
    private static void triQuad(PoseStack.Pose pose, VertexConsumer vc,
                                 float ax, float ay, float bx, float by, float cx, float cy, float z,
                                 int r, int g, int b, int a) {
        vc.addVertex(pose, ax, ay, z).setColor(r, g, b, a);
        vc.addVertex(pose, bx, by, z).setColor(r, g, b, a);
        vc.addVertex(pose, cx, cy, z).setColor(r, g, b, a);
        vc.addVertex(pose, cx, cy, z).setColor(r, g, b, a);
    }

    // ======================== 3D box rendering ========================

    @SuppressWarnings("null")
    private static void renderFaces(PoseStack ps, VertexConsumer vc, AABB b, int r, int g, int bl, int a) {
        if (a <= 0) return;
        float x0 = (float) b.minX, y0 = (float) b.minY, z0 = (float) b.minZ;
        float x1 = (float) b.maxX, y1 = (float) b.maxY, z1 = (float) b.maxZ;
        PoseStack.Pose p = ps.last();
        q(p, vc, x0,y0,z0, x1,y0,z0, x1,y0,z1, x0,y0,z1, r,g,bl,a);
        q(p, vc, x0,y1,z1, x1,y1,z1, x1,y1,z0, x0,y1,z0, r,g,bl,a);
        q(p, vc, x0,y0,z0, x0,y1,z0, x1,y1,z0, x1,y0,z0, r,g,bl,a);
        q(p, vc, x1,y0,z1, x1,y1,z1, x0,y1,z1, x0,y0,z1, r,g,bl,a);
        q(p, vc, x0,y0,z1, x0,y1,z1, x0,y1,z0, x0,y0,z0, r,g,bl,a);
        q(p, vc, x1,y0,z0, x1,y1,z0, x1,y1,z1, x1,y0,z1, r,g,bl,a);
    }

    @SuppressWarnings("null")
    private static void q(PoseStack.Pose p, VertexConsumer v,
                          float ax, float ay, float az, float bx, float by, float bz,
                          float cx, float cy, float cz, float dx, float dy, float dz,
                          int r, int g, int b, int a) {
        v.addVertex(p, ax, ay, az).setColor(r, g, b, a);
        v.addVertex(p, bx, by, bz).setColor(r, g, b, a);
        v.addVertex(p, cx, cy, cz).setColor(r, g, b, a);
        v.addVertex(p, dx, dy, dz).setColor(r, g, b, a);
    }

    @SuppressWarnings("null")
    private static void renderEdgeQuads(PoseStack ps, VertexConsumer vc, AABB b, Vec3 cam,
                                         int r, int g, int bl, int a, float halfW) {
        if (a <= 0) return;
        float x0 = (float) b.minX, y0 = (float) b.minY, z0 = (float) b.minZ;
        float x1 = (float) b.maxX, y1 = (float) b.maxY, z1 = (float) b.maxZ;
        // bottom
        edgeQuad(ps, vc, x0,y0,z0, x1,y0,z0, halfW, cam, r,g,bl,a);
        edgeQuad(ps, vc, x1,y0,z0, x1,y0,z1, halfW, cam, r,g,bl,a);
        edgeQuad(ps, vc, x1,y0,z1, x0,y0,z1, halfW, cam, r,g,bl,a);
        edgeQuad(ps, vc, x0,y0,z1, x0,y0,z0, halfW, cam, r,g,bl,a);
        // top
        edgeQuad(ps, vc, x0,y1,z0, x1,y1,z0, halfW, cam, r,g,bl,a);
        edgeQuad(ps, vc, x1,y1,z0, x1,y1,z1, halfW, cam, r,g,bl,a);
        edgeQuad(ps, vc, x1,y1,z1, x0,y1,z1, halfW, cam, r,g,bl,a);
        edgeQuad(ps, vc, x0,y1,z1, x0,y1,z0, halfW, cam, r,g,bl,a);
        // verticals
        edgeQuad(ps, vc, x0,y0,z0, x0,y1,z0, halfW, cam, r,g,bl,a);
        edgeQuad(ps, vc, x1,y0,z0, x1,y1,z0, halfW, cam, r,g,bl,a);
        edgeQuad(ps, vc, x1,y0,z1, x1,y1,z1, halfW, cam, r,g,bl,a);
        edgeQuad(ps, vc, x0,y0,z1, x0,y1,z1, halfW, cam, r,g,bl,a);
    }

    @SuppressWarnings("null")
    private static void edgeQuad(PoseStack ps, VertexConsumer vc,
                                  float ax, float ay, float az,
                                  float bx, float by, float bz,
                                  float halfW, Vec3 cam,
                                  int r, int g, int b, int a) {
        float dx = bx - ax, dy = by - ay, dz = bz - az;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1e-6f) return;
        float ex = dx / len, ey = dy / len, ez = dz / len;

        float mx = (ax + bx) * 0.5f, my = (ay + by) * 0.5f, mz = (az + bz) * 0.5f;
        float cx = (float) cam.x - mx, cy = (float) cam.y - my, cz = (float) cam.z - mz;
        float cl = (float) Math.sqrt(cx * cx + cy * cy + cz * cz);
        if (cl < 1e-6f) return;
        cx /= cl; cy /= cl; cz /= cl;

        float px = ey * cz - ez * cy;
        float py = ez * cx - ex * cz;
        float pz = ex * cy - ey * cx;
        float pl = (float) Math.sqrt(px * px + py * py + pz * pz);
        if (pl < 1e-6f) {
            if (Math.abs(ex) < 0.9f) { px = 0; py = -ez; pz = ey; }
            else                     { px = ez; py = 0; pz = -ex; }
            pl = (float) Math.sqrt(px * px + py * py + pz * pz);
        }
        px = px / pl * halfW;
        py = py / pl * halfW;
        pz = pz / pl * halfW;

        PoseStack.Pose pose = ps.last();
        vc.addVertex(pose, ax - px, ay - py, az - pz).setColor(r, g, b, a);
        vc.addVertex(pose, ax + px, ay + py, az + pz).setColor(r, g, b, a);
        vc.addVertex(pose, bx + px, by + py, bz + pz).setColor(r, g, b, a);
        vc.addVertex(pose, bx - px, by - py, bz - pz).setColor(r, g, b, a);
    }

    // ======================== RenderType ========================

    @SuppressWarnings("null")
    private static RenderType makeQuadType(String name, boolean xray) {
        return RenderType.create(name,
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1024, false, true,
            RenderType.CompositeState.builder()
                .setShaderState(new RenderType.ShaderStateShard(GameRenderer::getPositionColorShader))
                .setTransparencyState(new RenderType.TransparencyStateShard("translucent", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                }, RenderSystem::disableBlend))
                .setWriteMaskState(new RenderType.WriteMaskStateShard(true, false))
                .setCullState(new RenderType.CullStateShard(false))
                .setDepthTestState(xray
                    ? new RenderType.DepthTestStateShard("always", 519)
                    : RenderType.LEQUAL_DEPTH_TEST)
                .createCompositeState(false));
    }

    // ======================== data ========================

    private record PortalHint(Vec3 pos, boolean isEnter, int xBlocks, int heightBlocks, int zBlocks,
                               PortalHintPositions.HintStyle hintStyle, String destinationKey) {}
}
