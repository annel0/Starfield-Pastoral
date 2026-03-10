# Love反应 - 阿比盖尔收到最爱的礼物

# 增加友谊值 +80
scoreboard players add @s stardew.friendship.abigail 80

# 增加本周送礼计数（使用临时分数来实现add）
execute store result score #temp stardew.temp run scoreboard players get @s stardew.gifted.abigail
scoreboard players add #temp stardew.temp 1
scoreboard players operation @s stardew.gifted.abigail = #temp stardew.temp

# 给玩家添加临时tag用于延迟打开对话
tag @s add abigail.open_dialogue

# 设置对话内容到storage（使用官方文本）
data modify storage stardew:dialogue current.npc set value "Abigail"
data modify storage stardew:dialogue current.npc_display_name set value '{"text":"阿比盖尔","color":"#9966FF","bold":true}'
data modify storage stardew:dialogue current.text set value ['{"text":"我超爱这个的！","color":"#2C1810"}','{"text":"你太棒了！","color":"#2C1810"}']
data modify storage stardew:dialogue current.portrait set value {id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12001}}

# 播放粒子效果
execute at @n[tag=npc.abigail] run particle heart ~ ~2 ~ 0.3 0.3 0.3 0 5

playsound entity.player.levelup player @s ~ ~ ~ 0.5 1.5