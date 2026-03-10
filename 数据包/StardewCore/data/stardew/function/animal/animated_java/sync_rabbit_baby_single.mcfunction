# ================================================================
# 同步单个幼年兔
# ================================================================
# 用途：让 root entity tp 到对应的逻辑兔位置
# @s = AJ root entity (rabbit_baby)

# 保存 ID
scoreboard players operation #sync_id stardew.animal.temp = @s stardew.animal.id

# tp 到对应的逻辑兔位置
# 因为 Auto Update Rig Orientation 启用，所有子实体会自动跟随
execute as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp at @s run tp @e[tag=aj.rabbit_baby.root,tag=stardew.animal.aj_bound,limit=1,sort=nearest] ~ ~ ~ ~ 0

# 更新动画状态
function stardew:animal/animated_java/update_rabbit_baby_animation

# 检查成长状态（如果已经5天或以上，需要切换模型）
execute as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp if score @s stardew.animal.age matches 5.. run scoreboard players set #need_grow stardew.animal.temp 1

# 如果需要成长，进行模型切换
# 注意：check_rabbit_growth 需要 #check_id，所以要复制 #sync_id 到 #check_id
execute if score #need_grow stardew.animal.temp matches 1 run scoreboard players operation #check_id stardew.animal.temp = #sync_id stardew.animal.temp
execute if score #need_grow stardew.animal.temp matches 1 as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp run function stardew:animal/animated_java/check_rabbit_growth
execute if score #need_grow stardew.animal.temp matches 1 run scoreboard players set #need_grow stardew.animal.temp 0
