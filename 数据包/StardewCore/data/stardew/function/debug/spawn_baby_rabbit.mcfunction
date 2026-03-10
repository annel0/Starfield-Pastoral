# ================================================================
# 星露谷物语 - 召唤幼年兔子（测试用）
# ================================================================
# 用途：在玩家位置召唤一只幼年兔子

# 在玩家前方召唤静音鸡作为逻辑实体（兔子使用鸡的AI，正常速度）
summon minecraft:chicken ^ ^ ^2 {Tags:["stardew.animal","stardew.animal.new"],Silent:1b,Age:0,Invulnerable:1b,DeathLootTable:"stardew:empty",EggLayTime:2147483647,active_effects:[{id:"minecraft:invisibility",amplifier:0,duration:-1,show_particles:0b}],attributes:[{id:"minecraft:movement_speed",base:0.25}]}

# 初始化兔子
execute as @e[type=chicken,tag=stardew.animal.new,limit=1] run function stardew:animal/core/init_animal

# 设置为兔子类型（103）
execute as @e[type=chicken,tag=stardew.animal.new,limit=1] run scoreboard players set @s stardew.animal.type 103

# 设置年龄为幼年（0天）
execute as @e[type=chicken,tag=stardew.animal.new,limit=1] run scoreboard players set @s stardew.animal.age 0

# 生成视觉模型
execute as @e[type=chicken,tag=stardew.animal.new,limit=1] run function stardew:animal/visual/spawn_visual

# 移除new标签
tag @e[type=chicken,tag=stardew.animal.new] remove stardew.animal.new

tellraw @s [{"text":"[调试] 已召唤幼年兔子","color":"green"}]
