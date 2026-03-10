# stardew:mine/debug/spawn_entrance.mcfunction
# 调试: 在玩家位置生成矿洞入口 (主世界用)

# 生成入口交互实体 (在玩家前方2格，贴地)
execute positioned ^ ^ ^2 run summon minecraft:interaction ~ ~ ~ {Tags:["sd_mine_entrance"],width:2.0f,height:2.5f}

# 生成提示文字
execute positioned ^ ^ ^2 run summon minecraft:text_display ~ ~2.2 ~ {text:'{"text":"⛏ 矿洞入口","color":"gold","bold":true}',billboard:"vertical",shadow:true}
execute positioned ^ ^ ^2 run summon minecraft:text_display ~ ~1.7 ~ {text:'{"text":"[右键进入]","color":"yellow"}',billboard:"vertical",shadow:true,transformation:{scale:[0.7f,0.7f,0.7f]}}

tellraw @s {"text":"[调试] 矿洞入口已生成！对着文字下方右键进入矿洞。","color":"green"}
