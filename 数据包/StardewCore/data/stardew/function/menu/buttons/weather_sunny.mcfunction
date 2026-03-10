# data/stardew/function/menu/buttons/weather_sunny.mcfunction
# 切换到晴天
# 执行者: 玩家 (@s)

# 1. 设置为晴天 (sd_weather = 0)
scoreboard players set Global sd_weather 0

# 2. 发送提示消息
tellraw @s {"text":"已切换到晴天 ☀","color":"yellow","bold":true}

# 3. 播放音效
playsound entity.experience_orb.pickup player @s ~ ~ ~ 1 1.2
