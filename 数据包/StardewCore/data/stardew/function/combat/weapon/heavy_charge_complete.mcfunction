# 蓄力重击 - 蓄力完成

# 1. 标记蓄力完成
scoreboard players set @s sd_heavy_charge_ready 1
scoreboard players set @s sd_charge_ready 1

# 2. 播放"叮"的音效（完成时的清脆声音）
playsound minecraft:block.note_block.bell player @s ~ ~ ~ 1.0 2.0
playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.8 1.5
playsound minecraft:block.anvil.land player @s ~ ~ ~ 0.5 1.5

# 3. 粒子效果爆发（减少数量）
particle minecraft:end_rod ~ ~1.5 ~ 0.3 0.3 0.3 0.1 8 force @s
particle minecraft:enchant ~ ~1.5 ~ 0.5 0.5 0.5 0.5 12 force @s
particle minecraft:crit ~ ~1.5 ~ 0.4 0.4 0.4 0.3 6 force @s

# 4. 保持缓慢效果（蓄力完成后继续按住Shift会保持，松开会自动清除）
# 不在这里清除slowness，让weapon_charge_system.mcfunction控制
