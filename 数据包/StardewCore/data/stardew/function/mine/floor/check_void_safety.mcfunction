# stardew:mine/floor/check_void_safety.mcfunction
# 检测玩家是否掉入虚空并进行紧急传送
# 在teleport_only_delayed后2tick执行

# 检测矿井中的玩家是否Y<50 (掉虚空)
execute as @a[nbt={Dimension:"stardew:mine"}] if score @s sd_mine_floor matches 1.. at @s if entity @s[y=0,dy=50] run function stardew:mine/floor/emergency_teleport
