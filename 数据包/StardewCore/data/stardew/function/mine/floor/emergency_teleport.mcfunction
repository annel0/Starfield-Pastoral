# stardew:mine/floor/emergency_teleport.mcfunction
# 紧急传送 - 玩家掉入虚空时强制传回安全位置
# 执行者: @s (掉虚空的玩家)

# 警告信息
tellraw @s [{"text":"[矿井警告] ","color":"red","bold":true},{"text":"检测到虚空传送失败,尝试紧急修复...","color":"yellow"}]

# 重新计算传送位置
execute store result score #emergency_z sd_mine_temp run scoreboard players get @s sd_mine_floor
scoreboard players operation #emergency_z sd_mine_temp *= #100 sd_const
scoreboard players add #emergency_z sd_mine_temp 5

# 强制传送到目标层的中心安全位置 (X=25, Y=65, Z=floor*100+5)
execute store result storage stardew:mine emergency.z int 1 run scoreboard players get #emergency_z sd_mine_temp
function stardew:mine/floor/emergency_teleport_impl with storage stardew:mine emergency

# 播放警告音效
playsound minecraft:block.anvil.land master @s ~ ~ ~ 1 1
playsound minecraft:entity.enderman.teleport master @s ~ ~ ~ 1 0.8

tellraw @s [{"text":"[矿井] ","color":"gray"},{"text":"已传送到第","color":"white"},{"score":{"name":"@s","objective":"sd_mine_floor"},"color":"gold"},{"text":"层的安全位置","color":"white"}]
