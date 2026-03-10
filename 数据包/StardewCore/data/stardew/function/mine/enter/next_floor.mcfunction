# stardew:mine/enter/next_floor.mcfunction
# 进入下一层 (通过坑跳下)
# 执行者: 玩家 (@s)

# 清除最后一石高亮标签（换层了）
tag @s remove sd_mine_last_stone

# 增加层数
scoreboard players add @s sd_mine_floor 1

# 更新最深记录（电梯UI会使用这个）
execute if score @s sd_mine_floor > @s sd_mine_deepest run scoreboard players operation @s sd_mine_deepest = @s sd_mine_floor

# 播放进入矿洞深处音效
playsound minecraft:block.ladder.step master @s
playsound minecraft:block.gravel.break master @s

# 检查楼层是否需要刷新
function stardew:mine/floor/check_refresh

# 检查是否是宝箱层 (25, 50, 75, 100) - 使用新的统一生成逻辑
execute if score @s sd_mine_floor matches 25 if score #need_refresh sd_mine_temp matches 1 run function stardew:mine/floor/generate
execute if score @s sd_mine_floor matches 50 if score #need_refresh sd_mine_temp matches 1 run function stardew:mine/floor/generate
execute if score @s sd_mine_floor matches 75 if score #need_refresh sd_mine_temp matches 1 run function stardew:mine/floor/generate
execute if score @s sd_mine_floor matches 100 if score #need_refresh sd_mine_temp matches 1 run function stardew:mine/floor/generate

# 普通层 - 需要刷新时生成
execute unless score @s sd_mine_floor matches 25 unless score @s sd_mine_floor matches 50 unless score @s sd_mine_floor matches 75 unless score @s sd_mine_floor matches 100 if score #need_refresh sd_mine_temp matches 1 run function stardew:mine/floor/generate

# 普通层 - 不需要刷新时仅传送
execute unless score @s sd_mine_floor matches 25 unless score @s sd_mine_floor matches 50 unless score @s sd_mine_floor matches 75 unless score @s sd_mine_floor matches 100 if score #need_refresh sd_mine_temp matches 0 run function stardew:mine/floor/teleport_only

# 宝箱层 - 不需要刷新时仅传送
execute if score @s sd_mine_floor matches 25 if score #need_refresh sd_mine_temp matches 0 run function stardew:mine/floor/teleport_only
execute if score @s sd_mine_floor matches 50 if score #need_refresh sd_mine_temp matches 0 run function stardew:mine/floor/teleport_only
execute if score @s sd_mine_floor matches 75 if score #need_refresh sd_mine_temp matches 0 run function stardew:mine/floor/teleport_only
execute if score @s sd_mine_floor matches 100 if score #need_refresh sd_mine_temp matches 0 run function stardew:mine/floor/teleport_only

# 标记该层为今日已访问
function stardew:mine/floor/mark_visited

# 显示层数
function stardew:mine/ui/show_floor_title
