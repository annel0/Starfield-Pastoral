# data/stardew/function/menu/storage/buttons/set_color.mcfunction
# 设置背包颜色（通过参数）
# 调用方式: scoreboard players set #SelectedColor sd_bag_color <颜色ID>

# 保存颜色到storage
execute store result storage stardew:temp macro.bag_id int 1 run scoreboard players get @s sd_storage_selected
function stardew:menu/storage/set_color_macro with storage stardew:temp macro

# 返回背包详情以显示新颜色
function stardew:menu/storage/pages/bag_detail
