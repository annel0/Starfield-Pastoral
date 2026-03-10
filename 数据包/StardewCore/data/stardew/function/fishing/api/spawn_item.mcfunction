# data/stardew/functions/fishing/api/spawn_item.mcfunction
# 宏：执行掉落
# 参数: {id: "...", quality: "..."}

# 核心：拼接路径并生成
$loot give @s loot stardew:items/fish/$(id)_$(quality)

# 播放音效
playsound minecraft:entity.item.pickup player @s ~ ~ ~ 1 1