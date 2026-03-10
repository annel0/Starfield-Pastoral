# 显示继续按钮（CMD 11004）
# 在最后一页时调用
# 此函数由 show_dialogue 调用，@s 是 interaction 实体

# 召唤按钮
summon item_display ~ ~ ~ {Tags:["dialogue_element","continue_button","new_button"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.2f,0.2f,0.1f],right_rotation:[0f,1f,0f,0f]},item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11004}}}

# 设置位置和朝向（使用 @s 也就是 interaction 的朝向）
execute rotated as @s rotated ~ 0 run tp @e[type=item_display,tag=new_button,distance=..3,limit=1,sort=nearest] ^-1.6875 ^-0.375 ^-0.0625 ~ ~

# 移除临时标签
tag @e[type=item_display,tag=new_button] remove new_button
