# data/stardew/function/menu/storage/save_and_close_cart.mcfunction
# [执行者: 玩家] 保存数据并关闭箱子矿车

# 保存背包ID到storage供宏使用
execute store result storage stardew:temp macro.bag_id int 1 run scoreboard players get @s sd_storage_selected

# 找到矿车并先把Items存到storage的临时位置
execute at @s as @e[type=chest_minecart,tag=sd_storage_cart,limit=1,sort=nearest] run data modify storage stardew:temp cart_items set from entity @s Items

# 然后调用宏把临时数据保存到正确位置
function stardew:menu/storage/save_cart_items_macro with storage stardew:temp macro

# 清除临时数据
data remove storage stardew:temp cart_items

# 清空矿车物品防止掉落
execute at @s as @e[type=chest_minecart,tag=sd_storage_cart,limit=1,sort=nearest] run data modify entity @s Items set value []

# 最后杀死矿车
execute at @s run kill @e[type=chest_minecart,tag=sd_storage_cart,limit=1,sort=nearest]

# 重置状态
scoreboard players set @s sd_storage_cart_active 0
scoreboard players reset @s sd_storage_opened
