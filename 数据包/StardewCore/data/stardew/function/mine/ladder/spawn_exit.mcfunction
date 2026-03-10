# stardew:mine/ladder/spawn_exit.mcfunction
# 生成出口梯子的交互实体和文字 (梯子方块已在结构中)
# 执行位置: 出口梯子位置
# 注意: 必须在 stardew:mine 维度内调用

# 生成交互实体 (不再放置梯子方块，结构里已有)
execute in stardew:mine run summon minecraft:interaction ~ ~ ~ {Tags:["sd_mine_ladder_exit","sd_mine_entity"],width:1.0f,height:2.5f}

# 生成提示文字
execute in stardew:mine run summon minecraft:text_display ~ ~2.0 ~ {Tags:["sd_mine_entity"],text:'{"text":"⬆ 返回入口","color":"green","bold":true}',billboard:"vertical",shadow:true,transformation:{scale:[0.6f,0.6f,0.6f]}}