# stardew:mine/elevator/spawn_entity.mcfunction
# 生成电梯实体
# 执行位置: 电梯位置
# 注意: 必须在 stardew:mine 维度内调用

# 电梯视觉实体 (item_display，CMD 7301 的 stone，绕Y轴旋转270度/-90度朝西)
# left_rotation 四元数: 绕Y轴旋转270度 = [0, sin(135°), 0, cos(135°)] = [0, 0.7071, 0, -0.7071]
execute in stardew:mine run summon minecraft:item_display ~ ~0.5 ~ {Tags:["sd_mine_elevator_display","sd_mine_entity"],item:{id:"minecraft:stone",count:1,components:{"minecraft:custom_model_data":7301}},transformation:{translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.7071f,0.0f,-0.7071f],scale:[1.0f,1.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]}}

# 交互实体
execute in stardew:mine run summon minecraft:interaction ~ ~ ~ {Tags:["sd_mine_elevator","sd_mine_entity"],width:1.5f,height:2.5f}

# 电梯标识 (棕色文字，往下移动0.625格：从2.5改为1.875)
execute in stardew:mine run summon minecraft:text_display ~ ~1.875 ~ {Tags:["sd_mine_elevator_text","sd_mine_entity"],text:'{"text":"⬍ 电梯","color":"#8B4513","bold":true}',billboard:"vertical",shadow:true,transformation:{scale:[0.8f,0.8f,0.8f]}}

