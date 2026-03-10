# 召唤商店UI - 固定坐标版本 (interiors维度)
# 原始设定: 玩家在86 -54 104朝北 → 新设定: 玩家在0 64 7朝南
# 变换步骤: 1)转相对坐标 2)旋转180° 3)平移到新位置

# === 第一组元素 (原rotation:0 36 0 → 新rotation:0 -144 0 → left_rotation: 0 0.951 0 -0.309) ===

# 对话框 (84.5 -53.125 102.5625 → 相对(-1.5, 0.875, -1.4375) → 旋转(1.5, 0.875, 1.4375) → 新(2.5, 64.875, 8.4375))
execute in stardew:interiors run summon item_display 2.5 64.875 8.4375 {billboard:"fixed",Tags:["shop_ui","dialogue_frame","shop_animate"],transformation:{left_rotation:[0f,0.951f,0f,-0.309f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:11101}}}

# 对话文字 (调整到指定位置: 2.5665 65.1250 8.4375, 颜色改为 #853605)
execute in stardew:interiors run summon text_display 2.5665 65.1250 8.4375 {billboard:"fixed",Tags:["shop_ui","dialogue_text","shop_animate"],transformation:{left_rotation:[0f,0.951f,0f,-0.309f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},text:'{"text":"欢迎来到我的商店!","color":"#853605"}',alignment:"left",background:0}

# 头像框 (84.6875 -51.9375 102.625 → 相对(-1.3125, 2.0625, -1.375) → 旋转(1.3125, 2.0625, 1.375) → 新(2.3125, 66.0625, 8.375))
execute in stardew:interiors run summon item_display 2.3125 66.0625 8.375 {billboard:"fixed",Tags:["shop_ui","portrait_frame","shop_animate"],transformation:{left_rotation:[0f,0.951f,0f,-0.309f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:11100}}}

# NPC头像 (84.6875 -51.8125 102.625 → 相对(-1.3125, 2.1875, -1.375) → 旋转(1.3125, 2.1875, 1.375) → 新(2.3125, 66.1875, 8.375))
execute in stardew:interiors run summon item_display 2.3125 66.1875 8.375 {billboard:"fixed",Tags:["shop_ui","portrait","shop_animate"],transformation:{left_rotation:[0f,0.951f,0f,-0.309f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:12220}}}

# === 第二组元素 (原rotation:0 -18 0 → 新rotation:0 162 0 → left_rotation: 0 0.988 0 0.156) ===

# 关闭按钮 (85.625 -51.25 101.8758 → 相对(-0.375, 2.75, -2.1242) → 旋转(0.375, 2.75, 2.1242) → 新(1.375, 66.75, 9.1242))
execute in stardew:interiors run summon item_display 1.375 66.75 9.1242 {billboard:"fixed",Tags:["shop_ui","close_button","shop_button","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:11108}}}

# 上一页按钮 (85.625 -52.125 101.8758 → 相对(-0.375, 1.875, -2.1242) → 旋转(0.375, 1.875, 2.1242) → 新(1.375, 65.875, 9.1242))
execute in stardew:interiors run summon item_display 1.375 65.875 9.1242 {billboard:"fixed",Tags:["shop_ui","page_up","shop_button","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:11106}}}

# 下一页按钮 (85.625 -53 101.8758 → 相对(-0.375, 1, -2.1242) → 旋转(0.375, 1, 2.1242) → 新(1.375, 65, 9.1242))
execute in stardew:interiors run summon item_display 1.375 65 9.1242 {billboard:"fixed",Tags:["shop_ui","page_down","shop_button","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:11107}}}

# 商品区背景 (88.1875 -52.4375 102.4375 → 相对(2.1875, 1.5625, -1.5625) → 旋转(-2.1875, 1.5625, 1.5625) → 新(-1.1875, 65.5625, 8.5625))
execute in stardew:interiors run summon item_display -1.1875 65.5625 8.5625 {billboard:"fixed",Tags:["shop_ui","goods_background","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:11103}}}

# 商品槽位框1 (88.125 -51.25 102.625 → 相对(2.125, 2.75, -1.375) → 旋转(-2.125, 2.75, 1.375) → 新(-1.125, 66.75, 8.375))
execute in stardew:interiors run summon item_display -1.125 66.75 8.375 {billboard:"fixed",Tags:["shop_ui","slot_frame","slot_1","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:11105}}}

# 商品槽位框2 (88.125 -51.8125 102.625 → 相对(2.125, 2.1875, -1.375) → 旋转(-2.125, 2.1875, 1.375) → 新(-1.125, 66.1875, 8.375))
execute in stardew:interiors run summon item_display -1.125 66.1875 8.375 {billboard:"fixed",Tags:["shop_ui","slot_frame","slot_2","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:11105}}}

# 商品槽位框3 (88.125 -52.375 102.625 → 相对(2.125, 1.625, -1.375) → 旋转(-2.125, 1.625, 1.375) → 新(-1.125, 65.625, 8.375))
execute in stardew:interiors run summon item_display -1.125 65.625 8.375 {billboard:"fixed",Tags:["shop_ui","slot_frame","slot_3","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:11105}}}

# 金钱外框 (88.0625 -53.625 102.6875 → 相对(2.0625, 0.375, -1.3125) → 旋转(-2.0625, 0.375, 1.3125) → 新(-1.0625, 64.375, 8.3125))
execute in stardew:interiors run summon item_display -1.0625 64.375 8.3125 {billboard:"fixed",Tags:["shop_ui","money_frame","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:string",components:{custom_model_data:11102}}}

# 金钱数字 (调整到指定位置: -1.0372 64.370 8.3136, 颜色改为 #853605)
execute in stardew:interiors run summon text_display -1.0372 64.370 8.3136 {billboard:"fixed",Tags:["shop_ui","money_text","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},text:'{"score":{"name":"@p","objective":"sd_gold"},"color":"#853605","bold":true}',alignment:"center",background:0}

# === 商品显示 (3个槽位) ===

# 商品1图标 (86.625 -51.5786 102.1875 → 相对(0.625, 2.4214, -1.8125) → 旋转(-0.625, 2.4214, 1.8125) → 新(0.375, 66.4214, 8.8125))
execute in stardew:interiors run summon item_display 0.375 66.4214 8.8125 {billboard:"fixed",Tags:["shop_ui","item_icon","slot_1","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:dirt"}}

# 商品2图标 (86.625 -52.1411 102.1875 → 相对(0.625, 1.8589, -1.8125) → 旋转(-0.625, 1.8589, 1.8125) → 新(0.375, 65.8589, 8.8125))
execute in stardew:interiors run summon item_display 0.375 65.8589 8.8125 {billboard:"fixed",Tags:["shop_ui","item_icon","slot_2","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:stone"}}

# 商品3图标 (86.625 -52.7036 102.1875 → 相对(0.625, 1.2964, -1.8125) → 旋转(-0.625, 1.2964, 1.8125) → 新(0.375, 65.2964, 8.8125))
execute in stardew:interiors run summon item_display 0.375 65.2964 8.8125 {billboard:"fixed",Tags:["shop_ui","item_icon","slot_3","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},item:{id:"minecraft:oak_log"}}

# 商品1名称 (x:-1, y:66.2892, z:8.3750, 颜色: #853605)
execute in stardew:interiors run summon text_display -1 66.2892 8.3750 {billboard:"fixed",Tags:["shop_ui","item_name","slot_1","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},text:'{"text":"泥土","color":"#853605"}',alignment:"left",background:0}

# 商品2名称 (x:-1, y:65.7267, z:8.3750, 颜色: #853605)
execute in stardew:interiors run summon text_display -1 65.7267 8.3750 {billboard:"fixed",Tags:["shop_ui","item_name","slot_2","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},text:'{"text":"石头","color":"#853605"}',alignment:"left",background:0}

# 商品3名称 (x:-1, y:65.1642, z:8.3750, 颜色: #853605)
execute in stardew:interiors run summon text_display -1 65.1642 8.3750 {billboard:"fixed",Tags:["shop_ui","item_name","slot_3","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},text:'{"text":"橡木原木","color":"#853605"}',alignment:"left",background:0}

# 商品1价格 (相对slot1名称调整: x-1.1875, y+0.0095, z-0.3887, 颜色改为 #853605)
execute in stardew:interiors run summon text_display -2.1875 66.2987 7.9863 {billboard:"fixed",Tags:["shop_ui","item_price","slot_1","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},text:'{"text":"10","color":"#853605","bold":true}',alignment:"left",background:0}

# 商品2价格 (相对slot1价格向下偏移, 颜色改为 #853605)
execute in stardew:interiors run summon text_display -2.1875 65.7362 7.9238 {billboard:"fixed",Tags:["shop_ui","item_price","slot_2","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},text:'{"text":"20","color":"#853605","bold":true}',alignment:"left",background:0}

# 商品3价格 (相对slot1价格向下偏移, 颜色改为 #853605)
execute in stardew:interiors run summon text_display -2.1875 65.1737 7.8613 {billboard:"fixed",Tags:["shop_ui","item_price","slot_3","shop_animate"],transformation:{left_rotation:[0f,0.988f,0f,0.156f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},text:'{"text":"15","color":"#853605","bold":true}',alignment:"left",background:0}

# === Tooltip文本实体 (预先生成，hover时传送到位置) ===
execute in stardew:interiors run summon text_display -5 66 8 {billboard:"center",Tags:["shop_ui","shop_tooltip"],brightness:{block:15,sky:15},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0f,0f,0f]},text:'{"text":""}',alignment:"left",line_width:200,background:1275068416}

# === 交互实体 (Interaction entities) ===

# 关闭按钮交互区 (y下移0.3: 66.75→66.45)
execute in stardew:interiors run summon interaction 1.375 66.45 9.1242 {width:0.6f,height:0.6f,Tags:["shop_interaction","button_close"]}

# 上一页按钮交互区 (y下移0.3: 65.875→65.575)
execute in stardew:interiors run summon interaction 1.375 65.575 9.1242 {width:0.6f,height:0.6f,Tags:["shop_interaction","button_page_up"]}

# 下一页按钮交互区 (y下移0.3: 65→64.7)
execute in stardew:interiors run summon interaction 1.375 64.7 9.1242 {width:0.6f,height:0.6f,Tags:["shop_interaction","button_page_down"]}

# 商品槽1交互区 (保持 height:0.12f)
execute in stardew:interiors run summon interaction -0.5 66.35 9.2 {width:2.8f,height:0.12f,Tags:["shop_interaction","slot_1"]}

# 商品槽2交互区
execute in stardew:interiors run summon interaction -0.5 65.7875 9.2 {width:2.8f,height:0.12f,Tags:["shop_interaction","slot_2"]}

# 商品槽3交互区
execute in stardew:interiors run summon interaction -0.5 65.225 9.2 {width:2.8f,height:0.12f,Tags:["shop_interaction","slot_3"]}
