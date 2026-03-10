# data/stardew/functions/tree/calc_player_damage.mcfunction
# [执行者: 玩家]

execute store result score @s sd_const run data get entity @s SelectedItem.components."minecraft:custom_model_data"

# 铜斧 (701): 伤害 2, 冷却 20
execute if score @s sd_const matches 701 run scoreboard players set @e[tag=current_target_tree,limit=1] sd_axe_dmg 2
execute if score @s sd_const matches 701 run scoreboard players set @s sd_axe_cd 20
execute if score @s sd_const matches 701 run bossbar set stardew:axe_cooldown max 20

# 铁斧 (702): 伤害 3, 冷却 15
execute if score @s sd_const matches 702 run scoreboard players set @e[tag=current_target_tree,limit=1] sd_axe_dmg 3
execute if score @s sd_const matches 702 run scoreboard players set @s sd_axe_cd 15
execute if score @s sd_const matches 702 run bossbar set stardew:axe_cooldown max 15

# 金斧 (703): 伤害 6, 冷却 10
execute if score @s sd_const matches 703 run scoreboard players set @e[tag=current_target_tree,limit=1] sd_axe_dmg 6
execute if score @s sd_const matches 703 run scoreboard players set @s sd_axe_cd 10
execute if score @s sd_const matches 703 run bossbar set stardew:axe_cooldown max 10

# 钻斧 (704): 伤害 15, 冷却 5
execute if score @s sd_const matches 704 run scoreboard players set @e[tag=current_target_tree,limit=1] sd_axe_dmg 15
execute if score @s sd_const matches 704 run scoreboard players set @s sd_axe_cd 5
execute if score @s sd_const matches 704 run bossbar set stardew:axe_cooldown max 5