# ================================================================
# 星露谷物语 - 生成幼年猪（调试用）
# ================================================================

# 召唤幼年猪实体
summon minecraft:pig ~ ~ ~ {Tags:["stardew.animal","stardew.animal.new"],Silent:1b,Age:-999999,Invulnerable:1b,DeathLootTable:"stardew:empty",active_effects:[{id:"minecraft:invisibility",amplifier:0,duration:-1,show_particles:0b}],attributes:[{id:"minecraft:movement_speed",base:0.125}]}

# 初始化数据（会自动生成视觉模型）
execute as @e[type=pig,tag=stardew.animal.new,limit=1] run function stardew:animal/debug/init_pig

# 设置为幼年（0天）
scoreboard players set @e[type=pig,tag=stardew.animal,limit=1,sort=nearest] stardew.animal.age 0

tellraw @s ["",{"text":"[调试] ","color":"gold","bold":true},{"text":"已生成幼年猪","color":"light_purple"}]