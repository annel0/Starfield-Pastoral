# data/stardew/functions/tree/spawn_test.mcfunction
# 用于测试：生成一棵成熟的橡树 (Type 1)

# 1. 生成逻辑实体 (Interaction)
# width/height 决定了玩家能砍/摇的范围
summon interaction ~ ~ ~ {Tags:["sd_tree","tree_oak"],width:1f,height:3f,response:1b}

# 2. 初始化数据 (对最近生成的 interaction 操作)
# 血量 30 (作为基准)
execute as @e[type=interaction,tag=sd_tree,sort=nearest,limit=1] run scoreboard players set @s sd_tree_hp 30
# 类型 1 (橡树)
execute as @e[type=interaction,tag=sd_tree,sort=nearest,limit=1] run scoreboard players set @s sd_tree_type 1
# 未摇晃
execute as @e[type=interaction,tag=sd_tree,sort=nearest,limit=1] run scoreboard players set @s sd_shaked 0

# 3. [核心修复] 生成视觉实体 (Item Display)
# y+1.9375: 防止埋在地里
# scale:[2.7, 4.5, 2.7]: 根据实测调整大小
summon item_display ~ ~1.9375 ~ {Tags:["sd_tree_vis"],item_display:"fixed",transformation:{scale:[2.7f,4.5f,2.7f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]},item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":803}}}

# 4. 生成物理碰撞 (Barrier)
# 在树根放一个屏障，防止玩家穿过树干
setblock ~ ~ ~ minecraft:barrier
setblock ~ ~1 ~ minecraft:barrier