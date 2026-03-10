# 显示普通宝箱开到的物品消息
# 从storage中提取custom_name并传递给tellraw
data modify storage stardew:treasure display_name set from storage stardew:treasure temp_item.components."minecraft:custom_name"
function stardew:fishing/treasure_chest/tellraw_common_direct
