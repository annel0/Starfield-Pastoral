# 武器特殊技能 (Shift+右键)
# 根据 weapon_special_2 触发对应技能

# 检查武器是否有第二技能
execute unless data entity @s SelectedItem.components."minecraft:custom_data".weapon_special_2 run return 0

# 旋风斩技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"whirlwind"} run function stardew:combat/weapon/whirlwind

# 海王怒涛技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"neptune_wrath"} run function stardew:combat/weapon/neptune_wrath

# 连击技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"rapid_strike"} run function stardew:combat/weapon/rapid_strike

# 熔岩爆发技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"lava_eruption"} run function stardew:combat/weapon/lava_eruption

# 星辰冲击技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"stellar_impact"} run function stardew:combat/weapon/stellar_impact

# 剧毒之刃技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"poison_blade"} run function stardew:combat/weapon/poison_blade

# 暗影收割技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"shadow_reap"} run function stardew:combat/weapon/shadow_reap

# 剧毒爆发技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"poison_burst"} run function stardew:combat/weapon/poison_burst

# 银河觉醒技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"galaxy_awakening"} run function stardew:combat/weapon/galaxy_awakening

# 龙牙狂怒技能（锤子）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"dragon_fury"} run function stardew:combat/weapon/dragon_fury

# 泰坦之怒技能（锤子）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"titan_wrath"} run function stardew:combat/weapon/titan_wrath

# 无限觉醒技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"infinity_awakening"} run function stardew:combat/weapon/infinity_awakening

# 银河觉醒技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"galaxy_awakening"} run function stardew:combat/weapon/galaxy_awakening

# 无限觉醒技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"infinity_awakening"} run function stardew:combat/weapon/infinity_awakening

# 暴击涌动技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"critical_surge"} run function stardew:combat/weapon/critical_surge

# 重击蓄力技能（棍棒/锤子）- 支持多等级
# 如果已经蓄满，Shift+右键也可以释放攻击
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"heavy_charge"} if score @s sd_heavy_charge_ready matches 1 run function stardew:combat/weapon/heavy_charge
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"heavy_strike_2"} if score @s sd_heavy_charge_ready matches 1 run function stardew:combat/weapon/heavy_charge
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"heavy_strike_3"} if score @s sd_heavy_charge_ready matches 1 run function stardew:combat/weapon/heavy_charge

# 生命汲取（被动技能，不需要右键触发）
# execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"life_drain"} run tellraw @s {"text":"[生命汲取] 被动技能，攻击时自动触发","color":"green"}

# 未实现的技能提示（临时）
# execute unless data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"whirlwind"} unless data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"rapid_strike"} unless data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"flame_slash"} unless data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"stellar_impact"} unless data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"life_drain"} run tellraw @s {"text":"[特殊技能] 该技能尚未实现","color":"yellow"}
