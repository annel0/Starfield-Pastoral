# stardew:mine/ladder/highlight_last_stone.mcfunction
# 高亮显示最后一个石头
# 执行者: 刚被破坏的石头 interaction 实体 (即将被删除)
# 执行位置: 石头位置

# 计算当前楼层的中心位置 (X=20, Y=66, Z=floor*100+20)
# 从玩家获取楼层信息
execute store result score #center_z sd_mine_temp run scoreboard players get @p sd_mine_floor
scoreboard players operation #center_z sd_mine_temp *= #100 sd_const
scoreboard players add #center_z sd_mine_temp 20

# 将中心 Z 坐标存储到 storage
execute store result storage stardew:mine highlight.z int 1 run scoreboard players get #center_z sd_mine_temp

# 从房间中心位置搜索，40格半径足以覆盖任何房间类型
function stardew:mine/ladder/highlight_from_center with storage stardew:mine highlight

# 播放提示音效
execute at @p run playsound minecraft:block.note_block.bell master @a ~ ~ ~ 1 1.5

# 提示玩家
tellraw @p [{"text":"[矿洞] ","color":"gold"},{"text":"只剩最后一块石头了！已高亮显示。","color":"yellow"}]
