# data/stardew/function/crafting/clear_materials.mcfunction
# [执行者: 玩家] 清除合成材料 (宏函数)
# 参数: stone_clear, copper_clear

$clear @s paper[custom_model_data=7001] $(stone_clear)
$clear @s paper[custom_model_data=7003] $(copper_clear)
