# data/stardew/function/tools/pickaxe/remove_crop_entities.mcfunction
# 移除单个作物的所有实体
# [执行位置: 已对齐的方块中心 ~0.5 ~1.375 ~0.5]

# 检测是否有作物marker
execute if entity @e[type=marker,tag=sd_crop,distance=..0.3,limit=1,sort=nearest] run particle minecraft:block{block_state:"minecraft:wheat"} ~ ~ ~ 0.3 0.3 0.3 0 20

# 删除作物实体（使用极小的范围0.3确保精确定位）
kill @e[type=marker,tag=sd_crop,distance=..0.3,limit=1,sort=nearest]
kill @e[type=item_display,tag=sd_crop_vis,distance=..0.3,limit=1,sort=nearest]

# 删除文字和图标实体（位于更高处）
execute positioned ~ ~0.7 ~ run kill @e[type=text_display,tag=sd_info_text,distance=..0.3,limit=1,sort=nearest]
execute positioned ~ ~1.2 ~ run kill @e[type=item_display,tag=sd_crop_icon,distance=..0.3,limit=1,sort=nearest]

# 清除结构空位方块（作物上方）
execute if block ~ ~ ~ minecraft:structure_void run setblock ~ ~ ~ minecraft:air
