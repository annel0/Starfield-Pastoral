# 星辰护盾 - 使用盔甲架显示护盾球（超明显版本）
# 这个版本会召唤实体护盾球

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 160
function stardew:combat/cooldown/set_astral_aegis_cooldown_max

# 标记正在使用星辰护盾技能冷却
tag @s add sd_using_astral_aegis

# 播放音效
playsound minecraft:block.enchantment_table.use player @a ~ ~ ~ 1.5 1.2
playsound minecraft:block.beacon.activate player @a ~ ~ ~ 1.2 1.8
playsound minecraft:entity.guardian.ambient player @a ~ ~ ~ 0.8 1.5
playsound minecraft:block.end_portal.spawn player @a ~ ~ ~ 0.5 2

# 视觉效果 - 激活爆发
particle minecraft:enchant ~ ~1 ~ 0.8 0.8 0.8 3 80 force
particle minecraft:end_rod ~ ~1 ~ 0.5 0.5 0.5 0.3 50 force
particle minecraft:soul_fire_flame ~ ~1 ~ 0.6 0.6 0.6 0.08 30 force
particle minecraft:flash ~ ~1 ~ 0 0 0 0 3 force
particle minecraft:explosion ~ ~1 ~ 0.3 0.3 0.3 0 2 force
particle minecraft:witch ~ ~ ~ 2 0 2 0 30 force
particle minecraft:enchant ~ ~0.1 ~ 1.5 0 1.5 0 50 force

# 提示
title @s subtitle [{"text":"🛡 星辰护盾","color":"#9933FF","bold":true},{"text":" - 激活","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 读取护盾等级
execute store result score @s sd_shield_count run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_shield_count
execute unless score @s sd_shield_count matches 1.. run scoreboard players set @s sd_shield_count 3

# 读取反伤百分比
execute store result score @s sd_shield_reflect run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_shield_reflect
execute unless score @s sd_shield_reflect matches 1.. run scoreboard players set @s sd_shield_reflect 30

# 设置护盾持续时间
execute store result score @s sd_shield_timer run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_shield_duration
execute unless score @s sd_shield_timer matches 1.. run scoreboard players set @s sd_shield_timer 100

# 初始化旋转角度
scoreboard players set @s sd_shield_rotation 0

# 添加护盾激活标签
tag @s add sd_has_shield

# 召唤护盾球实体（盔甲架）
execute if score @s sd_shield_count matches 1.. run summon minecraft:armor_stand ~ ~1.3 ~1.8 {Tags:["sd_shield_orb","sd_shield_new"],Invisible:1b,Invulnerable:1b,NoGravity:1b,Small:1b,Marker:1b,DisabledSlots:4144959,Glowing:1b,Team:"sd_shield_team"}
execute if score @s sd_shield_count matches 2.. run summon minecraft:armor_stand ~1.8 ~1.3 ~ {Tags:["sd_shield_orb","sd_shield_new"],Invisible:1b,Invulnerable:1b,NoGravity:1b,Small:1b,Marker:1b,DisabledSlots:4144959,Glowing:1b,Team:"sd_shield_team"}
execute if score @s sd_shield_count matches 3.. run summon minecraft:armor_stand ~ ~1.3 ~-1.8 {Tags:["sd_shield_orb","sd_shield_new"],Invisible:1b,Invulnerable:1b,NoGravity:1b,Small:1b,Marker:1b,DisabledSlots:4144959,Glowing:1b,Team:"sd_shield_team"}
execute if score @s sd_shield_count matches 4.. run summon minecraft:armor_stand ~-1.8 ~1.3 ~ {Tags:["sd_shield_orb","sd_shield_new"],Invisible:1b,Invulnerable:1b,NoGravity:1b,Small:1b,Marker:1b,DisabledSlots:4144959,Glowing:1b,Team:"sd_shield_team"}

# 给新召唤的护盾球设置头盔（发光球体）
execute as @e[tag=sd_shield_new,distance=..3] run item replace entity @s armor.head with minecraft:light_blue_stained_glass

# 绑定护盾球到玩家
execute as @e[tag=sd_shield_new,distance=..3] run scoreboard players operation @s sd_temp = @p sd_temp
tag @e[tag=sd_shield_new,distance=..3] remove sd_shield_new

# 提示玩家（已删除actionbar占用）
