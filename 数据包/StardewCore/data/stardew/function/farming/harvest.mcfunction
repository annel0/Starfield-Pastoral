# data/stardew/functions/farming/harvest.mcfunction

# 1. 未成熟提示 (防刷屏)
execute if score @s sd_crop_age matches ..3 run tellraw @p[distance=..3,limit=1] {"text":"还没熟 (需4天)。","color":"gray"}

# 2. 成熟掉落逻辑 (Age >= 4)
# [1.21 适配] 生成物品使用了 components 格式
execute if score @s sd_crop_age matches 4.. run summon item ~ ~0.5 ~ {Tags:["new_drop"],Item:{id:"minecraft:wheat",Count:1b,components:{"minecraft:custom_model_data":1,"minecraft:custom_name":'{"text":"小麦","color":"white"}'}}}

# 稀有度判定 (利用 sd_rng)
# 钻石 (95-100)
execute if score @s sd_crop_age matches 4.. if score Global sd_rng matches 95.. run data merge entity @e[type=item,tag=new_drop,limit=1] {Item:{components:{"minecraft:custom_model_data":4,"minecraft:custom_name":'{"text":"★ 钻石小麦","color":"light_purple"}'}}}
# 金星 (80-94)
execute if score @s sd_crop_age matches 4.. if score Global sd_rng matches 80..94 run data merge entity @e[type=item,tag=new_drop,limit=1] {Item:{components:{"minecraft:custom_model_data":3,"minecraft:custom_name":'{"text":"★ 金星小麦","color":"gold"}'}}}
# 银星 (50-79)
execute if score @s sd_crop_age matches 4.. if score Global sd_rng matches 50..79 run data merge entity @e[type=item,tag=new_drop,limit=1] {Item:{components:{"minecraft:custom_model_data":2,"minecraft:custom_name":'{"text":"★ 银星小麦","color":"aqua"}'}}}

tag @e[tag=new_drop] remove new_drop

# 3. 销毁逻辑
execute if score @s sd_crop_age matches 4.. run particle minecraft:wax_on ~ ~0.5 ~ 0.2 0.2 0.2 1 10
execute if score @s sd_crop_age matches 4.. run playsound minecraft:entity.experience_orb.pickup block @a ~ ~ ~ 1 1.2

# [安全锁] 只有脚下是小麦才设置空气，防止破坏耕地
execute if score @s sd_crop_age matches 4.. if block ~ ~ ~ minecraft:wheat run setblock ~ ~ ~ minecraft:air

# 自杀
execute if score @s sd_crop_age matches 4.. run kill @s