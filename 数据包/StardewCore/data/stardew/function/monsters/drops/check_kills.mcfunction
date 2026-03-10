# stardew:monsters/drops/check_kills.mcfunction
# 统一检测怪物死亡并给予战利品（使用新的标签系统）

# 使用统一的死亡检测
execute as @e[tag=sd_monster,nbt={DeathTime:1s}] at @s run function stardew:monsters/drops/loot_table
