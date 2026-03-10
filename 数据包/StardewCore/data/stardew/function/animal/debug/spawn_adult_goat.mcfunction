# ================================================================
# 星露谷物语 - 生成成年山羊（调试用）
# ================================================================

# 召唤山羊（使用原版羊而非山羊，避免混淆）
summon minecraft:sheep ~ ~ ~ {Tags:["stardew.animal","stardew.animal.new"],Silent:1b,Age:0,Invulnerable:1b,DeathLootTable:"stardew:empty",active_effects:[{id:"minecraft:invisibility",amplifier:0,duration:-1,show_particles:0b}],attributes:[{id:"minecraft:movement_speed",base:0.125}],Sheared:1b}

# 初始化山羊
execute as @e[type=sheep,tag=stardew.animal.new,limit=1] run function stardew:animal/debug/init_goat

# 设置为成年
execute as @e[type=sheep,tag=stardew.animal.new,limit=1] run scoreboard players set @s stardew.animal.age 5

# 创建交互实体
execute as @e[type=sheep,tag=stardew.animal.new,limit=1] run function stardew:animal/interact/create_interaction

# 生成视觉实体
execute as @e[type=sheep,tag=stardew.animal.new,limit=1] at @s run function stardew:animal/animated_java/summon_goat

# 移除新标签
tag @e[type=sheep,tag=stardew.animal.new] remove stardew.animal.new

tellraw @s ["",{"text":"[调试] ","color":"gold","bold":true},{"text":"已生成成年山羊","color":"yellow"}]
