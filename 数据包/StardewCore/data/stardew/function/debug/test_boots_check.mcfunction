# 查看靴子数据
tellraw @s {"text":"=== 当前靴子数据 ===","color":"aqua"}

# 显示装备状态
execute if score @s sd_equip_boots matches 1.. run tellraw @s {"text":"✓ 已装备靴子","color":"green"}
execute unless score @s sd_equip_boots matches 1.. run tellraw @s {"text":"✗ 未装备靴子","color":"red"}

# 显示storage数据
tellraw @s [{"text":"Storage数据: ","color":"yellow"},{"nbt":"boots","storage":"stardew:equipment","color":"white"}]

# 显示计分板
tellraw @s [{"text":"装备标记: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_equip_boots"},"color":"white"}]
