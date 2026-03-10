# 发射护盾球到攻击者

# 添加发射音效
playsound minecraft:entity.firework_rocket.launch player @a ~ ~ ~ 1 1.5
playsound minecraft:entity.wither.shoot player @a ~ ~ ~ 0.8 2

# 轨迹粒子
particle minecraft:end_rod ~ ~ ~ 0.2 0.2 0.2 0.2 20 force
particle minecraft:soul_fire_flame ~ ~ ~ 0.1 0.1 0.1 0.1 10 force
particle minecraft:flash ~ ~ ~ 0 0 0 0 1 force

# 改变item_display大小和亮度（发射准备）- 适配新尺寸
execute if entity @s[tag=sd_orb_4] run data merge entity @s {transformation:{scale:[1.2f,1.2f,1.2f]},interpolation_duration:1}
execute unless entity @s[tag=sd_orb_4] run data merge entity @s {transformation:{scale:[0.9f,0.9f,0.9f]},interpolation_duration:1}

# 朝向目标
execute facing entity @e[tag=sd_monster,distance=..10,sort=nearest,limit=1] eyes run tp @s ~ ~ ~ ~ ~

# 启动护盾球飞行追踪
tag @s add sd_shield_flying
tag @s remove sd_shield_launch

# 10 ticks后移除（到达目标或消失）
scoreboard players set @s sd_temp 10
