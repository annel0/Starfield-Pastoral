# ================================================================
# 放置光源方块 (脚下)
# ================================================================
# @s = 玩家
# 在脚下位置放置 light block

# 根据发光等级放置不同亮度的光源
execute if score @s sd_glow_level matches 5..9 run setblock ~ ~ ~ minecraft:light[level=8,waterlogged=false] keep
execute if score @s sd_glow_level matches 10.. run setblock ~ ~ ~ minecraft:light[level=15,waterlogged=false] keep

# 召唤 marker 记录位置,用于后续清理
# 使用玩家的 UUID 作为标记
summon marker ~ ~ ~ {Tags:["stardew.light_block","stardew.light_block.feet"]}
data modify entity @e[tag=stardew.light_block,tag=!stardew.light_block.owned,sort=nearest,limit=1] data.owner_uuid set from entity @s UUID
tag @e[tag=stardew.light_block,tag=!stardew.light_block.owned,sort=nearest,limit=1] add stardew.light_block.owned
