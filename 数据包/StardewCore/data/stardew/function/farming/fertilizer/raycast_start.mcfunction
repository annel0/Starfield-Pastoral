# data/stardew/functions/farming/fertilizer/raycast_start.mcfunction
# 开始射线检测寻找耕地

# 初始化射线距离
scoreboard players set @s sd_raycast 0

# 从玩家眼睛位置开始射线
execute anchored eyes positioned ^ ^ ^ run function stardew:farming/fertilizer/raycast_step
