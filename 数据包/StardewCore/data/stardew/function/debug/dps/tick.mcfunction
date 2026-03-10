# stardew:debug/dps/tick.mcfunction
# 全局DPS系统 - 每个玩家每tick运行

# 初始化计时器和窗口
execute unless score @s sd_player_dps_timer matches 0.. run scoreboard players set @s sd_player_dps_timer 0
execute unless score @s sd_player_total_dmg matches 0.. run scoreboard players set @s sd_player_total_dmg 0
execute unless score @s sd_dmg_window_1 matches 0.. run scoreboard players set @s sd_dmg_window_1 0
execute unless score @s sd_dmg_window_2 matches 0.. run scoreboard players set @s sd_dmg_window_2 0
execute unless score @s sd_dmg_window_3 matches 0.. run scoreboard players set @s sd_dmg_window_3 0
execute unless score @s sd_dmg_window_4 matches 0.. run scoreboard players set @s sd_dmg_window_4 0
execute unless score @s sd_dmg_window_5 matches 0.. run scoreboard players set @s sd_dmg_window_5 0

# 更新计时器（每20tick = 1秒）
scoreboard players add @s sd_player_dps_timer 1

# 每秒更新滑动窗口并计算平均DPS
execute if score @s sd_player_dps_timer matches 20.. run function stardew:debug/dps/calculate_dps
