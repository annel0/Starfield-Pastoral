# stardew:mine/ore/spawn_emerald.mcfunction
# 生成翡翠矿（使用正确的 jade 类型和 CMD 7228）

data modify storage stardew:macro_args args set value {stone_type:"jade",stone_hp:40,required_pickaxe_tier:3,ore_drop_type:"gem",model_cmd:7228,name:"翡翠矿"}
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run function stardew:mining/spawn_stone_impl with storage stardew:macro_args args
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_gem_ore
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_mine_stone
data remove storage stardew:macro_args args

# 注意: 石头计数由 try_spawn_stone.mcfunction 处理，这里不重复计数
