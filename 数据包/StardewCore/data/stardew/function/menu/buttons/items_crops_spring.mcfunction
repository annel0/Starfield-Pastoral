# data/stardew/function/menu/buttons/items_crops_spring.mcfunction
# 春季作物按钮点击处理
# 执行者: 玩家 (@s)

# 打开春季作物品质选择页面
tag @s add sd_menu_opener
function stardew:menu/pages/items_crops_spring
tag @s remove sd_menu_opener
