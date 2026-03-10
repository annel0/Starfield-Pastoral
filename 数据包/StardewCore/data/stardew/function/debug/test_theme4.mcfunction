# stardew:debug/test_theme4.mcfunction
# 快速测试 Theme4 (76-100层)
# 使用方法: /function stardew:debug/test_theme4

tellraw @s {"text":"========================================","color":"light_purple"}
tellraw @s {"text":"Theme4 测试 (第76-100层)","color":"light_purple","bold":true}
tellraw @s {"text":"特色矿物: 钻石8%, 五彩碎片0.2%","color":"yellow"}
tellraw @s {"text":"========================================","color":"light_purple"}

# 解锁到100层
scoreboard players set @s sd_mine_deepest 100

# 传送到85层（Theme4的中间层）
data modify storage stardew:mine target_floor set value 85
function stardew:mine/enter/to_floor

tellraw @s {"text":"✓ 已传送到第85层，开始测试！","color":"green"}
