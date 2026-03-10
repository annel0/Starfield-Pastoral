# 使用 Motion 修改物品速度，让物品自然地向玩家移动
# 玩家坐标已经在 #player_x/y/z sd_temp 中（*1000）

# 获取物品当前坐标（*1000）
execute store result score #item_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #item_y sd_temp run data get entity @s Pos[1] 1000
execute store result score #item_z sd_temp run data get entity @s Pos[2] 1000

# 计算方向向量 (player - item)
scoreboard players operation #dx sd_temp = #player_x sd_temp
scoreboard players operation #dx sd_temp -= #item_x sd_temp

scoreboard players operation #dy sd_temp = #player_y sd_temp
scoreboard players operation #dy sd_temp -= #item_y sd_temp

scoreboard players operation #dz sd_temp = #player_z sd_temp
scoreboard players operation #dz sd_temp -= #item_z sd_temp

# 设置 Motion（提高速度：0.00005 → 0.0001）
# 3格距离 → Motion = 0.3（比较快的吸引速度）
execute store result entity @s Motion[0] double 0.0001 run scoreboard players get #dx sd_temp
execute store result entity @s Motion[1] double 0.0001 run scoreboard players get #dy sd_temp
execute store result entity @s Motion[2] double 0.0001 run scoreboard players get #dz sd_temp
