# data/stardew/function/equipment/test/reset_equipment.mcfunction
# [执行者: 玩家] 重置所有装备数据

scoreboard players set @s sd_equip_boots 0
scoreboard players set @s sd_equip_ring1 0
scoreboard players set @s sd_equip_ring2 0
scoreboard players set @s sd_equip_ring3 0
scoreboard players set @s sd_equip_ring4 0
scoreboard players set @s sd_unlock_ring3 0
scoreboard players set @s sd_unlock_ring4 0

scoreboard players set @s sd_equip_boots_cmd 0
scoreboard players set @s sd_equip_ring1_cmd 0
scoreboard players set @s sd_equip_ring2_cmd 0
scoreboard players set @s sd_equip_ring3_cmd 0
scoreboard players set @s sd_equip_ring4_cmd 0

tellraw @s {"text":"已重置装备数据！","color":"yellow"}
