# stardew:mine/ore/spawn_amethyst.mcfunction
# 生成紫水晶矿

data modify storage stardew:macro_args args set value {stone_type:"amethyst",stone_hp:45,required_pickaxe_tier:3,ore_drop_type:"gem",model_cmd:7230,name:"紫水晶矿"}
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run function stardew:mining/spawn_stone_impl with storage stardew:macro_args args
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_gem_ore
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_mine_stone
data remove storage stardew:macro_args args

# 注意: 石头计数由 try_spawn_stone.mcfunction 处理，这里不重复计数
