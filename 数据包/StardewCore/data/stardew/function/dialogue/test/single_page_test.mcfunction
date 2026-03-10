# 测试单页对话（应该显示按钮）
# 使用方法: function stardew:dialogue/test/single_page_test

# 设置NPC数据到storage（单页模式）
data modify storage stardew:dialogue current set value {npc:"Alex",npc_display_name:'{"text":"亚历克斯","color":"#FFD700","bold":true}',dialogue_page:0,total_pages:1}

# 设置对话内容（单页）
data modify storage stardew:dialogue current.text set value ['{"text":"这是第一行测试","color":"#2C1810"}','{"text":"这是第二行测试","color":"#2C1810"}','{"text":"这是第三行测试","color":"#2C1810"}']

# 设置头像（String CMD 12010 = Alex中性）
data modify storage stardew:dialogue current.portrait set value {id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12010}}

# 打开对话框
scoreboard players set @s stardew_dialogue_open 1
function stardew:dialogue/player_open
