# 上一页

# 减少页码 (最小为0)
scoreboard players remove @a[scores={sd_in_shop=1..}] sd_shop_page 1
execute as @a[scores={sd_in_shop=1..,sd_shop_page=..-1}] run scoreboard players set @s sd_shop_page 0

# 更新显示
execute as @a[scores={sd_in_shop=1..}] run function stardew:shop/update_display

# 播放音效
execute as @a[scores={sd_in_shop=1..}] at @s run playsound minecraft:ui.button.click master @s ~ ~ ~ 0.5 1