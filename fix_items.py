import re

with open('src/main/java/com/stardew/craft/item/ModItems.java', 'r') as f:
    text = f.read()

pattern = r'public static final DeferredItem<Item> DECOR_ANCHOR[^;]+;'
text = re.sub(pattern, '', text, flags=re.DOTALL)

with open('src/main/java/com/stardew/craft/item/ModItems.java', 'w') as f:
    f.write(text)
