# ================================================================
# 诊断兔子状态
# ================================================================

tellraw @a [{"text":"========== 兔子诊断 ==========","color":"gold"}]

# 找到最近的兔子
tag @e[type=chicken,tag=stardew.animal,limit=1,sort=nearest] add temp.diagnose_rabbit

# 检查是否找到
execute unless entity @e[tag=temp.diagnose_rabbit] run tellraw @a [{"text":"❌ 未找到兔子！","color":"red"}]
execute unless entity @e[tag=temp.diagnose_rabbit] run return 0

# 显示基础信息
execute as @e[tag=temp.diagnose_rabbit] run tellraw @a [{"text":"动物ID: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"white"}]
execute as @e[tag=temp.diagnose_rabbit] run tellraw @a [{"text":"类型ID: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.type"},"color":"white"},{"text":" (103=兔子)","color":"gray"}]
execute as @e[tag=temp.diagnose_rabbit] run tellraw @a [{"text":"年龄: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.age"},"color":"white"},{"text":"天","color":"white"}]
execute as @e[tag=temp.diagnose_rabbit] run tellraw @a [{"text":"友好度: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"white"}]
execute as @e[tag=temp.diagnose_rabbit] run tellraw @a [{"text":"心情: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.mood"},"color":"white"}]

tellraw @a [{"text":"","color":"gray"}]

# 保存ID用于检查模型
execute as @e[tag=temp.diagnose_rabbit] run scoreboard players operation #diag_id stardew.animal.temp = @s stardew.animal.id

# 检查成年兔模型
execute store result score #adult_rabbit_count stardew.animal.temp if entity @e[tag=aj.rabbit.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #diag_id stardew.animal.temp
tellraw @a [{"text":"成年兔模型数量: ","color":"aqua"},{"score":{"name":"#adult_rabbit_count","objective":"stardew.animal.temp"},"color":"white"}]

# 检查幼年兔模型（rabbit_baby，type=103）
execute store result score #baby_rabbit_count stardew.animal.temp as @e[tag=aj.rabbit_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #diag_id stardew.animal.temp if score @s stardew.animal.type matches 103 run return 1
tellraw @a [{"text":"幼年兔模型数量: ","color":"aqua"},{"score":{"name":"#baby_rabbit_count","objective":"stardew.animal.temp"},"color":"white"}]

tellraw @a [{"text":"","color":"gray"}]

# 诊断结果
execute as @e[tag=temp.diagnose_rabbit] if score @s stardew.animal.type matches 103 if score @s stardew.animal.age matches 5.. if score #baby_rabbit_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"💡 诊断: 这是一只卡住的兔子！","color":"aqua"},{"text":"\n   年龄已达标但还是幼年模型","color":"white"}]

execute as @e[tag=temp.diagnose_rabbit] if score @s stardew.animal.type matches 103 if score @s stardew.animal.age matches ..4 run tellraw @a [{"text":"💡 诊断: 兔子还未成熟","color":"aqua"},{"text":"\n   需要等到5天才能长大","color":"white"}]

execute as @e[tag=temp.diagnose_rabbit] if score @s stardew.animal.type matches 103 if score @s stardew.animal.age matches 5.. unless score #baby_rabbit_count stardew.animal.temp matches 1.. unless score #adult_rabbit_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"💡 诊断: 兔子模型丢失！","color":"red"},{"text":"\n   需要重新生成模型","color":"white"}]

execute as @e[tag=temp.diagnose_rabbit] unless score @s stardew.animal.type matches 103 run tellraw @a [{"text":"❌ 这不是兔子！","color":"red"},{"text":"\n   类型ID不是103","color":"white"}]

tellraw @a [{"text":"================================","color":"gold"}]

# 清理标记
tag @e[tag=temp.diagnose_rabbit] remove temp.diagnose_rabbit
