# data/stardew/function/equipment/storage/create_empty_data.mcfunction
# [执行者: 玩家] 创建空装备数据

# 初始化所有槽位为空
scoreboard players set @s sd_equip_boots 0
scoreboard players set @s sd_equip_ring1 0
scoreboard players set @s sd_equip_ring2 0
scoreboard players set @s sd_equip_ring3 0
scoreboard players set @s sd_equip_ring4 0

# 初始化槽位3-4为锁定状态
scoreboard players set @s sd_unlock_ring3 0
scoreboard players set @s sd_unlock_ring4 0

# 初始化属性为0
scoreboard players set @s sd_defense 0
scoreboard players set @s sd_immunity 0
scoreboard players set @s sd_attack_bonus 0
scoreboard players set @s sd_crit_chance 0
scoreboard players set @s sd_crit_power 0
scoreboard players set @s sd_weapon_speed 0
scoreboard players set @s sd_knockback 0
scoreboard players set @s sd_glow_level 0
scoreboard players set @s sd_magnet_level 0
scoreboard players set @s sd_luck 0
