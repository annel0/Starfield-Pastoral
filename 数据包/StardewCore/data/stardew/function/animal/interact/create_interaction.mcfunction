# ================================================================
# 星露谷物语 - 为动物创建交互实体
# ================================================================
# 用途：在动物位置生成一个interaction实体，并绑定到该动物
# @s = 动物实体
# ================================================================

# 生成interaction实体
# 尺寸根据动物类型调整
execute if entity @s[type=minecraft:chicken] run summon interaction ~ ~ ~ {width:0.6f,height:0.7f,Tags:["stardew.animal.interaction","stardew.animal.interaction.new"]}
execute if entity @s[type=minecraft:rabbit] run summon interaction ~ ~ ~ {width:0.6f,height:0.5f,Tags:["stardew.animal.interaction","stardew.animal.interaction.new"]}
# 牛：根据年龄设置不同尺寸
execute if entity @s[type=minecraft:cow,scores={stardew.animal.age=..4}] run summon interaction ~ ~ ~ {width:0.7f,height:0.8f,Tags:["stardew.animal.interaction","stardew.animal.interaction.new"]}
execute if entity @s[type=minecraft:cow,scores={stardew.animal.age=5..}] run summon interaction ~ ~ ~ {width:1.1f,height:1.2f,Tags:["stardew.animal.interaction","stardew.animal.interaction.new"]}
# 绵羊：根据年龄设置不同尺寸（与牛相同）
execute if entity @s[type=minecraft:sheep,scores={stardew.animal.type=203,stardew.animal.age=..4}] run summon interaction ~ ~ ~ {width:0.7f,height:0.8f,Tags:["stardew.animal.interaction","stardew.animal.interaction.new"]}
execute if entity @s[type=minecraft:sheep,scores={stardew.animal.type=203,stardew.animal.age=5..}] run summon interaction ~ ~ ~ {width:1.1f,height:1.2f,Tags:["stardew.animal.interaction","stardew.animal.interaction.new"]}
# 山羊：根据年龄设置不同尺寸（与牛相同，山羊使用sheep实体，type=202）
execute if entity @s[type=minecraft:sheep,scores={stardew.animal.type=202,stardew.animal.age=..4}] run summon interaction ~ ~ ~ {width:0.7f,height:0.8f,Tags:["stardew.animal.interaction","stardew.animal.interaction.new"]}
execute if entity @s[type=minecraft:sheep,scores={stardew.animal.type=202,stardew.animal.age=5..}] run summon interaction ~ ~ ~ {width:1.1f,height:1.2f,Tags:["stardew.animal.interaction","stardew.animal.interaction.new"]}

# 猪：根据年龄设置不同尺寸（与山羊相同）
execute if entity @s[type=minecraft:pig,scores={stardew.animal.type=204,stardew.animal.age=..9}] run summon interaction ~ ~ ~ {width:0.7f,height:0.8f,Tags:["stardew.animal.interaction","stardew.animal.interaction.new"]}
execute if entity @s[type=minecraft:pig,scores={stardew.animal.type=204,stardew.animal.age=10..}] run summon interaction ~ ~ ~ {width:1.1f,height:1.2f,Tags:["stardew.animal.interaction","stardew.animal.interaction.new"]}

# 将动物ID复制到新创建的interaction（使用distance限制确保只复制给最近的）
execute as @e[type=interaction,tag=stardew.animal.interaction.new,limit=1,sort=nearest,distance=..1] run scoreboard players operation @s stardew.animal.id = @e[type=#stardew:animals,tag=stardew.animal,limit=1,sort=nearest,distance=..1] stardew.animal.id

# 标记动物已有interaction
tag @s add stardew.animal.has_interaction

# 移除新标签
tag @e[type=interaction,tag=stardew.animal.interaction.new,distance=..1] remove stardew.animal.interaction.new
