# 靴子被动效果 - 每tick检测并应用
# 仅在装备了靴子时执行

# 清除之前的效果标记
scoreboard players set #has_speed sd_temp 0
scoreboard players set #has_jump sd_temp 0
scoreboard players set #has_fire_immunity sd_temp 0

# 读取靴子效果数据到临时存储
data remove storage stardew:temp boots_effects
execute if score @s sd_equip_boots matches 1.. run data modify storage stardew:temp boots_effects set from storage stardew:equipment boots.effects

# 速度效果 (speed: 0.5 或 1)
execute if data storage stardew:temp boots_effects.speed run function stardew:equipment/effects/passive/apply_speed

# 跳跃提升效果 (jump_boost: true)
execute if data storage stardew:temp boots_effects.jump_boost run effect give @s jump_boost 2 0 true

# 火焰免疫效果 (fire_immunity: true)
execute if data storage stardew:temp boots_effects.fire_immunity run effect give @s fire_resistance 2 0 true

# 钓鱼等级加成 (fishing: 2)
execute if data storage stardew:temp boots_effects.fishing run function stardew:equipment/effects/passive/apply_fishing

# 精灵自然回复 (nature_regeneration: true) - 每10秒触发一次
execute if data storage stardew:temp boots_effects.nature_regeneration run function stardew:equipment/effects/passive/apply_nature_regen

# 物品磁力效果 (magnetism: 2)
execute if data storage stardew:temp boots_effects.magnetism run function stardew:equipment/effects/passive/apply_magnetism
