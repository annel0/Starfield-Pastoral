# ================================================================
# 放置光源方块 (身体位置)
# ================================================================
# @s = 玩家
# 在身体高度放置 light block (仅大光圈)

# 放置较弱的光源在身体位置
setblock ~ ~ ~ minecraft:light[level=12,waterlogged=false] keep

# 召唤 marker 记录位置
summon marker ~ ~ ~ {Tags:["stardew.light_block","stardew.light_block.body"]}
data modify entity @e[tag=stardew.light_block,tag=!stardew.light_block.owned,sort=nearest,limit=1] data.owner_uuid set from entity @s UUID
tag @e[tag=stardew.light_block,tag=!stardew.light_block.owned,sort=nearest,limit=1] add stardew.light_block.owned
