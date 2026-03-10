# data/stardew/function/menu/buttons/season_winter.mcfunction
# 切换到冬季
# 执行者: 玩家 (@s)

# 1. 设置为冬季 (sd_season = 4)
scoreboard players set Global sd_season 4

# 2. 发送提示消息
tellraw @s {"text":"已切换到冬季 ❄️","color":"aqua","bold":true}
# 3. 播放音效
playsound entity.experience_orb.pickup player @s ~ ~ ~ 1 0.8
