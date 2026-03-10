# stardew:mine/ladder/apply_glow.mcfunction
# 给最后一个石头添加发光效果
# 执行者: 石头的 interaction 实体
# 执行位置: 石头位置

# 给旁边的 item_display 添加发光效果并加上持久高亮标签
execute as @e[type=item_display,tag=sd_stone_display,distance=..1,limit=1,sort=nearest] run tag @s add sd_last_stone_glow
execute as @e[type=item_display,tag=sd_stone_display,distance=..1,limit=1,sort=nearest] run data merge entity @s {Glowing:1b,glow_color_override:16776960}
