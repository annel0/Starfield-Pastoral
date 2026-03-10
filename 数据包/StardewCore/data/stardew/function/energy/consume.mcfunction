# data/stardew/function/energy/consume.mcfunction
# 消耗能量的核心函数
# 需要设置 #energy_cost sd_temp（要消耗的能量值）

# 1. 检查是否有足够能量
execute if score @s sd_energy < #energy_cost sd_temp run function stardew:energy/warn_depleted
execute if score @s sd_energy < #energy_cost sd_temp run return 0

# 2. 扣除能量
scoreboard players operation @s sd_energy -= #energy_cost sd_temp

# 3. 返回成功
return 1
