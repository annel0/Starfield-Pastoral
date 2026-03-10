# 测试NPC - Lewis（镇长）的对话
# 使用方法: function stardew:dialogue/npcs/lewis

# 设置NPC数据到storage
data modify storage stardew:dialogue current set value {npc:"Lewis",npc_display_name:'{"text":"刘易斯","color":"#8B4513","bold":true}',dialogue_page:0,total_pages:1}

# 设置对话内容
data modify storage stardew:dialogue current.text set value ['{"text":"欢迎来到鹈鹕镇！","color":"#2C1810"}','{"text":"我是这里的镇长，刘易斯。","color":"#2C1810"}','{"text":"如果你需要任何帮助，随时来找我！","color":"#2C1810"}']

# 设置头像（String CMD 12160 = Lewis中性）
data modify storage stardew:dialogue current.portrait set value {id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12160}}

# 打开对话框
scoreboard players set @s stardew_dialogue_open 1
function stardew:dialogue/player_open
