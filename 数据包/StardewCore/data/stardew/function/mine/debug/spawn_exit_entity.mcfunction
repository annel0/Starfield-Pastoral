# stardew:mine/debug/spawn_exit_entity.mcfunction
# Debug: 在执行者位置生成返回主世界的实体

# 返回主世界的交互实体
execute in stardew:mine run summon minecraft:interaction ~ ~ ~ {Tags:["sd_mine_exit","sd_mine_lobby_entity"],width:2.0f,height:2.5f}

# 提示文字
execute in stardew:mine run summon minecraft:text_display ~ ~2 ~ {Tags:["sd_mine_lobby_entity"],text:'{"text":"← 返回地面","color":"aqua","bold":true}',billboard:"center",brightness:{sky:15,block:15},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.5f,1.5f,1.5f]},background:0}

tellraw @s {"text":"✓ 返回主世界实体已生成！","color":"green"}
playsound minecraft:block.note_block.pling master @s ~ ~ ~ 1 2
