# 将玩家推到NPC 2.5格外
# @s = 玩家
# 使用双marker方法计算推开位置

# 1. 召唤marker在玩家位置
execute at @s run summon marker ~ ~ ~ {Tags:["player_marker"]}

# 2. 召唤marker在NPC位置
execute at @e[tag=npc.abigail] run summon marker ~ ~ ~ {Tags:["npc_marker"]}

# 3. NPC marker面向玩家marker，前进2.5格
execute as @e[tag=npc_marker] at @s facing entity @e[tag=player_marker,limit=1] feet run tp @s ^ ^ ^2.5

# 4. 保存玩家视角
data modify storage stardew:temp rotation set from entity @s Rotation

# 5. 传送玩家到marker位置
execute at @e[tag=npc_marker,limit=1] run tp @s ~ ~ ~

# 6. 恢复玩家视角
data modify entity @s Rotation set from storage stardew:temp rotation

# 7. 清理markers
kill @e[tag=player_marker]
kill @e[tag=npc_marker]
