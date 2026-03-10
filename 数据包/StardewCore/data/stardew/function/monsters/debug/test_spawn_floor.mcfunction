# stardew:monsters/debug/test_spawn_floor.mcfunction
# 测试指定层数的怪物生成

# 使用方式: 先设置自己的 sd_mine_floor，然后执行此命令
# 例如: /scoreboard players set @s sd_mine_floor 50
# 然后: /function stardew:monsters/debug/test_spawn_floor

execute if score @s sd_mine_floor matches ..0 run tellraw @s {"text":"[ERROR] 请先设置层数: /scoreboard players set @s sd_mine_floor <层数>","color":"red"}
execute if score @s sd_mine_floor matches ..0 run return fail

tellraw @s [{"text":"[DEBUG] 正在第 ","color":"yellow"},{"score":{"name":"@s","objective":"sd_mine_floor"},"color":"gold"},{"text":" 层生成怪物...","color":"yellow"}]

function stardew:monsters/spawn/spawn_on_floor

