# ================================================================
# 星露谷物语 - 同步鸡的旋转（需要旋转180度）
# ================================================================
# 用途：同步鸡的旋转到视觉实体（加180度偏移）
# @s = 逻辑实体 (chicken)

# 读取鸡的旋转角度
execute store result score #rotation stardew.animal.temp run data get entity @s Rotation[0] 1

# 加180度
scoreboard players add #rotation stardew.animal.temp 180

# 如果超过180，减360（保持在-180到180之间）
execute if score #rotation stardew.animal.temp matches 181.. run scoreboard players remove #rotation stardew.animal.temp 360

# 应用到视觉实体
execute store result entity @e[type=item_display,tag=stardew.animal.visual,limit=1,sort=nearest] Rotation[0] float 1 run scoreboard players get #rotation stardew.animal.temp
