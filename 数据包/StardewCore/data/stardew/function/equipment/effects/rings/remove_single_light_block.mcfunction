# ================================================================
# 移除单个光源方块
# ================================================================
# @s = light_block marker

# 检查这个位置是否是 light block,如果是则移除
execute if block ~ ~ ~ minecraft:light run setblock ~ ~ ~ air replace

# 删除 marker
kill @s
