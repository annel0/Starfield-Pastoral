# 阿比盖尔的主Tick函数
# 管理阿比盖尔的所有实时逻辑

# 1. 交互检测 - 检测玩家是否与阿比盖尔交互
# 标记被点击的interaction
execute as @e[type=interaction,tag=npc.abigail.interaction] if data entity @s interaction run tag @s add npc.just_clicked
execute as @e[type=interaction,tag=npc.abigail.interaction] if data entity @s attack run tag @s add npc.just_shift_clicked

# 让附近的玩家处理（判断手持物品决定对话还是送礼）
execute as @a at @s if entity @e[type=interaction,tag=npc.abigail.interaction,tag=npc.just_clicked,distance=..5] run function stardew:npc/abigail/interact/check_interaction
execute as @a at @s if entity @e[type=interaction,tag=npc.abigail.interaction,tag=npc.just_shift_clicked,distance=..5] run function stardew:npc/abigail/interact/gift

# 清理标记和interaction数据
tag @e[type=interaction,tag=npc.abigail.interaction] remove npc.just_clicked
tag @e[type=interaction,tag=npc.abigail.interaction] remove npc.just_shift_clicked
execute as @e[type=interaction,tag=npc.abigail.interaction] run data remove entity @s interaction
execute as @e[type=interaction,tag=npc.abigail.interaction] run data remove entity @s attack

# 1.5 延迟打开对话 - 处理需要延迟打开对话的玩家
execute as @a[tag=abigail.open_dialogue] at @s run function stardew:npc/abigail/interact/open_dialogue_delayed

# 2. 动画更新 - 检测移动状态并切换动画
execute as @e[tag=npc.abigail] at @s run function stardew:npc/abigail/animation/update

# 2.5 路径移动系统 - 如果NPC正在路径移动中，执行移动逻辑
execute as @e[tag=npc.abigail] at @s run function stardew:npc/abigail/movement/tick

# 2.6 恢复idle朝向 - 如果NPC不在移动且有保存的idle朝向，恢复它
execute as @e[tag=npc.abigail] at @s unless score @s stardew.npc.path_id matches 1.. if score @s stardew.npc.idle_yaw matches -180.. store result entity @s Rotation[0] float 1 run scoreboard players get @s stardew.npc.idle_yaw

# 3. 位置和旋转同步 - **强制**使用target_yaw（如果在路径上）或villager朝向
# 3.1 如果在路径上（path_id存在），强制使用target_yaw
execute as @e[tag=npc.abigail] at @s if score @s stardew.npc.path_id matches 1.. if score @s stardew.npc.target_yaw matches -180.. run scoreboard players operation #yaw stardew.temp = @s stardew.npc.target_yaw
# 3.2 如果不在路径上，使用villager的朝向
execute as @e[tag=npc.abigail] at @s unless score @s stardew.npc.path_id matches 1.. store result score #yaw stardew.temp run data get entity @s Rotation[0]
# 3.3 同步到visual和interaction
execute as @e[tag=npc.abigail] at @s run tp @e[tag=npc.abigail.visual,limit=1,sort=nearest] ~ ~ ~ ~ 0
execute as @e[tag=npc.abigail] at @s store result entity @e[tag=npc.abigail.visual,limit=1,sort=nearest] Rotation[0] float 1 run scoreboard players get #yaw stardew.temp
execute as @e[tag=npc.abigail] at @s run tp @e[tag=npc.abigail.interaction,limit=1,sort=nearest] ~ ~ ~

# 注意: 日程检查已移至time/calc.mcfunction中的跨维度更新系统,每分钟执行一次

# 4. 延迟消耗礼物 - 在对话打开后的**下一个tick**消耗物品
execute as @a[tag=abigail.consume_gift_now] run item modify entity @s weapon.mainhand stardew:consume_one
execute as @a[tag=abigail.consume_gift_now] run tag @s remove abigail.consume_gift_now

