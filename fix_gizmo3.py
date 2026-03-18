with open('src/main/java/com/stardew/craft/client/deco/DecorAnchorWorldGizmo.java', 'r') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'public static void onRenderLevel(RenderLevelStageEvent event)' in line:
        lines.insert(i + 1, '        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;\n')
        lines.insert(i + 2, '        saveMatrices(event);\n')
        lines.insert(i + 3, '        PoseStack pose = event.getPoseStack();\n')
        lines.insert(i + 4, '        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();\n')
        lines.insert(i + 5, '        Vec3 camPos = event.getCamera().getPosition();\n')
        break
with open('src/main/java/com/stardew/craft/client/deco/DecorAnchorWorldGizmo.java', 'w') as f:
    f.writelines(lines)
