import re

with open('src/main/java/com/stardew/craft/network/PacketHandler.java', 'r') as f:
    text = f.read()

pattern = r'registrar\.playToServer\(\s*com\.stardew\.craft\.network\.payload\.UpdateDecorAnchorPayload.*?::handle\s*\);\s*'
text = re.sub(pattern, '', text, flags=re.DOTALL)

pattern = r'registrar\.playToClient\(\s*com\.stardew\.craft\.network\.payload\.OpenDecorAnchorEditorPayload.*?::handle\s*\);\s*'
text = re.sub(pattern, '', text, flags=re.DOTALL)

with open('src/main/java/com/stardew/craft/network/PacketHandler.java', 'w') as f:
    f.write(text)
