# 直接测试中毒伤害效果（不等待tick）
tellraw @s {"text":"=== 立即触发中毒伤害 ===","color":"red","bold":true}

# 设置中毒状态
scoreboard players set @s sd_debuff_poison 1
scoreboard players set @s sd_poison_level 2
scoreboard players set @s sd_poison_duration 200
scoreboard players set @s sd_poison_tick_timer 20

# 立即触发一次伤害效果
function stardew:status/debuff/poison_damage

tellraw @s {"text":"✓ 已触发一次中毒伤害","color":"green"}
tellraw @s {"text":"应该看到大量粒子和听到音效","color":"yellow"}
