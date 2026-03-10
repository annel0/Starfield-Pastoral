# data/stardew/function/crafting/unlock_recipe.mcfunction
# [执行者: 玩家] 解锁配方 (宏函数)
# 参数: recipe_id (配方ID), category (分类: tools/equipment/building/consumable/furniture)

# 检查是否已经解锁
$execute if score @s stardew.recipe.$(recipe_id) matches 1.. run return 0

# 设置配方解锁标记
$scoreboard players set @s stardew.recipe.$(recipe_id) 1

# 将配方ID添加到玩家的解锁队列中（按解锁顺序）
# 使用玩家UUID作为storage路径
$execute store result storage stardew:temp unlock.recipe_id int 1 run scoreboard players set #TempRecipeID sd_temp $(recipe_id)
$data modify storage stardew:player_recipes $(category) append from storage stardew:temp unlock.recipe_id

# 成功提示
playsound ui.toast.challenge_complete player @s ~ ~ ~ 1 1

