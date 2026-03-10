# Like反应 (+45友谊)
# @s = 送礼的玩家

scoreboard players add @s stardew.friendship.abigail 45

# 增加本周送礼计数
scoreboard players add @s stardew.gifted.abigail 1

# 给玩家添加临时tag用于延迟打开对话
tag @s add abigail.open_dialogue

# 设置对话数据
data modify storage stardew:dialogue current.npc set value "Abigail"
data modify storage stardew:dialogue current.npc_display_name set value '{"text":"阿比盖尔","color":"#9966FF","bold":true}'
data modify storage stardew:dialogue current.text set value ['{"text":"嘿，你怎么知道我饿了？","color":"#2C1810"}','{"text":"看起来很好吃！","color":"#2C1810"}']
data modify storage stardew:dialogue current.portrait set value {id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12000}}

# 开心粒子效果
execute at @n[tag=npc.abigail] run particle happy_villager ~ ~2 ~ 0.3 0.3 0.3 0 3