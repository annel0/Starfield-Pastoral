# data/stardew/function/menu/buttons/items_fish_fall.mcfunction
# 秋季鱼按钮点击处理
# 执行者: 玩家 (@s)

# 打开秋季鱼品质选择页面
tag @s add sd_menu_opener
function stardew:menu/pages/items_fish_fall
tag @s remove sd_menu_opener
