# stardew:mine/ore/spawn_coal.mcfunction
# 生成煤矿 (主题1样式)

data modify storage stardew:macro_args args set value {stone_type:"coal",stone_hp:15,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7205,name:"煤矿"}
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run function stardew:mining/spawn_stone_impl with storage stardew:macro_args args
data remove storage stardew:macro_args args
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_mine_stone

# 注意: 石头计数由 try_spawn_stone.mcfunction 处理，这里不重复计数
