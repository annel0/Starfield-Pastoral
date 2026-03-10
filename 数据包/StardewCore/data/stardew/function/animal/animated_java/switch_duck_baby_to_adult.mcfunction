# ================================================================
# 切换幼年鸭模型到成年鸭模型
# ================================================================
# @s = 鸭逻辑实体
# 需要 #check_id 已设置

# 移除幼年鸭模型（chicken_baby）
scoreboard players operation #remove_id stardew.animal.temp = #check_id stardew.animal.temp
function stardew:animal/animated_java/remove_duck_baby

# 召唤成年鸭模型
execute at @s run function stardew:animal/animated_java/summon_duck
