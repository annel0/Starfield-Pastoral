# data/stardew/function/menu/storage/build_cart_name_json.mcfunction
# 使用宏构建矿车的CustomName JSON
# $color_name - 颜色名
# current_bag_name - 背包名字（从storage读取）

# 构建完整的JSON：{"text":"背包名","color":"颜色"}
$data modify storage stardew:temp cart_name_json set value '{"nbt":"current_bag_name","storage":"stardew:temp","color":"$(color_name)"}'
