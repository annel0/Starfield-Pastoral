# 龙牙连击 - 失败

# 1. 清理所有状态
tag @s remove sd_dragon_combo_1
tag @s remove sd_dragon_combo_2
tag @s remove sd_dragon_combo_3
tag @s remove sd_dragon_combo_4
tag @s remove sd_dragon_combo_window
scoreboard players set @s sd_dragon_combo_timer 0

# 2. 移除Bossbar
bossbar remove stardew:dragon_combo_bar

# 3. 触发惩罚冷却（10秒）
scoreboard players set @s sd_skill_cooldown 200
function stardew:combat/cooldown/set_dragon_combo_cooldown_max

# 4. 标记正在使用龙牙连击冷却
tag @s add sd_using_dragon_combo

# 5. 失败音效
playsound minecraft:entity.villager.no player @s ~ ~ ~ 1.0 0.8
playsound minecraft:block.note_block.bass player @s ~ ~ ~ 0.6 0.5

# 6. 显示提示
title @s actionbar [{"text":"❌ 连击失败！龙牙被打断！","color":"red","bold":true}]
