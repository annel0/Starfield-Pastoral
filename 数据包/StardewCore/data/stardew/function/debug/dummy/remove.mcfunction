# stardew:debug/dummy/remove.mcfunction
# 移除所有稻草人假人

# 粒子效果
execute as @e[tag=sd_dummy] at @s run particle minecraft:poof ~ ~1 ~ 0.3 0.5 0.3 0.1 30
execute as @e[tag=sd_dummy] at @s run playsound minecraft:entity.zombie.death hostile @a ~ ~ ~ 1 0.8

# 重置DPS显示
team modify sd_ui_5 suffix [{"text":"  ⚔ ","color":"gray"},{"text":"未激活","color":"dark_gray"}]

# 显示移除信息
execute if entity @e[tag=sd_dummy,limit=1] run tellraw @a [{"text":"🎯 ","color":"yellow"},{"text":"已移除所有稻草人","color":"gold"}]
execute unless entity @e[tag=sd_dummy,limit=1] run tellraw @s [{"text":"❌ ","color":"red"},{"text":"未找到稻草人","color":"gray"}]

# 杀死所有稻草人
kill @e[tag=sd_dummy]

