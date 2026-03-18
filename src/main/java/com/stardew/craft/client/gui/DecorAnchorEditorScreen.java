package com.stardew.craft.client.gui;

import com.stardew.craft.blockentity.DecorAnchorBlockEntity;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.utility.DecorAnchorBlock;
import com.stardew.craft.client.deco.DecorAnchorWorldGizmo;
import com.stardew.craft.deco.DecorAnchorStyleRegistry;
import com.stardew.craft.network.payload.UpdateDecorAnchorPayload;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("null")
public class DecorAnchorEditorScreen extends Screen {
    private static final List<String> STYLES = DecorAnchorStyleRegistry.all();
    private static final int VISIBLE_MODEL_LINES = 8;

    private final BlockPos targetPos;
    private int styleIndex;
    private final List<Integer> filteredIndices = new ArrayList<>();

    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float rotX;
    private float rotY;
    private float rotZ;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float scaleZ = 1.0f;

    private EditBox filterBox;

    public DecorAnchorEditorScreen(BlockPos targetPos, DecorAnchorBlockEntity be) {
        this(targetPos, be.getStyleId(), be.getOffsetX(), be.getOffsetY(), be.getOffsetZ(), be.getRotX(), be.getRotY(), be.getRotZ(), be.getScaleX(), be.getScaleY(), be.getScaleZ());
    }

    public DecorAnchorEditorScreen(BlockPos targetPos, String styleId, float offsetX, float offsetY, float offsetZ, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
        super(Component.translatable("stardewcraft.decor_anchor.screen.title"));
        this.targetPos = targetPos;
        this.styleIndex = Math.max(0, STYLES.indexOf(styleId));
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int panelTop = height / 2 - 120;
        int bottom = height / 2 + 110;

        filterBox = new EditBox(font, cx - 110, panelTop + 28, 220, 20, Component.translatable("stardewcraft.decor_anchor.filter"));
        filterBox.setResponder(s -> applyFilterAndKeepSelection());
        filterBox.setMaxLength(64);
        addRenderableWidget(filterBox);

        applyFilterAndKeepSelection();

        int scX = cx + 80;
        int btnY = bottom - 48;
        addRenderableWidget(Button.builder(Component.literal("S+"), b -> {
            scaleX = Math.round((scaleX + 0.1f) * 10.0f) / 10.0f;
            scaleY = scaleX; scaleZ = scaleX;
        }).pos(scX, btnY).size(30, 20).build());

        addRenderableWidget(Button.builder(Component.literal("S-"), b -> {
            scaleX = Math.max(0.1f, Math.round((scaleX - 0.1f) * 10.0f) / 10.0f);
            scaleY = scaleX; scaleZ = scaleX;
        }).pos(scX + 32, btnY).size(30, 20).build());


        addRenderableWidget(Button.builder(Component.translatable("stardewcraft.decor_anchor.style.prev"), b -> cycleStyle(-1))
            .pos(cx - 160, bottom - 24)
            .size(60, 20)
            .build());

        addRenderableWidget(Button.builder(Component.translatable("stardewcraft.decor_anchor.style.next"), b -> cycleStyle(1))
            .pos(cx + 100, bottom - 24)
            .size(60, 20)
            .build());

        addRenderableWidget(Button.builder(Component.translatable("stardewcraft.decor_anchor.screen.world_gizmo"), b -> {
            PacketDistributor.sendToServer(new UpdateDecorAnchorPayload(
                targetPos,
                currentStyle(),
                offsetX,
                offsetY,
                offsetZ,
                rotX,
                rotY,
                rotZ,
                scaleX,
                scaleY,
                scaleZ
            ));

            DecorAnchorWorldGizmo.startFromPayload(
                targetPos,
                currentStyle(),
                offsetX,
                offsetY,
                offsetZ,
                rotX,
                rotY,
                rotZ,
                scaleX,
                scaleY,
                scaleZ
            );
            onClose();
        }).pos(cx - 160, bottom + 2).size(120, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("stardewcraft.decor_anchor.screen.save"), b -> {
            PacketDistributor.sendToServer(new UpdateDecorAnchorPayload(
                targetPos,
                currentStyle(),
                offsetX,
                offsetY,
                offsetZ,
                rotX,
                rotY,
                rotZ,
                scaleX,
                scaleY,
                scaleZ
            ));
            onClose();
        }).pos(cx - 32, bottom + 2).size(92, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
            .pos(cx + 68, bottom + 2)
            .size(92, 20)
            .build());
    }

    private void cycleStyle(int delta) {
        if (filteredIndices.isEmpty()) {
            return;
        }

        int currentFiltered = filteredIndices.indexOf(styleIndex);
        if (currentFiltered < 0) {
            currentFiltered = 0;
        }
        int nextFiltered = Math.floorMod(currentFiltered + delta, filteredIndices.size());
        styleIndex = filteredIndices.get(nextFiltered);
    }

    private String currentStyle() {
        return STYLES.get(styleIndex);
    }

    private void applyFilterAndKeepSelection() {
        String query = filterBox == null ? "" : filterBox.getValue().toLowerCase(Locale.ROOT).trim();
        filteredIndices.clear();

        for (int i = 0; i < STYLES.size(); i++) {
            String style = STYLES.get(i);
            if (query.isEmpty() || style.toLowerCase(Locale.ROOT).contains(query)) {
                filteredIndices.add(i);
            }
        }

        if (filteredIndices.isEmpty()) {
            filteredIndices.add(styleIndex);
            return;
        }

        if (!filteredIndices.contains(styleIndex)) {
            styleIndex = filteredIndices.get(0);
        }
    }

    private int listWindowStart() {
        int selectedInFiltered = filteredIndices.indexOf(styleIndex);
        if (selectedInFiltered < 0) {
            selectedInFiltered = 0;
        }
        int start = Math.max(0, selectedInFiltered - (VISIBLE_MODEL_LINES / 2));
        if (start + VISIBLE_MODEL_LINES > filteredIndices.size()) {
            start = Math.max(0, filteredIndices.size() - VISIBLE_MODEL_LINES);
        }
        return start;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int x0 = width / 2 - 170;
            int y0 = height / 2 - 120;
            int listY = y0 + 74;
            int row = ((int) mouseY - listY) / 12;
            boolean inListX = mouseX >= (x0 + 12) && mouseX <= (x0 + 158);
            boolean inListY = mouseY >= listY && mouseY <= (listY + VISIBLE_MODEL_LINES * 12);
            if (inListX && inListY && row >= 0 && row < VISIBLE_MODEL_LINES) {
                int start = listWindowStart();
                int idxInFiltered = start + row;
                if (idxInFiltered >= 0 && idxInFiltered < filteredIndices.size()) {
                    styleIndex = filteredIndices.get(idxInFiltered);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // 覆盖并且不调用 super.renderBackground() 来移除背景模糊（Blur）
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        

        int x0 = width / 2 - 170;
        int y0 = height / 2 - 120;
        int x1 = width / 2 + 170;
        int y1 = height / 2 + 130;
        g.fill(x0, y0, x1, y1, 0xD014141A);
        g.renderOutline(x0, y0, x1 - x0, y1 - y0, 0xFF4C4C5A);

        g.drawCenteredString(font, title, width / 2, y0 + 8, 0xFFF3EFD8);
        g.drawString(font, Component.translatable("stardewcraft.decor_anchor.filter"), x0 + 12, y0 + 32, 0xFFDDDDDD, false);
        g.drawString(font, Component.translatable("stardewcraft.decor_anchor.style", currentStyle()), x0 + 12, y0 + 56, 0xFFEADFA8, false);

        int listY = y0 + 74;
        g.fill(x0 + 10, listY - 2, x1 - 10, listY + VISIBLE_MODEL_LINES * 12 + 4, 0x66101014);

        int start = listWindowStart();
        int end = Math.min(filteredIndices.size(), start + VISIBLE_MODEL_LINES);

        for (int i = start; i < end; i++) {
            int idx = filteredIndices.get(i);
            boolean selected = idx == styleIndex;
            int lineY = listY + (i - start) * 12;
            if (selected) {
                g.fill(x0 + 12, lineY - 1, x1 - 12, lineY + 10, 0x664C6E8A);
            }
            g.drawString(font, STYLES.get(idx), x0 + 16, lineY, selected ? 0xFFFFFFFF : 0xFFBFC3D1, false);
        }

        int textY = height / 2 + 68;
        g.drawString(font, Component.translatable("stardewcraft.decor_anchor.translate_values", fmt(offsetX), fmt(offsetY), fmt(offsetZ)), x0 + 12, textY, 0xFFC9E6FF, false);
        g.drawString(font, Component.translatable("stardewcraft.decor_anchor.rotate_values", fmt(rotX), fmt(rotY), fmt(rotZ)), x0 + 12, textY + 12, 0xFFFFD7B0, false);
        g.drawString(font, Component.translatable("stardewcraft.decor_anchor.hint_world_gizmo"), x0 + 12, textY + 28, 0xFFB0B0B0, false);

        // --- 渲染 3D 预览（类似 InventoryScreen 渲染实体） ---
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            BlockState renderState = ModBlocks.DECOR_ANCHOR.get()
                    .defaultBlockState()
                    .setValue(DecorAnchorBlock.STYLE, styleIndex);

            PoseStack pose = g.pose();
            pose.pushPose();

            // 1. 设置在屏幕上的位置
            // 这里放在右边中间
            int previewX = x1 - 50; 
            int previewY = height / 2 + 20;
            pose.translate(previewX, previewY, 150.0F); // 提高 Z 轴确保渲染在 UI 图层上方

            // 2. 缩放 GUI 等比大小，Y轴反转（因为 OpenGL 和 GUI 坐标系 Y 轴方向相反）
            float scale = 40.0F; 
            pose.scale(scale, -scale, scale);

            // 3. 旋转实现等距 3D 视角（Isometric）
            pose.mulPose(Axis.XP.rotationDegrees(30.0F)); 
            pose.mulPose(Axis.YP.rotationDegrees(225.0F));
            
            // 可选：如果要让方块自带当前的旋转偏移
            pose.mulPose(Axis.XP.rotationDegrees(rotX));
            pose.mulPose(Axis.YP.rotationDegrees(rotY));
            pose.mulPose(Axis.ZP.rotationDegrees(rotZ));

            // 对于方块渲染居中偏移 (-0.5)
            pose.translate(-0.5F, -0.5F, -0.5F);

            // 4. 调用方块渲染器进行渲染（忽略附块或利用 BlockEntity）
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            mc.getBlockRenderer().renderSingleBlock(
                renderState,
                pose,
                bufferSource,
                LightTexture.FULL_BRIGHT, // UI 中使用满亮度避免全黑
                OverlayTexture.NO_OVERLAY,
                net.neoforged.neoforge.client.model.data.ModelData.EMPTY, 
                null
            );

            bufferSource.endBatch(); 
            pose.popPose();
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    private static String fmt(float value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
