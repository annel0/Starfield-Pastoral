# Alex动画状态更新

# 如果正在移动且当前是idle状态，切换到walk
execute if score #is_moving stardew_temp matches 1.. if score @s stardew_animation_state matches 1 run function animated_java:chicken/animations/walk/play
execute if score #is_moving stardew_temp matches 1.. if score @s stardew_animation_state matches 1 run scoreboard players set @s stardew_animation_state 2

# 如果停止移动且当前是walk状态，切换到idle  
execute if score #is_moving stardew_temp matches 0 if score @s stardew_animation_state matches 2 run function animated_java:chicken/animations/idle/play
execute if score #is_moving stardew_temp matches 0 if score @s stardew_animation_state matches 2 run scoreboard players set @s stardew_animation_state 1