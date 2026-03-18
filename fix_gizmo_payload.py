import io
import re

GIZMO_PATH = 'src/main/java/com/stardew/craft/client/deco/DecorAnchorWorldGizmo.java'

with io.open(GIZMO_PATH, 'r', encoding='utf-8') as f:
    text = f.read()

# I need to add scale values to Gizmo too, but user mostly asked for GUI scale right now.
# Wait, user said: "也可以调整大小（在GUI里面）". Gizmo itself just needs translation and rotation like Axiom.
# Let's fix the UpdateDecorAnchorPayload constructors in Gizmo.

text = text.replace('new UpdateDecorAnchorPayload(targetPos, styleId, offsetX, offsetY, offsetZ, rotX, rotY, rotZ)',
                    'new UpdateDecorAnchorPayload(targetPos, styleId, offsetX, offsetY, offsetZ, rotX, rotY, rotZ, 1.0f, 1.0f, 1.0f)')

with io.open(GIZMO_PATH, 'w', encoding='utf-8') as f:
    f.write(text)
    
print("Gizmo payload fixed.")
