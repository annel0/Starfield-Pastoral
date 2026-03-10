# ================================================================
# 切换幼年绵羊模型到成年绵羊模型
# ================================================================
# @s = 绵羊逻辑实体
# 需要 #check_id 已设置

# 移除幼年绵羊模型
scoreboard players operation #remove_id stardew.animal.temp = #check_id stardew.animal.temp
function stardew:animal/animated_java/remove_sheep_baby

# 召唤成年绵羊模型
execute at @s run function stardew:animal/animated_java/summon_sheep

# 更新interaction实体尺寸（从幼年的0.7x0.8变成成年的1.1x1.2，与牛相同）
execute as @e[type=interaction,tag=stardew.animal.interaction] if score @s stardew.animal.id = #check_id stardew.animal.temp run data merge entity @s {width:1.1f,height:1.2f}
