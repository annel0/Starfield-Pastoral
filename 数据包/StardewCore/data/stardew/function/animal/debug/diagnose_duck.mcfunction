# ================================================================
# 详细诊断附近的鸭子
# ================================================================
# 用途：获取最近鸭子的完整信息

# 找到最近的鸭子
execute as @e[type=chicken,tag=stardew.animal,limit=1,sort=nearest] run tag @s add temp.diagnose

tellraw @a [{"text":"========== 鸭子诊断报告 ==========","color":"gold","bold":true}]

# 检查是否找到鸭子
execute unless entity @e[tag=temp.diagnose] run tellraw @a [{"text":"❌ 附近没有找到动物实体","color":"red"}]
execute if entity @e[tag=temp.diagnose] run tellraw @a [{"text":"✓ 找到动物实体","color":"green"}]

# 显示动物类型
execute as @e[tag=temp.diagnose] run tellraw @a [{"text":"类型ID: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.type"},"color":"white"},{"text":" (102=鸭子)","color":"gray"}]

# 显示年龄
execute as @e[tag=temp.diagnose] run tellraw @a [{"text":"年龄: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.age"},"color":"white"},{"text":" 天 (5天成熟)","color":"gray"}]

# 显示ID
execute as @e[tag=temp.diagnose] run tellraw @a [{"text":"动物ID: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"white"}]

# 保存ID用于后续检查
execute as @e[tag=temp.diagnose] run scoreboard players operation #diag_id stardew.animal.temp = @s stardew.animal.id

tellraw @a [{"text":"","color":"yellow"}]

# 检查幼年鸡模型（chicken_baby，type=102的）
execute store result score #baby_duck_count stardew.animal.temp as @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #diag_id stardew.animal.temp if score @s stardew.animal.type matches 102 run return 1
execute if score #baby_duck_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"✓ 找到幼年鸭AJ模型 (chicken_baby)","color":"green"}]
execute unless score #baby_duck_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"❌ 没有找到幼年鸭AJ模型","color":"red"}]

# 检查成年鸭模型
execute store result score #adult_duck_count stardew.animal.temp as @e[tag=aj.duck.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #diag_id stardew.animal.temp run return 1
execute if score #adult_duck_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"✓ 找到成年鸭AJ模型 (duck)","color":"green"}]
execute unless score #adult_duck_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"❌ 没有找到成年鸭AJ模型","color":"red"}]

# 检查旧的item_display模型
execute store result score #old_visual_count stardew.animal.temp as @e[type=item_display,tag=stardew.animal.visual] if score @s stardew.animal.id = #diag_id stardew.animal.temp run return 1
execute if score #old_visual_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"⚠ 找到旧的item_display模型（应该清理）","color":"gold"}]

tellraw @a [{"text":"","color":"yellow"}]

# 给出建议
execute as @e[tag=temp.diagnose] if score @s stardew.animal.type matches 102 if score @s stardew.animal.age matches 5.. if score #baby_duck_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"💡 诊断: 这是一只卡住的鸭子！","color":"aqua"},{"text":"\n   年龄已达标但还是幼年模型","color":"white"}]

execute as @e[tag=temp.diagnose] if score @s stardew.animal.type matches 102 if score @s stardew.animal.age matches ..4 run tellraw @a [{"text":"💡 诊断: 鸭子还未成熟","color":"aqua"},{"text":"\n   需要等到5天才能长大","color":"white"}]

execute as @e[tag=temp.diagnose] if score @s stardew.animal.type matches 102 if score @s stardew.animal.age matches 5.. unless score #baby_duck_count stardew.animal.temp matches 1.. unless score #adult_duck_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"💡 诊断: 鸭子模型丢失！","color":"red"},{"text":"\n   需要重新生成模型","color":"white"}]

execute as @e[tag=temp.diagnose] unless score @s stardew.animal.type matches 102 run tellraw @a [{"text":"❌ 这不是鸭子！","color":"red"},{"text":"\n   类型ID不是102","color":"white"}]

execute if score #old_visual_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"⚠ 建议: 运行清理命令","color":"gold"},{"text":"\n   /function stardew:animal/visual/cleanup_duck_visual","color":"white"}]

tellraw @a [{"text":"==================================","color":"gold","bold":true}]

# 清理
tag @e remove temp.diagnose
