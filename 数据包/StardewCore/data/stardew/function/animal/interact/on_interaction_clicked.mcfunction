# ================================================================
# 星露谷物语 - 处理交互实体被点击
# ================================================================
# 用途：当玩家右键interaction实体时触发
# @s = interaction实体
# ================================================================

# 只处理右键（interaction），忽略左键（attack）
data remove entity @s attack

# 检查是否有右键交互数据
execute unless data entity @s interaction run return 0

# 找到对应的动物
execute as @e[type=#stardew:animals,tag=stardew.animal,distance=..2,limit=1,sort=nearest] if score @s stardew.animal.id = @e[type=interaction,tag=stardew.animal.interaction,limit=1,sort=nearest] stardew.animal.id run tag @s add stardew.animal.clicked

# 检查玩家手持物品类型
execute as @a[distance=..10,limit=1,sort=nearest] at @s run tag @s add temp.interacting_player

# 挤奶桶 (paper CMD 8101) - 对牛使用
execute if entity @a[tag=temp.interacting_player,nbt={SelectedItem:{id:"minecraft:paper",components:{"minecraft:custom_model_data":8101}}},distance=..10] if entity @e[type=cow,tag=stardew.animal.clicked] run tag @s add temp.tool_used
execute if entity @s[tag=temp.tool_used] as @e[type=cow,tag=stardew.animal.clicked] run function stardew:animal/interact/try_milk_cow
execute if entity @s[tag=temp.tool_used] run tag @e[tag=stardew.animal.clicked] remove stardew.animal.clicked
execute if entity @s[tag=temp.tool_used] run tag @a[tag=temp.interacting_player] remove temp.interacting_player
execute if entity @s[tag=temp.tool_used] run tag @s remove temp.tool_used
execute if entity @s[tag=temp.tool_used] run return 0

# 挤奶桶 (paper CMD 8101) - 对山羊使用（山羊使用sheep实体，type=202）
execute if entity @a[tag=temp.interacting_player,nbt={SelectedItem:{id:"minecraft:paper",components:{"minecraft:custom_model_data":8101}}},distance=..10] if entity @e[type=sheep,tag=stardew.animal.clicked,scores={stardew.animal.type=202}] run tag @s add temp.tool_used
execute if entity @s[tag=temp.tool_used] as @e[type=sheep,tag=stardew.animal.clicked,scores={stardew.animal.type=202}] run function stardew:animal/interact/try_milk_goat
execute if entity @s[tag=temp.tool_used] run tag @e[tag=stardew.animal.clicked] remove stardew.animal.clicked
execute if entity @s[tag=temp.tool_used] run tag @a[tag=temp.interacting_player] remove temp.interacting_player
execute if entity @s[tag=temp.tool_used] run tag @s remove temp.tool_used
execute if entity @s[tag=temp.tool_used] run return 0

# 剪刀 (paper CMD 8100) - 对绵羊使用（绵羊type=203，排除山羊type=202）
execute if entity @a[tag=temp.interacting_player,nbt={SelectedItem:{id:"minecraft:paper",components:{"minecraft:custom_model_data":8100}}},distance=..10] if entity @e[type=sheep,tag=stardew.animal.clicked,scores={stardew.animal.type=203}] run tag @s add temp.tool_used
execute if entity @s[tag=temp.tool_used] as @e[type=sheep,tag=stardew.animal.clicked,scores={stardew.animal.type=203}] run function stardew:animal/interact/try_shear_sheep
execute if entity @s[tag=temp.tool_used] run tag @e[tag=stardew.animal.clicked] remove stardew.animal.clicked
execute if entity @s[tag=temp.tool_used] run tag @a[tag=temp.interacting_player] remove temp.interacting_player
execute if entity @s[tag=temp.tool_used] run tag @s remove temp.tool_used
execute if entity @s[tag=temp.tool_used] run return 0

# 检查玩家是否潜行+空手（抚摸）
execute as @a[tag=temp.interacting_player,predicate=stardew:is_sneaking_looking_up] at @s unless items entity @s weapon.mainhand * run tag @s add stardew.can_pet_animal

# 如果满足条件（潜行+空手），执行抚摸
execute as @e[type=#stardew:animals,tag=stardew.animal.clicked] if entity @a[tag=stardew.can_pet_animal,distance=..5] run function stardew:animal/interact/pet_animal_as_animal

# 如果不满足以上任何条件（单纯右键），显示动物信息
execute as @e[type=#stardew:animals,tag=stardew.animal.clicked] unless entity @a[tag=stardew.can_pet_animal,distance=..5] unless entity @a[tag=temp.interacting_player,nbt={SelectedItem:{id:"minecraft:paper",components:{"minecraft:custom_model_data":8101}}},distance=..5] run function stardew:animal/interact/show_animal_info

# 清除交互数据
data remove entity @s interaction

# 清除标签
tag @e[tag=stardew.animal.clicked] remove stardew.animal.clicked
tag @a[tag=stardew.can_pet_animal] remove stardew.can_pet_animal
