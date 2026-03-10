# 重新spawn阿比盖尔（先删除再召唤）

tellraw @s {"text":"正在移除旧的阿比盖尔...","color":"yellow"}
function stardew:npc/abigail/remove

tellraw @s {"text":"在您的位置召唤新的阿比盖尔...","color":"yellow"}
execute at @s run function stardew:npc/abigail/spawn

tellraw @s {"text":"完成!阿比盖尔已重新spawn","color":"green"}
