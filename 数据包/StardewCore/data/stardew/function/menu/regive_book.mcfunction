# data/stardew/function/menu/regive_book.mcfunction
# 给玩家发放菜单书

# 给予菜单书
loot give @s loot stardew:items/system/menu

# 成功提示
tellraw @s [{"text":"[系统] ","color":"gold","bold":true},{"text":"已重新获取菜单书！","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
