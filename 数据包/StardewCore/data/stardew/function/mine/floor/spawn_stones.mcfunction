# stardew:mine/floor/spawn_stones.mcfunction
# 在房间内随机生成矿石
# 参数: $(z_min), $(z_max) - 房间 Z 范围

# 重置石头计数
scoreboard players set @s sd_mine_stones 0

# 保存 Z 范围到 scoreboard 供循环使用
$scoreboard players set #room_z_min sd_mine_temp $(z_min)
$scoreboard players set #room_z_max sd_mine_temp $(z_max)

# 生成 30-50 个石头 (简易版固定数量)
scoreboard players set #spawn_count sd_mine_temp 40
function stardew:mine/floor/spawn_stone_loop
