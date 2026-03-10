# 测试所有靴子 - 使用summon方式生成物品
# 使用方法: /function stardew:debug/test_boots

# 基础靴子 (4个)
loot spawn ~ ~ ~ loot stardew:items/boots/leather_boots
loot spawn ~ ~ ~ loot stardew:items/boots/work_boots
loot spawn ~ ~ ~ loot stardew:items/boots/combat_boots
loot spawn ~ ~ ~ loot stardew:items/boots/tundra_boots

# 中级靴子 (3个)
loot spawn ~ ~ ~ loot stardew:items/boots/thermal_boots
loot spawn ~ ~ ~ loot stardew:items/boots/dark_boots
loot spawn ~ ~ ~ loot stardew:items/boots/firewalker_boots

# 高级靴子 (4个)
loot spawn ~ ~ ~ loot stardew:items/boots/genie_shoes
loot spawn ~ ~ ~ loot stardew:items/boots/space_boots
loot spawn ~ ~ ~ loot stardew:items/boots/cowboy_boots
loot spawn ~ ~ ~ loot stardew:items/boots/lucky_boots

# 特殊靴子 (4个)
loot spawn ~ ~ ~ loot stardew:items/boots/cinderclown_shoes
loot spawn ~ ~ ~ loot stardew:items/boots/emilys_magic_boots
loot spawn ~ ~ ~ loot stardew:items/boots/dragon_scale_boots
loot spawn ~ ~ ~ loot stardew:items/boots/mermaid_boots

tellraw @s {"text":"✅ 已生成全部15种靴子！","color":"green"}
