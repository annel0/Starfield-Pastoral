import io
import re

GUI_PATH = 'src/main/java/com/stardew/craft/client/gui/DecorAnchorEditorScreen.java'

with io.open(GUI_PATH, 'r', encoding='utf-8') as f:
    text = f.read()

# Add scale members
if 'private float scaleX = 1.0f;' not in text:
    text = text.replace('private float rotZ;', 'private float rotZ;\n    private float scaleX = 1.0f;\n    private float scaleY = 1.0f;\n    private float scaleZ = 1.0f;')
    
    # Initialize from block entity in Screen Constructor
    target = 'this.rotZ = be.getRotZ();'
    text = text.replace(target, target + '\n            this.scaleX = be.getScaleX();\n            this.scaleY = be.getScaleY();\n            this.scaleZ = be.getScaleZ();')
    
    # update payload for gizmo button
    target_payload = '''                  rotX,
                  rotY,
                  rotZ'''
    new_payload = '''                  rotX,
                  rotY,
                  rotZ,
                  scaleX,
                  scaleY,
                  scaleZ'''
    text = text.replace(target_payload, new_payload)

    scale_btn = '''
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
'''
    text = text.replace('applyFilterAndKeepSelection();', 'applyFilterAndKeepSelection();\n' + scale_btn)
    
    
with io.open(GUI_PATH, 'w', encoding='utf-8') as f:
    f.write(text)

print("Screen Updated.")
