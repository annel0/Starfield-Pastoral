# data/stardew/function/crafting/remove_recipe.mcfunction
# [执行者: 玩家] 移除配方 (宏函数)
# 参数: recipe_id (配方ID)

# 清除配方解锁标记（设置为0而不是reset，这样条件判断才能正确工作）
$scoreboard players set @s stardew.recipe.$(recipe_id) 0

# 成功提示
$tellraw @s [{"text":"[配方移除] ","color":"red","bold":true},{"text":"已移除配方 ID: $(recipe_id)","color":"gray"}]
playsound block.anvil.land player @s ~ ~ ~ 0.5 1
