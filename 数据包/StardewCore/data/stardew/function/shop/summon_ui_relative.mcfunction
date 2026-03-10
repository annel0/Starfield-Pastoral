# 召唤商店UI - 固定坐标版本 (interiors维度)
# 玩家站位: 0 64 7，朝向正南
# UI召唤在玩家前方1格: 0 64 8
# UI朝向正北，玩家从南侧观看

# === 第一组元素 (left_rotation: 0 -0.309 0 0.951 - Y轴旋转180度) ===

# 对话框
execute in stardew:interiors run summon item_display -1.5 64.875 9.4375 {billboard:"fixed",Tags:["shop_ui","dialogue_frame"],transformation:{left_rotation:[0f,-0.309f,0f,0.951f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.7f,0.7f,0.7f]},item:{id:"minecraft:string",components:{custom_model_data:11101}}}

# 对话文字
execute in stardew:interiors run summon text_display 3.9375 65.125 9.4375 {billboard:"fixed",Tags:["shop_ui","dialogue_text"],transformation:{left_rotation:[0f,-0.309f,0f,0.951f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.4f,0.4f,0.4f]},text:'{"text":"欢迎来到我的商店!","color":"white"}',alignment:"left",background:0}

# 头像框
execute in stardew:interiors run summon item_display -1.3125 66.0625 8.5 {billboard:"fixed",Tags:["shop_ui","portrait_frame"],transformation:{left_rotation:[0f,-0.309f,0f,0.951f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:string",components:{custom_model_data:11100}}}

# NPC头像
execute in stardew:interiors run summon item_display -1.3125 66.1875 8.5 {billboard:"fixed",Tags:["shop_ui","portrait"],transformation:{left_rotation:[0f,-0.309f,0f,0.951f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},item:{id:"minecraft:string",components:{custom_model_data:12220}}}

# === 第二组元素 (left_rotation: 0 0.156 0 0.988 - Y轴旋转180度) ===

# 关闭按钮
execute in stardew:interiors run summon item_display -0.375 66.75 7.3758 {billboard:"fixed",Tags:["shop_ui","close_button"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:string",components:{custom_model_data:11108}}}

# 上一页按钮
execute in stardew:interiors run summon item_display -0.375 65.875 7.3758 {billboard:"fixed",Tags:["shop_ui","page_up"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:string",components:{custom_model_data:11106}}}

# 下一页按钮
execute in stardew:interiors run summon item_display -0.375 65 7.3758 {billboard:"fixed",Tags:["shop_ui","page_down"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:string",components:{custom_model_data:11107}}}

# 商品区背景
execute in stardew:interiors run summon item_display 2.1875 65.5625 8.9375 {billboard:"fixed",Tags:["shop_ui","goods_background"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.4f,1.27f,0.6f]},item:{id:"minecraft:string",components:{custom_model_data:11103}}}

# 商品槽位框1
execute in stardew:interiors run summon item_display 2.125 66.75 8.625 {billboard:"fixed",Tags:["shop_ui","slot_frame","slot_1"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.3f,1.3f,1f]},item:{id:"minecraft:string",components:{custom_model_data:11105}}}

# 商品槽位框2
execute in stardew:interiors run summon item_display 2.125 66.1875 8.625 {billboard:"fixed",Tags:["shop_ui","slot_frame","slot_2"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.3f,1.3f,1f]},item:{id:"minecraft:string",components:{custom_model_data:11105}}}

# 商品槽位框3
execute in stardew:interiors run summon item_display 2.125 65.625 8.625 {billboard:"fixed",Tags:["shop_ui","slot_frame","slot_3"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.3f,1.3f,1f]},item:{id:"minecraft:string",components:{custom_model_data:11105}}}

# 金钱外框
execute in stardew:interiors run summon item_display 2.0625 64.375 8.6875 {billboard:"fixed",Tags:["shop_ui","money_frame"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.8f,0.6f,0.3f]},item:{id:"minecraft:string",components:{custom_model_data:11102}}}

# 金钱数字
execute in stardew:interiors run summon text_display 2 64.4375 8.75 {billboard:"fixed",Tags:["shop_ui","money_text"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"score":{"name":"@p","objective":"sd_gold"},"color":"gold","bold":true}',alignment:"center",background:0}

# === 商品显示 (3个槽位) ===

# 商品1图标
execute in stardew:interiors run summon item_display 0.625 66.4214 7.8125 {billboard:"fixed",Tags:["shop_ui","item_icon","slot_1"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.2f,0.2f,0.1f]},item:{id:"minecraft:dirt"}}

# 商品2图标
execute in stardew:interiors run summon item_display 0.625 65.8589 7.8125 {billboard:"fixed",Tags:["shop_ui","item_icon","slot_2"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.2f,0.2f,0.1f]},item:{id:"minecraft:stone"}}

# 商品3图标
execute in stardew:interiors run summon item_display 0.625 65.2964 7.8125 {billboard:"fixed",Tags:["shop_ui","item_icon","slot_3"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.2f,0.2f,0.1f]},item:{id:"minecraft:oak_log"}}

# 商品1名称
execute in stardew:interiors run summon text_display 1.2112 66.2892 7.875 {billboard:"fixed",Tags:["shop_ui","item_name","slot_1"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"泥土","color":"white"}',alignment:"left",background:0}

# 商品2名称
execute in stardew:interiors run summon text_display 1.2112 65.7267 7.875 {billboard:"fixed",Tags:["shop_ui","item_name","slot_2"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"石头","color":"white"}',alignment:"left",background:0}

# 商品3名称
execute in stardew:interiors run summon text_display 1.2112 65.1642 7.875 {billboard:"fixed",Tags:["shop_ui","item_name","slot_3"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"橡木原木","color":"white"}',alignment:"left",background:0}

# 商品1价格
execute in stardew:interiors run summon text_display 3.1875 66.2987 9.5137 {billboard:"fixed",Tags:["shop_ui","item_price","slot_1"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"10","color":"gold","bold":true}',alignment:"left",background:0}

# 商品2价格
execute in stardew:interiors run summon text_display 3.1875 65.7362 9.5137 {billboard:"fixed",Tags:["shop_ui","item_price","slot_2"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"20","color":"gold","bold":true}',alignment:"left",background:0}

# 商品3价格
execute in stardew:interiors run summon text_display 3.1875 65.1737 9.5137 {billboard:"fixed",Tags:["shop_ui","item_price","slot_3"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},text:'{"text":"15","color":"gold","bold":true}',alignment:"left",background:0}

