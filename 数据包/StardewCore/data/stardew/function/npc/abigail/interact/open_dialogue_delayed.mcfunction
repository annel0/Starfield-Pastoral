# 延迟打开对话界面
# 在玩家被推开后才执行
# @s = 带有abigail.open_dialogue标签的玩家
# 执行位置 = 玩家位置

function stardew:dialogue/player_open
tag @s remove abigail.open_dialogue

# 如果有consume_gift tag，转换为consume_gift_now（下一个tick才消耗）
execute if entity @s[tag=abigail.consume_gift] run tag @s add abigail.consume_gift_now
tag @s remove abigail.consume_gift
