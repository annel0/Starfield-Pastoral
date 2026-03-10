# 生成一只测试鸡
# 用于验证系统功能

# 召唤幼年鸡（隐形，降低移动速度到50%，不掉落物品，不下蛋）
summon minecraft:chicken ~ ~ ~ {Tags:["stardew.animal","stardew.animal.new"],Silent:1b,Age:-999999,Invulnerable:1b,DeathLootTable:"stardew:empty",EggLayTime:2147483647,active_effects:[{id:"minecraft:invisibility",amplifier:0,duration:-1,show_particles:0b}],attributes:[{id:"minecraft:movement_speed",base:0.125}]}

# 初始化数据（会自动生成视觉模型）
execute as @e[type=minecraft:chicken,tag=stardew.animal.new,limit=1] run function stardew:animal/debug/init

tellraw @s [{"text":"[测试] ","color":"green","bold":true},{"text":"已生成测试鸡（带模型，移动速度50%）","color":"white"}]
