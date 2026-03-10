# 处理对话交互
# @s = 对话的玩家

# 初始化计数器（如果未设置）
execute unless score @s stardew.talked.abigail matches -2147483648..2147483647 run scoreboard players set @s stardew.talked.abigail 0
execute unless score @s stardew.friendship.abigail matches -2147483648..2147483647 run scoreboard players set @s stardew.friendship.abigail 0

# 检查今天是否已经与阿比盖尔对话过
execute if score @s stardew.talked.abigail matches 1.. run tellraw @s {"text":"今天已经和阿比盖尔说过话了","color":"gray"}
execute if score @s stardew.talked.abigail matches 1.. run return 0

# 检测玩家与NPC的距离，如果<2格则推开
execute at @e[tag=npc.abigail,limit=1,sort=nearest] if entity @s[distance=..2] at @e[tag=npc.abigail,limit=1] run function stardew:npc/abigail/push_player_back

# 让NPC看向玩家
execute as @e[tag=npc.abigail,limit=1,sort=nearest] at @s facing entity @p eyes run tp @s ~ ~ ~ ~ ~

# 增加友谊值 +20
scoreboard players add @s stardew.friendship.abigail 20

# 标记今天已与阿比盖尔对话
scoreboard players set @s stardew.talked.abigail 1

# 给玩家添加临时tag用于延迟打开对话
tag @s add abigail.open_dialogue

# 设置NPC基础信息
data modify storage stardew:dialogue current.npc set value "Abigail"
data modify storage stardew:dialogue current.npc_display_name set value '{"text":"阿比盖尔","color":"#9966FF","bold":true}'
data modify storage stardew:dialogue current.portrait set value {id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12000}}

# 调用对话选择系统（根据友谊度、星期、季节等选择对话）
function stardew:npc/abigail/dialogue/get_dialogue

