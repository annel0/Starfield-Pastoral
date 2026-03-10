# stardew:mine/ladder/refresh_glow_at_player.mcfunction
# 以玩家为中心刷新石头高亮
# 执行者: 玩家
# 执行位置: 玩家位置 (at @s)

# 给玩家周围50格范围内的所有石头item_display刷新高亮效果
execute as @e[type=item_display,tag=sd_stone_display,distance=..50] run data merge entity @s {brightness:{sky:15,block:15},Glowing:1b,glow_color_override:16776960}

# 打标签标记这些石头已被高亮
execute as @e[type=item_display,tag=sd_stone_display,distance=..50] run tag @s add sd_last_stone_glow
