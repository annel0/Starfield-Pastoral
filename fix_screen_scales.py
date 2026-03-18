import re

file_path = 'src/main/java/com/stardew/craft/client/gui/DecorAnchorEditorScreen.java'
try:
    with open(file_path, 'r') as f:
        text = f.read()

    # The payload might be spread over multiple lines. Let's just blindly replace UpdateDecorAnchorPayload instantiation if it contains 8 arguments, but maybe it's simpler to do string replacements.
    # It seems to be instantiating new UpdateDecorAnchorPayload(
    #        BlockPos, String, float, float, float, float, float, float
    # )
    
    # We can just look for rotZ()... wait, we don't know what the arguments are named. Let's read the file first to be safe.
except:
    pass
