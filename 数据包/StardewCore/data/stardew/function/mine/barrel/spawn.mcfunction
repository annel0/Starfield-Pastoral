# stardew:mine/barrel/spawn.mcfunction
# 在当前位置生成木桶
# 执行位置: stripped_oak_log位置
# 执行者: 玩家 (@s)

# 生成 item_display (木桶视觉模型 - oak_log CMD 110, Y+0.5格, 缩放1.2倍)
summon item_display ~ ~0.5 ~ {Tags:["sd_mine_barrel","sd_barrel_display","sd_mine_entity"],item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":110}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]},billboard:"fixed"}

# 生成 interaction (交互检测 - 1.1格略大于屏障,response=true 允许检测攻击)
summon interaction ~ ~ ~ {Tags:["sd_mine_barrel","sd_barrel_interaction"],width:1.1f,height:1.1f,response:1b}

# 放置屏障方块 (碰撞箱)
setblock ~ ~ ~ minecraft:barrier

# 播放放置音效
playsound minecraft:block.wood.place master @a ~ ~ ~ 0.8 1
