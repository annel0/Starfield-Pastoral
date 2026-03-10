# stardew:monsters/spawn/spawn_random_monster.mcfunction
# 在当前位置生成随机怪物（类似矿石的 spawn_random_ore）
# 执行位置: 已经在目标位置（Y=65）
# 执行环境: in stardew:mine

# 根据怪物类型调用对应的 types/ 函数
execute if score #spawn_type sd_temp matches 1 run function stardew:monsters/spawn/types/slime
execute if score #spawn_type sd_temp matches 2 run function stardew:monsters/spawn/types/spider
execute if score #spawn_type sd_temp matches 3 run function stardew:monsters/spawn/types/silverfish
execute if score #spawn_type sd_temp matches 4 run function stardew:monsters/spawn/types/skeleton
execute if score #spawn_type sd_temp matches 5 run function stardew:monsters/spawn/types/bat
execute if score #spawn_type sd_temp matches 6 run function stardew:monsters/spawn/types/ghost
execute if score #spawn_type sd_temp matches 7 run function stardew:monsters/spawn/types/golem
execute if score #spawn_type sd_temp matches 8 run function stardew:monsters/spawn/types/shadow
execute if score #spawn_type sd_temp matches 9 run function stardew:monsters/spawn/types/squid
