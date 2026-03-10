# data/stardew/functions/tools/charge_complete.mcfunction
# 蓄力完成时触发

# 1. 标记蓄力完成
scoreboard players set @s sd_charge_ready 1

# 2. 播放"叮"的音效（完成时的清脆声音）
execute at @s run playsound minecraft:block.note_block.bell master @s ~ ~ ~ 1.0 2.0
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 0.8 1.5

# 3. 粒子效果爆发（减少数量）
execute at @s run particle minecraft:end_rod ~ ~1.5 ~ 0.3 0.3 0.3 0.1 8 force @s
execute at @s run particle minecraft:enchant ~ ~1.5 ~ 0.5 0.5 0.5 0.5 12 force @s

# 4. 保持缓慢效果（蓄力完成后继续按住Shift会保持，松开会自动清除）
# 不在这里清除slowness，让charge_system.mcfunction控制
