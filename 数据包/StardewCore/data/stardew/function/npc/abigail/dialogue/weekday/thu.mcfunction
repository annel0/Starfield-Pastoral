# 周四对话
execute store result score #hearts stardew.temp run scoreboard players get @s stardew.friendship.abigail
scoreboard players operation #hearts stardew.temp /= #250 stardew.const
scoreboard players operation #season stardew.temp = Global sd_season

execute if score #hearts stardew.temp matches 10.. run data modify storage stardew:dialogue current.text set value ['{"text":"哇哦，我才想起来","color":"#2C1810"}','{"text":"今天是周四。","color":"#2C1810"}']
execute if score #hearts stardew.temp matches 10.. run return 1

data modify storage stardew:dialogue current.text set value ['{"text":"今天晚上估计要由","color":"#2C1810"}','{"text":"我爸下厨了","color":"#2C1810"}','{"text":"我今天什么都不想干","color":"#2C1810"}']