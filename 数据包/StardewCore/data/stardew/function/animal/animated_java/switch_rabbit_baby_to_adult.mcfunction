# ================================================================
# 切换幼年兔模型到成年兔模型
# ================================================================
# @s = 兔逻辑实体
# 需要 #check_id 已设置

# 移除幼年兔模型
scoreboard players operation #remove_id stardew.animal.temp = #check_id stardew.animal.temp
function stardew:animal/animated_java/remove_rabbit_baby

# 召唤成年兔模型
execute at @s run function stardew:animal/animated_java/summon_rabbit
