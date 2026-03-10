# 武器蓄力系统 - 检测蓄力重击武器的蓄力
# 复用工具的蓄力视觉效果（粒子、音效、ActionBar UI）

# 1. 检查是否手持蓄力重击武器（检查 weapon_special 或 weapon_special_2，支持多等级）
scoreboard players set #is_charge_weapon sd_temp 0
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"heavy_charge"} run scoreboard players set #is_charge_weapon sd_temp 1
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"heavy_strike_2"} run scoreboard players set #is_charge_weapon sd_temp 1
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"heavy_strike_3"} run scoreboard players set #is_charge_weapon sd_temp 1
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"heavy_charge"} run scoreboard players set #is_charge_weapon sd_temp 1
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"heavy_strike_2"} run scoreboard players set #is_charge_weapon sd_temp 1
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"heavy_strike_3"} run scoreboard players set #is_charge_weapon sd_temp 1

# 2. 检查是否在技能冷却中
execute if score @s sd_skill_cooldown matches 1.. run scoreboard players set #is_charge_weapon sd_temp 0

# 3. 如果手持蓄力重击武器且按住Shift，开始蓄力
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_const 20

# 4. 先清除不符合条件的状态（在所有效果之前）
# 4.1 如果不持有蓄力武器，清除所有状态
execute unless score #is_charge_weapon sd_temp matches 1 run scoreboard players set @s sd_heavy_charge_time 0
execute unless score #is_charge_weapon sd_temp matches 1 run scoreboard players set @s sd_heavy_charge_ready 0

# 4.2 如果松开Shift，立即清除所有蓄力状态（不管是否完成）
execute if score #is_charge_weapon sd_temp matches 1 unless score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_heavy_charge_time 0
execute if score #is_charge_weapon sd_temp matches 1 unless score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_heavy_charge_ready 0

# 5. 如果满足条件（手持蓄力武器 + Shift），增加蓄力时间
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 run scoreboard players add @s sd_heavy_charge_time 1

# 6. 蓄力音效（每5 tick播放一次，音调渐变）
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_time matches 5 if score @s sd_heavy_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.5 1.5
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_time matches 10 if score @s sd_heavy_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.5 1.7
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_time matches 15 if score @s sd_heavy_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.5 1.9
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_time matches 20 if score @s sd_heavy_charge_ready matches 0 at @s run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.5 2.0

# 7. 检查蓄力是否完成（20 ticks）
execute if score @s sd_heavy_charge_ready matches 0 if score @s sd_heavy_charge_time matches 20.. if score #is_charge_weapon sd_temp matches 1 run function stardew:combat/weapon/heavy_charge_complete

# 7.1 蓄力完成后，如果玩家继续按住，每10tick播放提示音
execute if score @s sd_heavy_charge_ready matches 1 if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 run scoreboard players operation #charge_mod sd_temp = @s sd_heavy_charge_time
execute if score @s sd_heavy_charge_ready matches 1 if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 run scoreboard players operation #charge_mod sd_temp %= #10 sd_const
execute if score @s sd_heavy_charge_ready matches 1 if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score #charge_mod sd_temp matches 0 at @s run playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2.0

# 8. 视觉效果 - 粒子（减少数量，每5 tick显示一次，只在蓄力未完成时显示）
scoreboard players operation #charge_particle sd_temp = @s sd_heavy_charge_time
scoreboard players operation #charge_particle sd_temp %= #5 sd_const
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_ready matches 0 if score #charge_particle sd_temp matches 0 if score @s sd_heavy_charge_time matches 1..10 at @s run particle minecraft:dust{color:[1.0,1.0,0.0],scale:0.5} ~ ~1.5 ~ 0.2 0.2 0.2 0 1 force @s
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_ready matches 0 if score #charge_particle sd_temp matches 0 if score @s sd_heavy_charge_time matches 11..20 at @s run particle minecraft:dust{color:[1.0,0.5,0.0],scale:0.7} ~ ~1.5 ~ 0.25 0.25 0.25 0 1 force @s
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_ready matches 0 if score #charge_particle sd_temp matches 0 if score @s sd_heavy_charge_time matches 21.. at @s run particle minecraft:dust{color:[1.0,0.0,0.0],scale:0.9} ~ ~1.5 ~ 0.3 0.3 0.3 0 1 force @s

# 8.1 FOV缩放效果 - 使用三个阶段的缓慢效果模拟镜头拉近
# 8.1.1 先清除缓慢效果（如果不满足条件）
execute unless score #is_charge_weapon sd_temp matches 1 run effect clear @s minecraft:slowness
execute if score #is_charge_weapon sd_temp matches 1 unless score @s sd_is_sneaking matches 1 run effect clear @s minecraft:slowness

# 8.1.2 根据蓄力进度给予对应等级的缓慢效果（只在按住Shift时）
# 第一阶段 (1-7 ticks): 缓慢 I (移动速度 -15%, FOV轻微缩小)
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_ready matches 0 if score @s sd_heavy_charge_time matches 1..7 run effect give @s minecraft:slowness 1 0 true
# 第二阶段 (8-14 ticks): 缓慢 II (移动速度 -30%, FOV中度缩小)
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_ready matches 0 if score @s sd_heavy_charge_time matches 8..14 run effect give @s minecraft:slowness 1 1 true
# 第三阶段 (15-20 ticks): 缓慢 III (移动速度 -45%, FOV最大缩小)
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_ready matches 0 if score @s sd_heavy_charge_time matches 15.. run effect give @s minecraft:slowness 1 2 true

# 8.1.3 蓄力完成后继续按住Shift，保持缓慢III效果（最大FOV缩小）
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_ready matches 1 run effect give @s minecraft:slowness 1 2 true

# 9. 蓄力进度集成到 ActionBar UI（设置 sd_special_type = 1）
# 注意：必须每帧都设置，因为 actionbar.mcfunction 会在渲染后重置 sd_special_type = 0
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_time matches 1.. if score @s sd_heavy_charge_ready matches 0 run scoreboard players operation @s sd_special_value = @s sd_heavy_charge_time
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_time matches 1.. if score @s sd_heavy_charge_ready matches 0 run scoreboard players set @s sd_special_max 20
execute if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 if score @s sd_heavy_charge_time matches 1.. if score @s sd_heavy_charge_ready matches 0 run scoreboard players set @s sd_special_type 1

# 9.1 蓄力完成后每帧保持 sd_special_type = 1 以显示✅（只在按住Shift时）
execute if score @s sd_heavy_charge_ready matches 1 if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_special_type 1
execute if score @s sd_heavy_charge_ready matches 1 if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_special_value 20
execute if score @s sd_heavy_charge_ready matches 1 if score #is_charge_weapon sd_temp matches 1 if score @s sd_is_sneaking matches 1 run scoreboard players set @s sd_special_max 20
