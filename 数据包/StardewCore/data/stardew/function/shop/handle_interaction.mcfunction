# 处理商店交互 - 检测interaction实体的交互并执行对应逻辑
# 使用interaction的attack或interaction NBT数据检测点击

# === 检测关闭按钮点击 ===
execute as @e[type=interaction,tag=button_close,nbt={interaction:{}}] at @s run function stardew:shop/close_shop
execute as @e[type=interaction,tag=button_close,nbt={attack:{}}] at @s run function stardew:shop/close_shop

# === 检测上一页按钮点击 ===
execute as @e[type=interaction,tag=button_page_up,nbt={interaction:{}}] at @s run function stardew:shop/page_up
execute as @e[type=interaction,tag=button_page_up,nbt={attack:{}}] at @s run function stardew:shop/page_up

# === 检测下一页按钮点击 ===
execute as @e[type=interaction,tag=button_page_down,nbt={interaction:{}}] at @s run function stardew:shop/page_down
execute as @e[type=interaction,tag=button_page_down,nbt={attack:{}}] at @s run function stardew:shop/page_down

# === 检测商品槽点击 ===
execute as @e[type=interaction,tag=slot_1,nbt={interaction:{}}] at @s run function stardew:shop/purchase_slot_1
execute as @e[type=interaction,tag=slot_1,nbt={attack:{}}] at @s run function stardew:shop/purchase_slot_1

execute as @e[type=interaction,tag=slot_2,nbt={interaction:{}}] at @s run function stardew:shop/purchase_slot_2
execute as @e[type=interaction,tag=slot_2,nbt={attack:{}}] at @s run function stardew:shop/purchase_slot_2

execute as @e[type=interaction,tag=slot_3,nbt={interaction:{}}] at @s run function stardew:shop/purchase_slot_3
execute as @e[type=interaction,tag=slot_3,nbt={attack:{}}] at @s run function stardew:shop/purchase_slot_3

# === 重置所有interaction的NBT数据 ===
execute as @e[type=interaction,tag=shop_interaction] run data remove entity @s interaction
execute as @e[type=interaction,tag=shop_interaction] run data remove entity @s attack
