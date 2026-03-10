# 玩家昏倒（生命值归0）

# 设置最低生命值
scoreboard players set @s sd_health 1

# 昏倒效果
effect give @s minecraft:blindness 5 0
effect give @s minecraft:slowness 5 4
effect give @s minecraft:weakness 5 4

# 传送到出生点（或医院）
tp @s ~ ~1 ~

# 昏倒提示
title @s title {"text":"☠ 你昏倒了... ☠","color":"dark_red","bold":true}
title @s subtitle {"text":"生命值已降至最低","color":"red"}
playsound minecraft:entity.player.death player @s ~ ~ ~ 1 0.8

# 惩罚（扣除部分金币或物品）
# TODO: 后续添加金币系统后实现

tellraw @s [{"text":"━━━━━━━━━━━━━━━━━━━━━━━━","color":"dark_red","bold":true}]
tellraw @s [{"text":"  你在矿洞中昏倒了...","color":"red","bold":true}]
tellraw @s [{"text":"  有人把你送回了家。","color":"gray"}]
tellraw @s [{"text":"━━━━━━━━━━━━━━━━━━━━━━━━","color":"dark_red","bold":true}]
