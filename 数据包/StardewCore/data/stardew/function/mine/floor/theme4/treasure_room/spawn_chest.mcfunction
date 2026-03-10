# 生成 Theme4 Treasure Room 的宝箱
# 参数: $(z12)
# 固定位置: X=14 Z+12

$execute in stardew:mine positioned 14 65 $(z12) run function stardew:mine/floor/spawn_chest {loot_table:"stardew:mining/treasure_room/theme4"}
