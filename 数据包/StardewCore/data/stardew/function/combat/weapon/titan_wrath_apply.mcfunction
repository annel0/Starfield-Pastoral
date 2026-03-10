# 应用泰坦之怒效果
# 注意：+伤害加成通过player_attack.mcfunction检测sd_titan_wrath tag实现
# 注意：AOE攻击通过hit_monster.mcfunction检测sd_titan_wrath tag实现
# 注意：击中回血通过hit_monster.mcfunction检测sd_titan_wrath tag实现

# 粒子效果提示
particle minecraft:flame ~ ~1 ~ 0.5 0.5 0.5 0.2 20 force
particle minecraft:lava ~ ~0.5 ~ 0.3 0.3 0.3 0.05 10 force
particle minecraft:enchanted_hit ~ ~1 ~ 0.3 0.5 0.3 0.1 10 force
particle minecraft:soul_fire_flame ~ ~0.1 ~ 0.5 0 0.5 0.05 10 force
