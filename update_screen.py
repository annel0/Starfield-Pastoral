import os
import re

GUI_PATH = 'src/main/java/com/stardew/craft/client/gui/DecorAnchorEditorScreen.java'

with open(GUI_PATH, 'r', encoding='utf-8') as f:
    text = f.read()

# Add scale members
if 'private float scaleX = 1.0f;' not in text:
    text = text.replace('private float rotZ;', 'private float rotZ;\n    private float scaleX = 1.0f;\n    private float scaleY = 1.0f;\n    private float scaleZ = 1.0f;')
    
    # Initialize from block entity in Screen Constructor
    target = 'this.rotZ = be.getRotZ();'
    text = text.replace(target, target + '\n            this.scaleX = be.getScaleX();\n            this.scaleY = be.getScaleY();\n            this.scaleZ = be.getScaleZ();')
    
    # Send payload scale variables
    old_payload = '''                  rotX,
                  rotY,
                  rotZ'''
    new_payload = '''                  rotX,
                  rotY,
                  rotZ,
                  scaleX,
                  scaleY,
                  scaleZ'''
    text = text.replace(old_payload, new_payload)

    # In rendering, apply the scale. Wait, we are already sending payload. Let's provide scale control elements in GUI.
    # Where should scale buttons go? We can add + and - for scale.
    # To keep it simple, I will just add overall scale buttons.
    scale_btn = '''
        int scX = cx + 172;
        int btnY = panelTop + 28;
        addRenderableWidget(Button.builder(Component.literal("Scale +"), b -> {
            scaleX += 0.1f; scaleY += 0.1f; scaleZ += 0.1f;
        }).pos(scX, btnY).size(40, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Scale -"), b -> {
            scaleX = Math.max(0.1f, scaleX - 0.1f);
            scaleY = Math.max(0.1f, scaleY - 0.1f);
            scaleZ = Math.max(0.1f, scaleZ - 0.1f);
        }).pos(scX + 42, btnY).size(40, 20).build());
'''
    text = text.replace('applyFilterAndKeepSelection();', 'applyFilterAndKeepSelection();\n' + scale_btn)
    
    # the GUI scale block preview overlay
    # we need to render the model correctly scaled
    
with open(GUI_PATH, 'w', encoding='utf-8') as f:
    f.write(text)

print("Screen Updated.")
