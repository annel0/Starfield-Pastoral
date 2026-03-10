# stardew:monsters/spawn/spawn_by_floor.mcfunction
# 根据当前矿洞层数生成怪物

# 获取当前层数 (从记分板或存储获取)
# 假设玩家有 sd_mine_floor 记分板

# 1-10层
execute if score @s sd_mine_floor matches 1..10 run function stardew:monsters/spawn/floor_1_10

# 11-30层
execute if score @s sd_mine_floor matches 11..30 run function stardew:monsters/spawn/floor_1_10

# 31-40层
execute if score @s sd_mine_floor matches 31..40 run function stardew:monsters/spawn/floor_31_40

# 41-60层
execute if score @s sd_mine_floor matches 41..60 run function stardew:monsters/spawn/floor_41_60

# 61-70层
execute if score @s sd_mine_floor matches 61..70 run function stardew:monsters/spawn/floor_41_60

# 71-90层
execute if score @s sd_mine_floor matches 71..90 run function stardew:monsters/spawn/floor_71_90

# 91-120层
execute if score @s sd_mine_floor matches 91..120 run function stardew:monsters/spawn/floor_71_90

tellraw @s {"text":"怪物已在当前层生成！","color":"yellow"}
