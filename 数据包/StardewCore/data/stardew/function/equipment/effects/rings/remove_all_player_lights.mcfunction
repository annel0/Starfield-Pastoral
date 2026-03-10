# ================================================================
# 移除所有玩家的光源方块
# ================================================================
# @s = 玩家
# 当玩家不再有发光效果时调用

# 清理当前位置的光源
execute align xyz positioned ~0.5 ~ ~0.5 if block ~ ~ ~ minecraft:light run setblock ~ ~ ~ air replace
execute align xyz positioned ~0.5 ~1 ~0.5 if block ~ ~ ~ minecraft:light run setblock ~ ~ ~ air replace
