# 周一对话
# @s = 玩家

execute store result score #hearts stardew.temp run scoreboard players get @s stardew.friendship.abigail
scoreboard players operation #hearts stardew.temp /= #250 stardew.const
scoreboard players operation #season stardew.temp = Global sd_season

# 高友谊（8心+）- 夏天
execute if score #hearts stardew.temp matches 8.. if score #season stardew.temp matches 1 run data modify storage stardew:dialogue current.pages set value [['{"text":"我原以为鹈鹕镇","color":"#2C1810"}','{"text":"是个枯燥乏味的地方，","color":"#2C1810"}'],['{"text":"但我竟然慢慢","color":"#2C1810"}','{"text":"爱上它了。","color":"#2C1810"}']]
execute if score #hearts stardew.temp matches 8.. if score #season stardew.temp matches 1 run return 1

execute if score #hearts stardew.temp matches 8.. run data modify storage stardew:dialogue current.pages set value [['{"text":"你是专程来看我的吗？","color":"#2C1810"}','{"text":"真贴心呢。","color":"#2C1810"}'],['{"text":"这么说来，","color":"#2C1810"}','{"text":"你去探索过那些山洞了？","color":"#2C1810"}'],['{"text":"有意思。","color":"#2C1810"}','{"text":"我也想找个机会，","color":"#2C1810"}','{"text":"独自去走上一趟。","color":"#2C1810"}']]
execute if score #hearts stardew.temp matches 8.. run return 1

# 中友谊（4心+）- 夏天
execute if score #hearts stardew.temp matches 4.. if score #season stardew.temp matches 1 run data modify storage stardew:dialogue current.text set value ['{"text":"我的宠物豚鼠大卫，","color":"#2C1810"}','{"text":"就不喜欢闷热的天气。","color":"#2C1810"}','{"text":"他好难养的。","color":"#2C1810"}']
execute if score #hearts stardew.temp matches 4.. if score #season stardew.temp matches 1 run return 1

execute if score #hearts stardew.temp matches 4.. run data modify storage stardew:dialogue current.pages set value [['{"text":"啊，你好啊，@。","color":"#2C1810"}','{"text":"你是在休息吗？","color":"#2C1810"}'],['{"text":"我也是。噢！","color":"#2C1810"}','{"text":"我没干什么体力活","color":"#2C1810"}','{"text":"我刚才在上网络课程。","color":"#2C1810"}']]
execute if score #hearts stardew.temp matches 4.. run return 1

# 低友谊（0-3心）
execute if score #season stardew.temp matches 1 run data modify storage stardew:dialogue current.text set value ['{"text":"我的宠物豚鼠大卫，","color":"#2C1810"}','{"text":"就不喜欢闷热的天气。","color":"#2C1810"}','{"text":"他好难养的。","color":"#2C1810"}']
execute if score #season stardew.temp matches 1 run return 1

data modify storage stardew:dialogue current.text set value ['{"text":"我今天得活动活动","color":"#2C1810"}','{"text":"腿脚，呼吸一下","color":"#2C1810"}','{"text":"新鲜空气。","color":"#2C1810"}']
execute if score #season stardew.temp matches 2 run return 1

data modify storage stardew:dialogue current.text set value ['{"text":"哦，嘿。","color":"#2C1810"}','{"text":"你是在休息吗？","color":"#2C1810"}']