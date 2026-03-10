# ================================================================
# 星露谷物语 - 同步单个视觉实体
# ================================================================
# 用途：使用Motion平滑同步视觉模型到逻辑实体
# @s = 视觉实体 (item_display)

# 保存当前视觉实体的ID
scoreboard players operation #sync_id stardew.animal.temp = @s stardew.animal.id

# 获取动物类型
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp run scoreboard players operation #animal_type stardew.animal.temp = @s stardew.animal.type

# 跳过鸡（101）、鸭（102）和兔（103），它们使用 Animated Java 系统
execute if score #animal_type stardew.animal.temp matches 101..103 run return 0

# 保存目标动物的位置（*1000）
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp store result score #target_x stardew.animal.temp run data get entity @s Pos[0] 1000
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp store result score #target_y stardew.animal.temp run data get entity @s Pos[1] 1000
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp store result score #target_z stardew.animal.temp run data get entity @s Pos[2] 1000

# Y坐标抬高0.5格
scoreboard players add #target_y stardew.animal.temp 500

# 保存视觉实体当前位置（*1000）
execute store result score #visual_x stardew.animal.temp run data get entity @s Pos[0] 1000
execute store result score #visual_y stardew.animal.temp run data get entity @s Pos[1] 1000
execute store result score #visual_z stardew.animal.temp run data get entity @s Pos[2] 1000

# 计算距离差
scoreboard players operation #dx stardew.animal.temp = #target_x stardew.animal.temp
scoreboard players operation #dx stardew.animal.temp -= #visual_x stardew.animal.temp

scoreboard players operation #dy stardew.animal.temp = #target_y stardew.animal.temp
scoreboard players operation #dy stardew.animal.temp -= #visual_y stardew.animal.temp

scoreboard players operation #dz stardew.animal.temp = #target_z stardew.animal.temp
scoreboard players operation #dz stardew.animal.temp -= #visual_z stardew.animal.temp

# 检查距离是否很小，如果太近就直接tp（避免抖动）
scoreboard players operation #dist_check stardew.animal.temp = #dx stardew.animal.temp
execute if score #dist_check stardew.animal.temp matches ..-1 run scoreboard players operation #dist_check stardew.animal.temp *= #-1 stardew.animal.temp
execute if score #dist_check stardew.animal.temp matches ..50 run scoreboard players set #use_tp stardew.animal.temp 1
execute if score #dist_check stardew.animal.temp matches 51.. run scoreboard players set #use_tp stardew.animal.temp 0

# 如果距离太近，直接tp精确对齐
execute if score #use_tp stardew.animal.temp matches 1 if score #animal_type stardew.animal.temp matches 101 as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp at @s rotated ~ 0 run tp @e[type=item_display,tag=stardew.animal.visual,limit=1,sort=nearest] ~ ~0.5 ~ ~180 0
execute if score #use_tp stardew.animal.temp matches 1 unless score #animal_type stardew.animal.temp matches 101 as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp at @s run tp @e[type=item_display,tag=stardew.animal.visual,limit=1,sort=nearest] ~ ~0.5 ~ ~ 0

# 如果距离较远，使用Motion平滑移动
# 速度 = 距离 * 0.05（快速跟随）
execute if score #use_tp stardew.animal.temp matches 0 store result entity @s Motion[0] double 0.00005 run scoreboard players get #dx stardew.animal.temp
execute if score #use_tp stardew.animal.temp matches 0 store result entity @s Motion[1] double 0.00005 run scoreboard players get #dy stardew.animal.temp
execute if score #use_tp stardew.animal.temp matches 0 store result entity @s Motion[2] double 0.00005 run scoreboard players get #dz stardew.animal.temp

# 同步旋转（仅在使用Motion时需要单独同步）
execute if score #use_tp stardew.animal.temp matches 0 if score #animal_type stardew.animal.temp matches 101 as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp run function stardew:animal/visual/sync_rotation_chicken
execute if score #use_tp stardew.animal.temp matches 0 unless score #animal_type stardew.animal.temp matches 101 as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp store result entity @e[type=item_display,tag=stardew.animal.visual,limit=1,sort=nearest] Rotation[0] float 1 run data get entity @s Rotation[0]

# 检查模型更新
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp run function stardew:animal/visual/check_model_update
