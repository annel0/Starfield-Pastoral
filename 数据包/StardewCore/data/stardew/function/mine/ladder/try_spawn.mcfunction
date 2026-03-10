# stardew:mine/ladder/try_spawn.mcfunction
# 尝试生成下层坑 (石头破坏时调用)
# 执行者: 被破坏的石头 interaction 实体
# 执行位置: 石头位置

# 检查是否已经有下层坑
execute if score @p sd_mine_ladder matches 1 run return 0

# 计算当前楼层的中心位置用于计数
# 房间中心: X=20, Y=66, Z=floor*100+20
execute store result score #center_z sd_mine_temp run scoreboard players get @p sd_mine_floor
scoreboard players operation #center_z sd_mine_temp *= #100 sd_const
scoreboard players add #center_z sd_mine_temp 20
execute store result storage stardew:mine count.z int 1 run scoreboard players get #center_z sd_mine_temp

# 从房间中心计数剩余石头 (40格半径覆盖任何房间)
# 注意：当前石头还没被删除，所以要减1
function stardew:mine/ladder/count_stones_from_center with storage stardew:mine count
scoreboard players remove @p sd_mine_stones 1

# 检查是否剩最后两块石头或更少 (保底机制 - 必出梯子)
execute if score @p sd_mine_stones matches ..1 run function stardew:mine/ladder/spawn
execute if score @p sd_mine_stones matches ..1 run return 1

# 如果只剩最后两块石头，标记玩家进入"最后2石"状态（会自动触发持续高亮）
execute if score @p sd_mine_ladder matches 0 if score @p sd_mine_stones matches 1..2 unless entity @p[tag=sd_mine_last_stone] run tellraw @p [{"text":"[矿洞] ","color":"gold"},{"text":"只剩最后两块石头了！已高亮显示。","color":"yellow"}]
execute if score @p sd_mine_ladder matches 0 if score @p sd_mine_stones matches 1..2 unless entity @p[tag=sd_mine_last_stone] run playsound minecraft:block.note_block.bell master @p ~ ~ ~ 1 1.5
execute if score @p sd_mine_ladder matches 0 if score @p sd_mine_stones matches 1..2 run tag @p add sd_mine_last_stone

# 随机概率 (3% 出下层坑)
execute store result score #ladder_roll sd_mine_temp run random value 1..100
execute if score #ladder_roll sd_mine_temp matches 1..3 run function stardew:mine/ladder/spawn
