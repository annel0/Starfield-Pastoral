# stardew:mine/ladder/spawn_impl.mcfunction
# 实际生成梯子实体 (使用宏参数指定坐标)
# 参数: $(x), $(y), $(z)

# 生成坑视觉实体 (item_display，CMD 7300 的 stone)
# 往上移动 0.625 格
$execute in stardew:mine positioned $(x) $(y) $(z) run summon minecraft:item_display ~ ~ ~ {Tags:["sd_mine_ladder_down","sd_mine_entity"],item:{id:"minecraft:stone",count:1,components:{"minecraft:custom_model_data":7300}},transformation:{translation:[0.0f,0.625f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[1.0f,1.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]}}

# 生成交互实体 (用于右键检测)
$execute in stardew:mine positioned $(x) $(y) $(z) run summon minecraft:interaction ~ ~ ~ {Tags:["sd_mine_ladder_down","sd_mine_entity"],width:1.0f,height:0.5f}

# 生成提示文字
$execute in stardew:mine positioned $(x) $(y) $(z) run summon minecraft:text_display ~ ~0.8 ~ {Tags:["sd_mine_entity","sd_mine_ladder_text"],text:'{"text":"⬇ 进入下一层","color":"yellow"}',billboard:"vertical",shadow:true,transformation:{scale:[0.5f,0.5f,0.5f]}}
