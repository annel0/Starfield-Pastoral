# ================================================================
# 星露谷物语 - 门关闭时的逻辑
# ================================================================
# 用途：处理门关闭状态下的动物范围限制
# 调用：从apply_range_limit调用，作为建筑marker执行

# 策略（修正版）：
# 门关闭 = 边界锁定，动物不能穿过10格边界（无论方向）
# 1. 动物在10格内 且 没有is_outside标签 → 在里面，防止出去
# 2. 动物在10格外 且 有is_outside标签 → 在外面，防止进入
# 3. 动物在10格外 但 没有is_outside标签 → 非法状态，拉回去（除非正在回家）
# 4. 动物在10格内 但 有is_outside标签 → 非法状态，推出去（除非正在回家）

# 先标记所有这个建筑的动物
tag @e[type=#stardew:animals,tag=stardew.animal] remove stardew.temp.this_building
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.building_id = @e[type=marker,tag=stardew.temp.target_building,limit=1] stardew.building.id run tag @s add stardew.temp.this_building

# 情况1：动物在范围内 且 没有is_outside标签 → 正常状态（在里面）
# 不需要特殊处理

# 情况2：动物在范围外 且 有is_outside标签 → 正常状态（在外面）
# 不需要特殊处理

# 情况3：动物在范围外 但 没有is_outside标签 → 非法：在里面的鸡跑出去了
# 白天：无条件拉回
# 晚上18:00后：如果正在回家则允许，否则拉回
execute unless score Global sd_time matches 1080.. as @e[type=#stardew:animals,tag=stardew.temp.this_building,tag=!stardew.animal.is_outside,distance=10.1..] at @s run function stardew:building/animal/pull_back_inside
execute if score Global sd_time matches 1080.. as @e[type=#stardew:animals,tag=stardew.temp.this_building,tag=!stardew.animal.is_outside,tag=!stardew.animal.going_home,distance=10.1..] at @s run function stardew:building/animal/pull_back_inside

# 情况4：动物在范围内 但 有is_outside标签 → 非法：在外面的鸡进入了
# 白天：无条件推出
# 晚上18:00后：如果正在回家则允许，否则推出
execute unless score Global sd_time matches 1080.. as @e[type=#stardew:animals,tag=stardew.temp.this_building,tag=stardew.animal.is_outside,distance=..10] at @s run function stardew:building/animal/push_out
execute if score Global sd_time matches 1080.. as @e[type=#stardew:animals,tag=stardew.temp.this_building,tag=stardew.animal.is_outside,tag=!stardew.animal.going_home,distance=..10] at @s run function stardew:building/animal/push_out

# 清除临时标签
tag @e remove stardew.temp.this_building
tag @e remove stardew.temp.stay_inside
tag @e remove stardew.temp.stay_outside
