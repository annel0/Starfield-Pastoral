# data/stardew/functions/farming/fertilizer/clear_at_position.mcfunction
# 清除指定位置的肥料marker和视觉实体

# 移除肥料marker (在耕地下方)
kill @e[type=marker,tag=sd_fertilizer_marker,distance=..1.5]

# 移除视觉实体 (在耕地上方)
kill @e[type=item_display,tag=sd_fertilizer_visual,distance=..1.5]
