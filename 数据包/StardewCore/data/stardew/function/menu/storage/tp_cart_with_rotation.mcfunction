# data/stardew/function/menu/storage/tp_cart_with_rotation.mcfunction
# 使用宏传送矿车并设置旋转
# $cart_yaw - 矿车的yaw旋转角度

$tp @e[type=chest_minecart,tag=sd_storage_cart,limit=1,sort=nearest] ~ ~0.3 ~ $(cart_yaw) 0
