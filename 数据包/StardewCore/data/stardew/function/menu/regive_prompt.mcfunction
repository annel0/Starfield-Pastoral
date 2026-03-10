# data/stardew/function/menu/regive_prompt.mcfunction
# 显示是否重新获取菜单书的提示

# 使用分数记录冷却，避免重复触发
scoreboard players add @s sd_regive_cd 0
execute if score @s sd_regive_cd matches 1.. run return 0

# 设置冷却时间（60刻 = 3秒）
scoreboard players set @s sd_regive_cd 60

# 显示可点击的提示消息
tellraw @s [{"text":"[系统] ","color":"gold","bold":true},{"text":"是否要重新获取菜单书？ ","color":"white"},{"text":"[获取]","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:menu/regive_book"},"hoverEvent":{"action":"show_text","contents":"点击获取菜单书"}}]
playsound ui.button.click player @s ~ ~ ~ 0.3 1.5
