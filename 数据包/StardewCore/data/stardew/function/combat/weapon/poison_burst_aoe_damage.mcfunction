# AOE伤害应用
# 所有在爆炸范围内的敌人都受到相同的总伤害（基础+引爆）

# 应用总伤害
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #total_damage sd_temp

# 显示伤害数字
execute store result storage stardew:temp damage int 1 run scoreboard players get #total_damage sd_temp
data modify storage stardew:temp icon set value "💥"
data modify storage stardew:temp color set value "#32CD32"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 如果这个敌人也有毒性，清除它（避免重复引爆）
execute if entity @s[tag=sd_poisoned] run scoreboard players set @s sd_poison_damage 0
execute if entity @s[tag=sd_poisoned] run scoreboard players set @s sd_poison_timer 0
execute if entity @s[tag=sd_poisoned] run tag @s remove sd_poisoned

# 视觉效果
damage @s 0 minecraft:generic by @p
particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.5 8 force
particle minecraft:item_slime ~ ~1 ~ 0.3 0.5 0.3 0.1 15 force

# 音效
playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1 0.8
