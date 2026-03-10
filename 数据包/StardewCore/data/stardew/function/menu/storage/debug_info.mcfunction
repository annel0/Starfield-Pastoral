# data/stardew/function/menu/storage/debug_info.mcfunction
# 显示调试信息

tellraw @s [{"text":"=== 存储系统调试信息 ===","color":"gold"}]
tellraw @s [{"text":"背包数量: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_bag_count"},"color":"green"}]
tellraw @s [{"text":"菜单层级: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_menu_level"},"color":"green"}]
tellraw @s [{"text":"菜单页码: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_menu_page"},"color":"green"}]
tellraw @s [{"text":"选中背包: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_storage_selected"},"color":"green"}]
tellraw @s [{"text":"打开背包: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_storage_opened"},"color":"green"}]
tellraw @s [{"text":"矿车激活: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_storage_cart_active"},"color":"green"}]
tellraw @s [{"text":"重命名中: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_storage_renaming"},"color":"green"}]

# 检查副手物品
execute if items entity @s weapon.offhand writable_book run tellraw @s [{"text":"副手: 书与笔","color":"aqua"}]
execute unless items entity @s weapon.offhand writable_book run tellraw @s [{"text":"副手: 无书与笔","color":"red"}]

# 检查矿车实体
execute store result score #CartCount sd_storage_temp if entity @e[type=chest_minecart,tag=sd_storage_cart]
tellraw @s [{"text":"矿车数量: ","color":"yellow"},{"score":{"name":"#CartCount","objective":"sd_storage_temp"},"color":"green"}]
