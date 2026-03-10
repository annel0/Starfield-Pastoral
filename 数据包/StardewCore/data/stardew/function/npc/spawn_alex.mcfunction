# NPC召唤系统 - Alex示例
# 使用方法: function stardew:npc/spawn_alex

# 1. 召唤Animated Java实体 (使用人形模型,假设您会创建alex模型)
# 这里先用chicken作为示例，您可以替换为alex模型
function animated_java:chicken/summon {args:{animation:"idle"}}

# 2. 为Animated Java实体添加NPC标签和数据
execute as @n[tag=aj.chicken.root,tag=aj.new] run tag @s add npc
execute as @n[tag=aj.chicken.root,tag=aj.new] run tag @s add npc_alex
execute as @n[tag=aj.chicken.root,tag=aj.new] run scoreboard players set @s stardew_npc_id 1
execute as @n[tag=aj.chicken.root,tag=aj.new] run scoreboard players set @s stardew_animation_state 1

# 3. 在Animated Java实体位置召唤Interaction实体
execute as @n[tag=aj.chicken.root,tag=aj.new] at @s run summon interaction ~ ~ ~ {Tags:["npc_interaction","npc_alex_interaction"],width:1.0f,height:2.0f,response:1b}

# 4. 绑定Interaction与Animated Java实体
execute as @n[tag=aj.chicken.root,tag=aj.new] run scoreboard players operation @n[tag=npc_alex_interaction,distance=..2] stardew_npc_id = @s stardew_npc_id

# 5. 初始化NPC数据到storage
execute as @n[tag=aj.chicken.root,tag=aj.new] run function stardew:npc/alex/init_data

# 6. 移除new标签
execute as @n[tag=aj.chicken.root,tag=aj.new] run tag @s remove aj.new

tellraw @a[tag=debug] {"text":"Alex NPC 已召唤","color":"green"}