# data/stardew/function/equipment/test/give_test_ring.mcfunction
# [执行者: 玩家] 给予测试戒指

give @s minecraft:carrot_on_a_stick[\
custom_model_data=601,\
custom_name='{"text":"发光戒指","color":"light_purple","italic":false}',\
lore=[\
'{"text":"发光效果 +1","color":"yellow","italic":false}',\
'{"text":"攻击力 +3","color":"red","italic":false}'\
],\
custom_data={\
stardew:{\
type:"ring",\
id:"glow_ring",\
effects:{\
glow:1,\
attack:3\
}\
}\
}\
] 1

tellraw @s {"text":"已获得发光戒指！右键装备","color":"green"}
