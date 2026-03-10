# 继续对话（进入下一页或关闭）

playsound ui.button.click block @s ~ ~ ~ 0.5 1.0

# 当前页数+1
execute store result score #current_page sd_temp run data get storage stardew:dialogue current.dialogue_page
scoreboard players add #current_page sd_temp 1

# 获取总页数
execute store result score #total_pages sd_temp run data get storage stardew:dialogue current.total_pages

# 如果还有下一页，显示下一页（然后结束，不继续判断）
execute if score #current_page sd_temp < #total_pages sd_temp run return run function stardew:dialogue/next_page

# 否则（只有当不满足上面条件时才会执行到这里），关闭对话框
function stardew:dialogue/player_close
