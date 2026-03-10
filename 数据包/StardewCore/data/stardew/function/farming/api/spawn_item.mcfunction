# 宏：种植系统专用掉落
# 参数: {id: "crops/spring/tomato", quality: "base"}

$loot spawn ~ ~0.5 ~ loot stardew:items/$(id)_$(quality)

# 播放通用的拾取音效
playsound minecraft:entity.item.pickup block @a ~ ~ ~ 1 1.2