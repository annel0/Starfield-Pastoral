# combat/init_slime_split.mcfunction
# 初始化分裂出的小史莱姆（从storage中读取血量和攻击力）

# 从storage读取父史莱姆的一半属性
execute store result score @s sd_monster_hp run data get storage stardew:temp slime_hp
execute store result score @s sd_monster_max_hp run data get storage stardew:temp slime_hp
execute store result score @s sd_monster_damage run data get storage stardew:temp slime_atk

# 确保最小值（至少1点）
execute if score @s sd_monster_hp matches ..0 run scoreboard players set @s sd_monster_hp 1
execute if score @s sd_monster_max_hp matches ..0 run scoreboard players set @s sd_monster_max_hp 1
execute if score @s sd_monster_damage matches ..0 run scoreboard players set @s sd_monster_damage 1

# 移除临时标签，添加正常初始化流程
tag @s remove sd_slime_split
tag @s add sd_monster_init

# 执行正常的初始化
function stardew:combat/init_monster
