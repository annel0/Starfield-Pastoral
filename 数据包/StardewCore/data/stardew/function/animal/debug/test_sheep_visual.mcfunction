# ================================================================
# 测试绵羊视觉模型生成
# ================================================================

tellraw @s [{"text":"[诊断] 开始测试绵羊视觉生成...","color":"yellow"}]

# 检查是否有绵羊实体
execute store result score #sheep_count stardew.animal.temp if entity @e[type=sheep,tag=stardew.animal,distance=..10]
tellraw @s [{"text":"[诊断] 找到绵羊数量: ","color":"gray"},{"score":{"name":"#sheep_count","objective":"stardew.animal.temp"},"color":"white"}]

# 检查绵羊的type值
execute as @e[type=sheep,tag=stardew.animal,limit=1,sort=nearest,distance=..10] store result score #test_type stardew.animal.temp run scoreboard players get @s stardew.animal.type
tellraw @s [{"text":"[诊断] 绵羊type值: ","color":"gray"},{"score":{"name":"#test_type","objective":"stardew.animal.temp"},"color":"white"}]

# 检查绵羊的age值
execute as @e[type=sheep,tag=stardew.animal,limit=1,sort=nearest,distance=..10] store result score #test_age stardew.animal.temp run scoreboard players get @s stardew.animal.age
tellraw @s [{"text":"[诊断] 绵羊age值: ","color":"gray"},{"score":{"name":"#test_age","objective":"stardew.animal.temp"},"color":"white"}]

# 检查是否有AJ模型
execute store result score #aj_sheep_count stardew.animal.temp if entity @e[tag=aj.sheep.root,distance=..10]
tellraw @s [{"text":"[诊断] 成年绵羊AJ模型数量: ","color":"gray"},{"score":{"name":"#aj_sheep_count","objective":"stardew.animal.temp"},"color":"aqua"}]

execute store result score #aj_baby_count stardew.animal.temp if entity @e[tag=aj.sheep_baby.root,distance=..10]
tellraw @s [{"text":"[诊断] 幼年绵羊AJ模型数量: ","color":"gray"},{"score":{"name":"#aj_baby_count","objective":"stardew.animal.temp"},"color":"aqua"}]

# 尝试手动生成模型
tellraw @s [{"text":"[诊断] 尝试手动生成模型...","color":"yellow"}]
execute as @e[type=sheep,tag=stardew.animal,limit=1,sort=nearest,distance=..10] at @s run function stardew:animal/visual/spawn_visual

# 再次检查
execute store result score #aj_sheep_count stardew.animal.temp if entity @e[tag=aj.sheep.root,distance=..10]
tellraw @s [{"text":"[诊断] 生成后成年绵羊AJ模型数量: ","color":"gray"},{"score":{"name":"#aj_sheep_count","objective":"stardew.animal.temp"},"color":"gold"}]

execute store result score #aj_baby_count stardew.animal.temp if entity @e[tag=aj.sheep_baby.root,distance=..10]
tellraw @s [{"text":"[诊断] 生成后幼年绵羊AJ模型数量: ","color":"gray"},{"score":{"name":"#aj_baby_count","objective":"stardew.animal.temp"},"color":"gold"}]

tellraw @s [{"text":"[诊断] 测试完成！","color":"green"}]
