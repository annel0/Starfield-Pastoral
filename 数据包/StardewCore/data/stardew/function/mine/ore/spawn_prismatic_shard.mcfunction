# stardew:mine/ore/spawn_prismatic_shard.mcfunction
# 生成五彩碎片矿石 (CMD 7231)
# 执行位置: 要生成矿石的位置

data modify storage stardew:macro_args args set value {stone_type:"prismatic_shard",stone_hp:80,required_pickaxe_tier:4,ore_drop_type:"gem",model_cmd:7231,name:"五彩碎片矿"}
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run function stardew:mining/spawn_stone_impl with storage stardew:macro_args args
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_gem_ore
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_mine_stone
data remove storage stardew:macro_args args

# 注意: 石头计数由 try_spawn_stone.mcfunction 处理，这里不重复计数
