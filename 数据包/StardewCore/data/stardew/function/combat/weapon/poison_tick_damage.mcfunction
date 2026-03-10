# 毒性每秒伤害

# 应用毒性伤害
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= @s sd_poison_damage

# 显示伤害数字（DOT效果）
execute store result storage stardew:temp damage int 1 run scoreboard players get @s sd_poison_damage
data modify storage stardew:temp icon set value "☠"
data modify storage stardew:temp color set value "#32CD32"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 视觉效果
damage @s 0 minecraft:generic
particle minecraft:damage_indicator ~ ~1 ~ 0.2 0.3 0.2 0.2 3 force

# 音效（轻微）
execute if predicate {"condition":"minecraft:random_chance","chance":0.3} run playsound minecraft:entity.spider.hurt player @a ~ ~ ~ 0.3 1.2
