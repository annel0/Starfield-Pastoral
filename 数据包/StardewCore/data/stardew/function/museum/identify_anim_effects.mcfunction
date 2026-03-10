# 鉴定动画特效 - 在物品到达中央时播放
# 粒子效果 - 在玩家正前方中央位置
execute at @p run particle minecraft:end_rod ^ ^1.5 ^1.5 0.2 0.3 0.2 0.08 25 force @a[distance=..20]
execute at @p run particle minecraft:glow ^ ^1.5 ^1.5 0.25 0.35 0.25 0.05 20 force @a[distance=..20]
execute at @p run particle minecraft:enchant ^ ^1.5 ^1.5 0.3 0.3 0.3 0.5 15 force @a[distance=..20]

# 声音效果(柔和的魔法音效)
execute at @p run playsound minecraft:block.enchantment_table.use master @a[distance=..20] ~ ~ ~ 0.8 1.3
execute at @p run playsound minecraft:entity.experience_orb.pickup master @a[distance=..20] ~ ~ ~ 0.7 1.5
