# 下一页

# 增加页码 (需要根据季节判断最大页数)
scoreboard players add @a[scores={sd_in_shop=1..}] sd_shop_page 1

# 限制最大页码 (春季6页=索引6, 夏季7页=索引7, 秋季7页=索引7, 冬季4页=索引4)
execute as @a[scores={sd_in_shop=1..,sd_shop_season=1,sd_shop_page=7..}] run scoreboard players set @s sd_shop_page 6
execute as @a[scores={sd_in_shop=1..,sd_shop_season=2,sd_shop_page=8..}] run scoreboard players set @s sd_shop_page 7
execute as @a[scores={sd_in_shop=1..,sd_shop_season=3,sd_shop_page=8..}] run scoreboard players set @s sd_shop_page 7
execute as @a[scores={sd_in_shop=1..,sd_shop_season=4,sd_shop_page=5..}] run scoreboard players set @s sd_shop_page 4

# 更新显示
execute as @a[scores={sd_in_shop=1..}] run function stardew:shop/update_display

# 播放音效
execute as @a[scores={sd_in_shop=1..}] at @s run playsound minecraft:ui.button.click master @s ~ ~ ~ 0.5 1