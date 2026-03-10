# data/stardew/function/utility/tapper/complete_tapping.mcfunction
# 提取完成
# 执行者: 完成提取的树interaction (@s)

# 1. 获取对应视觉实体的ID
execute as @e[type=item_display,tag=sd_tapper_visual,distance=..2,limit=1] run scoreboard players operation #current_id sd_tapper_id = @s sd_tapper_id

# 2. 更新有相同ID的文本为"已完成！"
execute as @e[type=text_display,tag=sd_tapper_time] if score @s sd_tapper_id = #current_id sd_tapper_id run data merge entity @s {text:'{"text":"已完成！","color":"green","bold":true}',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.8f,0.8f,0.8f]}}

# 3. 播放完成音效（只播放一次）
execute at @s run playsound minecraft:entity.experience_orb.pickup block @a ~ ~ ~ 0.8 1.2

# 4. 改变状态为"已完成"（设置为2，防止重复触发）
scoreboard players set @s sd_tapper_state 2
# 清除通用活跃标记
scoreboard players set @s sd_utility_active 0
