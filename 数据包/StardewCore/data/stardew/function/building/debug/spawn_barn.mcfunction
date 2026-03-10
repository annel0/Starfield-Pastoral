# ================================================================
# 星露谷物语 - 生成畜棚（Debug）
# ================================================================
# 用途：在玩家位置生成畜棚核心
# 调用：/function stardew:building/debug/spawn_barn

# 生成位置：玩家脚下
summon marker ~ ~ ~ {Tags:["stardew.building","stardew.building.new","stardew.building.barn"]}
summon interaction ~ ~0.5 ~ {width:1.5f,height:1.5f,response:1b,Tags:["stardew.building.interaction","stardew.building.new"]}
summon armor_stand ~ ~ ~ {Tags:["stardew.building.visual","stardew.building.new"],Marker:1b,Invisible:0b,NoGravity:1b,CustomName:'{"text":"🐄 畜棚","color":"gold"}',CustomNameVisible:1b}

# 初始化建筑数据
execute as @e[tag=stardew.building.new,type=marker,limit=1] run function stardew:building/core/init_building

tellraw @s [{"text":"[建筑系统] ","color":"gold"},{"text":"已生成等级1畜棚 (ID: "},{"score":{"name":"@e[tag=stardew.building.barn,type=marker,sort=nearest,limit=1]","objective":"stardew.building.id"}},{"text":")","color":"green"}]
