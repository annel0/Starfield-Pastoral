# 突刺单步移动 - 每次向前0.5格，检测碰撞

# 检测前方是否有方块（碰撞检测）
execute unless block ^ ^ ^0.5 #minecraft:air unless block ^ ^ ^0.5 #minecraft:replaceable run return fail

# 召唤标记实体在前方0.5格位置
execute rotated ~ 0 positioned ^ ^ ^0.5 run summon marker ~ ~ ~ {Tags:["dash_target"]}

# 传送到标记实体位置
tp @s @e[type=marker,tag=dash_target,limit=1,sort=nearest]

# 清理标记实体
kill @e[type=marker,tag=dash_target]

# 粒子效果
particle minecraft:cloud ~ ~1 ~ 0.1 0.3 0.1 0.01 5 force
particle minecraft:crit ~ ~1 ~ 0.1 0.3 0.1 0.01 3 force

# 继续下一步
scoreboard players remove #dash_steps sd_temp 1
execute if score #dash_steps sd_temp matches 1.. run function stardew:combat/weapon/dash_strike_step
