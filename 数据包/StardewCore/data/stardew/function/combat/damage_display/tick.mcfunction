# 伤害显示主循环

# 根据计时器阶段调用不同动画
# 上升阶段（1-6 ticks）
execute if score @s sd_dmg_anim matches 1..6 run function stardew:combat/damage_display/animate_rise

# 停留阶段（7-18 ticks）
execute if score @s sd_dmg_anim matches 7..18 run function stardew:combat/damage_display/animate_stay

# 下坠阶段（19-23 ticks）
execute if score @s sd_dmg_anim matches 19..23 run function stardew:combat/damage_display/animate_fall

# 消失阶段（24+ ticks）
execute if score @s sd_dmg_anim matches 24.. run kill @s

# 增加动画计时器
scoreboard players add @s sd_dmg_anim 1