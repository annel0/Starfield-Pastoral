# 连击技能 (Rapid Strike)
# 快速攻击3次，每次造成60%伤害

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 100
function stardew:combat/cooldown/set_rapid_strike_cooldown_max

# 标记正在使用连击技能冷却
tag @s add sd_using_rapid_strike

# 播放音效
playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1.5 1.5
playsound minecraft:entity.player.attack.strong player @a ~ ~ ~ 1 2

# 粒子效果
particle minecraft:sweep_attack ~ ~1 ~ 0.5 0.5 0.5 0.1 10
particle minecraft:crit ~ ~1 ~ 0.5 0.5 0.5 0.2 20

# 提示
title @s subtitle [{"text":"⚔ 连击","color":"#FF6347","bold":true},{"text":" - 快速3连击","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 执行3次攻击（间隔5 ticks）
schedule function stardew:combat/weapon/rapid_strike_1 1t
schedule function stardew:combat/weapon/rapid_strike_2 6t
schedule function stardew:combat/weapon/rapid_strike_3 11t
