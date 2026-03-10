# data/stardew/function/tools/pickaxe/remove_single_crop.mcfunction
# 移除单个作物的所有实体
# [执行位置: 作物marker实体位置]

# 显示粒子效果
particle minecraft:block{block_state:"minecraft:wheat"} ~ ~ ~ 0.3 0.3 0.3 0 20

# 删除视觉实体 (在同一位置,使用稍大的范围)
kill @e[type=item_display,tag=sd_crop_vis,distance=..0.8,limit=1,sort=nearest]

# 删除文字实体 (可能在marker上方0.7~1.1格,使用1.5范围)
execute positioned ~ ~0.7 ~ run kill @e[type=text_display,tag=sd_info_text,distance=..1.0,limit=1,sort=nearest]

# 删除图标实体 (可能在marker上方1.2~1.6格,使用2.0范围)
execute positioned ~ ~1.2 ~ run kill @e[type=item_display,tag=sd_crop_icon,distance=..1.0,limit=1,sort=nearest]

# 清除结构空位方块（作物上方）
execute if block ~ ~ ~ minecraft:structure_void run setblock ~ ~ ~ minecraft:air

# 最后删除marker自己
kill @s
