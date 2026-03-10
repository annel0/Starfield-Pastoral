# 海浪粒子效果（单层）
# 蓝色水流粒子形成波浪形状

# 中心波浪
particle minecraft:falling_water ~ ~0.5 ~ 0.3 0.3 0.3 0.1 10 force
particle minecraft:splash ~ ~0.3 ~ 0.5 0.2 0.5 0.1 15 force
particle minecraft:bubble ~ ~0.5 ~ 0.4 0.3 0.4 0.05 8 force

# 左侧波浪
particle minecraft:falling_water ~-1 ~0.5 ~ 0.3 0.3 0.3 0.1 8 force
particle minecraft:splash ~-1 ~0.3 ~ 0.4 0.2 0.4 0.1 12 force

# 右侧波浪
particle minecraft:falling_water ~1 ~0.5 ~ 0.3 0.3 0.3 0.1 8 force
particle minecraft:splash ~1 ~0.3 ~ 0.4 0.2 0.4 0.1 12 force

# 水花特效
particle minecraft:dolphin ~ ~0.8 ~ 0.8 0.2 0.8 0.1 5 force
