# 召唤商店UI所有组件 - 最终版本
# 玩家站位: 86 -54 104

# === 第一组元素 (left_rotation: 0 0.309 0 0.951, right_rotation: 0 0 0 1) ===

# 对话框
summon item_display 84.5000 -53.1250 102.5625 {billboard:"fixed",Tags:["shop_ui","dialogue_frame"],transformation:{left_rotation:[0f,0.309f,0f,0.951f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.7f,0.7f,0.7f]},item:{id:"minecraft:string",components:{custom_model_data:11101}}}

# 对话文字 (从storage读取)
summon text_display 89.9375 -52.8750 102.5625 {billboard:"fixed",Tags:["shop_ui","dialogue_text"],transformation:{left_rotation:[0f,0.309f,0f,0.951f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.4f,0.4f,0.4f]},text:'{"text":"欢迎来到我的商店!","color":"white"}',alignment:"left",background:0}

# 头像框
summon item_display 84.6875 -51.9375 102.6250 {billboard:"fixed",Tags:["shop_ui","portrait_frame"],transformation:{left_rotation:[0f,0.309f,0f,0.951f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:string",components:{custom_model_data:11100}}}

# NPC头像 (使用string.json中的X_0表情, 向上0.125格)
summon item_display 84.6875 -51.8125 102.6250 {billboard:"fixed",Tags:["shop_ui","portrait"],transformation:{left_rotation:[0f,0.309f,0f,0.951f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},item:{id:"minecraft:string",components:{custom_model_data:12220}}}

# === 第二组元素 (left_rotation: 0 -0.156 0 0.988, right_rotation: 0 0 0 1) ===

# 关闭按钮
summon item_display 85.6250 -51.2500 101.8758 {billboard:"fixed",Tags:["shop_ui","close_button"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:string",components:{custom_model_data:11108}}}

# 上一页按钮 (close_button下方0.875格)
summon item_display 85.6250 -52.1250 101.8758 {billboard:"fixed",Tags:["shop_ui","page_up"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:string",components:{custom_model_data:11106}}}

# 下一页按钮 (page_up下方0.875格)
summon item_display 85.6250 -53.0000 101.8758 {billboard:"fixed",Tags:["shop_ui","page_down"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:string",components:{custom_model_data:11107}}}

# 商品区背景 (z坐标减少0.5格)
summon item_display 88.1875 -52.4375 102.4375 {billboard:"fixed",Tags:["shop_ui","goods_background"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.4f,1.27f,0.6f]},item:{id:"minecraft:string",components:{custom_model_data:11103}}}

# 商品槽位框1
summon item_display 88.1250 -51.2500 102.6250 {billboard:"fixed",Tags:["shop_ui","slot_frame","slot_1"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.3f,1.3f,1f]},item:{id:"minecraft:string",components:{custom_model_data:11105}}}

# 商品槽位框2 (下方0.5625格)
summon item_display 88.1250 -51.8125 102.6250 {billboard:"fixed",Tags:["shop_ui","slot_frame","slot_2"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.3f,1.3f,1f]},item:{id:"minecraft:string",components:{custom_model_data:11105}}}

# 商品槽位框3 (下方0.5625格)
summon item_display 88.1250 -52.3750 102.6250 {billboard:"fixed",Tags:["shop_ui","slot_frame","slot_3"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.3f,1.3f,1f]},item:{id:"minecraft:string",components:{custom_model_data:11105}}}

# 金钱外框
summon item_display 88.0625 -53.6250 102.6875 {billboard:"fixed",Tags:["shop_ui","money_frame"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.8f,0.6f,0.3f]},item:{id:"minecraft:string",components:{custom_model_data:11102}}}

# 金钱数字显示
summon text_display 88.0000 -53.5625 102.7500 {billboard:"fixed",Tags:["shop_ui","money_text"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"score":{"name":"@p","objective":"sd_gold"},"color":"gold","bold":true}',alignment:"center",background:0}

# === 商品图标 (3个槽位) ===

# 商品1图标
summon item_display 86.6250 -51.5786 102.1875 {billboard:"fixed",Tags:["shop_ui","item_icon","slot_1"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.2f,0.2f,0.1f]},item:{id:"minecraft:dirt"}}

# 商品2图标 (下方0.5625格)
summon item_display 86.6250 -52.1411 102.1875 {billboard:"fixed",Tags:["shop_ui","item_icon","slot_2"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.2f,0.2f,0.1f]},item:{id:"minecraft:stone"}}

# 商品3图标 (下方0.5625格)
summon item_display 86.6250 -52.7036 102.1875 {billboard:"fixed",Tags:["shop_ui","item_icon","slot_3"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.2f,0.2f,0.1f]},item:{id:"minecraft:oak_log"}}

# === 商品名称文本 (3个槽位) ===

# 商品1名称
summon text_display 87.2112 -51.7108 102.3750 {billboard:"fixed",Tags:["shop_ui","item_name","slot_1"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"泥土","color":"white"}',alignment:"left",background:0}

# 商品2名称 (下方0.5625格)
summon text_display 87.2112 -52.2733 102.3750 {billboard:"fixed",Tags:["shop_ui","item_name","slot_2"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"石头","color":"white"}',alignment:"left",background:0}

# 商品3名称 (下方0.5625格)
summon text_display 87.2112 -52.8358 102.3750 {billboard:"fixed",Tags:["shop_ui","item_name","slot_3"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"橡木原木","color":"white"}',alignment:"left",background:0}

# === 商品价格文本 (3个槽位) ===

# 商品1价格
summon text_display 89.1875 -51.7013 103.0137 {billboard:"fixed",Tags:["shop_ui","item_price","slot_1"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"10","color":"gold","bold":true}',alignment:"left",background:0}

# 商品2价格 (下方0.5625格)
summon text_display 89.1875 -52.2638 103.0137 {billboard:"fixed",Tags:["shop_ui","item_price","slot_2"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"20","color":"gold","bold":true}',alignment:"left",background:0}

# 商品3价格 (下方0.5625格)
summon text_display 89.1875 -52.8263 103.0137 {billboard:"fixed",Tags:["shop_ui","item_price","slot_3"],transformation:{left_rotation:[0f,-0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"15","color":"gold","bold":true}',alignment:"left",background:0}
