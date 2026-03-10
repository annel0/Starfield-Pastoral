# stardew:mine/enter/check_entrance.mcfunction
# 检测主世界矿洞入口的交互
# 执行者: 玩家 (@s)
# 执行位置: 玩家位置

# 检测玩家是否右键了矿洞入口实体
execute as @e[type=interaction,tag=sd_mine_entrance,limit=1,sort=nearest,distance=..5] if data entity @s interaction run execute on target run function stardew:mine/enter/from_overworld

# 清除交互数据
execute as @e[type=interaction,tag=sd_mine_entrance] run data remove entity @s interaction
