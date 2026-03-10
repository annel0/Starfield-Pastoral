# data/stardew/function/menu/storage/buttons/rename_bag.mcfunction
# 开始重命名背包

# 检查是否已经在重命名状态
execute if score @s sd_storage_renaming matches 1 run return 0

# 给玩家一本书与笔
give @s writable_book[custom_name='{"text":"输入背包名称（最多10字符）","italic":false}'] 1

# 设置重命名状态
scoreboard players set @s sd_storage_renaming 1

# 提示玩家
tellraw @s {"text":"请在书中输入背包名称（最多10个字符），然后按F键放到副手","color":"green"}