# 调试/重置函数 - 用于修复卡住的状态

# 1. 重置所有技能相关的Scoreboard
scoreboard players set @a sd_rhythm_cooldown 0
scoreboard players set @a sd_heavy_cooldown 0
scoreboard players set @a sd_rhythm_strike_timer 0
scoreboard players set @a sd_heavy_charge_timer 0
scoreboard players set @a sd_skill_cooldown 0
scoreboard players set @a sd_skill_2_cooldown 0

# 2. 移除所有技能相关的Tag
tag @a remove sd_rhythm_1
tag @a remove sd_rhythm_2
tag @a remove sd_rhythm_window
tag @a remove sd_heavy_charge_ready
tag @a remove sd_charging_heavy_charge

# 3. 移除/隐藏所有Bossbar
bossbar set stardew:rhythm_bar visible false
bossbar set stardew:rhythm_cooldown visible false
bossbar set stardew:heavy_charge_progress visible false
bossbar set stardew:heavy_cooldown visible false

# 4. 播放提示音
playsound minecraft:ui.button.click player @a ~ ~ ~ 1.0 1.0
title @a actionbar [{"text":"✔ 技能状态已重置","color":"green"}]
