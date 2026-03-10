# data/stardew/functions/farming/grow_manager.mcfunction

# [新增] 浇水判定
# 如果 sd_watered 不是 1，直接停止，不执行后续生长逻辑
execute unless score @s sd_watered matches 1 run return 0

# --- 只有浇了水的才能继续向下执行 ---

# 春季(1)
execute if score Global sd_season matches 1 if entity @s[tag=season_1] run function stardew:farming/grow_logic_impl
# 夏季(2)
execute if score Global sd_season matches 2 if entity @s[tag=season_2] run function stardew:farming/grow_logic_impl
# 秋季(3)
execute if score Global sd_season matches 3 if entity @s[tag=season_3] run function stardew:farming/grow_logic_impl
# 冬季(4)
execute if score Global sd_season matches 4 if entity @s[tag=season_4] run function stardew:farming/grow_logic_impl