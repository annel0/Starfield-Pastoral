# stardew:mine/ladder/check_interact.mcfunction
# 检测梯子交互
# 执行者: 玩家 (@s)，在矿洞维度
# 执行位置: 玩家位置

# 检测下层梯子 (进入下一层)
execute as @e[type=interaction,tag=sd_mine_ladder_down,limit=1,sort=nearest,distance=..3] if data entity @s interaction run execute on target run function stardew:mine/ladder/use_down

# 检测出口梯子 (返回 0 层)
execute as @e[type=interaction,tag=sd_mine_ladder_exit,limit=1,sort=nearest,distance=..3] if data entity @s interaction run execute on target run function stardew:mine/ladder/use_exit

# 清除交互数据
execute as @e[type=interaction,tag=sd_mine_ladder_down] run data remove entity @s interaction
execute as @e[type=interaction,tag=sd_mine_ladder_exit] run data remove entity @s interaction
