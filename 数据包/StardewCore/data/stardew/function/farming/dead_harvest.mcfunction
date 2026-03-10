# data/stardew/functions/farming/dead_harvest.mcfunction

# 1. 特效
playsound minecraft:block.grass.break block @a ~ ~ ~ 1 0.8

# 2. 清理结构空位
execute if block ~ ~ ~ minecraft:structure_void run setblock ~ ~ ~ minecraft:air

# 3. 清理附属实体
# 视觉模型 (item_display with sd_crop_vis tag) - 可能在上方0~0.4格
execute at @s run kill @e[type=item_display,tag=sd_crop_vis,distance=..0.8,limit=1,sort=nearest]
# 文字 - 可能在上方0.7~1.1格
execute at @s run kill @e[type=text_display,tag=sd_info_text,distance=..1.5,limit=1,sort=nearest]
# 图标 - 可能在上方1.2~1.6格
execute at @s run kill @e[type=item_display,tag=sd_crop_icon,distance=..2.0,limit=1,sort=nearest]

# 4. 销毁作物主体 (marker)
kill @s