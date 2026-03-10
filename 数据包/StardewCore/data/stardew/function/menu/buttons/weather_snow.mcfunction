# data/stardew/function/menu/buttons/weather_snow.mcfunction
# 切换到下雪
# 执行者: 玩家 (@s)

# 1. 设置为下雪 (sd_weather = 3)
scoreboard players set Global sd_weather 3

# 2. 发送提示消息
tellraw @s {"text":"已切换到下雪 ❄","color":"aqua","bold":true}

# 3. 播放音效
playsound entity.experience_orb.pickup player @s ~ ~ ~ 1 1.4
