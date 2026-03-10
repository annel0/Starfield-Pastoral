# 1. 生成逻辑实体
summon interaction ~ ~ ~ {Tags:["sd_tree","tree_oak","new_tree"],width:1.2f,height:3f,response:1b,Invulnerable:1b}

# 2. 初始化数据 (展开写)
execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_tree_hp 30
execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_tree_type 1
execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_shaked 0
# 直接设为成熟年龄，防止被误认为树苗
execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_crop_age 28

# 移除标签
tag @e[tag=new_tree] remove new_tree

# 3. 视觉实体 (高度 1.25)
summon item_display ~ ~1.25 ~ {Tags:["sd_tree_vis"],item_display:"fixed",transformation:{scale:[2.7f,4.5f,2.7f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]},item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":803}}}

# 4. 物理碰撞
setblock ~ ~ ~ minecraft:barrier
setblock ~ ~1 ~ minecraft:barrier