# 固定位置测试对话框显示
# 用于调整UI元素位置的测试函数
# 固定召唤在世界坐标 (0, 100, 0)，朝向南方 (facing south, rotation 0 0)

# 清除旧的对话框元素
kill @e[type=item_display,tag=dialogue_element]
kill @e[type=text_display,tag=dialogue_element]
kill @e[type=interaction,tag=dialogue_interaction]

# 基准位置：0 100 0，朝向：0 0（南方）
# 所有实体都用绝对坐标召唤，方便调整

# ==================== UI 元素 ====================

# 1. 对话框统一背景（string CMD 11001）
# 缩放: 4.0 4.0 0.1
summon item_display 0 100 0 {Tags:["dialogue_element","dialogue_bg","test_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[4.0f,4.0f,0.1f],right_rotation:[0f,1f,0f,0f]},item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11001}}}

# 2. NPC Portrait（头像 - string CMD 12000 = Abigail中性）
# 缩放: 0.84 0.84 0.1 | 位置: 1.1875 100.125 0
summon item_display 1.1875 100.125 0 {Tags:["dialogue_element","npc_portrait","test_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.84f,0.84f,0.1f],right_rotation:[0f,1f,0f,0f]},item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12000}}}

# ==================== 文本元素 ====================

# 3. NPC名字标签
# 位置: 1.1875 99.5 -0.0625
summon text_display 1.1875 99.5 -0.0625 {Tags:["dialogue_element","npc_name","test_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.1f],right_rotation:[0f,1f,0f,0f]},text:'{"text":"阿比盖尔","color":"#6B4423","bold":true}',background:0,text_opacity:255,shadow:1b,alignment:"center"}

# 4. 对话文本（第一行）- 最多18个中文字符
# 位置: -0.75 100.375 -0.0625
summon text_display -0.75 100.375 -0.0625 {Tags:["dialogue_element","dialogue_text","dialogue_line_1","test_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.45f,0.45f,0.1f],right_rotation:[0f,1f,0f,0f]},text:'{"text":"你好！很高兴见到你。","color":"#2C1810"}',background:0,text_opacity:255,line_width:380,alignment:"left"}

# 5. 对话文本（第二行）
# 位置: -0.75 100.0 -0.0625 (第一行 - 0.375)
summon text_display -0.75 100.0 -0.0625 {Tags:["dialogue_element","dialogue_text","dialogue_line_2","test_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.45f,0.45f,0.1f],right_rotation:[0f,1f,0f,0f]},text:'{"text":"今天天气真不错呢！","color":"#2C1810"}',background:0,text_opacity:255,line_width:380,alignment:"left"}

# 6. 对话文本（第三行）
# 位置: -0.75 99.625 -0.0625 (第二行 - 0.375)
summon text_display -0.75 99.625 -0.0625 {Tags:["dialogue_element","dialogue_text","dialogue_line_3","test_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.45f,0.45f,0.1f],right_rotation:[0f,1f,0f,0f]},text:'{"text":"你要和我一起去探险吗？","color":"#2C1810"}',background:0,text_opacity:255,line_width:380,alignment:"left"}

# 7. 下一页按钮（string CMD 11004）
# 缩放: 0.2 0.2 0.1 | 位置: -1.6875 99.625 -0.0625
summon item_display -1.6875 99.625 -0.0625 {Tags:["dialogue_element","next_page_button","test_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.2f,0.2f,0.1f],right_rotation:[0f,1f,0f,0f]},item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11004}}}

# ==================== 提示信息 ====================

tellraw @s [{"text":"[对话系统] ","color":"green","bold":true},{"text":"固定位置测试对话框已召唤！","color":"yellow","bold":false}]
tellraw @s [{"text":"📍 位置: ","color":"aqua"},{"text":"0 100 0","color":"white"}]
tellraw @s [{"text":"🧭 朝向: ","color":"aqua"},{"text":"南方 (0 0)","color":"white"}]
tellraw @s [{"text":"","color":"gray"}]
tellraw @s [{"text":"✅ 位置已按照你的规划设置完成！","color":"green"}]
tellraw @s [{"text":"","color":"gray"}]
tellraw @s [{"text":"📊 当前坐标配置:","color":"gold"}]
tellraw @s [{"text":"  背景: 0, 100, 0","color":"white"}]
tellraw @s [{"text":"  头像: 1.1875, 100.125, 0 (缩放 0.84×0.84×0.1)","color":"white"}]
tellraw @s [{"text":"  名字: 1.1875, 99.5, -0.0625","color":"white"}]
tellraw @s [{"text":"  文本1: -0.75, 100.375, -0.0625 (最多18字)","color":"white"}]
tellraw @s [{"text":"  文本2: -0.75, 100.0, -0.0625","color":"white"}]
tellraw @s [{"text":"  文本3: -0.75, 99.625, -0.0625","color":"white"}]
tellraw @s [{"text":"  下一页: -1.6875, 99.625, -0.0625 (缩放 0.2×0.2×0.1)","color":"white"}]
tellraw @s [{"text":"","color":"gray"}]
tellraw @s [{"text":"🗑️  清除测试元素: ","color":"red"},{"text":"[点击清除]","color":"yellow","clickEvent":{"action":"run_command","value":"/kill @e[tag=test_element]"},"hoverEvent":{"action":"show_text","value":"清除所有测试对话框元素"}}]
