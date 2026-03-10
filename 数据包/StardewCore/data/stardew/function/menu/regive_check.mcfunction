# data/stardew/function/menu/regive_check.mcfunction
# 检测玩家是否潜行并90度抬头看天

# 检测所有潜行且抬头角度在-90到-80度之间的玩家
execute as @a[predicate=stardew:is_sneaking_looking_up,x_rotation=-90..-80] run function stardew:menu/regive_prompt
