# ================================================================
# 星露谷物语 - 生成测试鸭子
# ================================================================
# 用途：在玩家位置生成一只鸭子用于测试
# 调用：手动执行 /function stardew:animal/debug/spawn_duck

# 召唤幼年鸭子（隐形，降低移动速度到50%，不掉落物品，不下蛋）
# Age:-999999 = 永久幼年（被age_manager控制）
# EggLayTime:2147483647 = 永不下蛋（产蛋由我们的系统控制）
# active_effects:invisibility = 隐形（只显示自定义模型）
summon minecraft:chicken ~ ~ ~ {Tags:["stardew.animal","stardew.animal.new"],Silent:1b,Age:-999999,Invulnerable:1b,DeathLootTable:"stardew:empty",EggLayTime:2147483647,active_effects:[{id:"minecraft:invisibility",amplifier:0,duration:-1,show_particles:0b}],attributes:[{id:"minecraft:movement_speed",base:0.125}]}

# 初始化鸭子数据（会自动生成视觉模型）
execute as @e[type=minecraft:chicken,tag=stardew.animal.new,limit=1] run function stardew:animal/debug/init_duck

tellraw @s [{"text":"[测试] ","color":"green","bold":true},{"text":"已生成测试鸭子（带模型，移动速度50%）","color":"white"}]
