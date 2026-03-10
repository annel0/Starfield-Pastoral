# ================================================================
# 星露谷物语 - 设置鸡蛋模型
# ================================================================
# 用途：为视觉实体设置正确的 CMD
# 调用：从 spawn_egg_at_position.mcfunction 调用

# 根据 #visual_cmd 设置模型（103=普通鸡蛋，104=大鸡蛋）
execute if score #visual_cmd stardew.temp matches 103 run data merge entity @s {item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":103}}}
execute if score #visual_cmd stardew.temp matches 104 run data merge entity @s {item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":104}}}
