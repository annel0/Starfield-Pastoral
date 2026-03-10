# Alex 低友谊等级对话 (0-2心)

# 从storage读取对话数据
data modify storage stardew:dialogue current.text set from storage stardew:npc alex.dialogue.hearts_0_2

# 设置NPC基础信息
data modify storage stardew:dialogue current.npc set value "Alex"
data modify storage stardew:dialogue current.npc_display_name set from storage stardew:npc alex.display_name

# 设置头像 (临时使用string，应该替换为Alex的头像)
data modify storage stardew:dialogue current.portrait set value {id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12010}}

# 打开对话框
scoreboard players set @s stardew_dialogue_open 1
function stardew:dialogue/player_open