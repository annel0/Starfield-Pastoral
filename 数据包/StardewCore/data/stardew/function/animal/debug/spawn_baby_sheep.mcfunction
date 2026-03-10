# 生成一只幼年绵羊用于测试
# 快速看到幼年绵羊模型

# 召唤幼年绵羊（隐形，不掉落物品）
summon minecraft:sheep ~ ~ ~ {Tags:["stardew.animal","stardew.animal.new"],Silent:1b,Age:0,Invulnerable:1b,DeathLootTable:"stardew:empty",active_effects:[{id:"minecraft:invisibility",amplifier:0,duration:-1,show_particles:0b}],attributes:[{id:"minecraft:movement_speed",base:0.125}],Sheared:0b}

# 初始化数据（会自动生成视觉模型）
execute as @e[type=minecraft:sheep,tag=stardew.animal.new,limit=1] run function stardew:animal/debug/init_sheep

tellraw @s [{"text":"[测试] ","color":"green","bold":true},{"text":"已生成幼年绵羊（带模型）","color":"white"}]
