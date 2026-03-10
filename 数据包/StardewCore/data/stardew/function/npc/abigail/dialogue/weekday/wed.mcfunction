# 周三对话
execute store result score #hearts stardew.temp run scoreboard players get @s stardew.friendship.abigail
scoreboard players operation #hearts stardew.temp /= #250 stardew.const
scoreboard players operation #season stardew.temp = Global sd_season

execute if score #hearts stardew.temp matches 4.. run data modify storage stardew:dialogue current.pages set value [['{"text":"啊，你好啊，@。","color":"#2C1810"}','{"text":"你是在休息吗？","color":"#2C1810"}'],['{"text":"我也是。噢！","color":"#2C1810"}','{"text":"我没干什么体力活","color":"#2C1810"}','{"text":"我刚才在上网络课程。","color":"#2C1810"}']]
execute if score #hearts stardew.temp matches 4.. run return 1

data modify storage stardew:dialogue current.pages set value [['{"text":"嘿。要是我说了","color":"#2C1810"}','{"text":"什么难听的话，","color":"#2C1810"}','{"text":"请别介意。","color":"#2C1810"}'],['{"text":"我昨天晚上","color":"#2C1810"}','{"text":"没怎么睡着。","color":"#2C1810"}'],['{"text":"你有什么事吗？","color":"#2C1810"}']]