# stardew:mine/ladder/spawn_pit.mcfunction
# 生成通往下一层的坑（交互实体）
# 执行位置: 坑的位置（应该在 stardew:mine 维度）

# 生成坑视觉实体 (item_display，CMD 7300 的 stone)
# 往上移动 0.625 格 + 1格 = 1.625格
execute in stardew:mine run summon minecraft:item_display ~ ~1 ~ {Tags:["sd_mine_ladder_down","sd_mine_entity"],item:{id:"minecraft:stone",count:1,components:{"minecraft:custom_model_data":7300}},transformation:{translation:[0.0f,0.625f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[1.0f,1.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]}}

# 生成交互实体 (用于右键检测)
execute in stardew:mine run summon minecraft:interaction ~ ~1 ~ {Tags:["sd_mine_ladder_down","sd_mine_entity"],width:1.0f,height:0.5f}

# 生成提示文字
execute in stardew:mine run summon minecraft:text_display ~ ~1.8 ~ {Tags:["sd_mine_entity","sd_mine_ladder_text"],text:'{"text":"⬇ 进入下一层","color":"yellow","bold":true}',billboard:"vertical",shadow:true,transformation:{scale:[0.6f,0.6f,0.6f]}}
