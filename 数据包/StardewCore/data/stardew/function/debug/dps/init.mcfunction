# stardew:debug/dps/init.mcfunction
# 初始化全局DPS系统的记分板

scoreboard objectives add sd_player_total_dmg dummy "玩家总伤害"
scoreboard objectives add sd_player_dps_timer dummy "DPS计时器"
scoreboard objectives add sd_player_dps dummy "玩家DPS"
