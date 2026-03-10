# 处理送礼交互
# @s = 送礼的玩家

# 初始化计数器（如果未设置）
execute unless score @s stardew.gifted.abigail matches -2147483648..2147483647 run scoreboard players set @s stardew.gifted.abigail 0
execute unless score @s stardew.friendship.abigail matches -2147483648..2147483647 run scoreboard players set @s stardew.friendship.abigail 0

# 检查本周是否已给阿比盖尔送过2次礼物
execute if score @s stardew.gifted.abigail matches 2.. run tellraw @s {"text":"这周已经给阿比盖尔送过2次礼物了","color":"red"}
execute if score @s stardew.gifted.abigail matches 2.. run return 0

# 检测玩家与NPC的距离，如果<2格则推开
execute at @e[tag=npc.abigail,limit=1,sort=nearest] if entity @s[distance=..2] at @e[tag=npc.abigail,limit=1] run function stardew:npc/abigail/push_player_back

# 让NPC看向玩家
execute as @e[tag=npc.abigail,limit=1,sort=nearest] at @s facing entity @p eyes run tp @s ~ ~ ~ ~ ~

# 标记需要延迟消耗物品（在对话打开后消耗）
tag @s add abigail.consume_gift

# 检测手持物品并判断礼物类型（reaction文件会增加送礼计数）
function stardew:npc/abigail/gifts/check_gift
