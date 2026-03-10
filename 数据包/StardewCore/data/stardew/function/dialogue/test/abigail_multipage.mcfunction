# 测试NPC - Abigail的多页对话
# 使用方法: function stardew:dialogue/test/abigail_multipage

# 设置NPC数据到storage（多页模式）
data modify storage stardew:dialogue current set value {npc:"Abigail",npc_display_name:'{"text":"阿比盖尔","color":"#9370DB","bold":true}',dialogue_page:0,total_pages:3}

# 设置多页对话内容
data modify storage stardew:dialogue current.pages set value []
data modify storage stardew:dialogue current.pages append value ['{"text":"嗨！你也喜欢探险吗？","color":"#2C1810"}','{"text":"我刚刚在矿井里发现了一些","color":"#2C1810"}','{"text":"很酷的东西！","color":"#2C1810"}']
data modify storage stardew:dialogue current.pages append value ['{"text":"我爸妈总是不让我去矿井，","color":"#2C1810"}','{"text":"但是我觉得那里特别有意思。","color":"#2C1810"}','{"text":"你能理解那种感觉吗？","color":"#2C1810"}']
data modify storage stardew:dialogue current.pages append value ['{"text":"对了，你玩电子游戏吗？","color":"#2C1810"}','{"text":"我最近在玩一个超级好玩的","color":"#2C1810"}','{"text":"冒险游戏，有空一起玩吧！","color":"#2C1810"}']

# 设置头像（String CMD 12000 = Abigail中性）
data modify storage stardew:dialogue current.portrait set value {id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12000}}

# 打开对话框
scoreboard players set @s stardew_dialogue_open 1
function stardew:dialogue/player_open
