# Theme4 Treasure Room 生成逻辑 (Layer 100)
# 房间大小: 30x6x40
# 这是矿洞的最深处，没有往下的坑
# 参数: $(z) - 从 storage stardew:mine gen 传入

# ===== 放置结构 =====
$execute in stardew:mine run place template stardew:mine/theme4/treasure_room 0 64 $(z)

# ===== 传送玩家到 X=14, Z+1 =====
$execute in stardew:mine run tp @s 14 65 $(z) ~ ~
execute in stardew:mine run tp @s ~ ~ ~1 180 0

# ===== 生成出口梯子 (X=14, Z+1) =====
$execute in stardew:mine positioned 14 65 $(z) positioned ~ ~ ~1 run function stardew:mine/ladder/spawn_exit

# ===== 第100层固定生成电梯 (X=17, Z+1) =====
$execute in stardew:mine positioned 17 65 $(z) positioned ~ ~ ~1 run function stardew:mine/elevator/spawn_entity

# ===== 生成"最深处"标记 (X=9, Z+13) - 下调1格到Y=64，第100层没有坑 =====
$execute in stardew:mine positioned 9 64 $(z) positioned ~ ~ ~13 run summon minecraft:text_display ~ ~2.0 ~ {Tags:["sd_mine_entity"],text:'{"text":"⬤ 矿洞最深处","color":"gold","bold":true}',billboard:"vertical",shadow:true,transformation:{scale:[0.8f,0.8f,0.8f]}}

# ===== 生成宝箱 (X=14, Z+12) - 使用 theme4 战利品表 =====
$execute in stardew:mine positioned 14 65 $(z) positioned ~ ~ ~12 run data remove block ~ ~ ~ Items
$execute in stardew:mine positioned 14 65 $(z) positioned ~ ~ ~12 run setblock ~ ~ ~ minecraft:chest[facing=south]{LootTable:"stardew:mining/treasure_room/theme4"}

# ===== 生成钓鱼区标记 (X=14, Z+27) =====
$execute in stardew:mine positioned 14 65 $(z) positioned ~ ~ ~27 run summon minecraft:marker ~ ~ ~ {Tags:["sd_fishing_spot","sd_mine_entity","sd_mine_theme4"]}
