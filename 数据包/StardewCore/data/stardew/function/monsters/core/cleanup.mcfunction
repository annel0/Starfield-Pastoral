# stardew:monsters/core/cleanup.mcfunction
# 清理当前区域的所有怪物

# 清理矿洞中的怪物
execute in stardew:mine run kill @e[tag=sd_monster]

# 提示消息
tellraw @a[distance=..50] {"text":"[矿洞] 已清理本层怪物","color":"yellow"}

