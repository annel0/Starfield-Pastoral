# data/stardew/functions/tools/charge_system.mcfunction
# 工具蓄力系统 - 音效和视觉反馈

# 1. 检测玩家是否手持工具并潜行
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"

# 2. 判定是否需要蓄力（潜行 + 高级工具）
scoreboard players set @s sd_const 0

# 铁工具（需要20tick）
execute if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 302 run scoreboard players set @s sd_const 20
execute if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 502 run scoreboard players set @s sd_const 20

# 金工具（需要30tick）
execute if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 303 run scoreboard players set @s sd_const 30
execute if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 503 run scoreboard players set @s sd_const 30

# 钻石工具（需要40tick）
execute if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 304 run scoreboard players set @s sd_const 40
execute if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 504 run scoreboard players set @s sd_const 40

# 3. 如果需要蓄力（sd_const > 0），增加蓄力时间
execute if score @s sd_const matches 1.. run scoreboard players add @s sd_charge_time 1

# 4. 如果不需要蓄力，重置蓄力时间，但保留 sd_charge_ready 状态（蓄力完成后可以松开Shift）
execute unless score @s sd_const matches 1.. run scoreboard players set @s sd_charge_time 0
# 只有当不是蓄力工具时才重置 ready 状态（但保留已经完成的蓄力）
execute unless score @s sd_const matches 1.. unless score @s sd_charge_ready matches 1 run scoreboard players set @s sd_charge_ready 0
execute unless score @s sd_const matches 1.. unless score @s sd_charge_ready matches 1 run effect clear @s minecraft:slowness

# 4.2 如果不手持任何蓄力工具（sd_temp 不匹配任何CMD），清除所有蓄力相关显示
execute unless score @s sd_temp matches 302 unless score @s sd_temp matches 303 unless score @s sd_temp matches 304 unless score @s sd_temp matches 502 unless score @s sd_temp matches 503 unless score @s sd_temp matches 504 run scoreboard players set @s sd_charge_ready 0
execute unless score @s sd_temp matches 302 unless score @s sd_temp matches 303 unless score @s sd_temp matches 304 unless score @s sd_temp matches 502 unless score @s sd_temp matches 503 unless score @s sd_temp matches 504 run scoreboard players set @s sd_charge_time 0
execute unless score @s sd_temp matches 302 unless score @s sd_temp matches 303 unless score @s sd_temp matches 304 unless score @s sd_temp matches 502 unless score @s sd_temp matches 503 unless score @s sd_temp matches 504 run scoreboard players set @s sd_special_type 0
execute unless score @s sd_temp matches 302 unless score @s sd_temp matches 303 unless score @s sd_temp matches 304 unless score @s sd_temp matches 502 unless score @s sd_temp matches 503 unless score @s sd_temp matches 504 run scoreboard players set @s sd_special_value 0
execute unless score @s sd_temp matches 302 unless score @s sd_temp matches 303 unless score @s sd_temp matches 304 unless score @s sd_temp matches 502 unless score @s sd_temp matches 503 unless score @s sd_temp matches 504 run scoreboard players set @s sd_special_max 0

# 4.1 如果松开Shift但蓄力未完成，重置蓄力（允许玩家取消蓄力）
execute if score @s sd_const matches 1.. unless score @s sd_is_sneaking matches 1 unless score @s sd_charge_ready matches 1 run scoreboard players set @s sd_charge_time 0
execute if score @s sd_const matches 1.. unless score @s sd_is_sneaking matches 1 unless score @s sd_charge_ready matches 1 run scoreboard players set @s sd_charge_ready 0

# 4.2 如果松开Shift且已经完成蓄力，清除蓄力显示和状态（下次从0开始）
execute if score @s sd_const matches 1.. if score @s sd_charge_ready matches 1 unless score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_charge_ready 0
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 1.. unless score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_charge_time 0
execute if score @s sd_const matches 1.. unless score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_special_type 0
execute if score @s sd_const matches 1.. unless score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_special_value 0
execute if score @s sd_const matches 1.. unless score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_special_max 0

# 5. 蓄力音效（每5 tick播放一次）
# 简化版：直接检查 charge_time % 5 == 0
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 5 if score @s sd_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat master @s ~ ~ ~ 0.5 1.5
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 10 if score @s sd_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat master @s ~ ~ ~ 0.5 1.6
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 15 if score @s sd_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat master @s ~ ~ ~ 0.5 1.7
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 20 if score @s sd_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat master @s ~ ~ ~ 0.5 1.8
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 25 if score @s sd_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat master @s ~ ~ ~ 0.5 1.9
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 30 if score @s sd_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat master @s ~ ~ ~ 0.5 2.0
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 35 if score @s sd_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat master @s ~ ~ ~ 0.5 2.0
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 40 if score @s sd_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat master @s ~ ~ ~ 0.5 2.0

# 6. 检查蓄力是否完成
execute if score @s sd_charge_ready matches 0 if score @s sd_charge_time >= @s sd_const if score @s sd_const matches 1.. run function stardew:tools/charge_complete

# 6.1 蓄力完成后，如果玩家继续按住，每10tick播放提示音
execute if score @s sd_charge_ready matches 1 if score @s sd_const matches 1.. if score @s sd_charge_time matches 1.. run scoreboard players operation #charge_mod sd_temp = @s sd_charge_time
execute if score @s sd_charge_ready matches 1 if score @s sd_const matches 1.. if score @s sd_charge_time matches 1.. run scoreboard players operation #charge_mod sd_temp %= #10 sd_const
execute if score @s sd_charge_ready matches 1 if score @s sd_const matches 1.. if score #charge_mod sd_temp matches 0 at @s run playsound minecraft:block.note_block.pling master @s ~ ~ ~ 0.5 2.0

# 7. 视觉效果 - 粒子和FOV
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 1.. run function stardew:tools/charge_fov

# 7.1 FOV缩放效果 - 使用三个阶段的缓慢效果模拟镜头拉近
# 第一阶段 (0-33%): 缓慢 I (移动速度 -15%, FOV轻微缩小)
execute if score @s sd_const matches 1.. if score @s sd_is_sneaking matches 1 if score @s sd_charge_ready matches 0 run scoreboard players operation #charge_percent sd_temp = @s sd_charge_time
execute if score @s sd_const matches 1.. if score @s sd_is_sneaking matches 1 if score @s sd_charge_ready matches 0 run scoreboard players operation #charge_percent sd_temp *= #100 sd_const
execute if score @s sd_const matches 1.. if score @s sd_is_sneaking matches 1 if score @s sd_charge_ready matches 0 run scoreboard players operation #charge_percent sd_temp /= @s sd_const
execute if score @s sd_const matches 1.. if score @s sd_is_sneaking matches 1 if score @s sd_charge_ready matches 0 if score #charge_percent sd_temp matches 1..33 run effect give @s minecraft:slowness 1 0 true
# 第二阶段 (34-66%): 缓慢 II (移动速度 -30%, FOV中度缩小)
execute if score @s sd_const matches 1.. if score @s sd_is_sneaking matches 1 if score @s sd_charge_ready matches 0 if score #charge_percent sd_temp matches 34..66 run effect give @s minecraft:slowness 1 1 true
# 第三阶段 (67-99%): 缓慢 III (移动速度 -45%, FOV最大缩小)
execute if score @s sd_const matches 1.. if score @s sd_is_sneaking matches 1 if score @s sd_charge_ready matches 0 if score #charge_percent sd_temp matches 67.. run effect give @s minecraft:slowness 1 2 true

# 7.2 蓄力完成后继续按住Shift，保持缓慢III效果（最大FOV缩小）
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 run effect give @s minecraft:slowness 1 2 true

# 7.3 松开Shift立即清除缓慢效果
execute unless score @s sd_is_sneaking matches 1 run effect clear @s minecraft:slowness

# 8. 蓄力进度集成到 actionbar 特殊进度条
# 设置特殊进度条的数据（会被 actionbar.mcfunction 读取）
# 注意：必须每帧都设置，因为 actionbar.mcfunction 会在渲染后重置 sd_special_type = 0
# 只在蓄力中（未完成）时显示进度
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 1.. if score @s sd_charge_ready matches 0 run scoreboard players operation @s sd_special_value = @s sd_charge_time
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 1.. if score @s sd_charge_ready matches 0 run scoreboard players operation @s sd_special_max = @s sd_const
execute if score @s sd_const matches 1.. if score @s sd_charge_time matches 1.. if score @s sd_charge_ready matches 0 run scoreboard players set @s sd_special_type 1

# 8.1 蓄力完成后每帧保持 sd_special_type = 1 以显示✅（只在按住Shift时显示）
# 这样可以确保 actionbar 继续显示完成标记，松开Shift后立即消失
# 完成时进度条显示满格（special_value = special_max）
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 302 run scoreboard players set @s sd_special_value 20
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 502 run scoreboard players set @s sd_special_value 20
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 303 run scoreboard players set @s sd_special_value 30
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 503 run scoreboard players set @s sd_special_value 30
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 304 run scoreboard players set @s sd_special_value 40
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 504 run scoreboard players set @s sd_special_value 40
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 302 run scoreboard players set @s sd_special_max 20
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 502 run scoreboard players set @s sd_special_max 20
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 303 run scoreboard players set @s sd_special_max 30
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 503 run scoreboard players set @s sd_special_max 30
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 304 run scoreboard players set @s sd_special_max 40
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_temp matches 504 run scoreboard players set @s sd_special_max 40
execute if score @s sd_charge_ready matches 1 if score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_special_type 1
