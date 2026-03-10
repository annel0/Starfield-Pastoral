# 测试墓地传送

tellraw @s {"text":"=== 测试直接传送到墓地 ===","color":"gold"}

# 显示阿比盖尔当前位置
execute as @e[tag=npc.abigail,limit=1] at @s run tellraw @a ["",{"text":"阿比盖尔当前位置: ","color":"yellow"},{"nbt":"Pos","entity":"@s","color":"white"}]

# 直接调用goto_graveyard
tellraw @s {"text":"调用goto_graveyard...","color":"aqua"}
execute as @e[tag=npc.abigail] at @s run function stardew:npc/abigail/schedule/goto_graveyard

# 显示调用后的位置
execute as @e[tag=npc.abigail,limit=1] at @s run tellraw @a ["",{"text":"调用后位置: ","color":"yellow"},{"nbt":"Pos","entity":"@s","color":"white"}]
execute as @e[tag=npc.abigail,limit=1] run tellraw @a ["",{"text":"状态值: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.npc.schedule"},"color":"white"}]
