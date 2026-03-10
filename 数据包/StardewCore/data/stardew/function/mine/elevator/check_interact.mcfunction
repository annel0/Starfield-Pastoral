# stardew:mine/elevator/check_interact.mcfunction
# 检测电梯交互
# 执行者: 玩家 (@s)

# 检测玩家是否右键了电梯实体 - 显示tellraw菜单
execute as @e[type=interaction,tag=sd_mine_elevator,distance=..3] if data entity @s interaction run execute on target run function stardew:mine/elevator/show_menu

# 清除交互数据
execute as @e[type=interaction,tag=sd_mine_elevator,distance=..3] if data entity @s interaction run data remove entity @s interaction
