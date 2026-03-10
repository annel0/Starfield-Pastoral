# data/stardew/function/utility/tapper/place_tapper.mcfunction
# 在树上放置树液提取器
# 执行者: 树的interaction实体 (@s)
# 上下文: 玩家手持树液提取器右键树

# 0. 检查树是否成熟
execute unless score @s sd_crop_age matches 28.. run tellraw @a[tag=sd_placing_tapper,limit=1] {"text":"只能在成熟的树上放置提取器！","color":"red"}
execute unless score @s sd_crop_age matches 28.. run tag @a[tag=sd_placing_tapper] remove sd_placing_tapper
execute unless score @s sd_crop_age matches 28.. run return 1

# 1. 检查树是否已有提取器
execute if score @s sd_tapper_state matches 1.. run tellraw @a[tag=sd_placing_tapper,limit=1] {"text":"这棵树已经有提取器了！","color":"red"}
execute if score @s sd_tapper_state matches 1.. run tag @a[tag=sd_placing_tapper] remove sd_placing_tapper
execute if score @s sd_tapper_state matches 1.. run return 1

# 2. 固定放在树的南侧 (Z+方向)，顺时针旋转90°
# 提取器统一朝向，固定在树干南侧中央，便于位置调整
# 坐标：Y从0.8改为0.5(-0.3)，Z从0.5改为0(-0.5)
execute at @s run summon minecraft:item_display ~0.5 ~0.5 ~0 {Tags:["sd_utility","sd_tapper","sd_tapper_visual","init_tapper"],transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.9f,0.9f,0.9f]},item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":109}}}

# 3. 设置固定旋转角度
scoreboard players set #rotation sd_temp 90

# 4. 初始化提取器数据（在树的interaction实体上）
tag @s add sd_has_tapper
scoreboard players set @s sd_tapper_state 0
scoreboard players set @s sd_tapper_timer 0
scoreboard players set @s sd_tapper_max_time 0

# 5. 根据树的类型确定产物并开始工作
# 橡树(1) -> 橡树树脂, 7天(10080分钟)
execute if score @s sd_tree_type matches 1 run scoreboard players set @s sd_tapper_type 1
execute if score @s sd_tree_type matches 1 run scoreboard players set @s sd_tapper_max_time 10080

# 枫树(2) -> 枫糖浆, 9天(12960分钟)
execute if score @s sd_tree_type matches 2 run scoreboard players set @s sd_tapper_type 2
execute if score @s sd_tree_type matches 2 run scoreboard players set @s sd_tapper_max_time 12960

# 松树(3) -> 松焦油, 5天(7200分钟)
execute if score @s sd_tree_type matches 3 run scoreboard players set @s sd_tapper_type 3
execute if score @s sd_tree_type matches 3 run scoreboard players set @s sd_tapper_max_time 7200

# 桃花心木(4) -> 树液, 1天(1440分钟)
execute if score @s sd_tree_type matches 4 run scoreboard players set @s sd_tapper_type 4
execute if score @s sd_tree_type matches 4 run scoreboard players set @s sd_tapper_max_time 1440

# 6. 开始工作
scoreboard players set @s sd_tapper_state 1
scoreboard players operation @s sd_tapper_last_time = Global sd_time
scoreboard players set @s sd_utility_active 1

# 7. 获取视觉实体的UUID作为唯一标识，并同步到树实体
execute store result score @e[tag=init_tapper,limit=1] sd_tapper_id run data get entity @e[tag=init_tapper,limit=1] UUID[0]
execute as @e[tag=init_tapper] at @s run scoreboard players operation @e[type=interaction,tag=sd_tree,distance=..2,limit=1] sd_tapper_id = @s sd_tapper_id

# 8. 召唤产物展示（统一固定旋转角度90°）
# 产物Y坐标：0.7（原0.2+0.5）
# 橡树树脂 (CMD 840)
execute if score @s sd_tree_type matches 1 as @e[tag=init_tapper] at @s run summon minecraft:item_display ~ ~0.7 ~ {Tags:["sd_tapper_product","sd_current_product"],transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.4f,0.4f,0.4f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":840}}}

# 枫糖浆 (CMD 841)
execute if score @s sd_tree_type matches 2 as @e[tag=init_tapper] at @s run summon minecraft:item_display ~ ~0.7 ~ {Tags:["sd_tapper_product","sd_current_product"],transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.4f,0.4f,0.4f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":841}}}

# 松焦油 (CMD 842)
execute if score @s sd_tree_type matches 3 as @e[tag=init_tapper] at @s run summon minecraft:item_display ~ ~0.7 ~ {Tags:["sd_tapper_product","sd_current_product"],transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.4f,0.4f,0.4f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":842}}}

# 树液 (CMD 9004)
execute if score @s sd_tree_type matches 4 as @e[tag=init_tapper] at @s run summon minecraft:item_display ~ ~0.7 ~ {Tags:["sd_tapper_product","sd_current_product"],transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.4f,0.4f,0.4f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":9004}}}

execute as @e[tag=init_tapper] run scoreboard players operation @e[tag=sd_current_product,limit=1,sort=nearest] sd_tapper_id = @s sd_tapper_id

# 9. 召唤时间文本显示
# 文本Y坐标：0.8（原0.6+0.2）
execute as @e[tag=init_tapper] at @s run summon minecraft:text_display ~ ~0.8 ~ {Tags:["sd_tapper_time","sd_current_time"],text:'{"text":"计算中...","color":"yellow"}',billboard:"center",alignment:"center",background:0}
execute as @e[tag=init_tapper] run scoreboard players operation @e[tag=sd_current_time,limit=1,sort=nearest] sd_tapper_id = @s sd_tapper_id

# 10. 播放放置音效
execute at @s run playsound minecraft:block.wood.place block @a ~ ~ ~ 1 0.9

# 11. 消耗物品（非创造模式）
execute as @a[tag=sd_placing_tapper,limit=1,gamemode=!creative,gamemode=!spectator] run item modify entity @s weapon.mainhand stardew:consume_one

# 11.5 初始化视觉实体的高亮系统
execute as @e[tag=init_tapper,type=item_display] run function stardew:utility/init_highlight

# 12. 初始化完成
tag @e[tag=init_tapper] remove init_tapper
tag @e[tag=sd_current_product] remove sd_current_product
tag @e[tag=sd_current_time] remove sd_current_time

# 13. 清除标记
tag @a[tag=sd_placing_tapper] remove sd_placing_tapper
