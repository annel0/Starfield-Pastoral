# Dislike反应 (-20友谊)
# @s = 送礼的玩家

scoreboard players remove @s stardew.friendship.abigail 20

# 增加本周送礼计数
scoreboard players add @s stardew.gifted.abigail 1

# 给玩家添加临时tag用于延迟打开对话
tag @s add abigail.open_dialogue

# 设置对话数据
data modify storage stardew:dialogue current.npc set value "Abigail"
data modify storage stardew:dialogue current.npc_display_name set value '{"text":"阿比盖尔","color":"#9966FF","bold":true}'
data modify storage stardew:dialogue current.text set value ['{"text":"哦谢谢你。","color":"#2C1810"}']
data modify storage stardew:dialogue current.portrait set value {id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12002}}

# 不开心粒子效果
execute at @n[tag=npc.abigail] run particle smoke ~ ~2 ~ 0.2 0.2 0.2 0 2