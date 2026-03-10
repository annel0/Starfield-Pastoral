# ================================================================
# 星露谷物语 - 绑定成年山羊ID
# ================================================================
# @s = 新生成的 AJ 山羊模型
# ================================================================

# 复制最近的山羊的ID（山羊使用sheep实体，type=202）
scoreboard players operation @s stardew.animal.id = @e[type=sheep,tag=stardew.animal,scores={stardew.animal.type=202},sort=nearest,limit=1] stardew.animal.id

# 设置动物类型
scoreboard players set @s stardew.animal.type 202

# 标记已绑定
tag @s add stardew.animal.aj_bound

# 初始化动画状态为 idle
scoreboard players set @s stardew.animal.anim_state 0
