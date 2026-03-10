# 室内出口系统 - 检测玩家右键出口交互体
# 在主循环中运行

# 检查所有出口交互体的右键交互
execute as @e[type=interaction,tag=exit_door] at @s if data entity @s interaction run function stardew:building/exit/handle_exit_click
