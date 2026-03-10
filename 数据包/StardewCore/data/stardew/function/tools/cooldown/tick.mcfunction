# data/stardew/functions/tools/cooldown/tick.mcfunction
# 每 tick 更新冷却系统

# 锄头冷却
execute as @a[scores={sd_hoe_cd=1..}] run function stardew:tools/cooldown/update_hoe

# 水壶冷却
execute as @a[scores={sd_water_cd=1..}] run function stardew:tools/cooldown/update_water

# 镰刀冷却
execute as @a[scores={sd_scythe_cd=1..}] run function stardew:tools/cooldown/update_scythe

# 斧头冷却
execute as @a[scores={sd_axe_cd=1..}] run function stardew:tools/cooldown/update_axe

# 镐子冷却
execute as @a[scores={sd_pickaxe_cd=1..}] run function stardew:tools/cooldown/update_pickaxe
