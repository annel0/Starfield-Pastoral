# data/stardew/function/menu/buttons/items_main.mcfunction
# 返回物品主菜单按钮点击处理
# 执行者: 玩家 (@s)

# 返回物品主菜单
tag @s add sd_menu_opener
function stardew:menu/pages/items_main
tag @s remove sd_menu_opener
