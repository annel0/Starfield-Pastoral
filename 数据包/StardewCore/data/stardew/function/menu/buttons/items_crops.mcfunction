# data/stardew/function/menu/buttons/items_crops.mcfunction
# 作物子菜单按钮点击处理
# 执行者: 玩家 (@s)

# 打开作物子菜单
tag @s add sd_menu_opener
function stardew:menu/pages/items_crops
tag @s remove sd_menu_opener
