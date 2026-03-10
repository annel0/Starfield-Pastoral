# 节奏打击 - 失败

# 1. 清理所有状态
tag @s remove sd_rhythm_1
tag @s remove sd_rhythm_2
tag @s remove sd_rhythm_window
scoreboard players set @s sd_rhythm_strike_timer 0

# 2. 移除Bossbar
bossbar remove stardew:rhythm_bar

# 3. 触发惩罚冷却（8秒）
scoreboard players set @s sd_skill_cooldown 160
function stardew:combat/cooldown/set_rhythm_strike_cooldown_max

# 4. 标记正在使用节奏打击冷却
tag @s add sd_using_rhythm_strike

# 5. 失败音效
playsound minecraft:entity.villager.no player @s ~ ~ ~ 1.0 0.8
playsound minecraft:block.note_block.bass player @s ~ ~ ~ 0.6 0.5

# 6. 显示提示
title @s actionbar [{"text":"❌ 连击失败！节奏被打断！","color":"red","bold":true}]
