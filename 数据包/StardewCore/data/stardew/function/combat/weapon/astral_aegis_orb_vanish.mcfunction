# 护盾球消失

# 消失粒子（更华丽）
particle minecraft:poof ~ ~ ~ 0.3 0.3 0.3 0.08 15 force
particle minecraft:end_rod ~ ~ ~ 0.2 0.2 0.2 0.15 10 force
particle minecraft:enchant ~ ~ ~ 0.25 0.25 0.25 0.5 8 force

# 音效
playsound minecraft:entity.item.pickup player @a ~ ~ ~ 0.8 2
playsound minecraft:block.enchantment_table.use player @a ~ ~ ~ 0.5 1.8

# 移除实体
kill @s
