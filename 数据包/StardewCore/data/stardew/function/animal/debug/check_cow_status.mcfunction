# ================================================================
# 调试命令 - 检查最近的牛的状态
# ================================================================

# 找到最近的牛
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"━━━━━━━━━━━━━━━━━━━━","color":"gray"}]
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"[牛状态调试] ","color":"yellow","bold":true}]
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"  ID: ","color":"gray"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"aqua"}]
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"  类型: ","color":"gray"},{"score":{"name":"@s","objective":"stardew.animal.type"},"color":"white"}]
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"  年龄: ","color":"gray"},{"score":{"name":"@s","objective":"stardew.animal.age"},"color":"white"}]
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"  友谊度: ","color":"gray"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"green"}]
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"  心情: ","color":"gray"},{"score":{"name":"@s","objective":"stardew.animal.mood"},"color":"yellow"}]
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"  已喂食: ","color":"gray"},{"score":{"name":"@s","objective":"stardew.animal.fed_today"},"color":"white"}]
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"  有产物: ","color":"gray"},{"score":{"name":"@s","objective":"stardew.animal.has_produce"},"color":"white"}]
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"  产物CMD: ","color":"gray"},{"score":{"name":"@s","objective":"stardew.animal.produce_cmd"},"color":"white"}]
execute as @e[type=cow,tag=stardew.animal,limit=1,sort=nearest,distance=..10] run tellraw @a[distance=..10] [{"text":"━━━━━━━━━━━━━━━━━━━━","color":"gray"}]

execute unless entity @e[type=cow,tag=stardew.animal,distance=..10] run tellraw @s [{"text":"[错误] ","color":"red"},{"text":"附近没有找到牛！","color":"white"}]
