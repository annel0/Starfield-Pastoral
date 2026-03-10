# stardew:mine/exit/check.mcfunction
# 检测返回主世界的交互
# 执行者: 玩家 (@s)

# 检测玩家是否右键了返回地面实体
execute as @e[type=interaction,tag=sd_mine_exit,limit=1,sort=nearest,distance=..3] if data entity @s interaction run execute on target run function stardew:mine/exit/to_overworld

# 清除交互数据
execute as @e[type=interaction,tag=sd_mine_exit] run data remove entity @s interaction
