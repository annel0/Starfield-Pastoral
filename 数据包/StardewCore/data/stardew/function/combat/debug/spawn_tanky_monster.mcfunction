# 生成血厚的测试怪物 (500血)

# 生成一个普通僵尸，直接用标签标记血量
summon minecraft:zombie ~ ~ ~3 {Tags:["sd_monster_init","sd_monster","sd_monster_zombie","sd_hp_500"],CustomName:'{"text":"测试僵尸","color":"red","bold":true}',HandItems:[{id:"minecraft:iron_sword",count:1},{}],ArmorItems:[{id:"minecraft:iron_boots",count:1},{id:"minecraft:iron_leggings",count:1},{id:"minecraft:iron_chestplate",count:1},{id:"minecraft:iron_helmet",count:1}]}

# 直接设置自定义血量（不依赖原版 Health）
execute as @e[tag=sd_hp_500,limit=1] run scoreboard players set @s sd_monster_hp 500
execute as @e[tag=sd_hp_500,limit=1] run scoreboard players set @s sd_monster_max_hp 500
execute as @e[tag=sd_hp_500,limit=1] run scoreboard players set @s sd_monster_damage 10
execute as @e[tag=sd_hp_500,limit=1] run tag @s remove sd_hp_500

tellraw @s [{"text":"[战斗测试] ","color":"yellow"},{"text":"已生成血厚测试怪物！","color":"green"}]
tellraw @s [{"text":"  • 名称: ","color":"gray"},{"text":"测试僵尸","color":"red","bold":true}]
tellraw @s [{"text":"  • 血量: ","color":"gray"},{"text":"500 HP","color":"red"}]
tellraw @s [{"text":"  • 攻击: ","color":"gray"},{"text":"10 点伤害","color":"gold"}]
tellraw @s [{"text":"  • 特性: 血量显示在名字上方","color":"aqua"}]
