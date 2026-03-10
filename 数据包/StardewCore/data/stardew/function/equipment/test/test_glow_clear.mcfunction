# ================================================================
# 清除发光效果测试
# ================================================================

# 移除发光等级
scoreboard players reset @s sd_glow_level

# 手动触发清理
function stardew:equipment/effects/rings/remove_all_player_lights

# 提示信息
tellraw @s [{"text":"[测试] ","color":"green","bold":true},{"text":"已清除发光效果","color":"yellow"}]
