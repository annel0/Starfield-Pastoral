# ================================================================
# 更新光源位置
# ================================================================
# @s = light_marker
# 玩家移动到新格子,更新光源位置

# 清理旧位置的光源方块
function stardew:equipment/effects/rings/clear_old_position

# 传送 marker 到玩家位置
tp @s @p[distance=..5]

# 在新位置放置光源
execute at @s as @p[distance=..1] run function stardew:equipment/effects/rings/refresh_lights
