# data/stardew/functions/fishing/debug_show_env.mcfunction
# 显示当前钓鱼环境变量

tellraw @s [{"text":"========== 钓鱼环境诊断 ==========","color":"gold","bold":true}]
tellraw @s [{"text":"季节 (sd_season): ","color":"yellow"},{"score":{"name":"Global","objective":"sd_season"},"color":"white"}]
tellraw @s [{"text":"区域 (sd_fish_region): ","color":"yellow"},{"score":{"name":"@s","objective":"sd_fish_region"},"color":"white"}]
tellraw @s [{"text":"天气 (sd_weather): ","color":"yellow"},{"score":{"name":"Global","objective":"sd_weather"},"color":"white"},{"text":" (0=晴 1=雨)","color":"gray"}]
tellraw @s [{"text":"时间段 (sd_time_slot): ","color":"yellow"},{"score":{"name":"@s","objective":"sd_time_slot"},"color":"white"}]
tellraw @s [{"text":"随机数 (sd_rng): ","color":"yellow"},{"score":{"name":"Global","objective":"sd_rng"},"color":"white"}]
tellraw @s [{"text":"鱼类型 (sd_fish_type): ","color":"yellow"},{"score":{"name":"@s","objective":"sd_fish_type"},"color":"white"}]
tellraw @s [{"text":"咬钩时间 (sd_bite_time): ","color":"yellow"},{"score":{"name":"@s","objective":"sd_bite_time"},"color":"white"}]
tellraw @s [{"text":"===================================","color":"gold","bold":true}]
