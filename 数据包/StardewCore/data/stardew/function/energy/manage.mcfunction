# data/stardew/function/energy/manage.mcfunction
# 能量系统管理

# 1. 保持玩家饱和（完全接管原版饱食度系统）
effect give @s minecraft:saturation infinite 0 true

# 2. 限制能量范围（0 到 max_energy）
execute if score @s sd_energy > @s sd_max_energy run scoreboard players operation @s sd_energy = @s sd_max_energy
execute if score @s sd_energy matches ..-1 run scoreboard players set @s sd_energy 0

# 3. 能量耗尽提示（每秒最多一次）
execute if score @s sd_energy matches 0 unless score @s sd_energy_warn matches 1.. run function stardew:energy/warn_depleted
execute if score @s sd_energy matches 0 run scoreboard players set @s sd_energy_warn 20
execute if score @s sd_energy_warn matches 1.. run scoreboard players remove @s sd_energy_warn 1
