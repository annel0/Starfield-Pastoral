# data/stardew/function/menu/buttons/season_summer.mcfunction
# 切换到夏季
# 执行者: 玩家 (@s)

# 1. 设置为夏季 (sd_season = 2)
scoreboard players set Global sd_season 2

# 2. 发送提示消息
tellraw @s {"text":"已切换到夏季 ☀️","color":"gold","bold":true}

# 3. 播放音效
playsound entity.experience_orb.pickup player @s ~ ~ ~ 1 1.4
