# 战斗系统调试菜单

tellraw @s {"text":"==========================================","color":"gold"}
tellraw @s [{"text":"【战斗系统调试菜单】","color":"yellow","bold":true}]
tellraw @s {"text":"==========================================","color":"gold"}
tellraw @s ""

tellraw @s [{"text":"━━ 生成测试怪物 ━━","color":"aqua","bold":true}]
tellraw @s [{"text":"[生成] ","color":"green","clickEvent":{"action":"run_command","value":"/function stardew:combat/debug/spawn_boss_100"},"hoverEvent":{"action":"show_text","contents":"快速测试用"}},{"text":"100血 小BOSS","color":"gray"}]

tellraw @s [{"text":"[生成] ","color":"green","clickEvent":{"action":"run_command","value":"/function stardew:combat/debug/spawn_boss_300"},"hoverEvent":{"action":"show_text","contents":"中等难度"}},{"text":"300血 中型BOSS","color":"white"}]

tellraw @s [{"text":"[生成] ","color":"green","clickEvent":{"action":"run_command","value":"/function stardew:combat/debug/spawn_tanky_monster"},"hoverEvent":{"action":"show_text","contents":"标准测试BOSS"}},{"text":"500血 测试BOSS","color":"yellow"}]

tellraw @s [{"text":"[生成] ","color":"green","clickEvent":{"action":"run_command","value":"/function stardew:combat/debug/spawn_boss_1000"},"hoverEvent":{"action":"show_text","contents":"高血量挑战"}},{"text":"1000血 超级BOSS","color":"aqua"}]

tellraw @s [{"text":"[生成] ","color":"green","clickEvent":{"action":"run_command","value":"/function stardew:combat/debug/spawn_boss_5000"},"hoverEvent":{"action":"show_text","contents":"终极耐久测试"}},{"text":"5000血 终极BOSS","color":"red","bold":true}]

tellraw @s ""
tellraw @s [{"text":"━━ 清理功能 ━━","color":"red","bold":true}]
tellraw @s [{"text":"[清理] ","color":"red","clickEvent":{"action":"run_command","value":"/kill @e[tag=sd_monster]"},"hoverEvent":{"action":"show_text","contents":"清除所有怪物"}},{"text":"清除所有怪物","color":"white"}]

tellraw @s [{"text":"[清理] ","color":"red","clickEvent":{"action":"run_command","value":"/bossbar remove stardew:test_boss"},"hoverEvent":{"action":"show_text","contents":"清除BOSS血条"}},{"text":"清除BOSS血条","color":"white"}]

tellraw @s ""
tellraw @s {"text":"==========================================","color":"gold"}
