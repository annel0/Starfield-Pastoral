# stardew:museum/identify_item
# 执行鉴定动画和物品转换
# macro参数: item_type, item_name, item_cn, cmd

# 存储副手物品数量
execute store result storage stardew:temp identify_count int 1 run data get entity @s Inventory[{Slot:-106b}].count

# 播放鉴定动画
$function stardew:museum/identify_animation {cmd:$(cmd)}

# 清除副手的unknown版本
item replace entity @s weapon.offhand with air

# 给予已鉴定版本(数量来自storage)
$function stardew:museum/give_identified_count {item_type:"$(item_type)",item_name:"$(item_name)"}

# 提示玩家
$tellraw @s [{"text":"✨ ","color":"gold"},{"text":"鉴定成功! ","color":"green"},{"text":"$(item_cn)","color":"yellow","bold":true},{"text":" x","color":"gray"},{"storage":"stardew:temp","nbt":"identify_count","color":"white"}]
