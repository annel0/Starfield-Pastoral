# stardew:mine/debug/force_refresh_floor.mcfunction
# 强制刷新当前楼层（无视今日是否已访问）
# 用于调试

tellraw @s [{"text":"[调试] ","color":"gold"},{"text":"强制刷新第 "},{"score":{"name":"@s","objective":"sd_mine_floor"},"color":"yellow"},{"text":" 层..."}]

# 强制设置需要刷新
scoreboard players set #need_refresh sd_mine_temp 1

# 调用生成
function stardew:mine/floor/generate

# 显示层数
function stardew:mine/ui/show_floor_title

tellraw @s [{"text":"[调试] ","color":"gold"},{"text":"楼层已刷新！","color":"green"}]
