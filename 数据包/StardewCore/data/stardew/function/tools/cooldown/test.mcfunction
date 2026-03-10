# data/stardew/functions/tools/cooldown/test.mcfunction
# 测试冷却系统

# 给予工具冷却
tellraw @s {"text":"========== 工具冷却系统测试 ==========","color":"gold"}
tellraw @s {"text":"1. 触发锄头冷却 (20 ticks = 1秒)","color":"green"}
scoreboard players set @s sd_hoe_cd 20

tellraw @s {"text":"2. 触发水壶冷却 (30 ticks = 1.5秒)","color":"aqua"}
scoreboard players set @s sd_water_cd 30

tellraw @s {"text":"3. 触发镰刀冷却 (20 ticks = 1秒)","color":"yellow"}
scoreboard players set @s sd_scythe_cd 20

tellraw @s {"text":"======================================","color":"gold"}
tellraw @s {"text":"尝试使用工具，将看到冷却效果！","color":"white"}
