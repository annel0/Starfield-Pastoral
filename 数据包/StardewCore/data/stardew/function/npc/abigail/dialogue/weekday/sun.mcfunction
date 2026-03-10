# 周日对话
execute store result score #hearts stardew.temp run scoreboard players get @s stardew.friendship.abigail
scoreboard players operation #hearts stardew.temp /= #250 stardew.const
scoreboard players operation #season stardew.temp = Global sd_season

execute if score #hearts stardew.temp matches 4.. run data modify storage stardew:dialogue current.pages set value [['{"text":"你有没有想去","color":"#2C1810"}','{"text":"冒险的冲动啊，@？","color":"#2C1810"}'],['{"text":"好吧假设你得到了","color":"#2C1810"}','{"text":"一个带薪假期。","color":"#2C1810"}','{"text":"你会去哪儿？","color":"#2C1810"}'],['{"text":"哦，海滩？确实","color":"#2C1810"}','{"text":"但你可能不会喜欢","color":"#2C1810"}','{"text":"我提到的地方。","color":"#2C1810"}']]
execute if score #hearts stardew.temp matches 4.. run return 1

data modify storage stardew:dialogue current.text set value ['{"text":"哎哟一整个周末","color":"#2C1810"}','{"text":"都没写作业。","color":"#2C1810"}','{"text":"看来又要熬个通宵了","color":"#2C1810"}']