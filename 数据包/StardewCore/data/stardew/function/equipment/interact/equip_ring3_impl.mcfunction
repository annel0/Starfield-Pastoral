# data/stardew/function/equipment/interact/equip_ring3_impl.mcfunction
# [执行者: 玩家] 装备戒指到槽位3

# 保存完整装备数据到storage（用于卸下时恢复）
data modify storage stardew:equipment ring3 set from entity @s SelectedItem.components."minecraft:custom_data".stardew
execute store result score @s sd_equip_ring3_cmd run data get entity @s SelectedItem.components."minecraft:custom_model_data"

# 保存装备名称到storage（用于hover文字显示）
data modify storage stardew:equipment ring3.display_name set from entity @s SelectedItem.components."minecraft:custom_name"

# 标记槽位已占用
scoreboard players set @s sd_equip_ring3 1

# 消耗物品
item modify entity @s weapon.mainhand stardew:consume_one

# 应用戒指效果（从effects读取）
# TODO: 实际效果应用在后续完善
# 暂时忽略效果数值，只记录装备

# 提示消息
tellraw @s [{"text":"已装备: ","color":"green"},{"nbt":"ring3.display_name","storage":"stardew:equipment","interpret":true},{"text":" (槽位3)","color":"gray"}]
playsound minecraft:item.armor.equip_generic master @s ~ ~ ~ 1 1.2

# 如果在装备菜单中，刷新显示
execute if score @s sd_menu_level matches 2 run function stardew:menu/pages/equipment_menu
