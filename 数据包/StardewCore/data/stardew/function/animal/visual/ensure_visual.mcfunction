# ================================================================
# 星露谷物语 - 确保视觉实体存在
# ================================================================
# 用途：如果动物没有视觉实体，生成一个
# @s = 动物逻辑实体

# 只处理鸡（101）
execute unless score @s stardew.animal.type matches 101 run return 0

# 检查是否已有视觉实体
scoreboard players operation #ensure_id stardew.animal.temp = @s stardew.animal.id
execute store success score #has_visual stardew.animal.temp if entity @e[type=item_display,tag=stardew.animal.visual] if score @e[type=item_display,tag=stardew.animal.visual,limit=1] stardew.animal.id = #ensure_id stardew.animal.temp

# 如果没有，生成一个
execute if score #has_visual stardew.animal.temp matches 0 at @s run function stardew:animal/visual/spawn_visual
