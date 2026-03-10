# stardew:mine/floor/do_generate.mcfunction
# 实际执行房间生成
# 执行者: 玩家 (@s)

# 在矿洞维度生成房间
execute in stardew:mine run function stardew:mine/floor/generate_room_impl with storage stardew:mine gen

# 生成怪物
execute in stardew:mine run function stardew:monsters/spawn/spawn_on_floor
