# 对话系统交互检测
# 使用右键点击检测玩家是否在和NPC对话

# 检测interaction实体的交互（攻击=左键，interaction=右键）
execute as @e[type=interaction,tag=dialogue_menu] if data entity @s attack on attacker run scoreboard players set @s stardew_dialogue_select 1
execute as @e[type=interaction,tag=dialogue_menu] if data entity @s interaction on target run scoreboard players set @s stardew_dialogue_select 1
execute as @e[type=interaction,tag=dialogue_menu] run data remove entity @s attack
execute as @e[type=interaction,tag=dialogue_menu] run data remove entity @s interaction
