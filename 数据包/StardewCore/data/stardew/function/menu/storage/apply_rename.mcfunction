# data/stardew/function/menu/storage/apply_rename.mcfunction
# 应用新名称

# 获取当前选中的背包ID
execute store result storage stardew:temp macro.bag_id int 1 run scoreboard players get @s sd_storage_selected

# 将新名称写入storage
function stardew:menu/storage/apply_rename_macro with storage stardew:temp macro

# 清除副手的书
item replace entity @s weapon.offhand with air

# 退出重命名状态
scoreboard players set @s sd_storage_renaming 0

tellraw @s {"text":"重命名完成！","color":"green"}

# 刷新显示
execute if score @s sd_menu_level matches 261 run function stardew:menu/storage/pages/bag_detail
