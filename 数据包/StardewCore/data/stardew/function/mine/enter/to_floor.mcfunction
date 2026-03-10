# stardew:mine/enter/to_floor.mcfunction
# 传送到指定层 (电梯使用)
# 执行者: 玩家 (@s)
# 参数: 通过 storage stardew:mine target_floor 传递目标层数

# 清除最后一石高亮标签（换层了）
tag @s remove sd_mine_last_stone

# 获取当前所在层 (用于判断是否需要批量清理中间层)
execute store result score #current_floor sd_mine_temp run scoreboard players get @s sd_mine_floor

# 获取目标层数
execute store result score @s sd_mine_floor run data get storage stardew:mine target_floor

# 【智能批量清理】只清理"过期的旧层",保留"今天已访问的层"
# 例如: 0层->80层, 如果1-79层中有些今天已访问过,则跳过清理(保留电梯/梯子)
scoreboard players operation #floor_diff sd_mine_temp = @s sd_mine_floor
scoreboard players operation #floor_diff sd_mine_temp -= #current_floor sd_mine_temp

# 向上跳层 (diff > 1)
execute if score #floor_diff sd_mine_temp matches 2.. run scoreboard players operation #range_start sd_mine_temp = #current_floor sd_mine_temp
execute if score #floor_diff sd_mine_temp matches 2.. run scoreboard players add #range_start sd_mine_temp 1
execute if score #floor_diff sd_mine_temp matches 2.. store result storage stardew:mine range.start int 1 run scoreboard players get #range_start sd_mine_temp
execute if score #floor_diff sd_mine_temp matches 2.. run scoreboard players operation #range_end sd_mine_temp = @s sd_mine_floor
execute if score #floor_diff sd_mine_temp matches 2.. run scoreboard players remove #range_end sd_mine_temp 1
execute if score #floor_diff sd_mine_temp matches 2.. store result storage stardew:mine range.end int 1 run scoreboard players get #range_end sd_mine_temp
execute if score #floor_diff sd_mine_temp matches 2.. run function stardew:mine/floor/clear_range_smart

# 向下跳层 (diff < -1)
execute if score #floor_diff sd_mine_temp matches ..-2 run scoreboard players operation #range_start sd_mine_temp = @s sd_mine_floor
execute if score #floor_diff sd_mine_temp matches ..-2 run scoreboard players add #range_start sd_mine_temp 1
execute if score #floor_diff sd_mine_temp matches ..-2 store result storage stardew:mine range.start int 1 run scoreboard players get #range_start sd_mine_temp
execute if score #floor_diff sd_mine_temp matches ..-2 run scoreboard players operation #range_end sd_mine_temp = #current_floor sd_mine_temp
execute if score #floor_diff sd_mine_temp matches ..-2 run scoreboard players remove #range_end sd_mine_temp 1
execute if score #floor_diff sd_mine_temp matches ..-2 store result storage stardew:mine range.end int 1 run scoreboard players get #range_end sd_mine_temp
execute if score #floor_diff sd_mine_temp matches ..-2 run function stardew:mine/floor/clear_range_smart

# 播放电梯音效
playsound minecraft:block.piston.extend master @s ~ ~ ~ 1 1.5
playsound minecraft:block.note_block.chime master @s ~ ~ ~ 1 1

# 0 层直接传送 (入口区域不刷新) - 传送到电梯附近
execute if score @s sd_mine_floor matches 0 in stardew:mine run tp @s -5 65 2
# 0层不显示title
execute if score @s sd_mine_floor matches 0 run data remove storage stardew:mine target_floor
execute if score @s sd_mine_floor matches 0 run return 0

# 检查楼层是否需要刷新
function stardew:mine/floor/check_refresh

# ===== 需要刷新时生成楼层 =====
# 所有层统一使用 generate (包括宝箱层25/50/75/100)
# 宝箱层的特殊处理已移至 generate_room_impl.mcfunction
# 播放梯子攀爬音效
execute if score #need_refresh sd_mine_temp matches 1 run playsound minecraft:block.ladder.step master @s ~ ~ ~ 1 1
execute if score #need_refresh sd_mine_temp matches 1 run function stardew:mine/floor/generate

# ===== 不需要刷新时只传送 (需要先加载区块) =====
# 播放梯子攀爬音效
execute if score #need_refresh sd_mine_temp matches 0 run playsound minecraft:block.ladder.step master @s ~ ~ ~ 1 1
# 计算目标区块范围并强制加载 (确保传送时区块已加载)
execute if score #need_refresh sd_mine_temp matches 0 store result score #tp_target_z sd_mine_temp run scoreboard players get @s sd_mine_floor
execute if score #need_refresh sd_mine_temp matches 0 run scoreboard players operation #tp_target_z sd_mine_temp *= #100 sd_const
execute if score #need_refresh sd_mine_temp matches 0 run scoreboard players operation #tp_z50 sd_mine_temp = #tp_target_z sd_mine_temp
execute if score #need_refresh sd_mine_temp matches 0 run scoreboard players add #tp_z50 sd_mine_temp 50
execute if score #need_refresh sd_mine_temp matches 0 store result storage stardew:mine teleport.z int 1 run scoreboard players get #tp_target_z sd_mine_temp
execute if score #need_refresh sd_mine_temp matches 0 store result storage stardew:mine teleport.z50 int 1 run scoreboard players get #tp_z50 sd_mine_temp
execute if score #need_refresh sd_mine_temp matches 0 run function stardew:mine/floor/forceload_teleport with storage stardew:mine teleport

# 更新玩家到过的最深层数（用于电梯菜单解锁）
execute if score @s sd_mine_floor > @s sd_mine_max_floor run scoreboard players operation @s sd_mine_max_floor = @s sd_mine_floor

# 标记该层为今日已访问
function stardew:mine/floor/mark_visited

# 不再显示层数 title
# function stardew:mine/ui/show_floor_title

# 清理临时数据
data remove storage stardew:mine target_floor
