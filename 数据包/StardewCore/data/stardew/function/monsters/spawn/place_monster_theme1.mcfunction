# stardew:monsters/spawn/place_monster_theme1.mcfunction
# 主题1（1-25层）：草原风格 - 基础怪物
# 史莱姆30%, 蜘蛛25%, 蠹虫20%, 幻翼25%

# 随机选择怪物类型
execute store result score #spawn_type sd_temp run random value 1..20

# 1-6: 史莱姆 (30%)
execute if score #spawn_type sd_temp matches 1..6 run scoreboard players set #spawn_type sd_temp 1

# 7-11: 蜘蛛 (25%)
execute if score #spawn_type sd_temp matches 7..11 run scoreboard players set #spawn_type sd_temp 2

# 12-15: 蠹虫 (20%)
execute if score #spawn_type sd_temp matches 12..15 run scoreboard players set #spawn_type sd_temp 3

# 16-20: 幻翼 (25%)
execute if score #spawn_type sd_temp matches 16..20 run scoreboard players set #spawn_type sd_temp 5

# 注意: 不在这里生成，而是由 try_spawn_monster 统一调用 spawn_random_monster
