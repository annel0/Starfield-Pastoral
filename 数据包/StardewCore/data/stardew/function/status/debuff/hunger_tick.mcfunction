# data/stardew/function/status/debuff/hunger_tick.mcfunction
# 饥饿效果 tick 处理 (流浪者攻击造成)
# 效果: 持续扣除能量值

# 持续时间倒计时
scoreboard players remove @s sd_hunger_duration 1

# 每40 tick (2秒) 扣除能量
scoreboard players add @s sd_hunger_timer 1
execute if score @s sd_hunger_timer matches 40.. run function stardew:status/debuff/hunger_drain

# 持续显示饥饿状态的轻微粒子
execute at @s run particle smoke ~ ~1 ~ 0.2 0.3 0.2 0.02 1
execute at @s run particle angry_villager ~ ~2 ~ 0.2 0.2 0.2 0 1

# 持续时间结束时清除效果
execute if score @s sd_hunger_duration matches ..0 run function stardew:status/debuff/hunger_remove
