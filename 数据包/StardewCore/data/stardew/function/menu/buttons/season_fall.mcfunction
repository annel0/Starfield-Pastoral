# data/stardew/function/menu/buttons/season_fall.mcfunction
# 切换到秋季
# 执行者: 玩家 (@s)

# 1. 设置为秋季 (sd_season = 3)
scoreboard players set Global sd_season 3

# 2. 发送提示消息
tellraw @s {"text":"已切换到秋季 🍂","color":"yellow","bold":true}

# 3. 播放音效
playsound entity.experience_orb.pickup player @s ~ ~ ~ 1 1.0
