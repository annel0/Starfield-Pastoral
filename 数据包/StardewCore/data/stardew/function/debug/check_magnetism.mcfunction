# 调试磁力效果
tellraw @s [{"text":"[磁力调试] ","color":"gold"}]

# 检查装备状态
execute if score @s sd_equip_boots matches 1.. run tellraw @s [{"text":"✓ 已装备靴子","color":"green"}]
execute unless score @s sd_equip_boots matches 1.. run tellraw @s [{"text":"✗ 未装备靴子","color":"red"}]

# 检查靴子数据
execute if data storage stardew:equipment boots run tellraw @s [{"text":"✓ 靴子数据存在","color":"green"}]
execute unless data storage stardew:equipment boots run tellraw @s [{"text":"✗ 靴子数据不存在","color":"red"}]

# 检查磁力效果
execute if data storage stardew:equipment boots.effects.magnetism run tellraw @s [{"text":"✓ 磁力效果存在: ","color":"green"},{"nbt":"boots.effects.magnetism","storage":"stardew:equipment"}]
execute unless data storage stardew:equipment boots.effects.magnetism run tellraw @s [{"text":"✗ 磁力效果不存在","color":"red"}]

# 检查临时数据
data modify storage stardew:temp boots_effects set from storage stardew:equipment boots.effects
execute if data storage stardew:temp boots_effects.magnetism run tellraw @s [{"text":"✓ 临时数据复制成功","color":"green"}]
execute unless data storage stardew:temp boots_effects.magnetism run tellraw @s [{"text":"✗ 临时数据复制失败","color":"red"}]

# 计数附近的物品
execute store result score #item_count sd_temp if entity @e[type=item,distance=..5]
tellraw @s [{"text":"附近5格内物品数: ","color":"yellow"},{"score":{"name":"#item_count","objective":"sd_temp"},"color":"white"}]

execute store result score #item_count_range sd_temp if entity @e[type=item,distance=1.5..3.5]
tellraw @s [{"text":"磁力范围(1.5-3.5格)内物品数: ","color":"yellow"},{"score":{"name":"#item_count_range","objective":"sd_temp"},"color":"white"}]
