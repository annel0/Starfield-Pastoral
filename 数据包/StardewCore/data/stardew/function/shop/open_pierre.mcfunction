# 打开Pierre商店 - 入口函数
# 用法: execute as <player> run function stardew:shop/open_pierre

# 传送玩家到interiors维度的固定位置
execute in stardew:interiors run tp @s 0 64 7 0 0

# 设置玩家状态为"在商店中"
scoreboard players set @s sd_in_shop 1

# 从游戏全局季节同步到商店季节
scoreboard players operation @s sd_shop_season = Global sd_season
scoreboard players set @s sd_shop_page 0

# 强制初始化商店数据 (测试用)
function stardew:shop/init_pierre

# 召唤UI
function stardew:shop/summon_ui_fixed

# 播放打开动画
function stardew:shop/animate_open

# 更新商品显示
execute as @s run function stardew:shop/update_display

# Debug信息
tellraw @s [{"text":"商店已打开! 季节: ","color":"green"},{"score":{"name":"@s","objective":"sd_shop_season"},"color":"gold"}]

# 播放打开音效
playsound minecraft:block.chest.open master @s ~ ~ ~ 1 1

# 显示欢迎信息
tellraw @s {"text":"欢迎来到Pierre的杂货店!","color":"green"}