# 星辰冲击能量波效果

# 在视线方向每0.5格生成粒子和伤害检测
execute anchored eyes positioned ^ ^ ^0.5 run function stardew:combat/weapon/stellar_impact_step
execute anchored eyes positioned ^ ^ ^1 run function stardew:combat/weapon/stellar_impact_step
execute anchored eyes positioned ^ ^ ^1.5 run function stardew:combat/weapon/stellar_impact_step
execute anchored eyes positioned ^ ^ ^2 run function stardew:combat/weapon/stellar_impact_step
execute anchored eyes positioned ^ ^ ^2.5 run function stardew:combat/weapon/stellar_impact_step
execute anchored eyes positioned ^ ^ ^3 run function stardew:combat/weapon/stellar_impact_step
execute anchored eyes positioned ^ ^ ^3.5 run function stardew:combat/weapon/stellar_impact_step
execute anchored eyes positioned ^ ^ ^4 run function stardew:combat/weapon/stellar_impact_step
execute anchored eyes positioned ^ ^ ^4.5 run function stardew:combat/weapon/stellar_impact_step
execute anchored eyes positioned ^ ^ ^5 run function stardew:combat/weapon/stellar_impact_step

# 如果武器范围≥7格，继续延伸
execute if score #stellar_range sd_temp matches 7.. anchored eyes positioned ^ ^ ^5.5 run function stardew:combat/weapon/stellar_impact_step
execute if score #stellar_range sd_temp matches 7.. anchored eyes positioned ^ ^ ^6 run function stardew:combat/weapon/stellar_impact_step
execute if score #stellar_range sd_temp matches 7.. anchored eyes positioned ^ ^ ^6.5 run function stardew:combat/weapon/stellar_impact_step
execute if score #stellar_range sd_temp matches 7.. anchored eyes positioned ^ ^ ^7 run function stardew:combat/weapon/stellar_impact_step
