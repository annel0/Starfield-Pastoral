# data/stardew/function/menu/storage/hover/set_name_text.mcfunction
# 设置背包名称文本
# 宏参数: $(display_name)

$data modify entity @s text set value '{"text":"$(display_name)","color":"aqua","bold":true}'
