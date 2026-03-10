# stardew:mine/debug/reset_progress.mcfunction
# 调试: 重置玩家矿洞进度

scoreboard players set @s sd_mine_floor 0
scoreboard players set @s sd_mine_deepest 0
scoreboard players set @s sd_mine_stones 0
scoreboard players set @s sd_mine_ladder 0

tellraw @s {"text":"[调试] 矿洞进度已重置！","color":"yellow"}
