# stardew:mine/ladder/maintain_glow.mcfunction
# 持续维持最后一个石头的高亮效果
# 执行者: 玩家 (tag=sd_mine_last_stone)
# 执行位置: 玩家位置

# 以玩家为中心，高亮周围50x50范围内的所有石头
execute at @s run function stardew:mine/ladder/refresh_glow_at_player

# 检查是否已经生成梯子，如果生成了就清除标签
execute if score @s sd_mine_ladder matches 1 run tag @s remove sd_mine_last_stone
