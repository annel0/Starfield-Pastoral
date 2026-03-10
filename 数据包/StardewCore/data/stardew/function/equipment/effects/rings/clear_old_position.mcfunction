# ================================================================
# 清理旧位置的光源
# ================================================================
# @s = light_marker
# 在旧位置移除光源方块

# 检查并移除脚下的 light block
execute align xyz positioned ~0.5 ~ ~0.5 if block ~ ~ ~ minecraft:light run setblock ~ ~ ~ air replace

# 检查并移除身体位置的 light block
execute align xyz positioned ~0.5 ~1 ~0.5 if block ~ ~ ~ minecraft:light run setblock ~ ~ ~ air replace
