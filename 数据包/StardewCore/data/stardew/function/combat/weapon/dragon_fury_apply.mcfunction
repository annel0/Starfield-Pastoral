# 应用龙牙狂怒效果
# 注意：+50%攻击伤害通过player_attack.mcfunction检测sd_dragon_fury tag实现
# 注意：AOE攻击通过hit_monster.mcfunction检测sd_dragon_fury tag实现

# 增加抗性 Resistance II (减伤约30%)
effect give @s minecraft:resistance 200 1 true

# 粒子效果提示
particle minecraft:dragon_breath ~ ~1 ~ 0.5 0.5 0.5 0.2 20 force
particle minecraft:flame ~ ~1 ~ 0.3 0.5 0.3 0.1 15 force
particle minecraft:enchanted_hit ~ ~1 ~ 0.3 0.5 0.3 0.1 10 force



