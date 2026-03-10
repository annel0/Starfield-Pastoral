# data/stardew/functions/tree/drop_wood_hard.mcfunction

# A. 生成随机数 (9-15)
execute store result score @s sd_const run random value 9..15

# B. 掉落硬木
loot spawn ~ ~0.5 ~ loot stardew:items/resource/hardwood

# C. 修改数量
tag @e[type=item,distance=..2,sort=nearest,limit=1] add new_hardwood_drop
execute store result entity @e[type=item,tag=new_hardwood_drop,limit=1] Item.count int 1 run scoreboard players get @s sd_const

# D. 清理
tag @e[tag=new_hardwood_drop] remove new_hardwood_drop