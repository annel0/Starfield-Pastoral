# data/stardew/function/equipment/interact/equip_boots_impl.mcfunction
# [执行者: 玩家] 实际装备鞋子

# 1. 保存完整装备数据到storage（用于卸下时恢复）
data modify storage stardew:equipment boots set from entity @s SelectedItem.components."minecraft:custom_data".stardew

# 2. 保存 CMD 到记分板（用于菜单图标显示）
execute store result score @s sd_equip_boots_cmd run data get entity @s SelectedItem.components."minecraft:custom_model_data"

# 3. 保存装备名称到storage（用于hover文字显示）
data modify storage stardew:equipment boots.display_name set from entity @s SelectedItem.components."minecraft:custom_name"

# 4. 标记槽位已占用
scoreboard players set @s sd_equip_boots 1

# 5. 消耗物品
item modify entity @s weapon.mainhand stardew:consume_one

# 6. 应用装备效果（从custom_data读取属性）
execute store result score #boots_def sd_temp run data get storage stardew:equipment temp_equip.defense
execute store result score #boots_imm sd_temp run data get storage stardew:equipment temp_equip.immunity
scoreboard players operation @s sd_defense += #boots_def sd_temp
scoreboard players operation @s sd_immunity += #boots_imm sd_temp

# 7. 提示消息
tellraw @s [{"text":"已装备: ","color":"green"},{"nbt":"boots.display_name","storage":"stardew:equipment","interpret":true}]
playsound minecraft:item.armor.equip_leather master @s ~ ~ ~ 1 1

# 8. 如果玩家在装备菜单中，刷新显示
execute if score @s sd_menu_level matches 2 run function stardew:menu/pages/equipment_menu

# 9. 清空临时数据
data remove storage stardew:equipment temp_equip
