# 判断礼物等级并应用反应
# @s = 送礼的玩家
# #gift_cmd stardew.temp = 物品的custom_model_data

# 检查最爱 - 紫水晶(7106)或南瓜(4204)
execute if score #gift_cmd stardew.temp matches 7106 run return run function stardew:npc/abigail/gifts/reaction_love
execute if score #gift_cmd stardew.temp matches 4204 run return run function stardew:npc/abigail/gifts/reaction_love

# 检查喜欢 - 其他宝石(7107-7112)
execute if score #gift_cmd stardew.temp matches 7107..7112 run return run function stardew:npc/abigail/gifts/reaction_like

# 检查不喜欢 - 大地水晶、火石英(7113-7114)
execute if score #gift_cmd stardew.temp matches 7113..7114 run return run function stardew:npc/abigail/gifts/reaction_dislike

# 检查讨厌 - 冰冻之泪(7115)
execute if score #gift_cmd stardew.temp matches 7115 run return run function stardew:npc/abigail/gifts/reaction_hate

# 默认中性反应
function stardew:npc/abigail/gifts/reaction_neutral