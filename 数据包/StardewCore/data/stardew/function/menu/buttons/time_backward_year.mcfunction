# data/stardew/function/menu/buttons/time_backward_year.mcfunction
# 倒退一年
# 执行者: 玩家 (@s)

# 1. 减少年份
scoreboard players remove Global sd_year 1

# 2. 限制最小年份为1
execute if score Global sd_year matches ..0 run scoreboard players set Global sd_year 1

# 3. 获取当前年份并发送消息
execute store result score #CurrentYear sd_menu_ctrl run scoreboard players get Global sd_year
tellraw @s [{"text":"📅 ","color":"gray"},{"text":"时间倒退一年。现在是第 ","color":"gray"},{"score":{"name":"#CurrentYear","objective":"sd_menu_ctrl"},"color":"white"},{"text":" 年","color":"gray"}]

# 4. 播放音效
playsound minecraft:block.bell.use player @s ~ ~ ~ 1.0 0.8
