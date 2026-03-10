# data/stardew/function/menu/cleanup_orphaned.mcfunction
# 清理孤立的菜单实体（没有对应玩家的菜单）
# 这个函数在初始化时执行一次，清理可能残留的幽灵菜单

# 记录清理信息
tellraw @a [{"text":"[StardewCore] ","color":"green"},{"text":"正在清理孤立的菜单实体...","color":"yellow"}]

# 杀死所有没有对应玩家的菜单实体
# 检查每个菜单实体，如果没有玩家的序列号与之匹配，则清理
execute as @e[tag=sd_menu_display] at @s run function stardew:menu/check_orphaned

# 清理射线marker
kill @e[tag=sd_menu_ray]

tellraw @a [{"text":"[StardewCore] ","color":"green"},{"text":"菜单清理完成！","color":"green"}]
