# 周二对话
# @s = 玩家

execute store result score #hearts stardew.temp run scoreboard players get @s stardew.friendship.abigail
scoreboard players operation #hearts stardew.temp /= #250 stardew.const
scoreboard players operation #season stardew.temp = Global sd_season

# 高友谊4心+ - 夏天
execute if score #hearts stardew.temp matches 4.. if score #season stardew.temp matches 1 run data modify storage stardew:dialogue current.text set value ['{"text":"嗨，很高兴见到你。","color":"#2C1810"}','{"text":"我想稍稍换换脑子","color":"#2C1810"}','{"text":"你今天过得怎么样？","color":"#2C1810"}']
execute if score #hearts stardew.temp matches 4.. if score #season stardew.temp matches 1 run return 1

# 高友谊4心+ - 秋天
execute if score #hearts stardew.temp matches 4.. if score #season stardew.temp matches 2 run data modify storage stardew:dialogue current.pages set value [['{"text":"虽然我经常和父母","color":"#2C1810"}','{"text":"吵架，但我知道","color":"#2C1810"}','{"text":"他们那是为我好。","color":"#2C1810"}'],['{"text":"他们只是站在","color":"#2C1810"}','{"text":"他们自己的立场上","color":"#2C1810"}','{"text":"做了他们认为最好的","color":"#2C1810"}'],['{"text":"事情。","color":"#2C1810"}','{"text":"我也搞不懂","color":"#2C1810"}','{"text":"为什么要和你说这个。","color":"#2C1810"}'],['{"text":"不要告诉任何人。","color":"#2C1810"}']]
execute if score #hearts stardew.temp matches 4.. if score #season stardew.temp matches 2 run return 1

# 高友谊4心+ - 冬天
execute if score #hearts stardew.temp matches 4.. if score #season stardew.temp matches 3 run data modify storage stardew:dialogue current.text set value ['{"text":"好冷啊，","color":"#2C1810"}','{"text":"要是能来杯热可可","color":"#2C1810"}','{"text":"就好了。","color":"#2C1810"}']
execute if score #hearts stardew.temp matches 4.. if score #season stardew.temp matches 3 run return 1

# 低友谊 - 夏天
execute if score #season stardew.temp matches 1 run data modify storage stardew:dialogue current.text set value ['{"text":"整个夏天，","color":"#2C1810"}','{"text":"空气里都是","color":"#2C1810"}','{"text":"蜂蜜和花粉的味道。","color":"#2C1810"}']
execute if score #season stardew.temp matches 1 run return 1

# 低友谊 - 秋天
execute if score #season stardew.temp matches 2 run data modify storage stardew:dialogue current.pages set value [['{"text":"我一般都会","color":"#2C1810"}','{"text":"在店里帮忙。","color":"#2C1810"}'],['{"text":"但自打 Joja 开业","color":"#2C1810"}','{"text":"以来，","color":"#2C1810"}','{"text":"生意就变得难做了。","color":"#2C1810"}']]
execute if score #season stardew.temp matches 2 run return 1

# 默认愤怒
data modify storage stardew:dialogue current.text set value ['{"text":"呃","color":"#2C1810"}','{"text":"我现在没那个心情。","color":"#2C1810"}']