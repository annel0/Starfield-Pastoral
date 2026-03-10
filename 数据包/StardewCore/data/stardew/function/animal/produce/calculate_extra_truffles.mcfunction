# ================================================================
# 星露谷物语 - 计算额外松露
# ================================================================
# 用途：根据友谊值计算额外松露数量
# @s = 猪实体
# 公式：额外松露概率 = 友谊值 / 1500
# ================================================================

# 计算额外松露概率（友谊值/1500，最大友谊1000 = 66%）
scoreboard players operation #extra_chance stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation #extra_chance stardew.animal.temp /= #1500 stardew.constant

# 循环检查额外松露
scoreboard players set #extra_count stardew.animal.temp 0

# 最多检查3次（防止无限循环）
execute if score #extra_chance stardew.animal.temp matches 1.. run function stardew:animal/produce/check_extra_truffle_loop

# 调试信息
execute if score #extra_count stardew.animal.temp matches 1.. run tellraw @a[tag=stardew.debug] ["",{"text":"[额外松露] ","color":"gold"},{"text":"猪 ","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":" 产出了 ","color":"white"},{"score":{"name":"#extra_count","objective":"stardew.animal.temp"},"color":"yellow"},{"text":" 个额外松露","color":"white"}]