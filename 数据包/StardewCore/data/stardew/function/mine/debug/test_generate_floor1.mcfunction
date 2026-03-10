# stardew:mine/debug/test_generate_floor1.mcfunction
# 手动测试生成第一层

tellraw @s {"text":"===== 测试生成第一层 =====","color":"gold"}

# 设置为第一层
scoreboard players set @s sd_mine_floor 1
tellraw @s [{"text":"设置 sd_mine_floor = ","color":"gray"},{"score":{"name":"@s","objective":"sd_mine_floor"},"color":"aqua"}]

# 检查 #100 常量
tellraw @s [{"text":"检查 #100 sd_const = ","color":"gray"},{"score":{"name":"#100","objective":"sd_const"},"color":"aqua"}]

# 手动计算 Z 坐标
execute store result score #target_z sd_mine_temp run scoreboard players get @s sd_mine_floor
tellraw @s [{"text":"初始 #target_z = ","color":"gray"},{"score":{"name":"#target_z","objective":"sd_mine_temp"},"color":"aqua"}]

scoreboard players operation #target_z sd_mine_temp *= #100 sd_const
tellraw @s [{"text":"乘以 100 后 #target_z = ","color":"gray"},{"score":{"name":"#target_z","objective":"sd_mine_temp"},"color":"aqua"}]

# 手动计算偏移
scoreboard players operation #z2 sd_mine_temp = #target_z sd_mine_temp
scoreboard players add #z2 sd_mine_temp 2
tellraw @s [{"text":"#z2 = ","color":"gray"},{"score":{"name":"#z2","objective":"sd_mine_temp"},"color":"aqua"}]

scoreboard players operation #z22 sd_mine_temp = #target_z sd_mine_temp
scoreboard players add #z22 sd_mine_temp 22
tellraw @s [{"text":"#z22 = ","color":"gray"},{"score":{"name":"#z22","objective":"sd_mine_temp"},"color":"aqua"}]

# 测试 storage
execute store result storage stardew:mine test.z int 1 run scoreboard players get #target_z sd_mine_temp
execute store result storage stardew:mine test.z2 int 1 run scoreboard players get #z2 sd_mine_temp
execute store result storage stardew:mine test.z22 int 1 run scoreboard players get #z22 sd_mine_temp

tellraw @s {"text":"storage stardew:mine test 已设置","color":"gray"}
data get storage stardew:mine test

tellraw @s {"text":"===== 现在调用实际生成函数 =====","color":"gold"}
function stardew:mine/floor/generate_room

tellraw @s {"text":"===== 检查生成结果 =====","color":"gold"}

# 检查 #room_z_min 和 #room_z_max
tellraw @s [{"text":"#room_z_min = ","color":"gray"},{"score":{"name":"#room_z_min","objective":"sd_mine_temp"},"color":"aqua"}]
tellraw @s [{"text":"#room_z_max = ","color":"gray"},{"score":{"name":"#room_z_max","objective":"sd_mine_temp"},"color":"aqua"}]
tellraw @s [{"text":"#spawn_count = ","color":"gray"},{"score":{"name":"#spawn_count","objective":"sd_mine_temp"},"color":"aqua"}]
tellraw @s [{"text":"@s sd_mine_stones = ","color":"gray"},{"score":{"name":"@s","objective":"sd_mine_stones"},"color":"aqua"}]

tellraw @s {"text":"===== 测试完成 =====","color":"gold"}
