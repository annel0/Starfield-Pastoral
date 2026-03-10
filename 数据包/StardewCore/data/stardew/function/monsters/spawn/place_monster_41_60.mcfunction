# stardew:monsters/spawn/place_monster_41_60.mcfunction
# 41-60层怪物类型 (冰霜区域)

execute store result score #monster_type sd_temp run random value 1..6
execute store result score #spawn_x sd_temp run random value -15..15
execute store result score #spawn_z sd_temp run random value -15..15

execute if score #monster_type sd_temp matches 1 positioned ~ ~ ~ run function stardew:monsters/spawn/types/blue_slime
execute if score #monster_type sd_temp matches 2 positioned ~ ~ ~ run function stardew:monsters/spawn/types/frost_bat
execute if score #monster_type sd_temp matches 3 positioned ~ ~ ~ run function stardew:monsters/spawn/types/frost_bat
execute if score #monster_type sd_temp matches 4 positioned ~ ~ ~ run function stardew:monsters/spawn/types/dust_sprite
execute if score #monster_type sd_temp matches 5 positioned ~ ~ ~ run function stardew:monsters/spawn/types/ghost
execute if score #monster_type sd_temp matches 6 positioned ~ ~ ~ run function stardew:monsters/spawn/types/rock_crab
