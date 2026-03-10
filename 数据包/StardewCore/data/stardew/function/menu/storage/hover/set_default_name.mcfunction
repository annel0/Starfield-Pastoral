# data/stardew/function/menu/storage/hover/set_default_name.mcfunction
# 设置默认背包名称
# 宏参数: $(display_num)

$data modify entity @s text set value '{"text":"背包#$(display_num)","color":"aqua","bold":true}'
