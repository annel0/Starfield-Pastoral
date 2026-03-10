# 生成一只幼年鸡用于测试
# 快速看到幼年鸡模型

# 召唤幼年鸡（隐形，不掉落物品，不下蛋）
summon minecraft:chicken ~ ~ ~ {Tags:["stardew.animal","stardew.animal.new"],Silent:1b,Age:0,Invulnerable:1b,DeathLootTable:"stardew:empty",EggLayTime:2147483647,active_effects:[{id:"minecraft:invisibility",amplifier:0,duration:-1,show_particles:0b}],attributes:[{id:"minecraft:movement_speed",base:0.125}]}

# 初始化数据（会自动生成视觉模型）
execute as @e[type=minecraft:chicken,tag=stardew.animal.new,limit=1] run function stardew:animal/debug/init

# 设置为幼年（0天）
scoreboard players set @e[type=minecraft:chicken,tag=stardew.animal,limit=1,sort=nearest] stardew.animal.age 0

tellraw @s [{"text":"[测试] ","color":"green","bold":true},{"text":"已生成幼年鸡（带模型）","color":"white"}]
