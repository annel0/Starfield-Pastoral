# data/stardew/function/menu/storage/hover/display_bag_name_macro.mcfunction
# 显示背包名称到text_display实体上
# 宏参数: $(bag_id) $(display_num)

# 读取背包名称和颜色
$data modify storage stardew:temp current_bag_name set from storage stardew:storage bags[$(bag_id)].name
$execute store result score #BagColor sd_bag_color run data get storage stardew:storage bags[$(bag_id)].color

# 根据颜色ID设置对应的颜色名称
execute if score #BagColor sd_bag_color matches 0 run data modify storage stardew:temp color_name set value "yellow"
execute if score #BagColor sd_bag_color matches 1 run data modify storage stardew:temp color_name set value "white"
execute if score #BagColor sd_bag_color matches 2 run data modify storage stardew:temp color_name set value "gray"
execute if score #BagColor sd_bag_color matches 3 run data modify storage stardew:temp color_name set value "dark_gray"
execute if score #BagColor sd_bag_color matches 4 run data modify storage stardew:temp color_name set value "black"
execute if score #BagColor sd_bag_color matches 5 run data modify storage stardew:temp color_name set value "gold"
execute if score #BagColor sd_bag_color matches 6 run data modify storage stardew:temp color_name set value "red"
execute if score #BagColor sd_bag_color matches 7 run data modify storage stardew:temp color_name set value "gold"
execute if score #BagColor sd_bag_color matches 8 run data modify storage stardew:temp color_name set value "yellow"
execute if score #BagColor sd_bag_color matches 9 run data modify storage stardew:temp color_name set value "green"
execute if score #BagColor sd_bag_color matches 10 run data modify storage stardew:temp color_name set value "dark_green"
execute if score #BagColor sd_bag_color matches 11 run data modify storage stardew:temp color_name set value "dark_aqua"
execute if score #BagColor sd_bag_color matches 12 run data modify storage stardew:temp color_name set value "aqua"
execute if score #BagColor sd_bag_color matches 13 run data modify storage stardew:temp color_name set value "blue"
execute if score #BagColor sd_bag_color matches 14 run data modify storage stardew:temp color_name set value "dark_purple"
execute if score #BagColor sd_bag_color matches 15 run data modify storage stardew:temp color_name set value "light_purple"
execute if score #BagColor sd_bag_color matches 16 run data modify storage stardew:temp color_name set value "light_purple"

# 使用宏显示带颜色的文字
function stardew:menu/storage/hover/display_colored_text_macro with storage stardew:temp
