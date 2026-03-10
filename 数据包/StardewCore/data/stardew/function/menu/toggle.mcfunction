# data/stardew/function/menu/toggle.mcfunction
# [执行者: 玩家] 切换菜单开关

# 如果玩家已有序列号(已打开UI),则关闭
execute if score @s sd_menu_sequence matches 1.. run return run function stardew:menu/close

# 否则打开UI(如果上面return了就不会执行到这里)
execute at @s run function stardew:menu/open
