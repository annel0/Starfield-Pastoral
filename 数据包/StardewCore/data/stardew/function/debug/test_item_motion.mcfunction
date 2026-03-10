# 调试单个物品的磁力拉取过程

# 显示物品当前位置
execute store result score #item_x sd_temp run data get entity @s Pos[0] 1
execute store result score #item_y sd_temp run data get entity @s Pos[1] 1
execute store result score #item_z sd_temp run data get entity @s Pos[2] 1

tellraw @a [{"text":"[物品位置] X:","color":"yellow"},{"score":{"name":"#item_x","objective":"sd_temp"}},{"text":" Y:"},{"score":{"name":"#item_y","objective":"sd_temp"}},{"text":" Z:"},{"score":{"name":"#item_z","objective":"sd_temp"}}]

# 显示物品当前Motion
execute store result score #motion_x sd_temp run data get entity @s Motion[0] 100
execute store result score #motion_y sd_temp run data get entity @s Motion[1] 100
execute store result score #motion_z sd_temp run data get entity @s Motion[2] 100

tellraw @a [{"text":"[物品速度] X:","color":"aqua"},{"score":{"name":"#motion_x","objective":"sd_temp"}},{"text":" Y:"},{"score":{"name":"#motion_y","objective":"sd_temp"}},{"text":" Z:"},{"score":{"name":"#motion_z","objective":"sd_temp"}}]

# 获取玩家位置
execute store result score #player_x sd_temp run data get entity @p Pos[0] 1
execute store result score #player_y sd_temp run data get entity @p Pos[1] 1
execute store result score #player_z sd_temp run data get entity @p Pos[2] 1

tellraw @a [{"text":"[玩家位置] X:","color":"green"},{"score":{"name":"#player_x","objective":"sd_temp"}},{"text":" Y:"},{"score":{"name":"#player_y","objective":"sd_temp"}},{"text":" Z:"},{"score":{"name":"#player_z","objective":"sd_temp"}}]

# 尝试直接设置一个测试Motion
data modify entity @s Motion set value [0.2d, 0.1d, 0.0d]
tellraw @a [{"text":"已设置测试Motion [0.2, 0.1, 0.0]","color":"gold"}]
