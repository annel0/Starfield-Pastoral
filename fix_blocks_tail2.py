import re

with open('src/main/java/com/stardew/craft/block/ModBlocks.java', 'r') as f:
    text = f.read()

text = re.sub(r'@SuppressWarnings\("null"\)\s*\}', '}', text)

with open('src/main/java/com/stardew/craft/block/ModBlocks.java', 'w') as f:
    f.write(text)

with open('src/main/java/com/stardew/craft/blockentity/ModBlockEntities.java', 'r') as f:
    text = f.read()

text = re.sub(r'@SuppressWarnings\("null"\)\s*\}', '}', text)

with open('src/main/java/com/stardew/craft/blockentity/ModBlockEntities.java', 'w') as f:
    f.write(text)

with open('src/main/java/com/stardew/craft/item/ModItems.java', 'r') as f:
    text = f.read()

text = re.sub(r'@SuppressWarnings\("null"\)\s*\}', '}', text)

with open('src/main/java/com/stardew/craft/item/ModItems.java', 'w') as f:
    f.write(text)
