# ================================================================
# 星露谷物语 - 设置兔子友好度（测试用）
# ================================================================
# 用途：设置最近兔子的友好度，用于测试不同品质的产物
# 使用方法：
#   /function stardew:debug/set_rabbit_friendship {friendship:100}  # 设置为100
#   /function stardew:debug/set_rabbit_friendship {friendship:220}  # 设置为220（可产兔子脚）

# 默认值（如果没有传参数）
$scoreboard players set #target_friendship stardew.animal.temp $(friendship)

# 找到最近的兔子
tag @e[type=chicken,tag=stardew.animal,limit=1,sort=nearest] add temp.set_friendship

# 检查是否找到
execute unless entity @e[tag=temp.set_friendship] run tellraw @s [{"text":"[调试] ❌ 附近没有动物！","color":"red"}]
execute unless entity @e[tag=temp.set_friendship] run return 0

# 检查是否是兔子（type=103）
execute as @e[tag=temp.set_friendship] unless score @s stardew.animal.type matches 103 run tellraw @s [{"text":"[调试] ❌ 这不是兔子！","color":"red"}]
execute as @e[tag=temp.set_friendship] unless score @s stardew.animal.type matches 103 run tag @e[tag=temp.set_friendship] remove temp.set_friendship
execute unless entity @e[tag=temp.set_friendship] run return 0

# 设置友好度
execute as @e[tag=temp.set_friendship] run scoreboard players operation @s stardew.animal.friendship = #target_friendship stardew.animal.temp

# 显示结果
tellraw @s [{"text":"========== 设置兔子友好度 ==========","color":"gold"}]
execute as @e[tag=temp.set_friendship] run tellraw @s [{"text":"兔子ID: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"white"}]
execute as @e[tag=temp.set_friendship] run tellraw @s [{"text":"新友好度: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"white"},{"text":"/255","color":"gray"}]

# 提示品质档次
execute if score #target_friendship stardew.animal.temp matches ..149 run tellraw @s [{"text":"📦 品质: 普通","color":"white"}]
execute if score #target_friendship stardew.animal.temp matches 150..199 run tellraw @s [{"text":"📦 品质: 银星","color":"gray"}]
execute if score #target_friendship stardew.animal.temp matches 200..249 run tellraw @s [{"text":"📦 品质: 金星","color":"gold"}]
execute if score #target_friendship stardew.animal.temp matches 250.. run tellraw @s [{"text":"📦 品质: 铱星","color":"light_purple"}]

# 提示兔子脚条件
execute if score #target_friendship stardew.animal.temp matches 220.. run tellraw @s [{"text":"🐰 满足兔子脚掉落条件（友好度≥220），约10%概率","color":"aqua"}]
execute if score #target_friendship stardew.animal.temp matches ..219 run tellraw @s [{"text":"❌ 不满足兔子脚掉落条件（需要≥220）","color":"red"}]

# 清理标签
tag @e[tag=temp.set_friendship] remove temp.set_friendship

tellraw @s [{"text":"✅ 设置完成！现在可以用 /function stardew:debug/force_rabbit_produce 测试产物","color":"green"}]
