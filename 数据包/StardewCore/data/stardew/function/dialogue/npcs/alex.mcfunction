# 测试NPC - Alex的对话 (兼容旧版本调用)
# 使用方法: function stardew:dialogue/npcs/alex
# 注意: 建议使用新的NPC系统: function stardew:npc/spawn_alex

# 设置NPC数据到storage (使用新的数据结构)
data modify storage stardew:dialogue current.npc set value "Alex"
data modify storage stardew:dialogue current.npc_display_name set from storage stardew:npc alex.display_name
data modify storage stardew:dialogue current.dialogue_page set value 0
data modify storage stardew:dialogue current.total_pages set value 1

# 设置对话内容 (从新的NPC数据读取)
data modify storage stardew:dialogue current.text set from storage stardew:npc alex.dialogue.hearts_0_2

# 设置头像（String CMD 12010 = Alex中性）
data modify storage stardew:dialogue current.portrait set value {id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12010}}

# 打开对话框
scoreboard players set @s stardew_dialogue_open 1
function stardew:dialogue/player_open
