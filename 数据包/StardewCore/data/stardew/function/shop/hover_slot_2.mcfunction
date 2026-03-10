# 悬停在槽位2的interaction上
# [执行者: interaction slot_2]

# 杀死射线
kill @e[tag=sd_shop_ray]

# 标记interaction为被悬停
scoreboard players set @s sd_shop_hover 1

# 如果是首次悬停（上一tick没悬停），触发item_icon高光和tooltip
execute unless score @s sd_shop_hover_prev matches 1 run function stardew:shop/on_hover_slot_2
