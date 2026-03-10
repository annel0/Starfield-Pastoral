# data/stardew/functions/debug/spawn_shipping_bin.mcfunction
# 在玩家前方生成一个“发光卖货箱” + 对应交互实体

# 1. 在玩家正前方 2 格、地面高度，放一个 chest 造型的 block_display
execute anchored feet positioned ^ ^ ^2 run summon block_display ~ ~ ~ {Tags:["sd_shipping_visual","sd_shipping_root"],block_state:{Name:"minecraft:chest",Properties:{facing:"north",type:"single",waterlogged:"false"}},transformation:{scale:[1.0f,1.0f,1.0f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]},glowing:1b,GlowColorOverride:16755200,NoGravity:1b}

# 2. 在同一位置塞一个 interaction，用来接管右键卖东西
execute anchored feet positioned ^ ^ ^2 run summon interaction ~ ~ ~ {Tags:["sd_shipping_bin","sd_shipping_root"]}

# 3. 提示一下
tellraw @s ["",{"text":"[DEBUG] ","color":"yellow"},{"text":"已在前方生成卖货箱（发光橙色）。","color":"green"}]
