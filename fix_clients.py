import re

with open('src/main/java/com/stardew/craft/client/ModClientEvents.java', 'r') as f:
    text = f.read()

text = re.sub(r'com\.stardew\.craft\.client\.deco\.DecorAnchorWorldGizmo\.onRenderLevel\(event\);\n?', '', text)
with open('src/main/java/com/stardew/craft/client/ModClientEvents.java', 'w') as f:
    f.write(text)

with open('src/main/java/com/stardew/craft/client/ModClientSetup.java', 'r') as f:
    text = f.read()

text = re.sub(r'event\.registerBlockEntityRenderer\(ModBlockEntities\.DECOR_ANCHOR\.get\(\), DecorAnchorBlockEntityRenderer::new\);\n?', '', text)
text = re.sub(r'import com\.stardew\.craft\.client\.render\.DecorAnchorBlockEntityRenderer;\n?', '', text)
with open('src/main/java/com/stardew/craft/client/ModClientSetup.java', 'w') as f:
    f.write(text)

with open('src/main/java/com/stardew/craft/network/PacketHandler.java', 'r') as f:
    text = f.read()

text = re.sub(r'import com\.stardew\.craft\.network\.payload\.OpenDecorAnchorEditorPayload;\n?', '', text)
text = re.sub(r'import com\.stardew\.craft\.network\.payload\.UpdateDecorAnchorPayload;\n?', '', text)

text = re.sub(r'registrar\.playBidirectional\(\n\s*OpenDecorAnchorEditorPayload\.TYPE,\n\s*OpenDecorAnchorEditorPayload\.STREAM_CODEC,\n\s*new DirectionalPayloadHandler<>\(\n\s*OpenDecorAnchorEditorPayload::handleClient,\n\s*OpenDecorAnchorEditorPayload::handleServer\n\s*\)\n\s*\);\n?', '', text)
text = re.sub(r'registrar\.playToServer\(\n\s*UpdateDecorAnchorPayload\.TYPE,\n\s*UpdateDecorAnchorPayload\.STREAM_CODEC,\n\s*UpdateDecorAnchorPayload::handle\n\s*\);\n?', '', text)
with open('src/main/java/com/stardew/craft/network/PacketHandler.java', 'w') as f:
    f.write(text)
