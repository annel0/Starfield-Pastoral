# data/stardew/function/menu/buttons/weather_thunder.mcfunction
# 切换到雷雨
# 执行者: 玩家 (@s)

# 1. 设置为雷雨 (sd_weather = 2)
scoreboard players set Global sd_weather 2

# 2. 发送提示消息
tellraw @s {"text":"已切换到雷雨 ⚡","color":"dark_purple","bold":true}

# 3. 播放音效
playsound entity.experience_orb.pickup player @s ~ ~ ~ 1 0.8
