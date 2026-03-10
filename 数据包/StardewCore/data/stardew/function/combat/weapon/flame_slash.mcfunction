# 火焰斩技能 (Flame Slash)
# 剑刃附着火焰，造成120%伤害+3秒燃烧

# 检查是否在冷却中
execute if score @s sd_flame_slash_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_flame_slash_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_flame_slash_cooldown matches 1.. run scoreboard players set @s sd_flame_slash_cooldown 80
function stardew:combat/cooldown/set_flame_slash_cooldown_max

# 标记正在使用火焰斩技能冷却
tag @s add sd_using_flame_slash

# 播放音效
playsound minecraft:entity.blaze.shoot player @a ~ ~ ~ 1 0.8
playsound minecraft:item.firecharge.use player @a ~ ~ ~ 1 1.2

# 火焰粒子效果
particle minecraft:flame ~ ~1 ~ 0.3 0.3 0.3 0.1 30 force
particle minecraft:lava ~ ~1 ~ 0.5 0.5 0.5 0 5 force

# 提示
title @s subtitle [{"text":"🔥 火焰斩","color":"#FF6347","bold":true},{"text":" - 燃烧3秒","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 标记前方的敌人
tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,distance=..4] add sd_flame_target

# 对每个敌人造成伤害和燃烧
execute as @e[tag=sd_flame_target] at @s run function stardew:combat/weapon/flame_slash_damage

# 清理标记
tag @e[tag=sd_flame_target] remove sd_flame_target

# 消息提示（已删除actionbar占用）
