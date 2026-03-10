# data/stardew/function/equipment/test/give_multiple_rings.mcfunction
# [执行者: 玩家] 给予多个测试戒指（测试队列系统）

give @s minecraft:carrot_on_a_stick[\
custom_model_data=601,\
custom_name='{"text":"戒指A","color":"light_purple","italic":false}',\
custom_data={stardew:{type:"ring",id:"ring_a"}}\
] 1

give @s minecraft:carrot_on_a_stick[\
custom_model_data=602,\
custom_name='{"text":"戒指B","color":"light_purple","italic":false}',\
custom_data={stardew:{type:"ring",id:"ring_b"}}\
] 1

give @s minecraft:carrot_on_a_stick[\
custom_model_data=603,\
custom_name='{"text":"戒指C","color":"light_purple","italic":false}',\
custom_data={stardew:{type:"ring",id:"ring_c"}}\
] 1

give @s minecraft:carrot_on_a_stick[\
custom_model_data=604,\
custom_name='{"text":"戒指D","color":"light_purple","italic":false}',\
custom_data={stardew:{type:"ring",id:"ring_d"}}\
] 1

tellraw @s {"text":"已获得4个测试戒指！","color":"green"}
