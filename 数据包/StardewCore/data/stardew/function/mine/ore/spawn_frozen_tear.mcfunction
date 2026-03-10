# stardew:mine/ore/spawn_frozen_tear.mcfunction
# 生成泪晶矿

data modify storage stardew:macro_args args set value {stone_type:"frozen_tear",stone_hp:30,required_pickaxe_tier:2,ore_drop_type:"gem",model_cmd:7227,name:"泪晶矿"}
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run function stardew:mining/spawn_stone_impl with storage stardew:macro_args args
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_gem_ore
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_mine_stone
data remove storage stardew:macro_args args

# 注意: 石头计数由 try_spawn_stone.mcfunction 处理，这里不重复计数
