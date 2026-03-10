# stardew:mine/floor/theme2/treasure_room/spawn_chest.mcfunction
# 在宝藏房间生成宝箱（theme2 冰川主题）
# 参数: $(z12)
# 位置: X=14, Y=65, Z+12

# 先清空旧箱子的物品（避免重新生成时物品爆出来）
$execute in stardew:mine run data remove block 14 65 $(z12) Items

# 生成新的宝箱（使用 theme2 战利品表）
$execute in stardew:mine run setblock 14 65 $(z12) minecraft:chest[facing=south]{LootTable:"stardew:mining/treasure_room/theme2"}
