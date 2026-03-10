# 第2阶段 - 爆发展示(3tick快速放大)
# 在玩家面前爆发式放大+粒子声音

# 爆发式放大到3.5倍
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.scale set value [3.5f,3.5f,3.5f]

data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] interpolation_duration set value 3
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] start_interpolation set value 0

# 播放粒子和声音特效
execute at @p run particle minecraft:totem_of_undying ^ ^1.5 ^1.5 0.4 0.5 0.4 0.2 40 force @a[distance=..20]
execute at @p run particle minecraft:end_rod ^ ^1.5 ^1.5 0.3 0.4 0.3 0.1 30 force @a[distance=..20]
execute at @p run particle minecraft:glow ^ ^1.5 ^1.5 0.3 0.4 0.3 0.08 25 force @a[distance=..20]

execute at @p run playsound minecraft:item.totem.use master @a[distance=..20] ~ ~ ~ 1.2 1.5
execute at @p run playsound minecraft:entity.player.levelup master @a[distance=..20] ~ ~ ~ 0.8 1.8
