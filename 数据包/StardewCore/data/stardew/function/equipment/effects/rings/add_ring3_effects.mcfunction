# 累加戒指槽位3的效果
# @s = 玩家

execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.glow 1
scoreboard players operation @s sd_glow_level += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.magnetism 1
scoreboard players operation @s sd_magnet_level += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.attack 100
scoreboard players operation @s sd_attack_bonus += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.defense 1
scoreboard players operation @s sd_defense += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.crit_chance 100
scoreboard players operation @s sd_crit_chance += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.crit_power 100
scoreboard players operation @s sd_crit_power += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.weapon_speed 100
scoreboard players operation @s sd_weapon_speed += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.knockback 100
scoreboard players operation @s sd_knockback += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.luck 1
scoreboard players operation @s sd_luck += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.immunity 1
scoreboard players operation @s sd_immunity += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.life_steal 1
scoreboard players operation @s sd_ring_life_steal += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.energy_steal 1
scoreboard players operation @s sd_ring_energy_steal += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.thorns 100
scoreboard players operation @s sd_ring_thorns += #temp stardew.temp
execute store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.protection 20
scoreboard players operation @s sd_ring_protection += #temp stardew.temp
