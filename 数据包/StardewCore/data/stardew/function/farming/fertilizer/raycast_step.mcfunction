# data/stardew/functions/farming/fertilizer/raycast_step.mcfunction
# 射线检测每一步

# 增加射线距离
scoreboard players add @s sd_raycast 1

# 检测是否到达耕地
execute if block ~ ~ ~ farmland run function stardew:farming/fertilizer/check_farmland

# 如果距离未超过5格且未找到耕地,继续前进
execute if score @s sd_raycast matches ..50 unless block ~ ~ ~ farmland positioned ^ ^ ^0.1 run function stardew:farming/fertilizer/raycast_step
