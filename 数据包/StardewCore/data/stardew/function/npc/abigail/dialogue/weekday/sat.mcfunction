# 周六对话
execute store result score #hearts stardew.temp run scoreboard players get @s stardew.friendship.abigail
scoreboard players operation #hearts stardew.temp /= #250 stardew.const
scoreboard players operation #season stardew.temp = Global sd_season

execute if score #hearts stardew.temp matches 6.. run data modify storage stardew:dialogue current.text set value ['{"text":"嗨。","color":"#2C1810"}','{"text":"今天你的头发好酷啊","color":"#2C1810"}','{"text":"你做了什么改变吗？","color":"#2C1810"}']
execute if score #hearts stardew.temp matches 6.. run return 1

data modify storage stardew:dialogue current.pages set value [['{"text":"在这样的日子里，","color":"#2C1810"}','{"text":"吸上一口山里的","color":"#2C1810"}','{"text":"新鲜空气，真是美啊","color":"#2C1810"}'],['{"text":"青蛙大概也很快","color":"#2C1810"}','{"text":"就会活跃起来了。","color":"#2C1810"}']]