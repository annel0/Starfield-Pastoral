# stardew:mine/debug/test_spawn_loop.mcfunction  
# 测试矿石生成循环
# 在矿洞内执行此命令

tellraw @s {"text":"===== 测试矿石生成循环 =====","color":"gold"}

# 显示当前层数
tellraw @s [{"text":"当前层数: ","color":"gray"},{"score":{"name":"@s","objective":"sd_mine_floor"}}]

# 手动设置 Z 范围 (假设在第1层，z=100 到 z=122)
scoreboard players set #room_z_min sd_mine_temp 102
scoreboard players set #room_z_max sd_mine_temp 122

tellraw @s [{"text":"Z 范围: ","color":"gray"},{"score":{"name":"#room_z_min","objective":"sd_mine_temp"}},{"text":" - "},{"score":{"name":"#room_z_max","objective":"sd_mine_temp"}}]

# 只生成 5 个测试
scoreboard players set #spawn_count sd_mine_temp 5

tellraw @s {"text":"开始生成 5 个矿石...","color":"yellow"}

# 调用循环
function stardew:mine/floor/spawn_stone_loop

tellraw @s [{"text":"生成完成! 石头数量: ","color":"green"},{"score":{"name":"@s","objective":"sd_mine_stones"}}]
