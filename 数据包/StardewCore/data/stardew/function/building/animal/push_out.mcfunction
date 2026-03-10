# ================================================================
# 星露谷物语 - 把动物推出建筑外
# ================================================================
# 用途：当门关闭时，外面的动物尝试进入10格内，推出去
# 调用：从door_closed_logic调用，作为动物执行

# 找到所属建筑
scoreboard players operation #find_building stardew.building.temp = @s stardew.animal.building_id
tag @e[type=marker,tag=stardew.building] remove stardew.temp.my_building
execute as @e[type=marker,tag=stardew.building] if score @s stardew.building.id = #find_building stardew.building.temp run tag @s add stardew.temp.my_building

# 立即推出：朝反方向传送11格
execute facing entity @e[type=marker,tag=stardew.temp.my_building,limit=1] feet run tp @s ^ ^ ^-11

# 粒子效果（带冷却）
scoreboard players add @s stardew.animal.tp_cooldown 0
execute if score @s stardew.animal.tp_cooldown matches ..0 run particle cloud ~ ~0.5 ~ 0.2 0.2 0.2 0.01 3
execute if score @s stardew.animal.tp_cooldown matches ..0 run scoreboard players set @s stardew.animal.tp_cooldown 20

# 冷却递减
execute if score @s stardew.animal.tp_cooldown matches 1.. run scoreboard players remove @s stardew.animal.tp_cooldown 1

# 清除标签
tag @e remove stardew.temp.my_building
