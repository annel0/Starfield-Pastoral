# 玩家返回主世界时同步NPC位置
# @s = 刚返回主世界的玩家

# 强制同步附近的NPC到正确位置
execute as @e[tag=npc.abigail,distance=..128] at @s if score @s stardew.npc.target_schedule matches 1.. run function stardew:npc/abigail/schedule/teleport_to_target

# 可选: 显示提示(调试用)
# tellraw @s [{"text":"[NPC系统] ","color":"yellow"},{"text":"已同步NPC位置","color":"white"}]
