# data/stardew/function/utility/keg/start_process.mcfunction
# 启动小桶加工流程 - 通用宏函数
# 参数:
#   $output_cmd - 输出产物的CMD
#   $type - 加工类型编号
#   $time - 需要的总时间(分钟)
# 注意: 材料检测和扣除由调用方完成

# 1. 找到被标记的小桶实体
execute as @e[type=interaction,tag=sd_interacting_keg,distance=..5,limit=1] run tag @s add sd_current_keg
execute as @e[type=item_display,tag=sd_keg_visual,distance=..5,limit=1,sort=nearest] run tag @s add sd_current_keg_visual

# 2. 设置小桶状态为工作中（使用增量计时）
scoreboard players set @e[tag=sd_current_keg] sd_keg_state 1
$scoreboard players set @e[tag=sd_current_keg] sd_keg_type $(type)
# 初始化已工作时间为 0，设置需要的总时间
scoreboard players set @e[tag=sd_current_keg] sd_keg_timer 0
$scoreboard players set @e[tag=sd_current_keg] sd_keg_max_time $(time)
# 记录上次更新时间为当前游戏时间（用于支持时间跳过）
execute as @e[tag=sd_current_keg] run scoreboard players operation @s sd_keg_last_time = Global sd_time
# 标记为通用实用设施活跃（用于 new_day 统一奖励）
execute as @e[tag=sd_current_keg] run scoreboard players set @s sd_utility_active 1

# 3. 视觉实体保持原样(小桶不改变模型)
# 4. 获取视觉实体的UUID前半部分作为唯一标识，并同步到 interaction 和 visual 实体
execute store result score @e[tag=sd_current_keg_visual] sd_keg_id run data get entity @e[tag=sd_current_keg_visual,limit=1] UUID[0]
execute as @e[tag=sd_current_keg_visual] at @s run scoreboard players operation @e[type=interaction,tag=sd_current_keg,distance=..2,limit=1] sd_keg_id = @s sd_keg_id

# 4.5 获取旋转角度
execute as @e[tag=sd_current_keg_visual] run scoreboard players operation #rotation sd_temp = @s sd_rotation

# 5. 在小桶上方召唤产物展示（根据旋转角度），并设置相同的ID
$execute if score #rotation sd_temp matches 0 as @e[tag=sd_current_keg_visual] at @s run summon minecraft:item_display ~ ~1.3 ~ {Tags:["sd_keg_product","sd_current_product"],transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(output_cmd)}}}
$execute if score #rotation sd_temp matches 90 as @e[tag=sd_current_keg_visual] at @s run summon minecraft:item_display ~ ~1.3 ~ {Tags:["sd_keg_product","sd_current_product"],transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(output_cmd)}}}
$execute if score #rotation sd_temp matches 180 as @e[tag=sd_current_keg_visual] at @s run summon minecraft:item_display ~ ~1.3 ~ {Tags:["sd_keg_product","sd_current_product"],transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(output_cmd)}}}
$execute if score #rotation sd_temp matches 270 as @e[tag=sd_current_keg_visual] at @s run summon minecraft:item_display ~ ~1.3 ~ {Tags:["sd_keg_product","sd_current_product"],transformation:{left_rotation:[0f,-0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(output_cmd)}}}
execute as @e[tag=sd_current_keg_visual] run scoreboard players operation @e[tag=sd_current_product,limit=1,sort=nearest] sd_keg_id = @s sd_keg_id

# 6. 召唤时间文本显示，并设置相同的ID
$execute as @e[tag=sd_current_keg_visual] at @s run summon minecraft:text_display ~ ~1.7 ~ {Tags:["sd_keg_time","sd_current_time"],text:'[{"text":"$(time)","color":"yellow"},{"text":"分钟","color":"yellow"}]',billboard:"center",alignment:"center",background:0}
execute as @e[tag=sd_current_keg_visual] run scoreboard players operation @e[tag=sd_current_time,limit=1,sort=nearest] sd_keg_id = @s sd_keg_id

# 7. 小桶不需要光源方块（注释掉）
# execute as @e[tag=sd_current_keg_visual] at @s align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:light[level=8]

# 8. 播放音效(小桶用木桶音效,不同于熔炉)
execute as @e[tag=sd_current_keg] at @s run playsound minecraft:block.barrel.open block @a ~ ~ ~ 1 0.8
execute as @e[tag=sd_current_keg] at @s run playsound minecraft:item.bottle.fill block @a ~ ~ ~ 0.8 0.9

# 9. 清除标记
tag @e[tag=sd_current_keg] remove sd_current_keg
tag @e[tag=sd_current_keg_visual] remove sd_current_keg_visual
tag @e[tag=sd_current_product] remove sd_current_product
tag @e[tag=sd_current_time] remove sd_current_time
