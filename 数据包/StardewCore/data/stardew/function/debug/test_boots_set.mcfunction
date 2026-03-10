# 设置测试靴子数据
data merge storage stardew:equipment {boots:{defense:5,immunity:3}}

# 标记玩家装备了靴子
scoreboard players set @s sd_equip_boots 1

tellraw @s {"text":"✓ 已设置测试靴子: defense=5, immunity=3","color":"green"}
tellraw @s {"text":"  - 防御：每受到伤害减少5点","color":"gray"}
tellraw @s {"text":"  - 免疫：Debuff持续时间减少30%","color":"gray"}
