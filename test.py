import io
import re

path = 'src/main/java/com/stardew/craft/client/render/MiningLadderHighlightRenderer.java'
with io.open(path, 'r', encoding='utf-8') as f:
    text = f.read()

custom_rt = '''
    private static RenderType XRAY_LINES = null;
    
    private static RenderType getXrayLines() {
        if (XRAY_LINES == null) {
            XRAY_LINES = RenderType.create(
                "xray_lines",
                com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_NORMAL,
                com.mojang.blaze3d.vertex.VertexFormat.Mode.LINES,
                256,
                false,
                false,
                RenderType.CompositeState.builder()
                    .setShaderState(net.minecraft.client.renderer.RenderStateShard.RENDERTYPE_LINES_SHADER)
                    .setLineState(new net.minecraft.client.renderer.RenderStateShard.LineStateShard(java.util.OptionalDouble.of(2.0)))
                    .setLayeringState(net.minecraft.client.renderer.RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(net.minecraft.client.renderer.RenderStateShard.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(net.minecraft.client.renderer.RenderStateShard.COLOR_DEPTH_WRITE)
                    .setCullState(net.minecraft.client.renderer.RenderStateShard.NO_CULL)
                    .setDepthTestState(net.minecraft.client.renderer.RenderStateShard.NO_DEPTH_TEST)
                    .createCompositeState(false)
            );
        }
        return XRAY_LINES;
    }
'''

text = text.replace('public class MiningLadderHighlightRenderer {\n', 'public class MiningLadderHighlightRenderer {\n' + custom_rt)

replacement = '''        AABB box = new AABB(0, 0, 0, 1, 1, 1);

        VertexConsumer vertexConsumer = mc.renderBuffers().bufferSource().getBuffer(getXrayLines());
        
        float r = 0.2f, g = 1.0f, b = 0.2f, a = 0.8f;
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, box, r, g, b, a);'''

text = re.sub(r'        AABB box = new AABB\(0, 0, 0, 1, 1, 1\);\n\n.*?        com\.mojang\.blaze3d\.systems\.RenderSystem\.lineWidth\(1\.0f\);', replacement, text, flags=re.DOTALL)

with io.open(path, 'w', encoding='utf-8') as f:
    f.write(text)
print('Fixed RenderType into Custom.')
