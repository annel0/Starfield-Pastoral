import re

with open('src/main/java/com/stardew/craft/block/ModBlocks.java', 'r') as f:
    text = f.read()

# remove decor anchor block
pattern = r'public static final DeferredBlock<Block> DECOR_ANCHOR[^;]+;'
text = re.sub(pattern, '', text, flags=re.DOTALL)
text = re.sub(r'import com\.stardew\.craft\.block\.utility\.DecorAnchorBlock;\n?', '', text)

with open('src/main/java/com/stardew/craft/block/ModBlocks.java', 'w') as f:
    f.write(text)

with open('src/main/java/com/stardew/craft/blockentity/ModBlockEntities.java', 'r') as f:
    text = f.read()

pattern = r'public static final DeferredHolder<BlockEntityType<\?>, BlockEntityType<DecorAnchorBlockEntity>> DECOR_ANCHOR[^;]+;'
text = re.sub(pattern, '', text, flags=re.DOTALL)
text = re.sub(r'import com\.stardew\.craft\.blockentity\.DecorAnchorBlockEntity;\n?', '', text)

with open('src/main/java/com/stardew/craft/blockentity/ModBlockEntities.java', 'w') as f:
    f.write(text)
