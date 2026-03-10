# ================================================================
# 临时调试命令 - 给所有动物喂食
# ================================================================
# 用途：在没有喂食系统的情况下，手动标记所有动物为"已喂食"
# ================================================================

# 标记所有动物为已喂食
execute as @e[type=#stardew:animals,tag=stardew.animal] run scoreboard players set @s stardew.animal.fed_today 1

# 提示信息
tellraw @s [{"text":"[调试] ","color":"yellow","bold":true},{"text":"已将所有动物标记为「已喂食」","color":"white"}]
tellraw @s [{"text":"  ➤ ","color":"gray"},{"text":"现在可以挤奶了！","color":"green"}]

