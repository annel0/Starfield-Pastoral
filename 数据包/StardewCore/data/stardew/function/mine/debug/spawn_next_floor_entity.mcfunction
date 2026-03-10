# stardew:mine/debug/spawn_next_floor_entity.mcfunction
# Debug: 在执行者位置生成进入下一层的实体

# 进入下一层的交互实体
execute in stardew:mine run summon minecraft:interaction ~ ~ ~ {Tags:["sd_mine_next_floor","sd_mine_lobby_entity"],width:2.0f,height:2.5f}

# 提示文字
execute in stardew:mine run summon minecraft:text_display ~ ~2 ~ {Tags:["sd_mine_lobby_entity"],text:'{"text":"→ 进入矿洞","color":"yellow","bold":true}',billboard:"center",brightness:{sky:15,block:15},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.5f,1.5f,1.5f]},background:0}

tellraw @s {"text":"✓ 进入下一层实体已生成！","color":"green"}
playsound minecraft:block.note_block.pling master @s ~ ~ ~ 1 2
