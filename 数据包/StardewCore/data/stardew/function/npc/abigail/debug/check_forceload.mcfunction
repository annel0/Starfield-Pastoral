# 检查强制加载状态
tellraw @s {"text":"========== 强制加载状态检查 ==========","color":"gold","bold":true}
tellraw @s ""

# 检查NPC的区块坐标
execute as @e[tag=npc.abigail,limit=1] run tellraw @a [{"text":"NPC区块X: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.npc.chunk_x"},"color":"white"}]
execute as @e[tag=npc.abigail,limit=1] run tellraw @a [{"text":"NPC区块Z: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.npc.chunk_z"},"color":"white"}]

tellraw @s ""
tellraw @s [{"text":"请执行以下命令查看强制加载的区块列表:","color":"aqua"}]
tellraw @s [{"text":"/forceload query","color":"green","clickEvent":{"action":"suggest_command","value":"/forceload query"}}]
tellraw @s ""
tellraw @s {"text":"===================================","color":"gold","bold":true}
