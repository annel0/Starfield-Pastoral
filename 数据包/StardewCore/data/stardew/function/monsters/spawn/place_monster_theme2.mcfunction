# stardew:monsters/spawn/place_monster_theme2.mcfunction
# 主题2（26-50层）：沙漠风格 - 远程威胁
# 史莱姆20%, 蜘蛛15%, 骷髅25%, 蠹虫10%, 幻翼15%, 溺尸15%

# 随机选择怪物类型
execute store result score #spawn_type sd_temp run random value 1..20

# 1-4: 史莱姆 (20%)
execute if score #spawn_type sd_temp matches 1..4 run scoreboard players set #spawn_type sd_temp 1

# 5-7: 蜘蛛 (15%)
execute if score #spawn_type sd_temp matches 5..7 run scoreboard players set #spawn_type sd_temp 2

# 8-12: 骷髅 (25%)
execute if score #spawn_type sd_temp matches 8..12 run scoreboard players set #spawn_type sd_temp 4

# 13-14: 蠹虫 (10%)
execute if score #spawn_type sd_temp matches 13..14 run scoreboard players set #spawn_type sd_temp 3

# 15-17: 幻翼 (15%)
execute if score #spawn_type sd_temp matches 15..17 run scoreboard players set #spawn_type sd_temp 5

# 18-20: 溺尸 (15%)
execute if score #spawn_type sd_temp matches 18..20 run scoreboard players set #spawn_type sd_temp 6

# 注意: 不在这里生成，而是由 try_spawn_monster 统一调用 spawn_random_monster
