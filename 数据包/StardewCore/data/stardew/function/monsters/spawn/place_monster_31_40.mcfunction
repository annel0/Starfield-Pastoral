# stardew:monsters/spawn/place_monster_31_40.mcfunction
# 31-40层怪物类型

execute store result score #monster_type sd_temp run random value 1..5
execute store result score #spawn_x sd_temp run random value -15..15
execute store result score #spawn_z sd_temp run random value -15..15

execute if score #monster_type sd_temp matches 1 positioned ~ ~ ~ run function stardew:monsters/spawn/types/green_slime
execute if score #monster_type sd_temp matches 2 positioned ~ ~ ~ run function stardew:monsters/spawn/types/rock_crab
execute if score #monster_type sd_temp matches 3 positioned ~ ~ ~ run function stardew:monsters/spawn/types/bug
execute if score #monster_type sd_temp matches 4 positioned ~ ~ ~ run function stardew:monsters/spawn/types/bat
execute if score #monster_type sd_temp matches 5 positioned ~ ~ ~ run function stardew:monsters/spawn/types/bat
