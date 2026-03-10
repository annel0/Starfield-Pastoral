# stardew:mine/elevator/spawn_at_entrance.mcfunction
# 在 0 层入口生成电梯
# 执行环境: in stardew:mine

# 先清除旧的大厅电梯实体
execute in stardew:mine positioned -5 65 0 run kill @e[tag=sd_mine_lobby_elevator,distance=..3]

# 电梯外观方块
execute in stardew:mine run setblock -5 65 0 minecraft:iron_block
execute in stardew:mine run setblock -5 66 0 minecraft:iron_block

# 交互实体 (使用大厅专用标签)
execute in stardew:mine run summon minecraft:interaction -5 65 0 {Tags:["sd_mine_elevator","sd_mine_lobby_entity","sd_mine_lobby_elevator"],width:1.5f,height:2.5f}

# 电梯标识
execute in stardew:mine run summon minecraft:text_display -5 67.3 0 {Tags:["sd_mine_lobby_entity","sd_mine_lobby_elevator"],text:'{"text":"⬍ 电梯","color":"aqua","bold":true}',billboard:"vertical",shadow:true,transformation:{scale:[0.7f,0.7f,0.7f]}}
