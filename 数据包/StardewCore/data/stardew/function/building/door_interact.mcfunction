# 检测玩家右键门并传送到室内
# 由 main.mcfunction 每tick调用

# 检测所有门交互体的右键交互
execute as @e[type=interaction,tag=building_door] at @s if data entity @s interaction run function stardew:building/handle_door_click
