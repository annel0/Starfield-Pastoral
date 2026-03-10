# 商店UI测试 - 单元素逐个生成，便于手动调整
kill @e[type=item_display,tag=shop.test.ui]

# 头像框 (CMD 11100)
summon item_display 87 -54 103 {Tags:["shop.test.ui","shop.ui.portrait_frame"],billboard:"fixed",item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11100}}}

# 头像 (Pierre的_0表情 = CMD 12220)
summon item_display 87 -54 103.05 {Tags:["shop.test.ui","shop.ui.portrait"],billboard:"fixed",item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12220}}}

# 对话框 (CMD 11101)
summon item_display 87 -50 103 {Tags:["shop.test.ui","shop.ui.dialogue_frame"],billboard:"fixed",item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11101}}}

# 对话文本
tellraw @a [{"text":"[测试] 生成对话文本位置: 87 -50 103.05"}]

# 金币框 (CMD 11102)
summon item_display 87 -48 103 {Tags:["shop.test.ui","shop.ui.money_frame"],billboard:"fixed",item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11102}}}

# 金币文本
tellraw @a [{"text":"[测试] 生成金币文本位置: 87 -48 103.05"}]

# 商品区外框+背景 (CMD 11103)
summon item_display 91 -52 103 {Tags:["shop.test.ui","shop.ui.goods_frame"],billboard:"fixed",item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11103}}}

# 商品槽背景 (CMD 11105)
summon item_display 91 -54.5 103.1 {Tags:["shop.test.ui","shop.ui.slot_bg_1"],billboard:"fixed",item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11105}}}

# 翻页按钮 (CMD 11106, 11107)
summon item_display 91 -48.5 103.1 {Tags:["shop.test.ui","shop.ui.page_up"],billboard:"fixed",item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11106}}}
summon item_display 91 -47.5 103.1 {Tags:["shop.test.ui","shop.ui.page_down"],billboard:"fixed",item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11107}}}

# 关闭按钮 (CMD 11108)
summon item_display 95 -55 103.1 {Tags:["shop.test.ui","shop.ui.close"],billboard:"fixed",item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11108}}}
