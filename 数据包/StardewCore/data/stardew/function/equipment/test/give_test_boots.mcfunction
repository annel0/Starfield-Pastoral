# data/stardew/function/equipment/test/give_test_boots.mcfunction
# [执行者: 玩家] 给予测试鞋子

give @s minecraft:carrot_on_a_stick[\
custom_model_data=501,\
custom_name='{"text":"测试鞋子","color":"aqua","italic":false}',\
lore=[\
'{"text":"防御力 +5","color":"blue","italic":false}',\
'{"text":"免疫力 +2","color":"green","italic":false}'\
],\
custom_data={\
stardew:{\
type:"boots",\
id:"test_boots",\
defense:5,\
immunity:2\
}\
}\
] 1

tellraw @s {"text":"已获得测试鞋子！右键装备","color":"green"}
