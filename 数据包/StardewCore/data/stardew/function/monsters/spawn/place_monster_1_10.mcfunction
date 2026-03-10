# stardew:monsters/spawn/place_monster_1_10.mcfunction
# 在1-10层随机位置生成一只怪物

# 随机选择怪物类型 (1-4)
execute store result score #monster_type sd_temp run random value 1..4

# 获取随机位置
execute store result score #spawn_x sd_temp run random value -15..15
execute store result score #spawn_z sd_temp run random value -15..15

# 根据类型生成怪物
execute if score #monster_type sd_temp matches 1 positioned ~ ~ ~ run function stardew:monsters/spawn/types/green_slime
execute if score #monster_type sd_temp matches 2 positioned ~ ~ ~ run function stardew:monsters/spawn/types/rock_crab
execute if score #monster_type sd_temp matches 3 positioned ~ ~ ~ run function stardew:monsters/spawn/types/bug
execute if score #monster_type sd_temp matches 4 positioned ~ ~ ~ run function stardew:monsters/spawn/types/duggy
