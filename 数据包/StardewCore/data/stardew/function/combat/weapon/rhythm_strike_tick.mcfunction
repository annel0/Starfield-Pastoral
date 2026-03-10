# 节奏打击 - Tick更新（状态机）

# 如果没有开始连击，不处理
execute unless entity @s[tag=sd_rhythm_1] unless entity @s[tag=sd_rhythm_2] run return 0

# === 阶段1和阶段2：攻击冷却阶段（黄色倒计时） ===
execute unless entity @s[tag=sd_rhythm_window] run scoreboard players remove @s sd_rhythm_strike_timer 1
execute unless entity @s[tag=sd_rhythm_window] store result bossbar stardew:rhythm_bar value run scoreboard players get @s sd_rhythm_strike_timer

# 音调渐变音效（倒计时期间，每3 ticks一次）
execute unless entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 24 run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.4 0.8
execute unless entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 21 run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.4 0.9
execute unless entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 18 run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.4 1.0
execute unless entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 15 run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.4 1.1
execute unless entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 12 run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.4 1.2
execute unless entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 9 run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.5 1.3
execute unless entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 6 run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.5 1.4
execute unless entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 3 run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.5 1.5
execute unless entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 1 run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.6 1.6

# 倒计时结束 -> 进入窗口期
execute unless entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches ..0 run tag @s add sd_rhythm_window
execute if entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches ..0 run scoreboard players set @s sd_rhythm_strike_timer 8
execute if entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 8 run bossbar set stardew:rhythm_bar color green
execute if entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 8 run bossbar set stardew:rhythm_bar style progress
execute if entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 8 run bossbar set stardew:rhythm_bar name {"text":"⚡ 点击！","color":"green","bold":true}
execute if entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 8 run bossbar set stardew:rhythm_bar max 8
execute if entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 8 run playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.8 2.0
execute if entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 8 run playsound minecraft:block.note_block.bell player @s ~ ~ ~ 0.6 2.0

# === 窗口期（绿色倒计时） ===
execute if entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches 1.. run scoreboard players remove @s sd_rhythm_strike_timer 1
execute if entity @s[tag=sd_rhythm_window] store result bossbar stardew:rhythm_bar value run scoreboard players get @s sd_rhythm_strike_timer

# 窗口期结束 -> 失败
execute if entity @s[tag=sd_rhythm_window] if score @s sd_rhythm_strike_timer matches ..0 run function stardew:combat/weapon/rhythm_strike_failed
