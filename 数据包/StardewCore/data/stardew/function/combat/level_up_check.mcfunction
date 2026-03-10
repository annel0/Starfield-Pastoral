# stardew:combat/level_up_check.mcfunction
# 检测玩家是否达到升级所需经验值

# 等级1: 需要100经验
execute if score @s sd_combat_level matches 0 run scoreboard players set @s sd_level_xp_req 100
# 等级2: 需要380经验
execute if score @s sd_combat_level matches 1 run scoreboard players set @s sd_level_xp_req 380
# 等级3: 需要770经验
execute if score @s sd_combat_level matches 2 run scoreboard players set @s sd_level_xp_req 770
# 等级4: 需要1300经验
execute if score @s sd_combat_level matches 3 run scoreboard players set @s sd_level_xp_req 1300
# 等级5: 需要2150经验
execute if score @s sd_combat_level matches 4 run scoreboard players set @s sd_level_xp_req 2150
# 等级6: 需要3300经验
execute if score @s sd_combat_level matches 5 run scoreboard players set @s sd_level_xp_req 3300
# 等级7: 需要4800经验
execute if score @s sd_combat_level matches 6 run scoreboard players set @s sd_level_xp_req 4800
# 等级8: 需要6900经验
execute if score @s sd_combat_level matches 7 run scoreboard players set @s sd_level_xp_req 6900
# 等级9: 需要10000经验
execute if score @s sd_combat_level matches 8 run scoreboard players set @s sd_level_xp_req 10000
# 等级10: 需要15000经验
execute if score @s sd_combat_level matches 9 run scoreboard players set @s sd_level_xp_req 15000

# 如果经验值达到要求，执行升级
execute if score @s sd_combat_level matches ..9 if score @s sd_combat_xp >= @s sd_level_xp_req run function stardew:combat/level_up_action
