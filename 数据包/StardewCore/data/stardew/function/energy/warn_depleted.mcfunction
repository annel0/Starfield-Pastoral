# data/stardew/function/energy/warn_depleted.mcfunction
# 能量耗尽警告

tellraw @s {"text":"⚠ 你的能量已经耗尽！需要进食或休息来恢复能量。","color":"red","bold":true}
playsound minecraft:entity.player.hurt master @s ~ ~ ~ 0.5 0.8

# 打上能量耗尽标签（用于第二天惩罚）
tag @s add sd_energy_depleted
