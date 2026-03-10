# 调试命令：查看DPS相关分数
tellraw @s [{"text":"===== DPS调试信息 =====","color":"gold","bold":true}]
tellraw @s [{"text":"sd_player_total_dmg: ","color":"gray"},{"score":{"name":"@s","objective":"sd_player_total_dmg"},"color":"yellow"}]
tellraw @s [{"text":"sd_player_dps: ","color":"gray"},{"score":{"name":"@s","objective":"sd_player_dps"},"color":"yellow"}]
tellraw @s [{"text":"sd_player_dps_timer: ","color":"gray"},{"score":{"name":"@s","objective":"sd_player_dps_timer"},"color":"yellow"}]
tellraw @s [{"text":"#display sd_player_dps: ","color":"gray"},{"score":{"name":"#display","objective":"sd_player_dps"},"color":"yellow"}]
tellraw @s [{"text":"#damage sd_temp: ","color":"gray"},{"score":{"name":"#damage","objective":"sd_temp"},"color":"yellow"}]
