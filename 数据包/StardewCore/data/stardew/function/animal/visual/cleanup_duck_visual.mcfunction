# ================================================================
# 清理鸭子的旧视觉实体
# ================================================================
# 用途：移除鸭子（type=102）的旧 item_display 视觉实体
# 因为鸭子现在使用 Animated Java 系统

# 找出所有type=102（鸭子）的ID
execute as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.type matches 102 run scoreboard players operation #cleanup_id stardew.animal.temp = @s stardew.animal.id

# 杀死对应的 item_display 视觉实体
execute as @e[type=item_display,tag=stardew.animal.visual] if score @s stardew.animal.id = #cleanup_id stardew.animal.temp run kill @s

tellraw @a [{"text":"[动物系统] ","color":"green"},{"text":"已清理鸭子的旧视觉实体模型","color":"yellow"}]
