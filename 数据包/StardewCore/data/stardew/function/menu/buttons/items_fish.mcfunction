# data/stardew/function/menu/buttons/items_fish.mcfunction
# 鱼类子菜单按钮点击处理
# 执行者: 玩家 (@s)

# 打开鱼类子菜单
tag @s add sd_menu_opener
function stardew:menu/pages/items_fish
tag @s remove sd_menu_opener
