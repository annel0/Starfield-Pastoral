# data/stardew/function/menu/storage/init.mcfunction
# [游戏加载时执行] 初始化存储系统记分板

# 存储页码（用于翻页）
scoreboard objectives add sd_storage_page dummy

# 背包数量（每个玩家拥有的背包数量）
scoreboard objectives add sd_bag_count dummy

# 当前打开的背包编号（0表示未打开）
scoreboard objectives add sd_storage_opened dummy

# 当前选中的背包编号（用于操作）
scoreboard objectives add sd_storage_selected dummy

# 背包颜色（0-16，对应不同颜色的bundle）
scoreboard objectives add sd_bag_color dummy

# 颜色选择子菜单页码
scoreboard objectives add sd_color_page dummy

# 箱子矿车实体ID标记
scoreboard objectives add sd_cart_id dummy

# 重命名状态（1表示正在重命名）
scoreboard objectives add sd_storage_renaming dummy

# 矿车激活状态（1表示矿车跟随中）
scoreboard objectives add sd_storage_cart_active dummy

# 临时变量
scoreboard objectives add sd_storage_temp dummy

# 箱子矿车激活状态
scoreboard objectives add sd_storage_cart_active dummy
