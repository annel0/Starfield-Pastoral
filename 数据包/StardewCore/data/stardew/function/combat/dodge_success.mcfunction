# 闪避成功！完全避免伤害

# 视觉效果 - 残影/幻影
particle minecraft:portal ~ ~1 ~ 0.3 0.5 0.3 1 30 force
particle minecraft:smoke ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force
particle minecraft:electric_spark ~ ~1 ~ 0.4 0.6 0.4 0.2 15 force
particle minecraft:enchanted_hit ~ ~1 ~ 0.3 0.5 0.3 0.1 10 force

# 音效
playsound minecraft:entity.shulker.teleport player @s ~ ~ ~ 1 1.5
playsound minecraft:entity.bat.takeoff player @s ~ ~ ~ 1 2
playsound minecraft:entity.evoker.prepare_wololo player @s ~ ~ ~ 0.8 2

# 提示信息（使用tellraw）
tellraw @s [{"text":"✨ ","color":"aqua","bold":true},{"text":"闪避成功！","color":"aqua","bold":true}]

# 重置advancement（避免继续触发伤害）
advancement revoke @s only stardew:combat/entity_hurt_player

# return（中断伤害流程）
return 1
