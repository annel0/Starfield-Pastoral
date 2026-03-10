# data/stardew/function/menu/storage/buttons/open_bag.mcfunction
# 打开当前选中的背包（召唤箱子矿车）

# 先保存玩家序列号
execute store result score #MySeq sd_storage_temp run scoreboard players get @s sd_menu_sequence

# 重置检测标记
scoreboard players set #AlreadyOpen sd_storage_temp 0

# 检测是否已经打开了背包（检测是否存在属于该玩家的矿车）
execute as @e[type=chest_minecart,tag=sd_storage_cart] if score @s sd_cart_id = #MySeq sd_storage_temp run scoreboard players set #AlreadyOpen sd_storage_temp 1

# 如果已经打开，提示并返回
execute if score #AlreadyOpen sd_storage_temp matches 1.. run tellraw @s {"text":"\u80cc\u5305\u5df2\u6253\u5f00\uff01","color":"yellow"}
execute if score #AlreadyOpen sd_storage_temp matches 1.. run return 0

# 先清理旧矿车
execute as @e[type=chest_minecart,tag=sd_storage_cart] if score @s sd_cart_id = #MySeq sd_storage_temp run kill @s

# 标记背包已打开
scoreboard players operation @s sd_storage_opened = @s sd_storage_selected

# 准备背包ID用于读取名字和颜色
execute store result storage stardew:temp macro.bag_id int 1 run scoreboard players get @s sd_storage_selected

# 读取背包名字和颜色到storage，并召唤矿车
execute at @s rotated ~ 0 positioned ^ ^ ^1.5 run function stardew:menu/storage/read_bag_display_data_macro with storage stardew:temp macro

# 给新矿车分配玩家ID
scoreboard players operation @e[tag=sd_storage_new,limit=1] sd_cart_id = #MySeq sd_storage_temp

# 加载背包数据到矿车
execute as @e[tag=sd_storage_new,limit=1] run function stardew:menu/storage/load_bag_macro with storage stardew:temp macro

# 移除new标签
tag @e[tag=sd_storage_new] remove sd_storage_new

# 开始每tick检测（让矿车跟随玩家）
scoreboard players set @s sd_storage_cart_active 1
