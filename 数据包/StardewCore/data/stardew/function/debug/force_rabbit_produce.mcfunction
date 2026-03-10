# ================================================================
# 星露谷物语 - 强制生成兔子产物（测试用）
# ================================================================
# 用途：让最近的兔子立即产生产物，用于测试

# 找到最近的兔子
tag @e[type=chicken,tag=stardew.animal,limit=1,sort=nearest] add temp.force_produce

# 检查是否找到
execute unless entity @e[tag=temp.force_produce] run tellraw @s [{"text":"[调试] ❌ 附近没有兔子！","color":"red"}]
execute unless entity @e[tag=temp.force_produce] run return 0

# 检查是否是兔子（type=103）
execute as @e[tag=temp.force_produce] unless score @s stardew.animal.type matches 103 run tellraw @s [{"text":"[调试] ❌ 这不是兔子！","color":"red"}]
execute as @e[tag=temp.force_produce] unless score @s stardew.animal.type matches 103 run tag @e[tag=temp.force_produce] remove temp.force_produce
execute unless entity @e[tag=temp.force_produce] run return 0

# 检查是否成年（5天+）
execute as @e[tag=temp.force_produce] if score @s stardew.animal.age matches ..4 run tellraw @s [{"text":"[调试] ⚠ 兔子还未成年（需要5天），但仍会生成产物...","color":"yellow"}]

# 强制设置为已喂食（因为还没做喂食系统）
execute as @e[tag=temp.force_produce] run scoreboard players set @s stardew.animal.fed_today 1

# 强制设置心情为100（保证100%产物）
execute as @e[tag=temp.force_produce] run scoreboard players set @s stardew.animal.mood 100

# 显示兔子信息
tellraw @s [{"text":"========== 强制生成兔子产物 ==========","color":"gold"}]
execute as @e[tag=temp.force_produce] run tellraw @s [{"text":"兔子ID: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"white"}]
execute as @e[tag=temp.force_produce] run tellraw @s [{"text":"年龄: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.age"},"color":"white"},{"text":" 天","color":"gray"}]
execute as @e[tag=temp.force_produce] run tellraw @s [{"text":"友好度: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"white"},{"text":"/255","color":"gray"}]

# 强制调用产物生成
execute as @e[tag=temp.force_produce] run function stardew:animal/produce/check_single_rabbit

# 清理标签
tag @e[tag=temp.force_produce] remove temp.force_produce

tellraw @s [{"text":"✅ 产物生成完毕！","color":"green"}]
