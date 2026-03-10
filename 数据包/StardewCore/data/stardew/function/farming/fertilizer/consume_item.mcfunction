# data/stardew/functions/farming/fertilizer/consume_item.mcfunction
# 消耗一个肥料物品

# 只在生存/冒险模式消耗物品
execute unless entity @s[gamemode=creative] run item modify entity @s weapon.mainhand stardew:consume_one
