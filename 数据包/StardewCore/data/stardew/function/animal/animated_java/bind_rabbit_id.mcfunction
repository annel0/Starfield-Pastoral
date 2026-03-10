# ================================================================
# 绑定成年兔 ID
# ================================================================
# @s = 刚召唤的 aj.rabbit.root

# 添加绑定标签
tag @s add stardew.animal.aj_bound

# 从最近的兔复制 ID
scoreboard players operation @s stardew.animal.id = @e[type=chicken,tag=stardew.animal,sort=nearest,limit=1] stardew.animal.id

# 设置类型为兔子（103）
scoreboard players set @s stardew.animal.type 103

# 初始化动画状态（0=静止，1=走路）
scoreboard players set @s stardew.animal.anim_state 0
