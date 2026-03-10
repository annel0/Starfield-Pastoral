# 将护盾球传送到指定角度的环绕位置（固定世界坐标，不随视角旋转）
# macro参数：$(angle)

# 传送到玩家位置，然后按固定角度偏移（不使用玩家朝向）
$execute at @p[tag=sd_has_shield] rotated $(angle) 0 positioned ~ ~1.3 ~ positioned ^ ^ ^1.8 run tp @s ~ ~ ~
