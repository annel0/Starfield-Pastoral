# data/stardew/function/menu/buttons/season_spring.mcfunction
# 切换到春季
# 执行者: 玩家 (@s)

# 1. 设置为春季 (sd_season = 1)
scoreboard players set Global sd_season 1

# 2. 发送提示消息
tellraw @s {"text":"已切换到春季 🌸","color":"green","bold":true}

# 3. 播放音效
playsound entity.experience_orb.pickup player @s ~ ~ ~ 1 1.2
