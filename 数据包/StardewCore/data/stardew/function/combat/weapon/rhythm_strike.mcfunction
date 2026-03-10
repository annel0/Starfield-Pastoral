# 节奏打击技能 (Rhythm Strike)
# 3段手动连击，需要在正确时机点击

# 1. 检查技能冷却（第一步，必须在最前面！）
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 2. 检查攻击冷却（原版攻击CD）
execute if score @s sd_attack_cooldown matches 1.. run return 0

# 3. 检查目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^3 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..2.5,sort=nearest,limit=1] add sd_rhythm_check

# 4. 无目标处理 - 不进入冷却
execute unless entity @e[tag=sd_rhythm_check] run tellraw @s {"text":"⚠ 附近没有目标！","color":"yellow"}
execute unless entity @e[tag=sd_rhythm_check] run return 0
tag @e[tag=sd_rhythm_check] remove sd_rhythm_check

# 5. 状态判断与分发
# 状态0（待机）-> Hit 1
execute unless entity @s[tag=sd_rhythm_1] unless entity @s[tag=sd_rhythm_2] run function stardew:combat/weapon/rhythm_strike_hit1

# 如果刚刚执行了 Hit 1，直接退出（因为已经添加了 sd_rhythm_1 标签）
execute if entity @s[tag=sd_rhythm_1] unless entity @s[tag=sd_rhythm_window] unless entity @s[tag=sd_rhythm_2] run return 0

# 状态1.5（第一段窗口期）-> Hit 2
execute if entity @s[tag=sd_rhythm_1,tag=sd_rhythm_window] run function stardew:combat/weapon/rhythm_strike_hit2

# 如果刚刚执行了 Hit 2，直接退出（因为已经添加了 sd_rhythm_2 标签，但还没有rhythm_3）
execute if entity @s[tag=sd_rhythm_2] unless entity @s[tag=sd_rhythm_window] unless entity @s[tag=sd_rhythm_3] run return 0

# 状态2.5（第二段窗口期）-> Hit 3
execute if entity @s[tag=sd_rhythm_2,tag=sd_rhythm_window] run function stardew:combat/weapon/rhythm_strike_hit3
execute if entity @s[tag=sd_rhythm_2,tag=sd_rhythm_window] run return 0

# 状态1（倒计时中乱按）-> 失败
execute if entity @s[tag=sd_rhythm_1] unless entity @s[tag=sd_rhythm_window] run function stardew:combat/weapon/rhythm_strike_failed
execute if entity @s[tag=sd_rhythm_1] unless entity @s[tag=sd_rhythm_window] run return 0

# 状态2（倒计时中乱按）-> 失败
execute if entity @s[tag=sd_rhythm_2] unless entity @s[tag=sd_rhythm_window] run function stardew:combat/weapon/rhythm_strike_failed
execute if entity @s[tag=sd_rhythm_2] unless entity @s[tag=sd_rhythm_window] run return 0
