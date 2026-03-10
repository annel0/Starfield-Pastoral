# data/stardew/function/menu/buttons/items_menu.mcfunction
# 物品菜单按钮点击处理
# 执行者: 玩家 (@s)

# 打开物品主菜单
tag @s add sd_menu_opener
function stardew:menu/pages/items_main
tag @s remove sd_menu_opener

# 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.2
