# stardew:combat/level_up_action.mcfunction
# 执行战斗等级提升

# 扣除经验值
scoreboard players operation @s sd_combat_xp -= @s sd_level_xp_req

# 提升等级
scoreboard players add @s sd_combat_level 1

# 播放升级音效
playsound minecraft:ui.toast.challenge_complete player @s ~ ~ ~ 1 1
playsound minecraft:entity.player.levelup player @s ~ ~ ~ 0.5 1.5

# 升级粒子效果
particle minecraft:totem_of_undying ~ ~1 ~ 0.5 1 0.5 0.1 50
particle minecraft:end_rod ~ ~1 ~ 0.3 0.8 0.3 0.1 30

# 显示升级标题
title @s title {"text":"⚔ 战斗等级提升! ⚔","color":"red","bold":true}
title @s subtitle [{"text":"等级 ","color":"gold"},{"score":{"name":"@s","objective":"sd_combat_level"},"color":"yellow","bold":true}]

# 根据等级解锁技能和奖励
execute if score @s sd_combat_level matches 1 run tellraw @s [{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true},{"text":"\n⚔ 战斗等级 1 ⚔","color":"gold","bold":true},{"text":"\n✦ 生命值 +5","color":"green"},{"text":"\n✦ 解锁配方: 皮革靴子","color":"aqua"},{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true}]
execute if score @s sd_combat_level matches 1 run scoreboard players add @s sd_max_health 5

execute if score @s sd_combat_level matches 2 run tellraw @s [{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true},{"text":"\n⚔ 战斗等级 2 ⚔","color":"gold","bold":true},{"text":"\n✦ 生命值 +5","color":"green"},{"text":"\n✦ 暴击率 +1%","color":"yellow"},{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true}]
execute if score @s sd_combat_level matches 2 run scoreboard players add @s sd_max_health 5
execute if score @s sd_combat_level matches 2 run scoreboard players add @s sd_crit_chance 1

execute if score @s sd_combat_level matches 3 run tellraw @s [{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true},{"text":"\n⚔ 战斗等级 3 ⚔","color":"gold","bold":true},{"text":"\n✦ 生命值 +5","color":"green"},{"text":"\n✦ 解锁配方: 黄晶指环","color":"aqua"},{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true}]
execute if score @s sd_combat_level matches 3 run scoreboard players add @s sd_max_health 5

execute if score @s sd_combat_level matches 4 run tellraw @s [{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true},{"text":"\n⚔ 战斗等级 4 ⚔","color":"gold","bold":true},{"text":"\n✦ 生命值 +5","color":"green"},{"text":"\n✦ 暴击率 +1%","color":"yellow"},{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true}]
execute if score @s sd_combat_level matches 4 run scoreboard players add @s sd_max_health 5
execute if score @s sd_combat_level matches 4 run scoreboard players add @s sd_crit_chance 1

execute if score @s sd_combat_level matches 5 run tellraw @s [{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true},{"text":"\n⚔ 战斗等级 5 ⚔","color":"gold","bold":true},{"text":"\n✦ 生命值 +5","color":"green"},{"text":"\n✦ 解锁配方: 战士戒指","color":"aqua"},{"text":"\n✦ 提示: 选择战斗专精!","color":"light_purple"},{"text":"\n  - 战士: 所有攻击 +10% 伤害","color":"gray"},{"text":"\n  - 侦察兵: 暴击率 +50%","color":"gray"},{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true}]
execute if score @s sd_combat_level matches 5 run scoreboard players add @s sd_max_health 5

execute if score @s sd_combat_level matches 6 run tellraw @s [{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true},{"text":"\n⚔ 战斗等级 6 ⚔","color":"gold","bold":true},{"text":"\n✦ 生命值 +5","color":"green"},{"text":"\n✦ 暴击率 +1%","color":"yellow"},{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true}]
execute if score @s sd_combat_level matches 6 run scoreboard players add @s sd_max_health 5
execute if score @s sd_combat_level matches 6 run scoreboard players add @s sd_crit_chance 1

execute if score @s sd_combat_level matches 7 run tellraw @s [{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true},{"text":"\n⚔ 战斗等级 7 ⚔","color":"gold","bold":true},{"text":"\n✦ 生命值 +5","color":"green"},{"text":"\n✦ 解锁配方: 吸血戒指","color":"aqua"},{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true}]
execute if score @s sd_combat_level matches 7 run scoreboard players add @s sd_max_health 5

execute if score @s sd_combat_level matches 8 run tellraw @s [{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true},{"text":"\n⚔ 战斗等级 8 ⚔","color":"gold","bold":true},{"text":"\n✦ 生命值 +5","color":"green"},{"text":"\n✦ 暴击率 +1%","color":"yellow"},{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true}]
execute if score @s sd_combat_level matches 8 run scoreboard players add @s sd_max_health 5
execute if score @s sd_combat_level matches 8 run scoreboard players add @s sd_crit_chance 1

execute if score @s sd_combat_level matches 9 run tellraw @s [{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true},{"text":"\n⚔ 战斗等级 9 ⚔","color":"gold","bold":true},{"text":"\n✦ 生命值 +5","color":"green"},{"text":"\n✦ 解锁配方: 铱星戒指","color":"aqua"},{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true}]
execute if score @s sd_combat_level matches 9 run scoreboard players add @s sd_max_health 5

execute if score @s sd_combat_level matches 10 run tellraw @s [{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true},{"text":"\n⚔ 战斗等级 10 - 大师! ⚔","color":"gold","bold":true},{"text":"\n✦ 生命值 +5","color":"green"},{"text":"\n✦ 暴击率 +2%","color":"yellow"},{"text":"\n✦ 提示: 选择最终专精!","color":"light_purple"},{"text":"\n  - 蛮力: 伤害 +15%","color":"gray"},{"text":"\n  - 防御者: 生命值 +25","color":"gray"},{"text":"\n  - 特技杀手: 暴击伤害 +50%","color":"gray"},{"text":"\n  - 绝杀: 怪物有几率被一击必杀","color":"gray"},{"text":"\n━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"red","bold":true}]
execute if score @s sd_combat_level matches 10 run scoreboard players add @s sd_max_health 5
execute if score @s sd_combat_level matches 10 run scoreboard players add @s sd_crit_chance 2

# 递归检查是否可以继续升级（处理跨等级升级情况）
execute if score @s sd_combat_level matches ..9 if score @s sd_combat_xp >= @s sd_level_xp_req run function stardew:combat/level_up_check
