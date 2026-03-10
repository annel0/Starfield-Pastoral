# 查看玩家状态
tellraw @s {"text":"=== 玩家状态 ===","color":"aqua"}

# 生命值
tellraw @s [{"text":"生命值: ","color":"red"},{"score":{"name":"@s","objective":"sd_health"},"color":"white"},{"text":" / ","color":"gray"},{"score":{"name":"@s","objective":"sd_max_health"},"color":"white"}]

# 能量值
tellraw @s [{"text":"能量值: ","color":"gold"},{"score":{"name":"@s","objective":"sd_energy"},"color":"white"},{"text":" / ","color":"gray"},{"score":{"name":"@s","objective":"sd_max_energy"},"color":"white"}]

# 护甲值
tellraw @s [{"text":"护甲值: ","color":"gray"},{"score":{"name":"@s","objective":"sd_armor"},"color":"white"}]

# 当前装备
tellraw @s {"text":"--- 装备状态 ---","color":"yellow"}
execute if score @s sd_equip_boots matches 1.. run tellraw @s {"text":"✓ 已装备靴子","color":"green"}
execute unless score @s sd_equip_boots matches 1.. run tellraw @s {"text":"✗ 未装备靴子","color":"red"}

# 当前Debuff
tellraw @s {"text":"--- 当前效果 ---","color":"yellow"}
execute if score @s sd_debuff_poison matches 1 run tellraw @s [{"text":"中毒 ","color":"dark_green"},{"text":"剩余: ","color":"gray"},{"score":{"name":"@s","objective":"sd_poison_duration"},"color":"white"},{"text":"t (level ","color":"gray"},{"score":{"name":"@s","objective":"sd_poison_level"},"color":"white"},{"text":")","color":"gray"}]

execute if score @s sd_debuff_hunger matches 1 run tellraw @s [{"text":"饥饿 ","color":"dark_red"},{"text":"剩余: ","color":"gray"},{"score":{"name":"@s","objective":"sd_hunger_duration"},"color":"white"},{"text":"t (level ","color":"gray"},{"score":{"name":"@s","objective":"sd_hunger_level"},"color":"white"},{"text":")","color":"gray"}]

execute if score @s sd_debuff_slime matches 1 run tellraw @s [{"text":"粘液 ","color":"green"},{"text":"剩余: ","color":"gray"},{"score":{"name":"@s","objective":"sd_slime_duration"},"color":"white"},{"text":"t","color":"gray"}]

execute if score @s sd_debuff_frozen matches 1 run tellraw @s [{"text":"冰冻 ","color":"aqua"},{"text":"剩余: ","color":"gray"},{"score":{"name":"@s","objective":"sd_frozen_duration"},"color":"white"},{"text":"t","color":"gray"}]

execute if score @s sd_debuff_weakness matches 1 run tellraw @s [{"text":"虚弱 ","color":"gray"},{"text":"剩余: ","color":"gray"},{"score":{"name":"@s","objective":"sd_weakness_duration"},"color":"white"},{"text":"t","color":"gray"}]

execute unless score @s sd_debuff_poison matches 1 unless score @s sd_debuff_hunger matches 1 unless score @s sd_debuff_slime matches 1 unless score @s sd_debuff_frozen matches 1 unless score @s sd_debuff_weakness matches 1 run tellraw @s {"text":"无效果","color":"gray"}
