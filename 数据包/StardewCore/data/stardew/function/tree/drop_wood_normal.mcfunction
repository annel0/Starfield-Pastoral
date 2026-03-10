# data/stardew/functions/tree/drop_wood_normal.mcfunction

# A. 生成随机数 (必加！否则 sd_const 可能是 0)
execute store result score @s sd_const run random value 19..25

# B. 掉落木材 (调用 Loot Table)
# 这里的路径必须对应你刚才 Python 生成的路径
loot spawn ~ ~0.5 ~ loot stardew:items/resource/wood

# C. 修改数量
# 1. 给最近生成的木材打标
tag @e[type=item,distance=..2,sort=nearest,limit=1] add new_wood_drop

# 2. 修改堆叠数量
execute store result entity @e[type=item,tag=new_wood_drop,limit=1] Item.count int 1 run scoreboard players get @s sd_const

# D. 清理
tag @e[tag=new_wood_drop] remove new_wood_drop