# data/stardew/function/utility/keg/complete_process.mcfunction
# 加工完成
# 执行者: 完成加工的小桶interaction (@s)

# 1. 获取对应视觉实体的ID
execute as @e[type=item_display,tag=sd_keg_visual,distance=..1,limit=1] run scoreboard players operation #current_id sd_keg_id = @s sd_keg_id

# 2. 更新有相同ID的文本为"已完成！"
execute as @e[type=text_display,tag=sd_keg_time] if score @s sd_keg_id = #current_id sd_keg_id run data merge entity @s {text:'{"text":"已完成！","color":"green","bold":true}',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.9f,0.9f,0.9f]}}

# 3. 播放完成音效（只播放一次）
execute at @s run playsound minecraft:block.brewing_stand.brew block @a ~ ~ ~ 0.5 1.2

# 4. 改变状态为"已完成"（设置为2，防止重复触发）
scoreboard players set @s sd_keg_state 2
# 清除通用活跃标记
scoreboard players set @s sd_utility_active 0

# 5. 重置动画状态，恢复视觉实体到默认 scale（保持旋转）
scoreboard players reset @s sd_anim_tick
scoreboard players reset @s sd_anim_phase
execute as @e[type=item_display,tag=sd_keg_visual] if score @s sd_keg_id = #current_id sd_keg_id run function stardew:utility/apply_rotation

# 6. 视觉实体保持不变(小桶不改变模型)

# 7. 小桶不需要移除光源方块（注释掉）
# execute as @e[type=item_display,tag=sd_keg_visual] if score @s sd_keg_id = #current_id sd_keg_id at @s align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:air
