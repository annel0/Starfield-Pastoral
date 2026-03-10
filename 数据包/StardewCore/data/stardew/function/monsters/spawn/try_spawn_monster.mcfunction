# stardew:monsters/spawn/try_spawn_monster.mcfunction
# 尝试在指定位置生成怪物
# 参数: $(x), $(z) - 绝对坐标
# 执行者: 玩家 (@s)
# 执行环境: in stardew:mine

# 检查位置是否可用:
# 1. Y=65 必须是空气 (地板上方，可以放东西)
# 2. Y=64 必须不是空气 (下方有实心地板)

# 选择怪物类型（根据主题/楼层范围）
execute if score @s sd_mine_floor matches 1..25 run function stardew:monsters/spawn/place_monster_theme1
execute if score @s sd_mine_floor matches 26..50 run function stardew:monsters/spawn/place_monster_theme2
execute if score @s sd_mine_floor matches 51..75 run function stardew:monsters/spawn/place_monster_theme3
execute if score @s sd_mine_floor matches 76..100 run function stardew:monsters/spawn/place_monster_theme4

# 检查位置并生成怪物
$execute in stardew:mine positioned $(x) 65 $(z) if block ~ ~ ~ air unless block ~ ~-1 ~ air run function stardew:monsters/spawn/spawn_random_monster
