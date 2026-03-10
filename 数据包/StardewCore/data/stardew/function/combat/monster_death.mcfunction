# 怪物死亡

# 死亡粒子效果
particle minecraft:explosion ~ ~1 ~ 0.5 0.5 0.5 0.1 5
particle minecraft:poof ~ ~1 ~ 0.3 0.5 0.3 0.1 20

# 死亡音效
playsound minecraft:entity.generic.death hostile @a ~ ~ ~ 1 0.8

# 【戒指效果】击杀触发
# 吸血戒指: 恢复生命值
execute as @p if score @s sd_ring_life_steal matches 1.. run scoreboard players operation @s sd_health += @s sd_ring_life_steal
execute as @p if score @s sd_ring_life_steal matches 1.. run tellraw @s [{"text":"⚕ ","color":"dark_red"},{"text":"吸血 +","color":"red"},{"score":{"name":"@s","objective":"sd_ring_life_steal"},"color":"red"},{"text":" HP","color":"red"}]
# 回能戒指: 恢复能量
execute as @p if score @s sd_ring_energy_steal matches 1.. run scoreboard players operation @s sd_energy += @s sd_ring_energy_steal
execute as @p if score @s sd_ring_energy_steal matches 1.. run tellraw @s [{"text":"⚡ ","color":"aqua"},{"text":"回能 +","color":"blue"},{"score":{"name":"@s","objective":"sd_ring_energy_steal"},"color":"blue"}]

# 【史莱姆特殊处理】禁用原版分裂，手动生成小史莱姆并初始化
# Size:2 史莱姆分裂成 4个 Size:1（继承父史莱姆的一半血量和攻击力）
execute if entity @s[type=minecraft:slime,nbt={Size:2}] store result storage stardew:temp slime_hp int 0.5 run scoreboard players get @s sd_monster_max_hp
execute if entity @s[type=minecraft:slime,nbt={Size:2}] store result storage stardew:temp slime_atk int 0.5 run scoreboard players get @s sd_monster_damage
execute if entity @s[type=minecraft:slime,nbt={Size:2}] at @s run summon minecraft:slime ~0.5 ~ ~0.5 {Size:1,Tags:["sd_monster_init","sd_monster","sd_mob_slime","sd_slime_split"],DeathLootTable:"stardew:monsters/slime_medium"}
execute if entity @s[type=minecraft:slime,nbt={Size:2}] at @s run summon minecraft:slime ~-0.5 ~ ~0.5 {Size:1,Tags:["sd_monster_init","sd_monster","sd_mob_slime","sd_slime_split"],DeathLootTable:"stardew:monsters/slime_medium"}
execute if entity @s[type=minecraft:slime,nbt={Size:2}] at @s run summon minecraft:slime ~0.5 ~ ~-0.5 {Size:1,Tags:["sd_monster_init","sd_monster","sd_mob_slime","sd_slime_split"],DeathLootTable:"stardew:monsters/slime_medium"}
execute if entity @s[type=minecraft:slime,nbt={Size:2}] at @s run summon minecraft:slime ~-0.5 ~ ~-0.5 {Size:1,Tags:["sd_monster_init","sd_monster","sd_mob_slime","sd_slime_split"],DeathLootTable:"stardew:monsters/slime_medium"}

# Size:1 史莱姆分裂成 4个 Size:0（继承父史莱姆的一半血量和攻击力）
execute if entity @s[type=minecraft:slime,nbt={Size:1}] store result storage stardew:temp slime_hp int 0.5 run scoreboard players get @s sd_monster_max_hp
execute if entity @s[type=minecraft:slime,nbt={Size:1}] store result storage stardew:temp slime_atk int 0.5 run scoreboard players get @s sd_monster_damage
execute if entity @s[type=minecraft:slime,nbt={Size:1}] at @s run summon minecraft:slime ~0.3 ~ ~0.3 {Size:0,Tags:["sd_monster_init","sd_monster","sd_mob_slime","sd_slime_split"],DeathLootTable:"stardew:monsters/slime_small"}
execute if entity @s[type=minecraft:slime,nbt={Size:1}] at @s run summon minecraft:slime ~-0.3 ~ ~0.3 {Size:0,Tags:["sd_monster_init","sd_monster","sd_mob_slime","sd_slime_split"],DeathLootTable:"stardew:monsters/slime_small"}
execute if entity @s[type=minecraft:slime,nbt={Size:1}] at @s run summon minecraft:slime ~0.3 ~ ~-0.3 {Size:0,Tags:["sd_monster_init","sd_monster","sd_mob_slime","sd_slime_split"],DeathLootTable:"stardew:monsters/slime_small"}
execute if entity @s[type=minecraft:slime,nbt={Size:1}] at @s run summon minecraft:slime ~-0.3 ~ ~-0.3 {Size:0,Tags:["sd_monster_init","sd_monster","sd_mob_slime","sd_slime_split"],DeathLootTable:"stardew:monsters/slime_small"}

# 初始化刚分裂的小史莱姆（设置血量和攻击力）
execute if entity @s[type=minecraft:slime] as @e[tag=sd_slime_split,distance=..3] run function stardew:combat/init_slime_split

# 【关键】立即移除 sd_monster 标签，防止 tick.mcfunction 继续给它加效果
tag @s remove sd_monster

# 【关键】移除抗性和生命恢复效果
effect clear @s minecraft:resistance
effect clear @s minecraft:regeneration

# 【修复】使用damage命令造成致命伤害，触发正常死亡和DeathLootTable
# damage命令会触发战利品表，而直接kill或设置Health=0不会
damage @s 9999 minecraft:generic
