# data/stardew/function/equipment/interact/ring_slots_full.mcfunction
# [执行者: 玩家] 所有戒指槽位已满

tellraw @s {"text":"戒指槽位已满！请先卸下一个戒指。","color":"red"}
playsound minecraft:block.note_block.bass master @s ~ ~ ~ 1 0.5
