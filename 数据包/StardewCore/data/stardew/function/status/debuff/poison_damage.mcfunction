# data/stardew/function/status/debuff/poison_damage.mcfunction
# 中毒效果造成伤害

# 重置计时器
scoreboard players set @s sd_poison_tick_timer 0

# 重置计时器
scoreboard players set @s sd_poison_tick_timer 0

# 根据等级造成伤害
execute if score @s sd_poison_level matches 1 run scoreboard players remove @s sd_health 3
execute if score @s sd_poison_level matches 2 run scoreboard players remove @s sd_health 5
execute if score @s sd_poison_level matches 3.. run scoreboard players remove @s sd_health 8

# 中毒伤害反馈 - 绿色毒雾效果（减少粒子数量和范围）
execute at @s run particle damage_indicator ~ ~1 ~ 0.2 0.3 0.2 0.1 5
execute at @s run particle effect ~ ~1 ~ 0.3 0.4 0.3 0.08 8
execute at @s run particle sneeze ~ ~1.1 ~ 0.2 0.3 0.2 0.05 4

# 中毒音效（降低音量）
execute at @s run playsound entity.player.hurt player @a ~ ~ ~ 0.5 0.9
execute at @s run playsound entity.spider.hurt player @a ~ ~ ~ 0.4 0.7

# 检测是否昏倒
execute if score @s sd_health matches ..0 run function stardew:combat/player_faint
