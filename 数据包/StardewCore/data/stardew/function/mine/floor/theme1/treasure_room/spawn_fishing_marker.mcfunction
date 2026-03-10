# stardew:mine/floor/theme1/treasure_room/spawn_fishing_marker.mcfunction
# 在宝藏房间生成钓鱼区标记
# 参数: $(z27)
# 位置: X=14, Y=65, Z+27
# 用途: 标记这里是矿井钓鱼区

$execute in stardew:mine run summon minecraft:marker 14 65 $(z27) {Tags:["stardew","sd_mine_fishing_zone"]}
