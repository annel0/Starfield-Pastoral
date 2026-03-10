# stardew:mine/debug/test_generate.mcfunction
# 测试楼层生成

# 显示当前层数
tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"当前层: "},{"score":{"name":"@s","objective":"sd_mine_floor"},"color":"yellow"}]

# 显示 #need_refresh
tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"need_refresh: "},{"score":{"name":"#need_refresh","objective":"sd_mine_temp"},"color":"yellow"}]

# 显示 #theme
tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"theme: "},{"score":{"name":"#theme","objective":"sd_mine_temp"},"color":"yellow"}]

# 显示 #room_type  
tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"room_type: "},{"score":{"name":"#room_type","objective":"sd_mine_temp"},"color":"yellow"}]

# 显示 gen.z
execute store result score #debug_z sd_mine_temp run data get storage stardew:mine gen.z
tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"gen.z: "},{"score":{"name":"#debug_z","objective":"sd_mine_temp"},"color":"yellow"}]

# 显示 gen.z5
execute store result score #debug_z5 sd_mine_temp run data get storage stardew:mine gen.z5
tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"gen.z5: "},{"score":{"name":"#debug_z5","objective":"sd_mine_temp"},"color":"yellow"}]
