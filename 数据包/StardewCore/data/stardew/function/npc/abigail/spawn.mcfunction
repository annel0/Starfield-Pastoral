# 召唤阿比盖尔NPC
# 使用方法: execute positioned <x> <y> <z> run function stardew:npc/abigail/spawn

# 1. 召唤核心村民实体（无AI，无敌，携带所有数据，名字常显示）
summon villager ~ ~ ~ {Tags:["npc","npc.abigail","npc.new"],NoAI:1b,Silent:1b,Invulnerable:1b,PersistenceRequired:1b,NoGravity:0b,VillagerData:{profession:"minecraft:none",level:1,type:"minecraft:plains"},CustomName:'{"text":"阿比盖尔","color":"#9966FF","bold":true}',CustomNameVisible:1b}

# 2. 给村民施加永久隐身效果
effect give @n[tag=npc.new] invisibility infinite 0 true

# 3. 设置NPC ID
scoreboard players set @n[tag=npc.new] stardew.npc.id 1

# 4. 召唤Animated Java视觉模型（在村民位置，使用宏设置初始动画）
execute as @n[tag=npc.new] at @s run function animated_java:abigail/summon {args:{animation:'idle'}}

# 5. 绑定AJ模型到村民（使用未绑定标记来找新召唤的实体）
execute as @n[tag=npc.new] at @s as @e[tag=aj.abigail.root,tag=!npc.abigail.visual,distance=..1,limit=1,sort=nearest] run function stardew:npc/abigail/bind_aj

# 6. 召唤Interaction检测实体（加大体积）
execute as @n[tag=npc.new] at @s run summon interaction ~ ~ ~ {Tags:["npc.interaction","npc.abigail.interaction","npc.new.interaction"],width:1.0f,height:2.0f,response:1b}

# 7. 绑定Interaction到村民
scoreboard players set @n[tag=npc.new.interaction] stardew.npc.id 1
tag @n[tag=npc.new.interaction] add npc.bound
tag @n[tag=npc.new.interaction] remove npc.new.interaction

# 8. 初始化NPC数据
execute as @n[tag=npc.new] run function stardew:npc/abigail/data/init

# 9. 初始化动画状态为idle
execute as @n[tag=npc.new] run scoreboard players set @s stardew.animation 1

# 10. 移除new标签
tag @n[tag=npc.new] remove npc.new

tellraw @s {"text":"已召唤阿比盖尔NPC","color":"green"}
