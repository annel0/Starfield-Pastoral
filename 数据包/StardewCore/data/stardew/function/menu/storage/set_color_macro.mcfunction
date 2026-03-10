# data/stardew/function/menu/storage/set_color_macro.mcfunction
# 使用宏设置颜色
# 参数: $(bag_id) - 背包ID (0-6), $(color) - 颜色ID (0-16)

# 先获取背包对象到临时位置
$data modify storage stardew:temp current_bag set from storage stardew:storage bags[$(bag_id)]

# 修改临时对象的颜色
execute store result storage stardew:temp current_bag.color int 1 run scoreboard players get #SelectedColor sd_bag_color

# 将修改后的对象写回原位置
$data modify storage stardew:storage bags[$(bag_id)] set from storage stardew:temp current_bag

# 清理临时数据
data remove storage stardew:temp current_bag
