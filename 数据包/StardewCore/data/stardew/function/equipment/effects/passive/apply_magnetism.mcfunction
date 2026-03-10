# 物品磁力效果（通用系统）
# 支持靴子、戒指等多个装备槽位的磁力叠加

# 初始化玩家的总磁力等级
scoreboard players set @s sd_magnet_level 0

# 累加靴子的磁力等级
execute if data storage stardew:temp boots_effects.magnetism store result score #boots_magnetism sd_temp run data get storage stardew:temp boots_effects.magnetism 1
execute if score #boots_magnetism sd_temp matches 1.. run scoreboard players operation @s sd_magnet_level += #boots_magnetism sd_temp

# TODO: 未来添加戒指磁力
# execute if data storage stardew:equipment ring1.effects.magnetism ...
# execute if data storage stardew:equipment ring2.effects.magnetism ...

# 保存当前玩家的位置到记分板（用于Motion计算）
execute store result score #player_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #player_y sd_temp run data get entity @s Pos[1] 1000
execute store result score #player_z sd_temp run data get entity @s Pos[2] 1000

# 根据总磁力等级应用效果
# 磁力等级1 = 2.5格范围
# 磁力等级2 = 3.5格范围  
# 磁力等级3+ = 4.5格范围
execute if score @s sd_magnet_level matches 1 as @e[type=item,distance=..2.5] at @s run function stardew:equipment/effects/passive/magnetism_pull
execute if score @s sd_magnet_level matches 2 as @e[type=item,distance=..3.5] at @s run function stardew:equipment/effects/passive/magnetism_pull
execute if score @s sd_magnet_level matches 3.. as @e[type=item,distance=..4.5] at @s run function stardew:equipment/effects/passive/magnetism_pull

# 实际的磁力拉取（先注释掉，确认上面的粒子能工作）
# execute if score @s sd_magnet_level matches 1 as @e[type=item,distance=1.5..2.5] at @s run function stardew:equipment/effects/passive/magnetism_pull
# execute if score @s sd_magnet_level matches 2 as @e[type=item,distance=1.5..3.5] at @s run function stardew:equipment/effects/passive/magnetism_pull
# execute if score @s sd_magnet_level matches 3.. as @e[type=item,distance=1.5..4.5] at @s run function stardew:equipment/effects/passive/magnetism_pull


