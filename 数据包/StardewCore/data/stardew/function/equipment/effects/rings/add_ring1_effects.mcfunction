# 累加戒指槽位1的效果
# @s = 玩家

# 发光效果 (glow: 2 或 5)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.glow 1
scoreboard players operation @s sd_glow_level += #temp stardew.temp

# 磁力效果 (magnetism: 1 或 2)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.magnetism 1
scoreboard players operation @s sd_magnet_level += #temp stardew.temp

# 攻击加成 (attack: 0.1 = 10%) - 存储为整数10
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.attack 100
scoreboard players operation @s sd_attack_bonus += #temp stardew.temp

# 防御值 (defense: 1 或 5)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.defense 1
scoreboard players operation @s sd_defense += #temp stardew.temp

# 暴击率 (crit_chance: 0.1 = 10%)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.crit_chance 100
scoreboard players operation @s sd_crit_chance += #temp stardew.temp

# 暴击伤害 (crit_power: 0.1 = 10%)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.crit_power 100
scoreboard players operation @s sd_crit_power += #temp stardew.temp

# 武器速度 (weapon_speed: 0.1 = 10%)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.weapon_speed 100
scoreboard players operation @s sd_weapon_speed += #temp stardew.temp

# 击退 (knockback: 0.1 = 10%)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.knockback 100
scoreboard players operation @s sd_knockback += #temp stardew.temp

# 幸运 (luck: 1)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.luck 1
scoreboard players operation @s sd_luck += #temp stardew.temp

# 免疫 (immunity: 4)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.immunity 1
scoreboard players operation @s sd_immunity += #temp stardew.temp

# 吸血 (life_steal: 2)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.life_steal 1
scoreboard players operation @s sd_ring_life_steal += #temp stardew.temp

# 回能 (energy_steal: 4)
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.energy_steal 1
scoreboard players operation @s sd_ring_energy_steal += #temp stardew.temp

# 反伤 (thorns: 0.1 = 10%) - 存储为整数10
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.thorns 100
scoreboard players operation @s sd_ring_thorns += #temp stardew.temp

# 无敌时间延长 (protection: 0.4秒 = 8ticks) - 存储为tick数
execute store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.protection 20
scoreboard players operation @s sd_ring_protection += #temp stardew.temp
