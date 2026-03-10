# stardew:debug/dps/calculate_dps.mcfunction
# 计算5秒滑动窗口平均DPS (每秒调用一次)

# 滑动窗口：将旧数据向后移动
scoreboard players operation @s sd_dmg_window_5 = @s sd_dmg_window_4
scoreboard players operation @s sd_dmg_window_4 = @s sd_dmg_window_3
scoreboard players operation @s sd_dmg_window_3 = @s sd_dmg_window_2
scoreboard players operation @s sd_dmg_window_2 = @s sd_dmg_window_1

# 将本秒的伤害记录到窗口1
scoreboard players operation @s sd_dmg_window_1 = @s sd_player_total_dmg

# 计算5秒内的总伤害
scoreboard players operation @s sd_player_dps = @s sd_dmg_window_1
scoreboard players operation @s sd_player_dps += @s sd_dmg_window_2
scoreboard players operation @s sd_player_dps += @s sd_dmg_window_3
scoreboard players operation @s sd_player_dps += @s sd_dmg_window_4
scoreboard players operation @s sd_player_dps += @s sd_dmg_window_5

# 计算平均DPS (总伤害 / 5秒)
scoreboard players set #5 sd_const 5
scoreboard players operation @s sd_player_dps /= #5 sd_const

# 将DPS同步到全局显示用的虚拟玩家
scoreboard players operation #display sd_player_dps = @s sd_player_dps

# 重置本秒伤害计数器
scoreboard players set @s sd_player_total_dmg 0
scoreboard players set @s sd_player_dps_timer 0
