# data/stardew/function/menu/buttons/time_forward_day.mcfunction
# 快进一天
# 执行者: 玩家 (@s)

# 1. 增加一天
scoreboard players add Global sd_day 1

# 2. 检查是否超过28天，如果超过则进入下一季
execute if score Global sd_day matches 29.. run scoreboard players add Global sd_season 1
execute if score Global sd_day matches 29.. run scoreboard players set Global sd_day 1

# 3. 检查是否超过4季，如果超过则进入下一年
execute if score Global sd_season matches 5.. run scoreboard players add Global sd_year 1
execute if score Global sd_season matches 5.. run scoreboard players set Global sd_season 1

# 4. 获取当前日期并发送消息
execute store result score #CurrentDay sd_menu_ctrl run scoreboard players get Global sd_day
execute if score Global sd_season matches 1 run tellraw @s [{"text":"📅 ","color":"green"},{"text":"时间快进一天！现在是春季第 ","color":"yellow"},{"score":{"name":"#CurrentDay","objective":"sd_menu_ctrl"},"color":"gold","bold":true},{"text":" 天","color":"yellow"}]
execute if score Global sd_season matches 2 run tellraw @s [{"text":"📅 ","color":"gold"},{"text":"时间快进一天！现在是夏季第 ","color":"yellow"},{"score":{"name":"#CurrentDay","objective":"sd_menu_ctrl"},"color":"gold","bold":true},{"text":" 天","color":"yellow"}]
execute if score Global sd_season matches 3 run tellraw @s [{"text":"📅 ","color":"yellow"},{"text":"时间快进一天！现在是秋季第 ","color":"yellow"},{"score":{"name":"#CurrentDay","objective":"sd_menu_ctrl"},"color":"gold","bold":true},{"text":" 天","color":"yellow"}]
execute if score Global sd_season matches 4 run tellraw @s [{"text":"📅 ","color":"aqua"},{"text":"时间快进一天！现在是冬季第 ","color":"yellow"},{"score":{"name":"#CurrentDay","objective":"sd_menu_ctrl"},"color":"gold","bold":true},{"text":" 天","color":"yellow"}]

# 5. 播放音效
playsound minecraft:block.note_block.chime player @s ~ ~ ~ 1.0 1.5
