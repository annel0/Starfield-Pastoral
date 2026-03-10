# data/stardew/function/crafting/craft_furnace.mcfunction
# [执行者: 玩家] 合成熔炉
# 配方: 25个石头 (CMD 7001) + 20个铜矿石 (CMD 7003)

# 0. 检查是否已解锁配方
execute unless score @s stardew.recipe.201 matches 1.. run tellraw @s [{"text":"[配方未解锁] ","color":"red","bold":true},{"text":"需要先解锁此配方！","color":"white"}]
execute unless score @s stardew.recipe.201 matches 1.. run playsound entity.villager.no player @s ~ ~ ~ 1 1
execute unless score @s stardew.recipe.201 matches 1.. run return 0

# 1. 检测是否潜行 (批量合成)
scoreboard players set #CraftAmount sd_temp 1
execute if predicate stardew:is_sneaking run scoreboard players set #CraftAmount sd_temp 5

# 2. 扫描玩家物品栏材料
execute store result score #StoneCount sd_temp run clear @s paper[custom_model_data=7001] 0
execute store result score #CopperCount sd_temp run clear @s paper[custom_model_data=7003] 0

# 3. 计算所需材料
scoreboard players set #StoneNeed sd_temp 25
scoreboard players set #CopperNeed sd_temp 20
scoreboard players operation #StoneNeed sd_temp *= #CraftAmount sd_temp
scoreboard players operation #CopperNeed sd_temp *= #CraftAmount sd_temp

# 4. 检查材料是否充足
scoreboard players set #CanCraft sd_temp 1
execute if score #StoneCount sd_temp < #StoneNeed sd_temp run scoreboard players set #CanCraft sd_temp 0
execute if score #CopperCount sd_temp < #CopperNeed sd_temp run scoreboard players set #CanCraft sd_temp 0

# 5. 如果材料不足，显示错误提示
execute if score #CanCraft sd_temp matches 0 run tellraw @s [{"text":"[材料不足] ","color":"red","bold":true},{"text":"需要: ","color":"white"},{"score":{"name":"#StoneNeed","objective":"sd_temp"},"color":"yellow"},{"text":"x石头, ","color":"white"},{"score":{"name":"#CopperNeed","objective":"sd_temp"},"color":"yellow"},{"text":"x铜矿石","color":"white"}]
execute if score #CanCraft sd_temp matches 0 run playsound entity.villager.no player @s ~ ~ ~ 1 1
execute if score #CanCraft sd_temp matches 0 run return 0

# 6. 材料充足，清除材料
# 注意：clear 命令需要正数，所以直接用原始的需求值
scoreboard players set #StoneNeed sd_temp 25
scoreboard players set #CopperNeed sd_temp 20
scoreboard players operation #StoneNeed sd_temp *= #CraftAmount sd_temp
scoreboard players operation #CopperNeed sd_temp *= #CraftAmount sd_temp
execute store result storage stardew:temp craft.stone_clear int 1 run scoreboard players get #StoneNeed sd_temp
execute store result storage stardew:temp craft.copper_clear int 1 run scoreboard players get #CopperNeed sd_temp
function stardew:crafting/clear_materials with storage stardew:temp craft

# 7. 给予成品
function stardew:crafting/give_furnace

# 8. 播放成功音效
playsound entity.player.levelup player @s ~ ~ ~ 1 1.5

# 9. 刷新菜单显示
function stardew:menu/pages/equipment_recipes
