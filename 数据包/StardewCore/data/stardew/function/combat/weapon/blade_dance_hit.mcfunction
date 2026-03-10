# 刀锋之舞单次攻击效果
# 伤害已在主函数计算好，存储在 #damage sd_temp 中

# 播放音效（每次攻击）
playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1.2 1.8
playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 0.8 1.5

# 粒子效果（每次攻击）
particle minecraft:sweep_attack ~ ~1 ~ 0.3 0.5 0.3 0.1 3 force
particle minecraft:enchanted_hit ~ ~1 ~ 0.5 0.5 0.5 0.2 10 force
particle minecraft:crit ~ ~1 ~ 0.5 0.5 0.5 0.3 15 force

# 执行伤害
function stardew:combat/weapon/blade_dance_damage
