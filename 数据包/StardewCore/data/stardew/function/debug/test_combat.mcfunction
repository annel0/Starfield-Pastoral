# 测试攻击系统

tellraw @s {"text":"━━━━━━━━━━━━━━━━━━━━━━━━","color":"gold","bold":true}
tellraw @s {"text":"   战斗系统诊断测试","color":"gold","bold":true}
tellraw @s {"text":"━━━━━━━━━━━━━━━━━━━━━━━━","color":"gold","bold":true}

# 1. 检查advancement是否存在
tellraw @s {"text":"1. 检查advancement...","color":"yellow"}
advancement revoke @s only stardew:combat/player_hurt_entity
advancement grant @s only stardew:combat/player_hurt_entity
tellraw @s {"text":"   ✓ Advancement可用","color":"green"}

# 2. 检查记分板
tellraw @s {"text":"2. 检查记分板...","color":"yellow"}
execute unless score @s sd_health matches 0.. run tellraw @s {"text":"   ✗ sd_health未初始化","color":"red"}
execute if score @s sd_health matches 0.. run tellraw @s [{"text":"   ✓ sd_health: ","color":"green"},{"score":{"name":"@s","objective":"sd_health"},"color":"white"}]

# 3. 检查武器
tellraw @s {"text":"3. 检查手持物品...","color":"yellow"}
execute unless data entity @s SelectedItem run tellraw @s {"text":"   ✗ 未手持物品","color":"red"}
execute if data entity @s SelectedItem run tellraw @s [{"text":"   ✓ 物品: ","color":"green"},{"nbt":"SelectedItem.id","entity":"@s"}]
execute if data entity @s SelectedItem.components."minecraft:custom_data".stardew_weapon run tellraw @s {"text":"   ✓ 是星露谷武器","color":"green"}
execute unless data entity @s SelectedItem.components."minecraft:custom_data".stardew_weapon run tellraw @s {"text":"   ✗ 不是星露谷武器","color":"red"}

# 4. 检查附近怪物
tellraw @s {"text":"4. 检查附近怪物...","color":"yellow"}
execute unless entity @e[tag=sd_monster,distance=..20] run tellraw @s {"text":"   ✗ 20格内没有sd_monster","color":"red"}
execute if entity @e[tag=sd_monster,distance=..20] run tellraw @s [{"text":"   ✓ 找到 ","color":"green"},{"text":"","color":"white"},{"text":" 只怪物","color":"white"}]
execute as @e[tag=sd_monster,distance=..20] run tellraw @p [{"text":"     - ","color":"gray"},{"selector":"@s"},{"text":" 距离: "},{"text":"X","color":"white"}]

# 5. 手动触发攻击测试
tellraw @s {"text":"5. 尝试手动触发攻击...","color":"yellow"}
execute if data entity @s SelectedItem.components."minecraft:custom_data".stardew_weapon run function stardew:combat/player_attack
execute unless data entity @s SelectedItem.components."minecraft:custom_data".stardew_weapon run tellraw @s {"text":"   ✗ 请先手持武器","color":"red"}

tellraw @s {"text":"━━━━━━━━━━━━━━━━━━━━━━━━","color":"gold","bold":true}
tellraw @s {"text":"现在尝试用剑攻击怪物，观察聊天栏DEBUG信息","color":"aqua"}
