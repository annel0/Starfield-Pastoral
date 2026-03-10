# data/stardew/function/menu/buttons/weather_rain.mcfunction
# 切换到雨天
# 执行者: 玩家 (@s)

# 1. 设置为雨天 (sd_weather = 1)
scoreboard players set Global sd_weather 1

# 2. 发送提示消息
tellraw @s {"text":"已切换到雨天 🌧","color":"blue","bold":true}

# 3. 播放音效
playsound entity.experience_orb.pickup player @s ~ ~ ~ 1 1.0
