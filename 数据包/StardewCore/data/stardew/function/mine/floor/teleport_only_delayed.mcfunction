# stardew:mine/floor/teleport_only_delayed.mcfunction
# 延迟执行：传送玩家到已存在的楼层
# 由 forceload_teleport schedule 3t 调用

# 使用楼层分数来定位玩家
execute as @a if score @s sd_mine_floor matches 1.. in stardew:mine run function stardew:mine/floor/teleport_only

# 【安全网】传送后检测是否掉虚空,如果Y<50则强制传回安全位置
schedule function stardew:mine/floor/check_void_safety 2t

# 卸载强制加载的区块
execute as @a if score @s sd_mine_floor matches 1.. run function stardew:mine/floor/forceload_remove_teleport with storage stardew:mine teleport
