# 测试 interaction 实体状态
tellraw @a [{"text":"=== 商店 Interaction 测试 ===","color":"gold"}]

# 检查商店内的interaction实体数量
execute store result score #temp sd_temp run execute in stardew:interiors if entity @e[type=interaction,tag=shop_interaction]
tellraw @a [{"text":"商店interaction总数: "},{"score":{"name":"#temp","objective":"sd_temp"},"color":"green"}]

# 检查每个按钮
execute store result score #temp sd_temp run execute in stardew:interiors if entity @e[type=interaction,tag=button_close]
tellraw @a [{"text":"  - button_close: "},{"score":{"name":"#temp","objective":"sd_temp"},"color":"aqua"}]

execute store result score #temp sd_temp run execute in stardew:interiors if entity @e[type=interaction,tag=button_page_up]
tellraw @a [{"text":"  - button_page_up: "},{"score":{"name":"#temp","objective":"sd_temp"},"color":"aqua"}]

execute store result score #temp sd_temp run execute in stardew:interiors if entity @e[type=interaction,tag=button_page_down]
tellraw @a [{"text":"  - button_page_down: "},{"score":{"name":"#temp","objective":"sd_temp"},"color":"aqua"}]

execute store result score #temp sd_temp run execute in stardew:interiors if entity @e[type=interaction,tag=slot_1]
tellraw @a [{"text":"  - slot_1: "},{"score":{"name":"#temp","objective":"sd_temp"},"color":"aqua"}]

execute store result score #temp sd_temp run execute in stardew:interiors if entity @e[type=interaction,tag=slot_2]
tellraw @a [{"text":"  - slot_2: "},{"score":{"name":"#temp","objective":"sd_temp"},"color":"aqua"}]

execute store result score #temp sd_temp run execute in stardew:interiors if entity @e[type=interaction,tag=slot_3]
tellraw @a [{"text":"  - slot_3: "},{"score":{"name":"#temp","objective":"sd_temp"},"color":"aqua"}]

# 检查interaction NBT (必须在stardew:interiors维度内执行)
tellraw @a [{"text":"\n检查 interaction NBT:","color":"yellow"}]
execute in stardew:interiors as @e[type=interaction,tag=button_close,limit=1] if data entity @s interaction run tellraw @a [{"text":"  ✓ button_close 有 interaction NBT","color":"green"}]
execute in stardew:interiors as @e[type=interaction,tag=button_close,limit=1] unless data entity @s interaction run tellraw @a [{"text":"  ✗ button_close 无 interaction NBT","color":"red"}]

execute in stardew:interiors as @e[type=interaction,tag=slot_1,limit=1] if data entity @s interaction run tellraw @a [{"text":"  ✓ slot_1 有 interaction NBT","color":"green"}]
execute in stardew:interiors as @e[type=interaction,tag=slot_1,limit=1] unless data entity @s interaction run tellraw @a [{"text":"  ✗ slot_1 无 interaction NBT","color":"red"}]

tellraw @a [{"text":"===================","color":"gold"}]
