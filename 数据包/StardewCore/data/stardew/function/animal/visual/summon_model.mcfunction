# ================================================================
# 星露谷物语 - 召唤模型实体
# ================================================================
# 用途：召唤item_display模型并绑定ID
# 前置：#model_cmd 已设置
# @s = 动物逻辑实体

# 根据CMD召唤对应模型（在动物上方0.5格处）
# 移除 interpolation_duration，使用 Motion 系统实现平滑移动
execute if score #model_cmd stardew.animal.temp matches 101 run summon item_display ~ ~0.5 ~ {Tags:["stardew.animal.visual","stardew.animal.visual.new"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.0f,1.0f,1.0f]},item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":101}}}
execute if score #model_cmd stardew.animal.temp matches 102 run summon item_display ~ ~0.5 ~ {Tags:["stardew.animal.visual","stardew.animal.visual.new"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.0f,1.0f,1.0f]},item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":102}}}

# 绑定ID到视觉实体
execute as @e[type=item_display,tag=stardew.animal.visual.new,limit=1,sort=nearest] run scoreboard players operation @s stardew.animal.id = @e[type=#stardew:animals,tag=stardew.animal,limit=1,sort=nearest] stardew.animal.id

# 绑定类型
execute as @e[type=item_display,tag=stardew.animal.visual.new,limit=1,sort=nearest] run scoreboard players operation @s stardew.animal.type = #animal_type stardew.animal.temp

# 移除new标签
tag @e[tag=stardew.animal.visual.new] remove stardew.animal.visual.new
