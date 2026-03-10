# 打开NPC对话框
# 使用方法: scoreboard players set @s stardew_dialogue_open 1
# 需要先设置storage stardew:dialogue {npc:"npc_name", dialogue_id:"dialogue_key"}

scoreboard players reset @s stardew_dialogue_open

# 给玩家添加对话标签
tag @s add in_dialogue
tag @s add closing

# 创建对话框基座实体（用于固定位置）
summon item_display ~ ~0.5 ~ {Tags:["dialogue_base","new_dialogue"]}
execute positioned ~ ~0.5 ~ run ride @s mount @e[type=item_display,tag=dialogue_base,tag=new_dialogue,distance=..0.2,limit=1]

# 创建交互实体（用于点击检测）- 覆盖整个对话框
# interaction 降低1格便于点击：^0.8，距离调整为 ^1.6
# 强制俯仰角为0，避免Y轴视角影响距离
execute rotated ~ 0 run summon interaction ^ ^0.8 ^1.6 {Tags:["dialogue_menu","new_dialogue"],width:5.0f,height:2.0f}

# 设置朝向：只使用玩家的水平旋转（Yaw），俯仰角固定为0
execute store result entity @e[type=interaction,tag=dialogue_menu,tag=new_dialogue,distance=..5,limit=1] Rotation[0] float 1 run data get entity @s Rotation[0]
data modify entity @e[type=interaction,tag=dialogue_menu,tag=new_dialogue,distance=..5,limit=1] Rotation[1] set value 0f

# 播放打开音效
playsound ui.toast.in block @s ~ ~ ~ 1 1.2

# 显示对话框内容（高度 ^2.2，距离 ^1.6）
# 强制俯仰角为0，避免Y轴视角影响距离
execute rotated ~ 0 positioned ^ ^2.2 ^1.6 as @e[type=interaction,tag=dialogue_menu,tag=new_dialogue,distance=..5] at @s run function stardew:dialogue/menus/show_dialogue

# 移除new_dialogue标签
tag @e[tag=new_dialogue] remove new_dialogue
