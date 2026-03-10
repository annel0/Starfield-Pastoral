# data/stardew/functions/fishing/api/drop_simple.mcfunction
# 宏：掉落无品质物品
# 参数: {id: "trash/cola"}

# 直接调用 base 品质
$loot give @s loot stardew:items/fish/$(id)_base

# 播放音效
playsound minecraft:entity.item.pickup player @s ~ ~ ~ 1 1