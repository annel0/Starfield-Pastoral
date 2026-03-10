# stardew:debug/mine/test_elevator_jump.mcfunction
# 测试电梯跳层功能
# 用法: /function stardew:debug/mine/test_elevator_jump

tellraw @s [{"text":"=== 矿井电梯跳层测试 ===","color":"gold","bold":true}]
tellraw @s [{"text":"1. 传送到80层: ","color":"gray"},{"text":"[点击]","color":"green","clickEvent":{"action":"run_command","value":"/data modify storage stardew:mine target_floor set value 80; function stardew:mine/enter/to_floor"}}]
tellraw @s [{"text":"2. 传送到0层: ","color":"gray"},{"text":"[点击]","color":"green","clickEvent":{"action":"run_command","value":"/data modify storage stardew:mine target_floor set value 0; function stardew:mine/enter/to_floor"}}]
tellraw @s [{"text":"3. 传送到50层: ","color":"gray"},{"text":"[点击]","color":"green","clickEvent":{"action":"run_command","value":"/data modify storage stardew:mine target_floor set value 50; function stardew:mine/enter/to_floor"}}]
tellraw @s [{"text":"4. 传送到5层: ","color":"gray"},{"text":"[点击]","color":"green","clickEvent":{"action":"run_command","value":"/data modify storage stardew:mine target_floor set value 5; function stardew:mine/enter/to_floor"}}]
tellraw @s [{"text":"5. 查看当前层: ","color":"gray"},{"text":"[点击]","color":"aqua","clickEvent":{"action":"run_command","value":"/scoreboard players get @s sd_mine_floor"}}]
tellraw @s ""
tellraw @s [{"text":"✅ 智能清理:","color":"yellow","italic":true},{"text":" 只清理过期楼层,保留今天访问过的层","color":"gray","italic":true}]
tellraw @s [{"text":"提示:","color":"green"},{"text":" 电梯和梯子现在会被正确保留!","color":"white"}]
