# stardew:debug/rebuild_entrance_entities.mcfunction
# 重新生成入口实体

# 清除旧实体
execute in stardew:mine positioned 0 65 0 run kill @e[tag=sd_mine_lobby_entity,distance=..30]

# 下层入口交互实体
execute in stardew:mine run summon minecraft:interaction 7 65 0 {Tags:["sd_mine_next_floor","sd_mine_lobby_entity"],width:2.0f,height:2.5f}

# 提示文字
execute in stardew:mine run summon minecraft:text_display 7 67 0 {Tags:["sd_mine_lobby_entity"],text:'{"text":"→ 进入矿洞","color":"yellow","bold":true}',billboard:"center",brightness:{sky:15,block:15},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.5f,1.5f,1.5f]},background:0}

# 返回主世界的传送点
execute in stardew:mine run summon minecraft:interaction -7 65 0 {Tags:["sd_mine_exit","sd_mine_lobby_entity"],width:2.0f,height:2.5f}
execute in stardew:mine run summon minecraft:text_display -7 67 0 {Tags:["sd_mine_lobby_entity"],text:'{"text":"← 返回地面","color":"aqua","bold":true}',billboard:"center",brightness:{sky:15,block:15},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.5f,1.5f,1.5f]},background:0}

# 生成电梯
execute in stardew:mine positioned -6 65 0 run function stardew:mine/elevator/spawn_entity

tellraw @a[nbt={Dimension:"stardew:mine"}] {"text":"✓ 入口实体已重新生成！","color":"green"}
