# 调试:检查戒指槽位和storage数据

tellraw @s [{"text":"=== 戒指槽位状态 ===","color":"gold"}]
tellraw @s [{"text":"槽位1: ","color":"white"},{"score":{"name":"@s","objective":"sd_equip_ring1"},"color":"yellow"}]
tellraw @s [{"text":"槽位2: ","color":"white"},{"score":{"name":"@s","objective":"sd_equip_ring2"},"color":"yellow"}]
tellraw @s [{"text":"槽位3: ","color":"white"},{"score":{"name":"@s","objective":"sd_equip_ring3"},"color":"yellow"}]
tellraw @s [{"text":"槽位4: ","color":"white"},{"score":{"name":"@s","objective":"sd_equip_ring4"},"color":"yellow"}]

tellraw @s [{"text":"=== Storage数据 ===","color":"gold"}]
tellraw @s [{"text":"Ring1: ","color":"white"},{"nbt":"ring1","storage":"stardew:equipment","color":"yellow"}]
tellraw @s [{"text":"Ring2: ","color":"white"},{"nbt":"ring2","storage":"stardew:equipment","color":"yellow"}]

tellraw @s [{"text":"=== 效果分数 ===","color":"gold"}]
tellraw @s [{"text":"发光: ","color":"white"},{"score":{"name":"@s","objective":"sd_glow_level"},"color":"yellow"}]
tellraw @s [{"text":"磁力: ","color":"white"},{"score":{"name":"@s","objective":"sd_magnet_level"},"color":"yellow"}]
tellraw @s [{"text":"攻击: ","color":"white"},{"score":{"name":"@s","objective":"sd_attack_bonus"},"color":"yellow"}]
tellraw @s [{"text":"防御: ","color":"white"},{"score":{"name":"@s","objective":"sd_defense"},"color":"yellow"}]
