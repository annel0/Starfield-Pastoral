# stardew:mine/ore/spawn_stone_theme3.mcfunction
# 生成 theme3 的石头 (CMD 7203)
# 执行位置: 要生成矿石的位置

data modify storage stardew:macro_args args set value {stone_type:"stone",stone_hp:10,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7203,name:"石头(主题3)"}
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run function stardew:mining/spawn_stone_impl with storage stardew:macro_args args
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_mine_stone
data remove storage stardew:macro_args args

# 注意: 石头计数由 try_spawn_stone.mcfunction 处理，这里不重复计数
