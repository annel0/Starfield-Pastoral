# stardew:mine/enter/check_next_floor.mcfunction
# 检测进入下一层的入口交互
# 执行者: 玩家 (@s)
# 执行位置: 玩家位置

# 检测玩家是否右键了下层入口实体
execute as @e[type=interaction,tag=sd_mine_next_floor,limit=1,sort=nearest,distance=..3] if data entity @s interaction run execute on target run function stardew:mine/enter/next_floor

# 清除交互数据
execute as @e[type=interaction,tag=sd_mine_next_floor] run data remove entity @s interaction
