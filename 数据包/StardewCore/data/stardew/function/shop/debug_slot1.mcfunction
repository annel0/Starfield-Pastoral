# 调试 slot1 图标问题
tellraw @a [{"text":"=== Slot1 调试 ===","color":"gold"}]

# 检查 storage 中的 item_id_1
execute if data storage stardew:temp item_id_1 run tellraw @a [{"text":"✓ item_id_1 存在: ","color":"green"},{"nbt":"item_id_1","storage":"stardew:temp","color":"yellow"}]
execute unless data storage stardew:temp item_id_1 run tellraw @a [{"text":"✗ item_id_1 不存在","color":"red"}]

# 检查 current_page[0]
execute if data storage stardew:temp current_page[0] run tellraw @a [{"text":"✓ current_page[0] 存在","color":"green"}]
execute unless data storage stardew:temp current_page[0] run tellraw @a [{"text":"✗ current_page[0] 不存在","color":"red"}]

# 显示完整的 current_page[0] 数据
tellraw @a [{"text":"current_page[0]: ","color":"aqua"},{"nbt":"current_page[0]","storage":"stardew:temp","color":"white"}]

# 检查 slot1 的 item_display 实体
execute in stardew:interiors store result score #slot1_count sd_temp if entity @e[type=item_display,tag=shop_ui,tag=item_icon,tag=slot_1]
tellraw @a [{"text":"Slot1 item_display 数量: ","color":"aqua"},{"score":{"name":"#slot1_count","objective":"sd_temp"},"color":"yellow"}]

# 显示 slot1 的当前物品
execute in stardew:interiors as @e[type=item_display,tag=shop_ui,tag=item_icon,tag=slot_1,limit=1] run data get entity @s item

tellraw @a [{"text":"=================","color":"gold"}]
