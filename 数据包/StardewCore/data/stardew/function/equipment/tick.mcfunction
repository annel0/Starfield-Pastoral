# data/stardew/function/equipment/tick.mcfunction
# 装备系统主 tick (每 tick 执行一次)

# 检测装备交互 (右键装备物品)
execute as @a[scores={sd_equip_use=1..}] at @s run function stardew:equipment/interact/detect_equip

# 重置使用计数
scoreboard players reset @a[scores={sd_equip_use=1..}] sd_equip_use

# 应用靴子被动效果 (速度、跳跃、火免、钓鱼、磁力等)
# 注意：必须加 at @s 确保在玩家位置执行
execute as @a[scores={sd_equip_boots=1..}] at @s run function stardew:equipment/effects/apply_boots_passive_effects

# 扫描所有戒指槽位并应用效果 (at @s 很重要,用于发光和磁力)
execute as @a at @s run function stardew:equipment/effects/rings/scan_all_rings
