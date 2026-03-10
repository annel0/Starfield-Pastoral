# data/stardew/function/utility/furnace/time_skip_bonus.mcfunction
# 时间跳跃奖励 - 当使用法杖或菜单跳过时间时调用
# 执行者: 正在工作的熔炉 interaction (@s)
# 前提: #time_delta sd_const 已经设置为跳过的时间（分钟数）

# 1. 检查是否已经完成（已完成的不需要增加时间）
execute if score @s sd_furnace_timer >= @s sd_furnace_max_time run return 0

# 2. 增加跳过的时间
scoreboard players operation @s sd_furnace_timer += #time_delta sd_const

# 3. 如果增加后超过了需要时间，设置为刚好完成
execute if score @s sd_furnace_timer > @s sd_furnace_max_time run scoreboard players operation @s sd_furnace_timer = @s sd_furnace_max_time

# 4. 如果达到完成条件，触发完成逻辑
execute if score @s sd_furnace_timer >= @s sd_furnace_max_time at @s run function stardew:utility/furnace/complete_smelt
