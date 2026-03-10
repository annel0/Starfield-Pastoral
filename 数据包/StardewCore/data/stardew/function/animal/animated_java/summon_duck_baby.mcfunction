# ================================================================
# 召唤幼年鸭 Animated Java 模型
# ================================================================
# 用途：为幼年鸭召唤 AJ 模型（使用 chicken_baby 模型）
# @s = 刚生成的幼年鸭逻辑实体

# 召唤 AJ 幼年鸡模型，初始播放 walk 动画（确保 scale 正常）
# 注意：幼年鸭使用 chicken_baby 模型
function animated_java:chicken_baby/summon {args:{animation:'walk'}}

# 绑定 ID 到 root entity
execute as @e[tag=aj.chicken_baby.root,tag=!stardew.animal.aj_bound,sort=nearest,limit=1] run function stardew:animal/animated_java/bind_duck_baby_id

# 立即暂停动画（静止状态）
execute as @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound,sort=nearest,limit=1] run function animated_java:chicken_baby/animations/pause_all
