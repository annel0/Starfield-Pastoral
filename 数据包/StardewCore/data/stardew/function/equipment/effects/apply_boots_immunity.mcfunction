# data/stardew/function/equipment/effects/apply_boots_immunity.mcfunction
# 读取靴子的 immunity 值并计算 Debuff 持续时间减免
# Immunity 每点减少 10% 的 Debuff 持续时间

# 从 storage 读取 immunity 值
execute store result score #boots_immunity sd_temp run data get storage stardew:equipment boots.immunity 1

# 计算减免比例: 100 - (immunity * 10)
# 例如: immunity=3 → 100 - 30 = 70% (持续时间变为原来的70%)
scoreboard players set #immunity_reduction sd_temp 100
scoreboard players set #10 sd_const 10
scoreboard players operation #immunity_percent sd_temp = #boots_immunity sd_temp
scoreboard players operation #immunity_percent sd_temp *= #10 sd_const
scoreboard players operation #immunity_reduction sd_temp -= #immunity_percent sd_temp

# 将减免比例存储到临时 storage 供后续使用
execute store result storage stardew:temp immunity_reduction int 1 run scoreboard players get #immunity_reduction sd_temp
