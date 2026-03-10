# data/stardew/functions/fishing/level_up_action.mcfunction
# [执行者: 玩家]
# 作用：执行升级动作，并消耗升级所需的经验值。

# 1. 经验消耗：消耗当前等级所需的经验值 (从 sd_level_xp_req 消耗)
scoreboard players operation @s sd_fishing_xp -= @s sd_level_xp_req

# 2. 提升等级
scoreboard players add @s sd_fishing_lvl 1

# 3. 升级特效与反馈
execute at @s run particle minecraft:totem_of_undying ~ ~1 ~ 0.5 0.5 0.5 0.1 20
execute at @s run playsound minecraft:entity.player.levelup player @s ~ ~ ~ 1 1

tellraw @s ["",{"text":"[技能] ","color":"gold"},{"text":"恭喜！你的钓鱼等级提升到 ","color":"white"},{"score":{"name":"@s","objective":"sd_fishing_lvl"},"color":"yellow","bold":true}]

# [修复] 递归检查 - 升级成功后立即检查是否还能继续升级
execute if score @s sd_fishing_lvl matches 1..9 run function stardew:fishing/level_up_check