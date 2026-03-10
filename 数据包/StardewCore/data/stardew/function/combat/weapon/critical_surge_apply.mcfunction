# 应用暴击涌动效果
# 注意：暴击率加成已经在 calculate_crit.mcfunction 中通过 sd_crit_surge_active tag 自动计算
# 不需要在这里手动修改 sd_crit_chance（那个变量每tick都会被重置）

# 粒子效果提示
particle minecraft:crit ~ ~1 ~ 0.5 0.5 0.5 0.2 20 force
particle minecraft:flame ~ ~1 ~ 0.3 0.5 0.3 0.1 15 force
particle minecraft:enchanted_hit ~ ~1 ~ 0.3 0.5 0.3 0.1 10 force
