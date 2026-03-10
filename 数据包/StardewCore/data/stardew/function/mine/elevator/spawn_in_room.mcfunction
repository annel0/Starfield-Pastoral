# stardew:mine/elevator/spawn_in_room.mcfunction
# 在矿洞房间内生成电梯
# 参数: $(z) - 房间 Z 坐标

# 电梯位置 (房间角落)
$execute in stardew:mine positioned -10 65 {add:5,value:$(z)} run function stardew:mine/elevator/spawn_entity
