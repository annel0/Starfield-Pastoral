# data/stardew/function/status/debuff/hunger_drain.mcfunction
# 饥饿效果扣除能量值

# 重置计时器
scoreboard players set @s sd_hunger_timer 0

# 重置计时器
scoreboard players set @s sd_hunger_timer 0

# 根据等级扣除能量值
execute if score @s sd_hunger_level matches 1 run scoreboard players remove @s sd_energy 5
execute if score @s sd_hunger_level matches 2 run scoreboard players remove @s sd_energy 10
execute if score @s sd_hunger_level matches 3.. run scoreboard players remove @s sd_energy 15

# 最低能量值为0
execute if score @s sd_energy matches ..0 run scoreboard players set @s sd_energy 0

# 饥饿扣能量反馈 - 虚弱烟雾效果（减少粒子数量和范围）
execute at @s run particle smoke ~ ~1 ~ 0.25 0.4 0.25 0.03 10
execute at @s run particle large_smoke ~ ~1.3 ~ 0.2 0.3 0.2 0.02 5
execute at @s run particle angry_villager ~ ~1.8 ~ 0.2 0.2 0.2 0 2

# 饥饿音效（降低音量）
execute at @s run playsound entity.generic.hurt player @a ~ ~ ~ 0.4 0.6
execute at @s run playsound entity.player.breath player @a ~ ~ ~ 0.3 0.8
