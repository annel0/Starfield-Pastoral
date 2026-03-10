# data/stardew/function/menu/storage/buttons/select_bag_1.mcfunction
# 选择背夅1并进入详细菜单

scoreboard players operation @s sd_storage_selected = #StartBag sd_storage_page
# 只有当背包已解锁时才能进入（bag_id < sd_bag_count）
execute if score @s sd_storage_selected < @s sd_bag_count run function stardew:menu/storage/pages/bag_detail
