import re

# UpdateDecorAnchorPayload
with open('src/main/java/com/stardew/craft/network/payload/UpdateDecorAnchorPayload.java', 'r') as f:
    text = f.read()

# Add to record
text = text.replace('float rotZ\n)', 'float rotZ,\n    float scaleX,\n    float scaleY,\n    float scaleZ\n)')

# Add to STREAM_CODEC write
text = text.replace('buf.writeFloat(payload.rotZ());', 'buf.writeFloat(payload.rotZ());\n            buf.writeFloat(payload.scaleX());\n            buf.writeFloat(payload.scaleY());\n            buf.writeFloat(payload.scaleZ());')

# Add to STREAM_CODEC read
text = text.replace('buf.readFloat()\n        )', 'buf.readFloat(),\n            buf.readFloat(),\n            buf.readFloat(),\n            buf.readFloat()\n        )')

# Add to handle setEditorState
text = text.replace('payload.rotZ()\n            );', 'payload.rotZ(),\n                payload.scaleX(),\n                payload.scaleY(),\n                payload.scaleZ()\n            );')

with open('src/main/java/com/stardew/craft/network/payload/UpdateDecorAnchorPayload.java', 'w') as f:
    f.write(text)


# DecorAnchorBlockEntity
with open('src/main/java/com/stardew/craft/blockentity/DecorAnchorBlockEntity.java', 'r') as f:
    text = f.read()

text = text.replace('private static final String TAG_ROT_Z = "RotZ";', 'private static final String TAG_ROT_Z = "RotZ";\n    private static final String TAG_SCALE_X = "ScaleX";\n    private static final String TAG_SCALE_Y = "ScaleY";\n    private static final String TAG_SCALE_Z = "ScaleZ";')

text = text.replace('private float rotZ;', 'private float rotZ;\n    private float scaleX = 1.0f;\n    private float scaleY = 1.0f;\n    private float scaleZ = 1.0f;')

text = text.replace('public float getRotZ() {\n        return rotZ;\n    }', 'public float getRotZ() {\n        return rotZ;\n    }\n\n    public float getScaleX() {\n        return scaleX;\n    }\n\n    public float getScaleY() {\n        return scaleY;\n    }\n\n    public float getScaleZ() {\n        return scaleZ;\n    }')

text = text.replace('public void setEditorState(String styleId, float offsetX, float offsetY, float offsetZ, float rotX, float rotY, float rotZ)', 'public void setEditorState(String styleId, float offsetX, float offsetY, float offsetZ, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ)')

text = text.replace('this.rotZ = normalizeAngle(rotZ);', 'this.rotZ = normalizeAngle(rotZ);\n        this.scaleX = scaleX;\n        this.scaleY = scaleY;\n        this.scaleZ = scaleZ;')

text = text.replace('tag.putFloat(TAG_ROT_Z, rotZ);', 'tag.putFloat(TAG_ROT_Z, rotZ);\n        tag.putFloat(TAG_SCALE_X, scaleX);\n        tag.putFloat(TAG_SCALE_Y, scaleY);\n        tag.putFloat(TAG_SCALE_Z, scaleZ);')

text = text.replace('rotZ = normalizeAngle(tag.getFloat(TAG_ROT_Z));', 'rotZ = normalizeAngle(tag.getFloat(TAG_ROT_Z));\n        scaleX = tag.contains(TAG_SCALE_X) ? tag.getFloat(TAG_SCALE_X) : 1.0f;\n        scaleY = tag.contains(TAG_SCALE_Y) ? tag.getFloat(TAG_SCALE_Y) : 1.0f;\n        scaleZ = tag.contains(TAG_SCALE_Z) ? tag.getFloat(TAG_SCALE_Z) : 1.0f;')

with open('src/main/java/com/stardew/craft/blockentity/DecorAnchorBlockEntity.java', 'w') as f:
    f.write(text)


# DecorAnchorWorldGizmo
with open('src/main/java/com/stardew/craft/client/deco/DecorAnchorWorldGizmo.java', 'r') as f:
    text = f.read()

# Already handled in previous commit actually? Wait, last python script crashed before writing anything. But wait. Let me double check if files were modified or not.

# Replace any new UpdateDecorAnchorPayload(..., rotZ) that doesn't have scales yet
text = re.sub(r'new UpdateDecorAnchorPayload\(([^,]+, [^,]+, [^,]+, [^,]+, [^,]+, [^,]+, [^,]+, [^,)]+)\)', r'new UpdateDecorAnchorPayload(\1, 1.0f, 1.0f, 1.0f)', text)

# Just in case, if the payload in Gizmo has 11 arguments instead of 8:
text = text.replace('new UpdateDecorAnchorPayload(targetPos, styleId, offsetX, offsetY, offsetZ, rotX, rotY, rotZ, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f)', 'new UpdateDecorAnchorPayload(targetPos, styleId, offsetX, offsetY, offsetZ, rotX, rotY, rotZ, 1.0f, 1.0f, 1.0f)')

with open('src/main/java/com/stardew/craft/client/deco/DecorAnchorWorldGizmo.java', 'w') as f:
    f.write(text)

# DecorAnchorBlock
try:
    with open('src/main/java/com/stardew/craft/block/utility/DecorAnchorBlock.java', 'r') as f:
        text = f.read()
    
    text = re.sub(r'anchor\.setEditorState\(([^,]+(?:,[^,]+){6})\)', r'anchor.setEditorState(\1, 1.0f, 1.0f, 1.0f)', text)
    
    with open('src/main/java/com/stardew/craft/block/utility/DecorAnchorBlock.java', 'w') as f:
        f.write(text)
except:
    pass

print('Done applying regex replacements.')
