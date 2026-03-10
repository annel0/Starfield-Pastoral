# ================================================================
# 切换幼年鸡模型到成年鸡模型
# ================================================================
# @s = 鸡逻辑实体
# 需要 #check_id 已设置

# 移除幼年鸡模型
scoreboard players operation #remove_id stardew.animal.temp = #check_id stardew.animal.temp
function stardew:animal/animated_java/remove_chicken_baby

# 召唤成年鸡模型
execute at @s run function stardew:animal/animated_java/summon_chicken
