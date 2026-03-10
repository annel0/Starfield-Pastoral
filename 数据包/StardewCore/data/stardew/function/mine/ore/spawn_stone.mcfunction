# stardew:mine/ore/spawn_stone.mcfunction
# 生成石头 (主题1样式)
# 执行位置: 生成位置 (在 stardew:mine 维度内)

# 使用现有的 mining 系统生成
# 主题1 石头 CMD: 9101 -> 模型 7201
data modify storage stardew:macro_args args set value {stone_type:"stone",stone_hp:10,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7201,name:"石头"}
# 确保在正确维度执行
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run function stardew:mining/spawn_stone_impl with storage stardew:macro_args args
data remove storage stardew:macro_args args

# 给新生成的石头添加矿洞标记 (spawn_stone_impl 已经移除了 sd_new_stone，所以用距离检测)
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_mine_stone

# 注意: 石头计数由 try_spawn_stone.mcfunction 处理，这里不重复计数
