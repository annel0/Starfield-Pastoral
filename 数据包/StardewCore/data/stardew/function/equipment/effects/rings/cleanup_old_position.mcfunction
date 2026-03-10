# ================================================================
# 清理旧位置的光源
# ================================================================
# @s = 玩家
# 使用保存的上一个位置清理光源

# 将坐标组装成字符串格式
execute store result storage stardew:temp old_x int 1 run scoreboard players get @s stardew.light.last_x
execute store result storage stardew:temp old_y int 1 run scoreboard players get @s stardew.light.last_y
execute store result storage stardew:temp old_z int 1 run scoreboard players get @s stardew.light.last_z

# 在旧位置清理光源方块
function stardew:equipment/effects/rings/cleanup_at_position with storage stardew:temp
