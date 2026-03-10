# 龙牙连击技能 (Dragon Combo)
# 5段手动连击，需要在正确时机点击

# 1. 检查龙牙连击独立冷却（第一步，必须在最前面！）
execute if score @s sd_dragon_combo_cooldown matches 1.. run return 0

# 2. 检查攻击冷却（原版攻击CD）
execute if score @s sd_attack_cooldown matches 1.. run return 0

# 3. 检查目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..2,sort=nearest,limit=1] add sd_dragon_combo_check

# 4. 无目标处理 - 不进入冷却
execute unless entity @e[tag=sd_dragon_combo_check] run tellraw @s {"text":"⚠ 附近没有目标！","color":"yellow"}
execute unless entity @e[tag=sd_dragon_combo_check] run return 0
tag @e[tag=sd_dragon_combo_check] remove sd_dragon_combo_check

# 5. 状态判断与分发
# 状态0（待机）-> Hit 1
execute unless entity @s[tag=sd_dragon_combo_1] unless entity @s[tag=sd_dragon_combo_2] unless entity @s[tag=sd_dragon_combo_3] unless entity @s[tag=sd_dragon_combo_4] run function stardew:combat/weapon/dragon_combo_hit1

# 如果刚刚执行了 Hit 1，直接退出（因为已经添加了 sd_dragon_combo_1 标签）
execute if entity @s[tag=sd_dragon_combo_1] unless entity @s[tag=sd_dragon_combo_window] unless entity @s[tag=sd_dragon_combo_2] run return 0

# 状态1.5（第一段窗口期）-> Hit 2
execute if entity @s[tag=sd_dragon_combo_1,tag=sd_dragon_combo_window] run function stardew:combat/weapon/dragon_combo_hit2

# 如果刚刚执行了 Hit 2，直接退出（因为已经添加了 sd_dragon_combo_2 标签）
execute if entity @s[tag=sd_dragon_combo_2] unless entity @s[tag=sd_dragon_combo_window] unless entity @s[tag=sd_dragon_combo_3] run return 0

# 状态2.5（第二段窗口期）-> Hit 3
execute if entity @s[tag=sd_dragon_combo_2,tag=sd_dragon_combo_window] run function stardew:combat/weapon/dragon_combo_hit3
execute if entity @s[tag=sd_dragon_combo_2,tag=sd_dragon_combo_window] run return 0

# 如果刚刚执行了 Hit 3，直接退出（因为已经添加了 sd_dragon_combo_3 标签）
execute if entity @s[tag=sd_dragon_combo_3] unless entity @s[tag=sd_dragon_combo_window] unless entity @s[tag=sd_dragon_combo_4] run return 0

# 状态3.5（第三段窗口期）-> Hit 4
execute if entity @s[tag=sd_dragon_combo_3,tag=sd_dragon_combo_window] run function stardew:combat/weapon/dragon_combo_hit4
execute if entity @s[tag=sd_dragon_combo_3,tag=sd_dragon_combo_window] run return 0

# 如果刚刚执行了 Hit 4，直接退出（因为已经添加了 sd_dragon_combo_4 标签）
execute if entity @s[tag=sd_dragon_combo_4] unless entity @s[tag=sd_dragon_combo_window] run return 0

# 状态4.5（第四段窗口期）-> Hit 5
execute if entity @s[tag=sd_dragon_combo_4,tag=sd_dragon_combo_window] run function stardew:combat/weapon/dragon_combo_hit5
execute if entity @s[tag=sd_dragon_combo_4,tag=sd_dragon_combo_window] run return 0

# 状态1（倒计时中乱按）-> 失败
execute if entity @s[tag=sd_dragon_combo_1] unless entity @s[tag=sd_dragon_combo_window] run function stardew:combat/weapon/dragon_combo_failed
execute if entity @s[tag=sd_dragon_combo_1] unless entity @s[tag=sd_dragon_combo_window] run return 0

# 状态2（倒计时中乱按）-> 失败
execute if entity @s[tag=sd_dragon_combo_2] unless entity @s[tag=sd_dragon_combo_window] run function stardew:combat/weapon/dragon_combo_failed
execute if entity @s[tag=sd_dragon_combo_2] unless entity @s[tag=sd_dragon_combo_window] run return 0

# 状态3（倒计时中乱按）-> 失败
execute if entity @s[tag=sd_dragon_combo_3] unless entity @s[tag=sd_dragon_combo_window] run function stardew:combat/weapon/dragon_combo_failed
execute if entity @s[tag=sd_dragon_combo_3] unless entity @s[tag=sd_dragon_combo_window] run return 0

# 状态4（倒计时中乱按）-> 失败
execute if entity @s[tag=sd_dragon_combo_4] unless entity @s[tag=sd_dragon_combo_window] run function stardew:combat/weapon/dragon_combo_failed
execute if entity @s[tag=sd_dragon_combo_4] unless entity @s[tag=sd_dragon_combo_window] run return 0
