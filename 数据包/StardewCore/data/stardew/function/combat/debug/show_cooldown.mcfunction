# 显示攻击冷却状态（调试用）

execute as @a run title @s actionbar [{"text":"攻击冷却: ","color":"white"},{"score":{"name":"@s","objective":"sd_attack_cooldown"},"color":"yellow"},{"text":" ticks","color":"gray"}]
