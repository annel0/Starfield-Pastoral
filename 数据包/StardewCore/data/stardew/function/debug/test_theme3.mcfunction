# stardew:debug/test_theme3.mcfunction
# 快速测试 Theme3 (51-75层)
# 使用方法: /function stardew:debug/test_theme3

tellraw @s {"text":"========================================","color":"gold"}
tellraw @s {"text":"Theme3 测试 (第51-75层)","color":"gold","bold":true}
tellraw @s {"text":"特色矿物: 金矿10%, 翡翠2%, 红宝石1%","color":"yellow"}
tellraw @s {"text":"========================================","color":"gold"}

# 解锁到75层
scoreboard players set @s sd_mine_deepest 75

# 传送到55层（Theme3的中间层）
data modify storage stardew:mine target_floor set value 55
function stardew:mine/enter/to_floor

tellraw @s {"text":"✓ 已传送到第55层，开始测试！","color":"green"}
