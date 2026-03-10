# ================================================================
# 扫描所有戒指槽位并累加效果
# ================================================================
# @s = 玩家
# 每tick调用,扫描4个戒指槽位并累加所有效果

# 重置所有戒指相关的效果分数
scoreboard players set @s sd_glow_level 0
scoreboard players set @s sd_magnet_level 0
scoreboard players set @s sd_ring_life_steal 0
scoreboard players set @s sd_ring_energy_steal 0
scoreboard players set @s sd_ring_thorns 0
scoreboard players set @s sd_ring_protection 0

# 重置装备属性(会在后面重新计算)
scoreboard players set @s sd_defense 0
scoreboard players set @s sd_immunity 0
scoreboard players set @s sd_attack_bonus 0
scoreboard players set @s sd_crit_chance 0
scoreboard players set @s sd_crit_power 0
scoreboard players set @s sd_weapon_speed 0
scoreboard players set @s sd_knockback 0
scoreboard players set @s sd_luck 0

# 扫描4个戒指槽位,累加效果
execute if score @s sd_equip_ring1 matches 1.. run function stardew:equipment/effects/rings/add_ring1_effects
execute if score @s sd_equip_ring2 matches 1.. run function stardew:equipment/effects/rings/add_ring2_effects
execute if score @s sd_equip_ring3 matches 1.. run function stardew:equipment/effects/rings/add_ring3_effects
execute if score @s sd_equip_ring4 matches 1.. run function stardew:equipment/effects/rings/add_ring4_effects

# 【新增】扫描主手武器的防御值
function stardew:equipment/effects/apply_weapon_defense

# 应用发光和磁力效果(如果有)
execute if score @s sd_glow_level matches 1.. run function stardew:equipment/effects/rings/apply_glow
execute if score @s sd_magnet_level matches 1.. run function stardew:equipment/effects/passive/apply_magnetism
