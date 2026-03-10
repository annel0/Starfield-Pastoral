# ================================================================
# 星露谷物语 - 绑定 AJ 幼年绵羊的 ID
# ================================================================
# 用途：为 AJ root entity 绑定动物系统的 ID
# @s = AJ root entity

# 复制最近的逻辑绵羊的 ID
scoreboard players operation @s stardew.animal.id = @e[type=sheep,tag=stardew.animal,sort=nearest,limit=1] stardew.animal.id

# 复制类型
scoreboard players set @s stardew.animal.type 203

# 标记为已绑定
tag @s add stardew.animal.aj_bound

# 初始化动画状态
scoreboard players set @s stardew.animal.anim_state 0
