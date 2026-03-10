# 周五对话
execute store result score #hearts stardew.temp run scoreboard players get @s stardew.friendship.abigail
scoreboard players operation #hearts stardew.temp /= #250 stardew.const
scoreboard players operation #season stardew.temp = Global sd_season

execute if score #hearts stardew.temp matches 10.. if score #season stardew.temp matches 2 run data modify storage stardew:dialogue current.pages set value [['{"text":"@，我想和你","color":"#2C1810"}','{"text":"说件事儿","color":"#2C1810"}'],['{"text":"其实那个就是","color":"#2C1810"}','{"text":"你的靴子！","color":"#2C1810"}','{"text":"看起来，呃真挺","color":"#2C1810"}'],['{"text":"干净的","color":"#2C1810"}','{"text":"就这些*唉*","color":"#2C1810"}']]
execute if score #hearts stardew.temp matches 10.. if score #season stardew.temp matches 2 run return 1

execute if score #hearts stardew.temp matches 6.. run data modify storage stardew:dialogue current.pages set value [['{"text":"今天小鸟们","color":"#2C1810"}','{"text":"叫得好欢啊。","color":"#2C1810"}','{"text":"它们那么单纯，","color":"#2C1810"}'],['{"text":"根本就不担心未来。","color":"#2C1810"}'],['{"text":"像它们那样也挺好的","color":"#2C1810"}','{"text":"，对不对？","color":"#2C1810"}']]
execute if score #hearts stardew.temp matches 6.. run return 1

data modify storage stardew:dialogue current.text set value ['{"text":"哇哦，我才想起来","color":"#2C1810"}','{"text":"今天是周五。","color":"#2C1810"}','{"text":"有时候我真是过得","color":"#2C1810"}']