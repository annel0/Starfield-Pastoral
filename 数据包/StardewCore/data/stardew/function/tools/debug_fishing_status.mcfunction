# data/stardew/functions/tools/debug_fishing_status.mcfunction
# [执行者: 玩家]
# 作用：切换调试模式的开关

# 1. 记录“当前是不是开着” (打临时标签 temp_was_on)
tag @s remove temp_was_on
execute if entity @s[tag=sd_debug_mode] run tag @s add temp_was_on

# 2. 执行反转
# 如果之前是开的 -> 关掉
execute if entity @s[tag=temp_was_on] run tag @s remove sd_debug_mode
# 如果之前是关的 -> 开启
execute unless entity @s[tag=temp_was_on] run tag @s add sd_debug_mode

# 3. 发送反馈
# 关闭反馈
execute if entity @s[tag=temp_was_on] run tellraw @s {"text":"[Debug] 钓鱼监控已关闭","color":"red"}

# 开启反馈
execute unless entity @s[tag=temp_was_on] run tellraw @s {"text":"[Debug] 钓鱼监控已开启！请切回鱼竿钓鱼，数据将显示在Actionbar上。","color":"green"}
execute unless entity @s[tag=temp_was_on] run playsound minecraft:block.note_block.pling player @s ~ ~ ~ 1 2

# 4. 清理临时标签
tag @s remove temp_was_on