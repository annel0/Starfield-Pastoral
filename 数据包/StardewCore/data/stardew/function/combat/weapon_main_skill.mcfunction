## 主技能路由器 - 根据 weapon_special 调用对应技能


# 格挡技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"block"} run function stardew:combat/weapon/sword_block

# 突刺技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"dash"} run function stardew:combat/weapon/dash_strike

# 森林赐福技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"forest_blessing"} run function stardew:combat/weapon/forest_blessing

# 星辰护盾技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"astral_aegis"} run function stardew:combat/weapon/astral_aegis

# 火焰斩技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"flame_slash"} run function stardew:combat/weapon/flame_slash

# 精准打击技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"precision_strike"} run function stardew:combat/weapon/precision_strike

# 背刺技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"backstab"} run function stardew:combat/weapon/backstab

# 暗影步技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"shadow_step"} run function stardew:combat/weapon/shadow_step

# 毒刃技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"poison_strike"} run function stardew:combat/weapon/poison_strike

# 星流连斩技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"star_flurry"} run function stardew:combat/weapon/star_flurry

# 刀锋之舞技能（匕首）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"blade_dance"} run function stardew:combat/weapon/blade_dance

# 震地重击技能（棍棒）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"ground_slam"} run function stardew:combat/weapon/ground_slam

# 陨星打击技能（锤子）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"meteor_strike"} run function stardew:combat/weapon/meteor_strike

# 骨裂打击技能（棍棒）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"bone_break"} run function stardew:combat/weapon/bone_break

# 节奏打击技能（棍棒）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"rhythm_strike"} run function stardew:combat/weapon/rhythm_strike

# 龙牙连击技能（锤子）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"dragon_combo"} run function stardew:combat/weapon/dragon_combo

# 蓄力重击技能（棍棒/锤子）- 支持多等级 (heavy_charge, heavy_strike_2, heavy_strike_3 等)
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"heavy_charge"} run function stardew:combat/weapon/heavy_charge
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"heavy_strike_2"} run function stardew:combat/weapon/heavy_charge
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"heavy_strike_3"} run function stardew:combat/weapon/heavy_charge
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"heavy_charge"} run function stardew:combat/weapon/heavy_charge
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"heavy_strike_2"} run function stardew:combat/weapon/heavy_charge
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"heavy_strike_3"} run function stardew:combat/weapon/heavy_charge

# 根据 weapon_special 触发对应技能

# 格挡技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"block"} run function stardew:combat/weapon/sword_block

# 突刺技能
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"dash"} run function stardew:combat/weapon/dash_strike

# 未实现的技能提示（临时）
# execute unless data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"block"} unless data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special:"dash"} run tellraw @s {"text":"[主技能] 该技能尚未实现","color":"yellow"}
