# data/stardew/function/menu/storage/read_bag_display_data_macro.mcfunction
# 读取背包的名字和颜色数据
# $bag_id - 背包ID

# 读取背包名字和颜色
$execute store result storage stardew:temp current_bag_color int 1 run data get storage stardew:storage bags[$(bag_id)].color
$data modify storage stardew:temp current_bag_name set from storage stardew:storage bags[$(bag_id)].name

# 存储颜色到临时记分板（用于后续判断）
execute store result score #temp sd_storage_temp run data get storage stardew:temp current_bag_color

# 映射颜色ID到颜色名（对应bundle颜色）
execute if score #temp sd_storage_temp matches 0 run data modify storage stardew:temp color_name set value "yellow"
execute if score #temp sd_storage_temp matches 1 run data modify storage stardew:temp color_name set value "white"
execute if score #temp sd_storage_temp matches 2 run data modify storage stardew:temp color_name set value "gray"
execute if score #temp sd_storage_temp matches 3 run data modify storage stardew:temp color_name set value "dark_gray"
execute if score #temp sd_storage_temp matches 4 run data modify storage stardew:temp color_name set value "black"
execute if score #temp sd_storage_temp matches 5 run data modify storage stardew:temp color_name set value "gold"
execute if score #temp sd_storage_temp matches 6 run data modify storage stardew:temp color_name set value "red"
execute if score #temp sd_storage_temp matches 7 run data modify storage stardew:temp color_name set value "gold"
execute if score #temp sd_storage_temp matches 8 run data modify storage stardew:temp color_name set value "yellow"
execute if score #temp sd_storage_temp matches 9 run data modify storage stardew:temp color_name set value "green"
execute if score #temp sd_storage_temp matches 10 run data modify storage stardew:temp color_name set value "dark_green"
execute if score #temp sd_storage_temp matches 11 run data modify storage stardew:temp color_name set value "dark_aqua"
execute if score #temp sd_storage_temp matches 12 run data modify storage stardew:temp color_name set value "aqua"
execute if score #temp sd_storage_temp matches 13 run data modify storage stardew:temp color_name set value "blue"
execute if score #temp sd_storage_temp matches 14 run data modify storage stardew:temp color_name set value "dark_purple"
execute if score #temp sd_storage_temp matches 15 run data modify storage stardew:temp color_name set value "light_purple"
execute if score #temp sd_storage_temp matches 16 run data modify storage stardew:temp color_name set value "light_purple"

# 召唤矿车（硬编码"背包"文字，应用背包颜色）
function stardew:menu/storage/summon_cart_with_color with storage stardew:temp
