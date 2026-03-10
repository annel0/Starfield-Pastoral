# stardew:mine/ladder/refresh_glow_at_center.mcfunction
# 从中心位置刷新石头的发光效果
# 执行位置: 房间中心 (X=20, Y=66, Z=floor*100+20)

# 给所有剩余的石头display刷新发光效果（每tick刷新防止失效）
execute positioned ~ ~ ~ as @e[type=item_display,tag=sd_stone_display,distance=..40] run data merge entity @s {Glowing:1b,glow_color_override:16776960}
execute positioned ~ ~ ~ as @e[type=item_display,tag=sd_stone_display,distance=..40] run tag @s add sd_last_stone_glow
