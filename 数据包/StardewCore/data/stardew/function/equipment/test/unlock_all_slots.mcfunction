# data/stardew/function/equipment/test/unlock_all_slots.mcfunction
# [执行者: 玩家] 解锁所有戒指槽位

scoreboard players set @s sd_unlock_ring3 1
scoreboard players set @s sd_unlock_ring4 1

tellraw @s {"text":"已解锁所有戒指槽位！","color":"gold"}
