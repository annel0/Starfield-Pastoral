# 更新商店商品显示 - 根据季节和页码显示3个商品
# 参数: 从玩家的sd_shop_season和sd_shop_page计分板读取
# 需要先初始化商店数据 (调用stardew:shop/init_pierre)

# 获取当前季节和页码
# sd_shop_season: 1=春季 2=夏季 3=秋季 4=冬季
# sd_shop_page: 当前页码 (从0开始)

# === 根据季节选择商品数据 ===
# 临时存储当前页的商品数据到temp storage
data remove storage stardew:temp current_page

# 春季 (season=1)
execute if score @s sd_shop_season matches 1 if score @s sd_shop_page matches 0 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.spring[0]
execute if score @s sd_shop_season matches 1 if score @s sd_shop_page matches 1 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.spring[1]
execute if score @s sd_shop_season matches 1 if score @s sd_shop_page matches 2 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.spring[2]
execute if score @s sd_shop_season matches 1 if score @s sd_shop_page matches 3 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.spring[3]
execute if score @s sd_shop_season matches 1 if score @s sd_shop_page matches 4 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.spring[4]
execute if score @s sd_shop_season matches 1 if score @s sd_shop_page matches 5 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.spring[5]
execute if score @s sd_shop_season matches 1 if score @s sd_shop_page matches 6 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.spring[6]

# 夏季 (season=2)
execute if score @s sd_shop_season matches 2 if score @s sd_shop_page matches 0 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.summer[0]
execute if score @s sd_shop_season matches 2 if score @s sd_shop_page matches 1 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.summer[1]
execute if score @s sd_shop_season matches 2 if score @s sd_shop_page matches 2 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.summer[2]
execute if score @s sd_shop_season matches 2 if score @s sd_shop_page matches 3 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.summer[3]
execute if score @s sd_shop_season matches 2 if score @s sd_shop_page matches 4 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.summer[4]
execute if score @s sd_shop_season matches 2 if score @s sd_shop_page matches 5 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.summer[5]
execute if score @s sd_shop_season matches 2 if score @s sd_shop_page matches 6 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.summer[6]
execute if score @s sd_shop_season matches 2 if score @s sd_shop_page matches 7 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.summer[7]

# 秋季 (season=3)
execute if score @s sd_shop_season matches 3 if score @s sd_shop_page matches 0 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.fall[0]
execute if score @s sd_shop_season matches 3 if score @s sd_shop_page matches 1 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.fall[1]
execute if score @s sd_shop_season matches 3 if score @s sd_shop_page matches 2 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.fall[2]
execute if score @s sd_shop_season matches 3 if score @s sd_shop_page matches 3 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.fall[3]
execute if score @s sd_shop_season matches 3 if score @s sd_shop_page matches 4 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.fall[4]
execute if score @s sd_shop_season matches 3 if score @s sd_shop_page matches 5 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.fall[5]
execute if score @s sd_shop_season matches 3 if score @s sd_shop_page matches 6 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.fall[6]
execute if score @s sd_shop_season matches 3 if score @s sd_shop_page matches 7 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.fall[7]

# 冬季 (season=4)
execute if score @s sd_shop_season matches 4 if score @s sd_shop_page matches 0 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.winter[0]
execute if score @s sd_shop_season matches 4 if score @s sd_shop_page matches 1 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.winter[1]
execute if score @s sd_shop_season matches 4 if score @s sd_shop_page matches 2 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.winter[2]
execute if score @s sd_shop_season matches 4 if score @s sd_shop_page matches 3 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.winter[3]
execute if score @s sd_shop_season matches 4 if score @s sd_shop_page matches 4 run data modify storage stardew:temp current_page set from storage stardew:shop pierre.winter[4]

# === 更新3个商品槽的显示 ===
# 由于item_display的item无法通过data modify直接修改，需要使用loot命令

# 槽位1
execute in stardew:interiors as @e[type=text_display,tag=shop_ui,tag=item_name,tag=slot_1,limit=1] run data modify entity @s text set from storage stardew:temp current_page[0].display_name
execute store result score #price1 sd_temp run data get storage stardew:temp current_page[0].price
execute in stardew:interiors as @e[type=text_display,tag=shop_ui,tag=item_price,tag=slot_1,limit=1] run data modify entity @s text set value '{"score":{"name":"#price1","objective":"sd_temp"},"color":"#853605","bold":true}'

# 槽位2
execute in stardew:interiors as @e[type=text_display,tag=shop_ui,tag=item_name,tag=slot_2,limit=1] run data modify entity @s text set from storage stardew:temp current_page[1].display_name
execute store result score #price2 sd_temp run data get storage stardew:temp current_page[1].price
execute in stardew:interiors as @e[type=text_display,tag=shop_ui,tag=item_price,tag=slot_2,limit=1] run data modify entity @s text set value '{"score":{"name":"#price2","objective":"sd_temp"},"color":"#853605","bold":true}'

# 槽位3
execute in stardew:interiors as @e[type=text_display,tag=shop_ui,tag=item_name,tag=slot_3,limit=1] run data modify entity @s text set from storage stardew:temp current_page[2].display_name
execute store result score #price3 sd_temp run data get storage stardew:temp current_page[2].price
execute in stardew:interiors as @e[type=text_display,tag=shop_ui,tag=item_price,tag=slot_3,limit=1] run data modify entity @s text set value '{"score":{"name":"#price3","objective":"sd_temp"},"color":"#853605","bold":true}'

# === 更新商品图标 ===
# 将item_id复制到temp storage,然后调用更新函数
data modify storage stardew:temp item_id_1 set from storage stardew:temp current_page[0].item_id
data modify storage stardew:temp item_id_2 set from storage stardew:temp current_page[1].item_id
data modify storage stardew:temp item_id_3 set from storage stardew:temp current_page[2].item_id

# 更新三个槽位的图标
function stardew:shop/update_item_icon

