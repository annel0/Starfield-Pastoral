# NPC动画更新系统
# 根据状态切换idle和walk动画

# 检查NPC是否需要移动
execute store success score #is_moving stardew_temp if data entity @s Motion[0]
execute store success score #is_moving2 stardew_temp if data entity @s Motion[2]
scoreboard players operation #is_moving stardew_temp += #is_moving2 stardew_temp

# Alex的动画控制
execute if entity @s[tag=npc_alex] run function stardew:npc/alex/update_animation

# 可以为其他NPC添加类似的动画控制