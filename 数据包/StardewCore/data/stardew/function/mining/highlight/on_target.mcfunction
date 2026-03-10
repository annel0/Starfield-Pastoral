# data/stardew/function/mining/highlight/on_target.mcfunction
# 射线命中矿石 - 添加高亮
# 执行者: 矿石 interaction (@s)
# 执行位置: 矿石位置

# 找到对应的视觉实体(item_display)并执行高亮逻辑
execute as @e[type=item_display,tag=sd_stone_display,distance=..1,limit=1,sort=nearest] at @s run function stardew:mining/highlight/apply_highlight
