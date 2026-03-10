# 显示稀有宝箱开到的物品消息
data modify storage stardew:treasure display_name set from storage stardew:treasure temp_item.components."minecraft:custom_name"
function stardew:fishing/treasure_chest/tellraw_rare_direct
