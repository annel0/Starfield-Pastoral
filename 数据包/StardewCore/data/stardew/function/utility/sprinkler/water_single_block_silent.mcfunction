# =========================================================
# 洒水器自动浇水单格
# =========================================================
# 执行位置: 目标方块
# 
# 注意: 此文件已废弃,浇水逻辑已移至water_check_*.mcfunction 中直接执行
# 保留此文件仅供参考

# 1. 视觉与方块更新
setblock ~ ~ ~ minecraft:farmland[moisture=7]
particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10

# 2. 标记作物已浇水
execute positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1
