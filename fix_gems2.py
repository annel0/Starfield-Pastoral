import re
import io

with io.open('src/main/java/com/stardew/craft/mining/MineFloorGenerator.java', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace('(\" null\\\)', '(\null\)')
text = text.replace('; roll', '&& roll')

with io.open('src/main/java/com/stardew/craft/mining/MineFloorGenerator.java', 'w', encoding='utf-8') as f:
 f.write(text)

print('Success')
