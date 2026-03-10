# 清理玩家周围2格内的所有光源方块
# @s = 玩家
# 在卸下发光戒指时调用

# 清理脚下和周围1格
fill ~-1 ~ ~-1 ~1 ~ ~1 air replace minecraft:light
# 清理身体高度(~1)和周围1格
fill ~-1 ~1 ~-1 ~1 ~1 ~1 air replace minecraft:light
# 清理头部高度(~2)和周围1格
fill ~-1 ~2 ~-1 ~1 ~2 ~1 air replace minecraft:light

# 清除位置追踪分数(重新初始化)
scoreboard players reset @s stardew.light.last_x
scoreboard players reset @s stardew.light.last_y
scoreboard players reset @s stardew.light.last_z
