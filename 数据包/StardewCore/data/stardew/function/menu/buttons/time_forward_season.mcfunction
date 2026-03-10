# data/stardew/function/menu/buttons/time_forward_season.mcfunction
# 快进一季（28天）
# 执行者: 玩家 (@s)

# 1. 增加28天
scoreboard players add Global sd_day 28

# 2. 检查是否超过28天，如果超过则进入下一季
execute if score Global sd_day matches 29.. run scoreboard players add Global sd_season 1
execute if score Global sd_day matches 29.. run scoreboard players set Global sd_day 1

# 3. 检查季节是否超过4，如果超过则进入下一年
execute if score Global sd_season matches 5.. run scoreboard players add Global sd_year 1
execute if score Global sd_season matches 5.. run scoreboard players set Global sd_season 1

# 4. 获取当前季节名称并发送消息
execute if score Global sd_season matches 1 run tellraw @s [{"text":"🌸 ","color":"green"},{"text":"时间快进一季！现在是 ","color":"yellow"},{"text":"春季","color":"green","bold":true}]
execute if score Global sd_season matches 2 run tellraw @s [{"text":"☀ ","color":"gold"},{"text":"时间快进一季！现在是 ","color":"yellow"},{"text":"夏季","color":"gold","bold":true}]
execute if score Global sd_season matches 3 run tellraw @s [{"text":"🍂 ","color":"yellow"},{"text":"时间快进一季！现在是 ","color":"yellow"},{"text":"秋季","color":"yellow","bold":true}]
execute if score Global sd_season matches 4 run tellraw @s [{"text":"❄ ","color":"aqua"},{"text":"时间快进一季！现在是 ","color":"yellow"},{"text":"冬季","color":"aqua","bold":true}]

# 5. 播放音效
playsound minecraft:block.note_block.chime player @s ~ ~ ~ 1.0 1.5
