# data/stardew/function/menu/storage/hover/set_custom_name.mcfunction
# 设置自定义背包名称
# 从storage读取display_name

data modify entity @s text set value '{"storage":"stardew:temp","nbt":"display_name","interpret":true,"color":"aqua","bold":true}'
