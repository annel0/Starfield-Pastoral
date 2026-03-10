# 结束精准打击效果

# 移除标记
tag @s remove sd_precision_active

# 重置持续时间
scoreboard players set @s sd_precision_duration 0

# 结束音效
playsound minecraft:block.enchantment_table.use player @s ~ ~ ~ 0.5 0.8

# 结束提示
title @s actionbar {"text":"精准打击效果结束","color":"gray"}
