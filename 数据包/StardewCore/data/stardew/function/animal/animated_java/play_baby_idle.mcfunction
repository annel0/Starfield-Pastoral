# ================================================================
# 幼年鸡静止状态（停止所有动画）
# ================================================================
# @s = aj.chicken_baby.root

function animated_java:chicken_baby/animations/pause_all
scoreboard players set @s stardew.animal.anim_state 0
