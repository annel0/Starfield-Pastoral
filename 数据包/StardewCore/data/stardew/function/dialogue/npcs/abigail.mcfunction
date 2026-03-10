# 测试NPC - Abigail的对话
# 使用方法: function stardew:dialogue/npcs/abigail

# 设置NPC数据到storage
data modify storage stardew:dialogue current set value {npc:"Abigail",npc_display_name:'{"text":"阿比盖尔","color":"#9370DB","bold":true}',dialogue_page:0,total_pages:1}

# 设置对话内容
data modify storage stardew:dialogue current.text set value ['{"text":"嗨！你也喜欢探险吗？","color":"#2C1810"}','{"text":"我正在玩一个很酷的电子游戏。","color":"#2C1810"}','{"text":"你想过去看看吗？","color":"#2C1810"}']

# 设置头像（String CMD 12000 = Abigail中性）
data modify storage stardew:dialogue current.portrait set value {id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12000}}

# 打开对话框
scoreboard players set @s stardew_dialogue_open 1
function stardew:dialogue/player_open
