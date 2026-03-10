# ====================================
# 完整状态效果测试
# ====================================
# 测试中毒和饥饿 Debuff 是否正确扣除我们系统的数据

tellraw @s {"text":"=== 完整状态效果测试 ===","color":"gold","bold":true}
tellraw @s {"text":""}

# 显示当前状态
tellraw @s [{"text":"当前生命值: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_health"},"color":"white"},{"text":"/","color":"gray"},{"score":{"name":"@s","objective":"sd_max_health"},"color":"white"}]
tellraw @s [{"text":"当前能量值: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_energy"},"color":"white"},{"text":"/","color":"gray"},{"score":{"name":"@s","objective":"sd_max_energy"},"color":"white"}]
tellraw @s {"text":""}

# 测试选项
tellraw @s [{"text":"[测试1] ","color":"aqua","bold":true},{"text":"应用中毒效果(level 2, 10秒)","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_poison"},"hoverEvent":{"action":"show_text","value":"每秒扣除5点sd_health"}}]

tellraw @s [{"text":"[测试2] ","color":"aqua","bold":true},{"text":"应用饥饿效果(level 2, 10秒)","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_hunger"},"hoverEvent":{"action":"show_text","value":"每2秒扣除10点sd_energy"}}]

tellraw @s [{"text":"[测试3] ","color":"aqua","bold":true},{"text":"查看当前状态","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/check_player_stats"},"hoverEvent":{"action":"show_text","value":"显示生命值和能量值"}}]

tellraw @s [{"text":"[测试4] ","color":"aqua","bold":true},{"text":"治疗+恢复能量(满血满能量)","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/restore_stats"},"hoverEvent":{"action":"show_text","value":"回复满状态"}}]

tellraw @s [{"text":"[测试5] ","color":"aqua","bold":true},{"text":"清除所有效果","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:status/clear_all"},"hoverEvent":{"action":"show_text","value":"清除所有Buff和Debuff"}}]

tellraw @s {"text":""}
tellraw @s {"text":"===========================","color":"gold","bold":true}
