# data/stardew/function/status/buff/regen_heal.mcfunction
# 再生效果恢复生命值

# 根据等级恢复生命值
execute if score @s sd_regen_level matches 1 run scoreboard players add @s sd_health 3
execute if score @s sd_regen_level matches 2 run scoreboard players add @s sd_health 5
execute if score @s sd_regen_level matches 3.. run scoreboard players add @s sd_health 8

# 不超过最大生命值
execute if score @s sd_health > @s sd_max_health run scoreboard players operation @s sd_health = @s sd_max_health

# 再生治疗反馈 - 治愈粒子效果（减少粒子数量和范围）
execute at @s run particle heart ~ ~1.3 ~ 0.2 0.2 0.2 0.05 2
execute at @s run particle happy_villager ~ ~1 ~ 0.25 0.3 0.25 0.05 4
execute at @s run particle end_rod ~ ~1 ~ 0.15 0.2 0.15 0.03 2

# 治疗音效（降低音量）
execute at @s run playsound entity.experience_orb.pickup player @a ~ ~ ~ 0.3 1.5
execute at @s run playsound block.amethyst_block.chime player @a ~ ~ ~ 0.2 1.8

# 显示治疗数值
execute if score @s sd_regen_level matches 1 run title @s actionbar [{"text":"💚 ","color":"green"},{"text":"再生 +3","color":"green","bold":true},{"text":" ❤","color":"red"}]
execute if score @s sd_regen_level matches 2 run title @s actionbar [{"text":"💚 ","color":"green"},{"text":"再生 +5","color":"green","bold":true},{"text":" ❤","color":"red"}]
execute if score @s sd_regen_level matches 3.. run title @s actionbar [{"text":"💚 ","color":"green"},{"text":"再生 +8","color":"green","bold":true},{"text":" ❤","color":"red"}]
