# 生成一只成年牛用于测试
# 快速看到成年牛模型

# 召唤成年牛（隐形，不掉落物品）
summon minecraft:cow ~ ~ ~ {Tags:["stardew.animal","stardew.animal.new"],Silent:1b,Age:0,Invulnerable:1b,DeathLootTable:"stardew:empty",active_effects:[{id:"minecraft:invisibility",amplifier:0,duration:-1,show_particles:0b}],attributes:[{id:"minecraft:movement_speed",base:0.125}]}

# 标记为成年牛（用于 init 识别）
tag @e[type=minecraft:cow,tag=stardew.animal.new,limit=1] add spawn_as_adult

# 初始化数据（会自动生成视觉模型）
execute as @e[type=minecraft:cow,tag=stardew.animal.new,limit=1] run function stardew:animal/debug/init_cow

tellraw @s [{"text":"[测试] ","color":"green","bold":true},{"text":"已生成成年牛（带模型）","color":"white"}]
