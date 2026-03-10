# stardew:monsters/core/setup_stats.mcfunction
# 统一设置怪物属性（血量、攻击力、速度等）

# 对所有新生成的怪物进行属性设置
execute as @e[tag=sd_monster,tag=!sd_stats_set] run function stardew:monsters/core/apply_stats

