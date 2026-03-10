# data/stardew/function/energy/calculate_tool_cost.mcfunction
# 根据工具等级计算能量消耗
# 输入: #base_cost sd_temp（基础消耗）, #tool_tier sd_temp（工具等级: 1=铜 2=铁 3=金 4=钻石）
# 输出: #energy_cost sd_temp（最终消耗）

# 工具效率:
# 铜 (1): 100% 消耗 (× 100 ÷ 100 = 1.00)
# 铁 (2): 60% 消耗  (× 60 ÷ 100 = 0.60)
# 金 (3): 40% 消耗  (× 40 ÷ 100 = 0.40)
# 钻石 (4): 25% 消耗 (× 25 ÷ 100 = 0.25)

# 计算公式: energy_cost = base_cost * multiplier / 100

scoreboard players operation #energy_cost sd_temp = #base_cost sd_temp

execute if score #tool_tier sd_temp matches 1 run scoreboard players operation #energy_cost sd_temp *= #100 sd_const
execute if score #tool_tier sd_temp matches 2 run scoreboard players set #multiplier sd_temp 60
execute if score #tool_tier sd_temp matches 2 run scoreboard players operation #energy_cost sd_temp *= #multiplier sd_temp
execute if score #tool_tier sd_temp matches 3 run scoreboard players set #multiplier sd_temp 40
execute if score #tool_tier sd_temp matches 3 run scoreboard players operation #energy_cost sd_temp *= #multiplier sd_temp
execute if score #tool_tier sd_temp matches 4 run scoreboard players set #multiplier sd_temp 25
execute if score #tool_tier sd_temp matches 4 run scoreboard players operation #energy_cost sd_temp *= #multiplier sd_temp

scoreboard players operation #energy_cost sd_temp /= #100 sd_const

# 保证最低消耗至少为1点能量
execute if score #energy_cost sd_temp matches ..0 run scoreboard players set #energy_cost sd_temp 1
