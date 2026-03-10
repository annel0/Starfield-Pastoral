# data/stardew/function/utility/furnace/animate_working.mcfunction
# 熔炉工作动画 - 弹跳式拉伸效果（类似史莱姆）+ 火焰粒子
# 仅对玩家附近（16格内）且正在工作的熔炉执行（sd_furnace_state=1）

# 获取当前熔炉 interaction 的 ID（如果有的话）
execute store result score #current_id sd_furnace_id run scoreboard players get @s sd_furnace_id

# 如果没有 ID（旧熔炉），从附近的视觉实体获取并设置
execute if score #current_id sd_furnace_id matches 0 at @s as @e[type=item_display,tag=sd_furnace_visual,distance=..1.5,limit=1,sort=nearest] run scoreboard players operation #current_id sd_furnace_id = @s sd_furnace_id
execute if score #current_id sd_furnace_id matches 0 at @s run scoreboard players operation @s sd_furnace_id = @e[type=item_display,tag=sd_furnace_visual,distance=..1.5,limit=1,sort=nearest] sd_furnace_id

# 递增动画时钟（每tick+1，循环0-29，即1.5秒一个周期，更舒缓的弹跳）
scoreboard players add @s sd_anim_tick 1
execute if score @s sd_anim_tick matches 30.. run scoreboard players set @s sd_anim_tick 0

# ==========================================
# 阶段 0-7: Y轴拉伸向上，XZ轴压缩（scale: 1.2→1.45 Y, 1.2→1.05 XZ）
# ==========================================
# 根据旋转角度设置不同的 left_rotation
execute if score @s sd_anim_tick matches 0 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 0 run data merge entity @s {interpolation_duration:8,start_interpolation:0,transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,-0.1f,0f],scale:[1.05f,1.45f,1.05f]}}
execute if score @s sd_anim_tick matches 0 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 90 run data merge entity @s {interpolation_duration:8,start_interpolation:0,transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,-0.1f,0f],scale:[1.05f,1.45f,1.05f]}}
execute if score @s sd_anim_tick matches 0 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 180 run data merge entity @s {interpolation_duration:8,start_interpolation:0,transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,-0.1f,0f],scale:[1.05f,1.45f,1.05f]}}
execute if score @s sd_anim_tick matches 0 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 270 run data merge entity @s {interpolation_duration:8,start_interpolation:0,transformation:{left_rotation:[0f,-0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,-0.1f,0f],scale:[1.05f,1.45f,1.05f]}}

# ==========================================
# 阶段 8-14: Y轴恢复，XZ轴拉伸（scale: 1.45→1.05 Y, 1.05→1.35 XZ）
# ==========================================
execute if score @s sd_anim_tick matches 8 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 0 run data merge entity @s {interpolation_duration:7,start_interpolation:0,transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.05f,0f],scale:[1.35f,1.05f,1.35f]}}
execute if score @s sd_anim_tick matches 8 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 90 run data merge entity @s {interpolation_duration:7,start_interpolation:0,transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.05f,0f],scale:[1.35f,1.05f,1.35f]}}
execute if score @s sd_anim_tick matches 8 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 180 run data merge entity @s {interpolation_duration:7,start_interpolation:0,transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.05f,0f],scale:[1.35f,1.05f,1.35f]}}
execute if score @s sd_anim_tick matches 8 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 270 run data merge entity @s {interpolation_duration:7,start_interpolation:0,transformation:{left_rotation:[0f,-0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.05f,0f],scale:[1.35f,1.05f,1.35f]}}

# ==========================================
# 阶段 15-22: 恢复到接近默认（scale: 回到 1.2 附近）
# ==========================================
execute if score @s sd_anim_tick matches 15 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 0 run data merge entity @s {interpolation_duration:8,start_interpolation:0,transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]}}
execute if score @s sd_anim_tick matches 15 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 90 run data merge entity @s {interpolation_duration:8,start_interpolation:0,transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]}}
execute if score @s sd_anim_tick matches 15 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 180 run data merge entity @s {interpolation_duration:8,start_interpolation:0,transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]}}
execute if score @s sd_anim_tick matches 15 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 270 run data merge entity @s {interpolation_duration:8,start_interpolation:0,transformation:{left_rotation:[0f,-0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]}}

# ==========================================
# 阶段 23-29: 稍微下压准备下一轮（scale: 1.2→1.15 Y, 1.2→1.25 XZ）
# ==========================================
execute if score @s sd_anim_tick matches 23 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 0 run data merge entity @s {interpolation_duration:7,start_interpolation:0,transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.02f,0f],scale:[1.25f,1.15f,1.25f]}}
execute if score @s sd_anim_tick matches 23 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 90 run data merge entity @s {interpolation_duration:7,start_interpolation:0,transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.02f,0f],scale:[1.25f,1.15f,1.25f]}}
execute if score @s sd_anim_tick matches 23 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 180 run data merge entity @s {interpolation_duration:7,start_interpolation:0,transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.02f,0f],scale:[1.25f,1.15f,1.25f]}}
execute if score @s sd_anim_tick matches 23 as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id if score @s sd_rotation matches 270 run data merge entity @s {interpolation_duration:7,start_interpolation:0,transformation:{left_rotation:[0f,-0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.02f,0f],scale:[1.25f,1.15f,1.25f]}}

# ==========================================
# 粒子效果：火焰和烟雾（每7-8 tick一次，减少性能消耗）
# ==========================================
execute if score @s sd_anim_tick matches 0 at @s run particle minecraft:flame ~ ~1.5 ~ 0.15 0.2 0.15 0.01 3
execute if score @s sd_anim_tick matches 0 at @s run particle minecraft:smoke ~ ~1.6 ~ 0.1 0.1 0.1 0.02 2

execute if score @s sd_anim_tick matches 8 at @s run particle minecraft:flame ~ ~1.5 ~ 0.15 0.2 0.15 0.01 3
execute if score @s sd_anim_tick matches 8 at @s run particle minecraft:smoke ~ ~1.6 ~ 0.1 0.1 0.1 0.02 2

execute if score @s sd_anim_tick matches 15 at @s run particle minecraft:flame ~ ~1.5 ~ 0.15 0.2 0.15 0.01 3
execute if score @s sd_anim_tick matches 15 at @s run particle minecraft:smoke ~ ~1.6 ~ 0.1 0.1 0.1 0.02 2

execute if score @s sd_anim_tick matches 23 at @s run particle minecraft:flame ~ ~1.5 ~ 0.15 0.2 0.15 0.01 3
execute if score @s sd_anim_tick matches 23 at @s run particle minecraft:smoke ~ ~1.6 ~ 0.1 0.1 0.1 0.02 2
