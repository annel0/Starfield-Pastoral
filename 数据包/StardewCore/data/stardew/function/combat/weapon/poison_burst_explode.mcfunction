# 以中毒敌人为中心进行爆炸
# 计算引爆伤害，然后对周围3格内所有敌人造成相同的总伤害

# 计算剩余毒性伤害（毒性伤害 × 剩余时间 / 20）
scoreboard players operation #poison_total sd_temp = @s sd_poison_damage
scoreboard players operation #poison_total sd_temp *= @s sd_poison_timer
scoreboard players set #20 sd_const 20
scoreboard players operation #poison_total sd_temp /= #20 sd_const

# 50%剩余毒性作为引爆伤害
scoreboard players set #50 sd_const 50
scoreboard players operation #poison_total sd_temp *= #50 sd_const
scoreboard players set #100 sd_const 100
scoreboard players operation #poison_total sd_temp /= #100 sd_const

# 总伤害 = 基础伤害 + 引爆伤害
scoreboard players operation #total_damage sd_temp = #damage sd_temp
scoreboard players operation #total_damage sd_temp += #poison_total sd_temp

# 标记周围3格内的所有敌人（包括自己）
tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..3] add sd_poison_aoe_target

# 对范围内所有敌人造成相同的总伤害
execute as @e[tag=sd_poison_aoe_target] at @s run function stardew:combat/weapon/poison_burst_aoe_damage

# 清除中毒状态（只清除爆炸中心的）
scoreboard players set @s sd_poison_damage 0
scoreboard players set @s sd_poison_timer 0
tag @s remove sd_poisoned

# 范围粒子效果
particle minecraft:explosion_emitter ~ ~1 ~ 0 0 0 0 1 force
particle minecraft:dust{color:[0.2,0.8,0.2],scale:2} ~ ~1 ~ 1.5 1.5 1.5 0 50 force

# 清理标记
tag @e[tag=sd_poison_aoe_target] remove sd_poison_aoe_target
