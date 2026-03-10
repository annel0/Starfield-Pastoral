# stardew:mine/floor/cleanup_floor.mcfunction
# 清理指定楼层的旧实体
# 参数: $(z15) - 房间中心Z坐标

$execute in stardew:mine positioned 15 65 $(z15) run kill @e[tag=sd_mine_entity,distance=..25]
$execute in stardew:mine positioned 15 65 $(z15) run kill @e[tag=sd_stone,distance=..25]
$execute in stardew:mine positioned 15 65 $(z15) run kill @e[tag=sd_stone_display,distance=..25]
