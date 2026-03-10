# ================================================================
# 刷新光源方块
# ================================================================
# @s = 玩家
# 在当前位置放置光源方块

# 在脚下放置光源
execute align xyz positioned ~0.5 ~ ~0.5 if block ~ ~ ~ #minecraft:replaceable if score @s sd_glow_level matches 5..9 run setblock ~ ~ ~ minecraft:light[level=8,waterlogged=false] replace
execute align xyz positioned ~0.5 ~ ~0.5 if block ~ ~ ~ #minecraft:replaceable if score @s sd_glow_level matches 10.. run setblock ~ ~ ~ minecraft:light[level=15,waterlogged=false] replace

# 大光圈在身体位置也放置
execute if score @s sd_glow_level matches 10.. align xyz positioned ~0.5 ~1 ~0.5 if block ~ ~ ~ #minecraft:replaceable run setblock ~ ~ ~ minecraft:light[level=12,waterlogged=false] replace
