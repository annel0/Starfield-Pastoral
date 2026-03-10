# stardew:mine/debug/spawn_elevator_entity.mcfunction
# Debug: 在执行者位置生成电梯实体

execute at @s run function stardew:mine/elevator/spawn_entity

tellraw @s {"text":"✓ 电梯实体已生成！","color":"green"}
playsound minecraft:block.note_block.pling master @s ~ ~ ~ 1 2
