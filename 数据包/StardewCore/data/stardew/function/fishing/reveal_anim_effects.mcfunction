# 钓鱼展示动画特效 - 在物品到达中央时播放（水花、泡泡主题）
# 粒子效果 - 在玩家正前方中央位置

# 水花粒子（主效果）
execute at @p run particle minecraft:splash ^ ^1.5 ^1.5 0.3 0.4 0.3 0.3 30 force @a[distance=..20]
execute at @p run particle minecraft:falling_water ^ ^1.5 ^1.5 0.25 0.35 0.25 0.1 25 force @a[distance=..20]

# 泡泡粒子
execute at @p run particle minecraft:bubble_pop ^ ^1.5 ^1.5 0.3 0.3 0.3 0.05 20 force @a[distance=..20]

# 闪光效果
execute at @p run particle minecraft:glow_squid_ink ^ ^1.5 ^1.5 0.2 0.3 0.2 0.02 15 force @a[distance=..20]
execute at @p run particle minecraft:glow ^ ^1.5 ^1.5 0.25 0.35 0.25 0.05 15 force @a[distance=..20]

# 声音效果（水声+成就音效）
execute at @p run playsound minecraft:entity.player.splash master @a[distance=..20] ~ ~ ~ 1.0 1.2
execute at @p run playsound minecraft:entity.fishing_bobber.splash master @a[distance=..20] ~ ~ ~ 0.8 1.3
execute at @p run playsound minecraft:entity.experience_orb.pickup master @a[distance=..20] ~ ~ ~ 0.7 1.4
