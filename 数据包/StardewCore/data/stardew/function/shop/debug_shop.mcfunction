# Debug商店系统 - 检查所有状态

# 检查玩家分数
tellraw @s [{"text":"=== 商店Debug信息 ===","color":"gold","bold":true}]
tellraw @s [{"text":"sd_in_shop: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_in_shop"},"color":"white"}]
tellraw @s [{"text":"sd_shop_season: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_shop_season"},"color":"white"}]
tellraw @s [{"text":"sd_shop_page: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_shop_page"},"color":"white"}]
tellraw @s [{"text":"Global sd_season: ","color":"yellow"},{"score":{"name":"Global","objective":"sd_season"},"color":"white"}]

# 检查storage数据
execute if data storage stardew:shop pierre.spring[0] run tellraw @s {"text":"✓ pierre.spring[0] 存在","color":"green"}
execute unless data storage stardew:shop pierre.spring[0] run tellraw @s {"text":"✗ pierre.spring[0] 不存在","color":"red"}

# 检查当前页数据
execute if data storage stardew:temp current_page run tellraw @s {"text":"✓ current_page 存在","color":"green"}
execute unless data storage stardew:temp current_page run tellraw @s {"text":"✗ current_page 不存在","color":"red"}

# 输出current_page内容
execute if data storage stardew:temp current_page run tellraw @s [{"text":"current_page: ","color":"yellow"},{"nbt":"current_page","storage":"stardew:temp","color":"white"}]

# 检查UI实体数量
execute store result score #ui_count sd_temp if entity @e[type=item_display,tag=shop_ui]
execute store result score #interaction_count sd_temp if entity @e[type=interaction,tag=shop_interaction]
tellraw @s [{"text":"item_display数量: ","color":"yellow"},{"score":{"name":"#ui_count","objective":"sd_temp"},"color":"white"}]
tellraw @s [{"text":"interaction数量: ","color":"yellow"},{"score":{"name":"#interaction_count","objective":"sd_temp"},"color":"white"}]

# 检查商品图标实体
execute store result score #icon_count sd_temp if entity @e[type=item_display,tag=shop_ui,tag=item_icon]
tellraw @s [{"text":"商品图标数量: ","color":"yellow"},{"score":{"name":"#icon_count","objective":"sd_temp"},"color":"white"}]

# 检查商品名称实体
execute store result score #name_count sd_temp if entity @e[type=text_display,tag=shop_ui,tag=item_name]
tellraw @s [{"text":"商品名称数量: ","color":"yellow"},{"score":{"name":"#name_count","objective":"sd_temp"},"color":"white"}]
