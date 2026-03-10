# 累加戒指槽位2的效果
# @s = 玩家

# 发光效果
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.glow 1
scoreboard players operation @s sd_glow_level += #temp stardew.temp

# 磁力效果
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.magnetism 1
scoreboard players operation @s sd_magnet_level += #temp stardew.temp

# 攻击加成
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.attack 100
scoreboard players operation @s sd_attack_bonus += #temp stardew.temp

# 防御值
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.defense 1
scoreboard players operation @s sd_defense += #temp stardew.temp

# 暴击率
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.crit_chance 100
scoreboard players operation @s sd_crit_chance += #temp stardew.temp

# 暴击伤害
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.crit_power 100
scoreboard players operation @s sd_crit_power += #temp stardew.temp

# 武器速度
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.weapon_speed 100
scoreboard players operation @s sd_weapon_speed += #temp stardew.temp

# 击退
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.knockback 100
scoreboard players operation @s sd_knockback += #temp stardew.temp

# 幸运
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.luck 1
scoreboard players operation @s sd_luck += #temp stardew.temp

# 免疫
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.immunity 1
scoreboard players operation @s sd_immunity += #temp stardew.temp

# 吸血
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.life_steal 1
scoreboard players operation @s sd_ring_life_steal += #temp stardew.temp

# 回能
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.energy_steal 1
scoreboard players operation @s sd_ring_energy_steal += #temp stardew.temp

# 反伤
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.thorns 100
scoreboard players operation @s sd_ring_thorns += #temp stardew.temp

# 无敌时间延长
execute store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.protection 20
scoreboard players operation @s sd_ring_protection += #temp stardew.temp
