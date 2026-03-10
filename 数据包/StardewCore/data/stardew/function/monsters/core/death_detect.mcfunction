# stardew:monsters/core/death_detect.mcfunction
# 统一检测怪物死亡并触发掉落

# 检测DeathTime=1s的怪物（刚死亡）
execute as @e[tag=sd_monster,nbt={DeathTime:1s}] at @s run function stardew:monsters/drops/loot_table

