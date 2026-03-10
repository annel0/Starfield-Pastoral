# data/stardew/function/menu/buttons/time_backward_season.mcfunction
# 倒退一季（28天）
# 执行者: 玩家 (@s)

# 1. 减少28天
scoreboard players remove Global sd_day 28

# 2. 检查是否小于1天，如果小于则回到上一季
execute if score Global sd_day matches ..0 run scoreboard players remove Global sd_season 1
execute if score Global sd_day matches ..0 run scoreboard players set Global sd_day 28

# 3. 检查季节是否小于1，如果小于则回到上一年
execute if score Global sd_season matches ..0 run scoreboard players remove Global sd_year 1
execute if score Global sd_season matches ..0 run scoreboard players set Global sd_season 4

# 4. 限制最小年份为1
execute if score Global sd_year matches ..0 run scoreboard players set Global sd_year 1

# 5. 获取当前季节名称并发送消息
execute if score Global sd_season matches 1 run tellraw @s [{"text":"🌸 ","color":"green"},{"text":"时间倒退一季。现在是 ","color":"gray"},{"text":"春季","color":"green"}]
execute if score Global sd_season matches 2 run tellraw @s [{"text":"☀ ","color":"gold"},{"text":"时间倒退一季。现在是 ","color":"gray"},{"text":"夏季","color":"gold"}]
execute if score Global sd_season matches 3 run tellraw @s [{"text":"🍂 ","color":"yellow"},{"text":"时间倒退一季。现在是 ","color":"gray"},{"text":"秋季","color":"yellow"}]
execute if score Global sd_season matches 4 run tellraw @s [{"text":"❄ ","color":"aqua"},{"text":"时间倒退一季。现在是 ","color":"gray"},{"text":"冬季","color":"aqua"}]

# 6. 播放音效
playsound minecraft:block.note_block.chime player @s ~ ~ ~ 1.0 0.8
