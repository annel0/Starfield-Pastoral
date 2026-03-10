# data/stardew/function/menu/storage/check_rename.mcfunction
# 检测玩家是否按F键将书与笔放到副手

# 检测副手是否有书
execute as @a[scores={sd_storage_renaming=1}] if items entity @s weapon.offhand writable_book run tellraw @s [{"text":"[调试] 检测到副手有writable_book！","color":"green"}]
execute as @a[scores={sd_storage_renaming=1}] if items entity @s weapon.offhand writable_book run function stardew:menu/storage/process_rename
