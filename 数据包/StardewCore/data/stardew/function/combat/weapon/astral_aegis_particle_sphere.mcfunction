# 在指定角度生成护盾球粒子（加强版）
# 使用macro参数：$(angle)

# 主护盾球 - 使用多层粒子形成"球体"效果
$execute rotated ~$(angle) 0 positioned ^ ^1.3 ^1.8 run particle minecraft:end_rod ~ ~ ~ 0.15 0.15 0.15 0 8 force
$execute rotated ~$(angle) 0 positioned ^ ^1.3 ^1.8 run particle minecraft:glow_squid_ink ~ ~ ~ 0.1 0.1 0.1 0 3 force
$execute rotated ~$(angle) 0 positioned ^ ^1.3 ^1.8 run particle minecraft:enchant ~ ~ ~ 0.2 0.2 0.2 0.8 10 force
$execute rotated ~$(angle) 0 positioned ^ ^1.3 ^1.8 run particle minecraft:soul_fire_flame ~ ~ ~ 0.08 0.08 0.08 0.01 2 force

# 轨迹拖尾效果（在护盾球后方留下轨迹）
$execute rotated ~$(angle) 0 positioned ^ ^1.3 ^1.6 run particle minecraft:witch ~ ~ ~ 0.05 0.05 0.05 0 1 force
$execute rotated ~$(angle) 0 positioned ^ ^1.3 ^1.4 run particle minecraft:portal ~ ~ ~ 0.03 0.03 0.03 0.1 2 force
