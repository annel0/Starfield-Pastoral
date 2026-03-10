# data/stardew/function/menu/storage/rename_too_long.mcfunction
# 名称太长，返回书

tellraw @s {"text":"背包名称超过5个字符！请重新输入。","color":"red"}
give @s writable_book[custom_name='{"text":"输入背包名称（最多5字符）","italic":false}'] 1
