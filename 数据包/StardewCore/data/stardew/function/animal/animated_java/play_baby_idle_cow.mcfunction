# ================================================================
# 幼年牛静止状态（播放idle动画）
# ================================================================
# @s = aj.cow_baby.root

function animated_java:cow_baby/animations/pause_all
function animated_java:cow_baby/animations/idle/play
scoreboard players set @s stardew.animal.anim_state 0
