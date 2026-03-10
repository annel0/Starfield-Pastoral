# data/stardew/function/menu/storage/summon_cart_final_macro.mcfunction
# 最终召唤矿车，使用宏插入名字和颜色
# $color_name - 颜色名称
# $current_bag_name - 背包名字（NBT格式）

# 召唤箱子矿车
summon chest_minecart ~ ~0.5 ~ {Tags:["sd_storage_cart","sd_storage_new"],Invulnerable:1b,NoGravity:1b}

# 构建带颜色的文本组件并设置为CustomName
$execute as @e[type=chest_minecart,tag=sd_storage_new,limit=1,sort=nearest] run data modify entity @s CustomName set value '{"nbt":"current_bag_name","storage":"stardew:temp","color":"$(color_name)"}'
