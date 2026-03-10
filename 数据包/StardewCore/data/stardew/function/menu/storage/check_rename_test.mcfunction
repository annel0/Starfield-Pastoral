# 测试函数
tellraw @s [{"text":"[2] check_rename_test被调用","color":"gold"}]

# 测试items条件
execute if items entity @s weapon.offhand writable_book run tellraw @s [{"text":"[3] 检测到副手writable_book","color":"green"}]
execute unless items entity @s weapon.offhand writable_book run tellraw @s [{"text":"[3] 副手没有writable_book","color":"red"}]

# 显示副手数据
execute if items entity @s weapon.offhand * run tellraw @s [{"text":"[4] 副手物品: ","color":"aqua"},{"nbt":"Inventory[{Slot:-106b}].id","entity":"@s"}]
