# data/stardew/function/menu/storage/set_bag_name_macro.mcfunction
# 设置背包名称的宏函数
# 宏参数: $(bag_id)

# 从临时存储读取新名称并写入bags数组
$data modify storage stardew:storage bags[$(bag_id)].name set from storage stardew:temp check_text
