# data/stardew/function/status/debuff/poison_tick.mcfunction
# 中毒效果 tick 处理 (蜘蛛/洞穴虫攻击造成)
# 效果: 持续伤害

# 持续时间倒计时
scoreboard players remove @s sd_poison_duration 1

# 每20 tick (1秒) 造成伤害
scoreboard players add @s sd_poison_tick_timer 1
execute if score @s sd_poison_tick_timer matches 20.. run function stardew:status/debuff/poison_damage

# 持续显示中毒状态的轻微粒子
execute at @s run particle effect ~ ~1 ~ 0.2 0.3 0.2 0.05 1
execute at @s run particle sneeze ~ ~1 ~ 0.15 0.25 0.15 0.02 1

# 持续时间结束时清除效果
execute if score @s sd_poison_duration matches ..0 run function stardew:status/debuff/poison_remove
