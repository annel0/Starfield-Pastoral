# data/stardew/function/crafting/clear_materials_keg.mcfunction
# [执行者: 玩家] 清除小桶材料 (宏函数)
# 参数: wood_clear, copper_bar_clear, iron_bar_clear, oak_resin_clear

$clear @s paper[custom_model_data=9002] $(wood_clear)
$clear @s paper[custom_model_data=7007] $(copper_bar_clear)
$clear @s paper[custom_model_data=7008] $(iron_bar_clear)
$clear @s paper[custom_model_data=840] $(oak_resin_clear)
