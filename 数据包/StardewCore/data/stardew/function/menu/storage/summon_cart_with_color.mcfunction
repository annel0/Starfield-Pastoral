# data/stardew/function/menu/storage/summon_cart_with_color.mcfunction
# 使用宏召唤带颜色名字的矿车
# $color_name - 颜色名

$summon chest_minecart ~ ~0.5 ~ {Tags:["sd_storage_cart","sd_storage_new"],CustomName:'{"text":"背包","color":"$(color_name)"}',Invulnerable:1b,NoGravity:1b}
