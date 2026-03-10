# 从storage读取CMD和物品ID并启动动画
# 先提取CMD到storage的简单字段
execute store result storage stardew:treasure display_cmd int 1 run data get storage stardew:treasure temp_item.components."minecraft:custom_model_data"

# 提取物品ID到storage
data modify storage stardew:treasure display_item_id set from storage stardew:treasure temp_item.id

# 调用带macro的动画函数
function stardew:fishing/treasure_chest/display_animation_start with storage stardew:treasure
