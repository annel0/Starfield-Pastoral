# stardew:debug/dummy/on_damage.mcfunction
# 当稻草人受到伤害时触发

# 计算本次伤害值
scoreboard players operation #damage_dealt sd_temp = @s sd_dummy_last_hp
scoreboard players operation #damage_dealt sd_temp -= @s sd_monster_hp

# 累加总伤害
scoreboard players operation @s sd_dummy_total_dmg += #damage_dealt sd_temp

# 重置血量到满血
scoreboard players set @s sd_monster_hp 999999
