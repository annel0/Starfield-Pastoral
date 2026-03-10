# ================================================================
# 星露谷物语 - 绑定幼年猪ID
# ================================================================
# @s = 新生成的 AJ 幼年猪模型
# ================================================================

# 复制最近的猪的ID
scoreboard players operation @s stardew.animal.id = @e[type=pig,tag=stardew.animal,sort=nearest,limit=1] stardew.animal.id

# 设置动物类型
scoreboard players set @s stardew.animal.type 204

# 标记已绑定
tag @s add stardew.animal.aj_bound

# 初始化动画状态为 idle
scoreboard players set @s stardew.animal.anim_state 0