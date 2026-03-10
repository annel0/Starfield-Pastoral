# 更新攻击冷却计时器（每tick运行）

# 对所有有冷却的玩家减少冷却
execute as @a[scores={sd_attack_cooldown=1..}] run scoreboard players remove @s sd_attack_cooldown 1
