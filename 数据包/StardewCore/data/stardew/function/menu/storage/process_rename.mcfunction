# data/stardew/function/menu/storage/process_rename.mcfunction
# 处理重命名逻辑

# 先清空临时存储
data remove storage stardew:temp rename_text

# 方法1: 直接从SelectedItem读取（玩家正在交互的物品）
data modify storage stardew:temp rename_text set from entity @s SelectedItem.components."minecraft:writable_book_content".pages[0].raw

# 方法2: 如果方法1没有，尝试从副手Inventory读取（新版格式）
execute store result score #RenameLen sd_storage_temp run data get storage stardew:temp rename_text
execute if score #RenameLen sd_storage_temp matches 0 run data modify storage stardew:temp rename_text set from entity @s Inventory[{Slot:-106b}].components."minecraft:writable_book_content".pages[0].raw

# 方法3: 如果还是没有，尝试text版本
execute store result score #RenameLen sd_storage_temp run data get storage stardew:temp rename_text
execute if score #RenameLen sd_storage_temp matches 0 run data modify storage stardew:temp rename_text set from entity @s Inventory[{Slot:-106b}].components."minecraft:writable_book_content".pages[0].text

# 方法4: 直接用pages[0]（可能是字符串）
execute store result score #RenameLen sd_storage_temp run data get storage stardew:temp rename_text
execute if score #RenameLen sd_storage_temp matches 0 run data modify storage stardew:temp rename_text set from entity @s Inventory[{Slot:-106b}].components."minecraft:writable_book_content".pages[0]

# 最终检查长度
execute store result score #NameLength sd_storage_temp run data get storage stardew:temp rename_text

# 如果长度在1-10，应用名称（约5个中文字符成10个英文字符）
execute if score #NameLength sd_storage_temp matches 1..10 run function stardew:menu/storage/apply_rename

# 如果长度>10，拒绝并清除书
execute if score #NameLength sd_storage_temp matches 11.. run tellraw @s {"text":"名称太长！最多10个字符。","color":"red"}
execute if score #NameLength sd_storage_temp matches 11.. run item replace entity @s weapon.offhand with air
execute if score #NameLength sd_storage_temp matches 11.. run scoreboard players set @s sd_storage_renaming 0

# 如果长度为0，提示空白
execute if score #NameLength sd_storage_temp matches 0 run tellraw @s {"text":"无法读取书的内容！请确保已经写入文字。","color":"red"}
