# ================================================================
# 应用发光效果 (Light Block)
# ================================================================
# @s = 玩家
# 需要 sd_glow_level 已设置 (5=小光圈, 10=大光圈)

# 如果没有发光等级,清理所有光源并返回
execute unless score @s sd_glow_level matches 1.. run return run function stardew:equipment/effects/rings/remove_all_player_lights

# 首次运行:初始化上次位置为当前位置(避免清理不存在的位置)
execute unless score @s stardew.light.last_x matches -2147483648..2147483647 store result score @s stardew.light.last_x run data get entity @s Pos[0] 1
execute unless score @s stardew.light.last_y matches -2147483648..2147483647 store result score @s stardew.light.last_y run data get entity @s Pos[1] 1
execute unless score @s stardew.light.last_z matches -2147483648..2147483647 store result score @s stardew.light.last_z run data get entity @s Pos[2] 1

# 保存当前位置
execute store result score #current_x stardew.temp run data get entity @s Pos[0] 1
execute store result score #current_y stardew.temp run data get entity @s Pos[1] 1
execute store result score #current_z stardew.temp run data get entity @s Pos[2] 1

# 检查是否移动了(与上次位置比较)
scoreboard players operation #diff_x stardew.temp = #current_x stardew.temp
scoreboard players operation #diff_x stardew.temp -= @s stardew.light.last_x
scoreboard players operation #diff_y stardew.temp = #current_y stardew.temp
scoreboard players operation #diff_y stardew.temp -= @s stardew.light.last_y
scoreboard players operation #diff_z stardew.temp = #current_z stardew.temp
scoreboard players operation #diff_z stardew.temp -= @s stardew.light.last_z

# 如果任意坐标改变,说明移动了
scoreboard players set #moved stardew.temp 0
execute unless score #diff_x stardew.temp matches 0 run scoreboard players set #moved stardew.temp 1
execute unless score #diff_y stardew.temp matches 0 run scoreboard players set #moved stardew.temp 1
execute unless score #diff_z stardew.temp matches 0 run scoreboard players set #moved stardew.temp 1

# 如果移动了且有上次位置记录,清理旧位置的光源
execute if score #moved stardew.temp matches 1 if score @s stardew.light.last_x matches -2147483648..2147483647 run function stardew:equipment/effects/rings/cleanup_old_position

# 在当前位置放置光源
function stardew:equipment/effects/rings/place_current_lights

# 保存当前位置为下次的"旧位置"
execute store result score @s stardew.light.last_x run data get entity @s Pos[0] 1
execute store result score @s stardew.light.last_y run data get entity @s Pos[1] 1
execute store result score @s stardew.light.last_z run data get entity @s Pos[2] 1
