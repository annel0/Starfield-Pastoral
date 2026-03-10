# data/stardew/function/menu/buttons/time_forward_year.mcfunction
# 快进一年
# 执行者: 玩家 (@s)

# 1. 增加年份
scoreboard players add Global sd_year 1

# 2. 获取当前年份并发送消息
execute store result score #CurrentYear sd_menu_ctrl run scoreboard players get Global sd_year
tellraw @s [{"text":"📅 ","color":"gold"},{"text":"时间快进一年！现在是第 ","color":"yellow"},{"score":{"name":"#CurrentYear","objective":"sd_menu_ctrl"},"color":"gold","bold":true},{"text":" 年","color":"yellow"}]

# 3. 播放音效
playsound minecraft:block.bell.use player @s ~ ~ ~ 1.0 1.5
