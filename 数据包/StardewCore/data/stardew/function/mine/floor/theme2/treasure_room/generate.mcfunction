# stardew:mine/floor/theme2/treasure_room/generate.mcfunction
# 生成 theme2 宝藏房间 (30x6x40) - 仅在第50层出现
# 参数: $(z) - 从 storage stardew:mine gen 传入
# 玩家出生/梯子: X=14 Z+1, 电梯: X=17 Z+1
# 固定坑: X=9 Z+13, 宝箱: X=14 Z+12, 钓鱼区: X=14 Z+27
# 特点: 不生成矿物,固定生成通往下层的坑

# ===== 放置结构 =====
$execute in stardew:mine run place template stardew:mine/theme2/treasure_room 0 64 $(z)

# ===== 传送玩家到 X=14, Z+1 =====
$execute in stardew:mine run tp @s 14 65 $(z) ~ ~
execute in stardew:mine run tp @s ~ ~ ~1 180 0

# ===== 生成出口梯子 (X=14, Z+1) =====
$execute in stardew:mine positioned 14 65 $(z) positioned ~ ~ ~1 run function stardew:mine/ladder/spawn_exit

# ===== 第50层固定生成电梯 (X=17, Z+1) =====
$execute in stardew:mine positioned 17 65 $(z) positioned ~ ~ ~1 run function stardew:mine/elevator/spawn_entity

# ===== 固定生成通往下层的坑 (X=9, Z+13) - 下调1格到Y=64 =====
$execute in stardew:mine positioned 9 64 $(z) positioned ~ ~ ~13 run function stardew:mine/ladder/spawn_pit

# ===== 生成宝箱 (X=14, Z+12) - 使用 theme2 战利品表 =====
$execute in stardew:mine positioned 14 65 $(z) positioned ~ ~ ~12 run data remove block ~ ~ ~ Items
$execute in stardew:mine positioned 14 65 $(z) positioned ~ ~ ~12 run setblock ~ ~ ~ minecraft:chest[facing=south]{LootTable:"stardew:mining/treasure_room/theme2"}

# ===== 生成钓鱼区标记 (X=14, Z+27) =====
$execute in stardew:mine positioned 14 65 $(z) positioned ~ ~ ~27 run summon minecraft:marker ~ ~ ~ {Tags:["sd_fishing_spot","sd_mine_entity","sd_mine_theme2"]}

# ===== 不生成矿石，这是宝藏房间 =====
