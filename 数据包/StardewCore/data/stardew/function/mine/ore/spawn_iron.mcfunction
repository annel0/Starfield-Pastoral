# stardew:mine/ore/spawn_iron.mcfunction
# 生成铁矿 (冰川主题)

data modify storage stardew:macro_args args set value {stone_type:"iron",stone_hp:25,required_pickaxe_tier:2,ore_drop_type:"resource",model_cmd:7214,name:"铁矿"}
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run function stardew:mining/spawn_stone_impl with storage stardew:macro_args args
data remove storage stardew:macro_args args
execute in stardew:mine align xyz positioned ~0.5 ~ ~0.5 run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..0.5] add sd_mine_stone

# 注意: 石头计数由 try_spawn_stone.mcfunction 处理，这里不重复计数
