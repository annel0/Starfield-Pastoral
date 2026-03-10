# data/stardew/function/utility/furnace/complete_smelt.mcfunction
# 熔炼完成
# 执行者: 完成熔炼的熔炉interaction (@s)

# 1. 获取对应视觉实体的ID
execute as @e[type=item_display,tag=sd_furnace_visual,distance=..1,limit=1] run scoreboard players operation #current_id sd_furnace_id = @s sd_furnace_id

# 2. 更新有相同ID的文本为"已完成！"
execute as @e[type=text_display,tag=sd_furnace_time] if score @s sd_furnace_id = #current_id sd_furnace_id run data merge entity @s {text:'{"text":"已完成！","color":"green","bold":true}',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.9f,0.9f,0.9f]}}

# 3. 播放完成音效（只播放一次）
execute at @s run playsound minecraft:block.anvil.use block @a ~ ~ ~ 0.5 1.5

# 4. 改变状态为"已完成"（设置为2，防止重复触发）
scoreboard players set @s sd_furnace_state 2
# 清除通用活跃标记
scoreboard players set @s sd_utility_active 0

# 5. 重置动画状态，恢复视觉实体到默认 scale（保持旋转）
scoreboard players reset @s sd_anim_tick
scoreboard players reset @s sd_anim_phase
execute as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id run function stardew:utility/apply_rotation

# 6. 将视觉实体变回空闲状态（CMD 101 - 未点燃的熔炉），保持旋转
execute as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id run data merge entity @s {item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":101}}}
execute as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id run function stardew:utility/apply_rotation

# 7. 移除光源方块（基于视觉实体位置，向上1格对齐）
execute as @e[type=item_display,tag=sd_furnace_visual] if score @s sd_furnace_id = #current_id sd_furnace_id at @s align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:air
