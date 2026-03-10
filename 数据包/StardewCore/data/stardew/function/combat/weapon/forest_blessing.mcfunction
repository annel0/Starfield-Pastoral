# 森林赐福技能 (Forest Blessing)
# 10秒内每秒恢复5生命值（总共50HP）

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 300
function stardew:combat/cooldown/set_forest_blessing_cooldown_max

# 标记正在使用森林赐福技能冷却
tag @s add sd_using_forest_blessing

# 播放音效
playsound minecraft:block.enchantment_table.use player @a ~ ~ ~ 1 1.5
playsound minecraft:entity.player.levelup player @a ~ ~ ~ 0.5 2
playsound minecraft:block.grass.break player @a ~ ~ ~ 1 0.8

# 视觉效果
particle minecraft:happy_villager ~ ~1 ~ 0.5 0.5 0.5 0.1 30 force
particle minecraft:composter ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force
particle minecraft:glow ~ ~1 ~ 0.4 0.6 0.4 0.05 25 force

# 提示
title @s subtitle [{"text":"🌿 森林赐福","color":"#32CD32","bold":true},{"text":" - 生命恢复","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 设置持续回血buff（10秒 = 200 ticks）
scoreboard players set @s sd_regen_timer 200
scoreboard players set @s sd_regen_amount 5

# 提示信息（已删除actionbar占用）
