# 执行精灵祝福的回复效果
scoreboard players set @s sd_nature_regen_timer 0

# 回复5点生命
scoreboard players add @s sd_health 5

# 回复5点能量
scoreboard players add @s sd_energy 5

# 精灵主题的粒子效果
execute at @s run particle end_rod ~ ~1 ~ 0.3 0.5 0.3 0.05 10
execute at @s run particle happy_villager ~ ~1.2 ~ 0.25 0.4 0.25 0.03 8
execute at @s run particle enchant ~ ~0.5 ~ 0.4 0.3 0.4 0.5 15

# 精灵主题的音效
execute at @s run playsound block.amethyst_block.chime player @a ~ ~ ~ 0.6 1.2
execute at @s run playsound block.enchantment_table.use player @a ~ ~ ~ 0.3 1.5
