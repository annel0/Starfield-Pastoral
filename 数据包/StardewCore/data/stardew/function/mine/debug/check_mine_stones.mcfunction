# stardew:mine/debug/check_mine_stones.mcfunction
# 检查矿洞石头状态

tellraw @s {"text":"===== 检查矿洞石头 =====","color":"gold"}

# 玩家状态
tellraw @s [{"text":"sd_mine_floor: ","color":"gray"},{"score":{"name":"@s","objective":"sd_mine_floor"},"color":"aqua"}]
tellraw @s [{"text":"sd_mine_ladder: ","color":"gray"},{"score":{"name":"@s","objective":"sd_mine_ladder"},"color":"aqua"}]
tellraw @s [{"text":"sd_mine_stones: ","color":"gray"},{"score":{"name":"@s","objective":"sd_mine_stones"},"color":"aqua"}]

# 统计附近的石头
execute store result score #stone_count sd_mine_temp run execute if entity @e[type=interaction,tag=sd_stone,distance=..30]
tellraw @s [{"text":"附近 sd_stone 数量: ","color":"gray"},{"score":{"name":"#stone_count","objective":"sd_mine_temp"},"color":"aqua"}]

execute store result score #mine_stone_count sd_mine_temp run execute if entity @e[type=interaction,tag=sd_mine_stone,distance=..30]
tellraw @s [{"text":"附近 sd_mine_stone 数量: ","color":"gray"},{"score":{"name":"#mine_stone_count","objective":"sd_mine_temp"},"color":"aqua"}]

# 列出最近的几个石头的标签
tellraw @s {"text":"--- 最近3个石头的标签 ---","color":"yellow"}
execute as @e[type=interaction,tag=sd_stone,distance=..30,limit=3,sort=nearest] run tellraw @a [{"text":"  ","color":"gray"},{"nbt":"Tags","entity":"@s","color":"white"}]

tellraw @s {"text":"===== 检查完成 =====","color":"gold"}
