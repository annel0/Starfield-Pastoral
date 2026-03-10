# 调试：显示当前对话状态
tellraw @s [{"text":"[调试] 当前页: ","color":"yellow"},{"nbt":"current.dialogue_page","storage":"stardew:dialogue","color":"green"}]
tellraw @s [{"text":"[调试] 总页数: ","color":"yellow"},{"nbt":"current.total_pages","storage":"stardew:dialogue","color":"green"}]

execute store result score #debug_page sd_temp run data get storage stardew:dialogue current.dialogue_page
execute store result score #debug_total sd_temp run data get storage stardew:dialogue current.total_pages
scoreboard players add #debug_page sd_temp 1

tellraw @s [{"text":"[调试] 当前页+1: ","color":"yellow"},{"score":{"name":"#debug_page","objective":"sd_temp"},"color":"green"}]
tellraw @s [{"text":"[调试] 总页数: ","color":"yellow"},{"score":{"name":"#debug_total","objective":"sd_temp"},"color":"green"}]

execute if score #debug_page sd_temp >= #debug_total sd_temp run tellraw @s {"text":"[调试] 应该显示按钮！","color":"red","bold":true}
execute if score #debug_page sd_temp < #debug_total sd_temp run tellraw @s {"text":"[调试] 不应该显示按钮（还有下一页）","color":"aqua"}
