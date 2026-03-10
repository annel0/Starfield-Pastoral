# data/stardew/function/menu/buttons/time_backward_day.mcfunction
# 倒退一天
# 执行者: 玩家 (@s)

# 1. 减少一天
scoreboard players remove Global sd_day 1

# 2. 检查是否小于1天，如果是则回到上一季的最后一天
execute if score Global sd_day matches ..0 run scoreboard players remove Global sd_season 1
execute if score Global sd_day matches ..0 run scoreboard players set Global sd_day 28

# 3. 检查是否小于第1季，如果是则回到上一年的冬季
execute if score Global sd_season matches ..0 run scoreboard players remove Global sd_year 1
execute if score Global sd_season matches ..0 run scoreboard players set Global sd_season 4

# 4. 限制最小年份为1
execute if score Global sd_year matches ..0 run scoreboard players set Global sd_year 1
execute if score Global sd_year matches 1 if score Global sd_season matches ..0 run scoreboard players set Global sd_season 1
execute if score Global sd_year matches 1 if score Global sd_season matches 1 if score Global sd_day matches ..0 run scoreboard players set Global sd_day 1

# 5. 获取当前日期并发送消息
execute store result score #CurrentDay sd_menu_ctrl run scoreboard players get Global sd_day
execute if score Global sd_season matches 1 run tellraw @s [{"text":"📅 ","color":"green"},{"text":"时间倒退一天。现在是春季第 ","color":"gray"},{"score":{"name":"#CurrentDay","objective":"sd_menu_ctrl"},"color":"white"},{"text":" 天","color":"gray"}]
execute if score Global sd_season matches 2 run tellraw @s [{"text":"📅 ","color":"gold"},{"text":"时间倒退一天。现在是夏季第 ","color":"gray"},{"score":{"name":"#CurrentDay","objective":"sd_menu_ctrl"},"color":"white"},{"text":" 天","color":"gray"}]
execute if score Global sd_season matches 3 run tellraw @s [{"text":"📅 ","color":"yellow"},{"text":"时间倒退一天。现在是秋季第 ","color":"gray"},{"score":{"name":"#CurrentDay","objective":"sd_menu_ctrl"},"color":"white"},{"text":" 天","color":"gray"}]
execute if score Global sd_season matches 4 run tellraw @s [{"text":"📅 ","color":"aqua"},{"text":"时间倒退一天。现在是冬季第 ","color":"gray"},{"score":{"name":"#CurrentDay","objective":"sd_menu_ctrl"},"color":"white"},{"text":" 天","color":"gray"}]

# 6. 播放音效
playsound minecraft:block.note_block.chime player @s ~ ~ ~ 1.0 0.8
