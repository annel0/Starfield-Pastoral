# ================================================================
# 星露谷物语 - 同步所有动物视觉
# ================================================================
# 用途：每tick同步所有模型的位置和朝向
# 调用：从animal/core/tick.mcfunction每tick调用

# 每tick同步，配合interpolation_duration:0实现即时跟随
execute as @e[type=item_display,tag=stardew.animal.visual] at @s run function stardew:animal/visual/sync_single
