# stardew:debug/goto_floor.mcfunction
# 快速前往指定楼层（测试用）
# 使用方法: 
#   /scoreboard players set @s sd_mine_floor 51
#   /function stardew:debug/goto_floor

# 确保层数有效
execute unless score @s sd_mine_floor matches 0..100 run scoreboard players set @s sd_mine_floor 1

# 自动解锁到该层
execute if score @s sd_mine_deepest < @s sd_mine_floor run scoreboard players operation @s sd_mine_deepest = @s sd_mine_floor

# 传送到该层
function stardew:mine/enter/to_floor

tellraw @s [{"text":"✓ 已传送到第 ","color":"green"},{"score":{"name":"@s","objective":"sd_mine_floor"},"color":"gold"},{"text":" 层","color":"green"}]
