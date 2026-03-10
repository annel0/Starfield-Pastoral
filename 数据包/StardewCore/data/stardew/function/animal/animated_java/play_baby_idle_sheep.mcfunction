# ================================================================
# 幼年绵羊静止状态（播放idle动画）
# ================================================================
# @s = aj.sheep_baby.root

function animated_java:sheep_baby/animations/pause_all
function animated_java:sheep_baby/animations/idle/play
scoreboard players set @s stardew.animal.anim_state 0
