# 将副手物品移回主手
# 使用item replace from entity复制副手到主手
item replace entity @s weapon.mainhand from entity @s weapon.offhand

# 清空副手
item replace entity @s weapon.offhand with air
