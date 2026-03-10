# 生成一只成年鸡用于测试
# 快速看到成年鸡模型

# 召唤成年鸡（隐形，不掉落物品，不下蛋）
summon minecraft:chicken ~ ~ ~ {Tags:["stardew.animal","stardew.animal.new"],Silent:1b,Age:0,Invulnerable:1b,DeathLootTable:"stardew:empty",EggLayTime:2147483647,active_effects:[{id:"minecraft:invisibility",amplifier:0,duration:-1,show_particles:0b}],attributes:[{id:"minecraft:movement_speed",base:0.125}]}

# 标记为成年鸡（用于 init 识别）
tag @e[type=minecraft:chicken,tag=stardew.animal.new,limit=1] add spawn_as_adult

# 初始化数据（会自动生成视觉模型）
execute as @e[type=minecraft:chicken,tag=stardew.animal.new,limit=1] run function stardew:animal/debug/init

tellraw @s [{"text":"[测试] ","color":"green","bold":true},{"text":"已生成成年鸡（带模型）","color":"white"}]
