# stardew:mine/floor/generate_room_delayed.mcfunction
# 延迟执行：生成新房间并传送玩家
# 由 generate_room schedule 1t 调用（在清理之后）

# 使用楼层分数来定位玩家（更可靠）
# 因为只有刚进入该楼层的玩家才会有对应的 sd_mine_floor 分数
execute as @a if score @s sd_mine_floor = #generating_floor sd_temp in stardew:mine run function stardew:mine/floor/generate_room_impl with storage stardew:mine gen

# 生成怪物（同样用楼层分数定位）
# 【修复】宝箱层（25/50/75/100）不生成怪物
execute as @a if score @s sd_mine_floor = #generating_floor sd_temp unless score @s sd_mine_floor matches 25 unless score @s sd_mine_floor matches 50 unless score @s sd_mine_floor matches 75 unless score @s sd_mine_floor matches 100 in stardew:mine run function stardew:monsters/spawn/spawn_on_floor

# 卸载强制加载的区块
function stardew:mine/floor/forceload_remove with storage stardew:mine gen
