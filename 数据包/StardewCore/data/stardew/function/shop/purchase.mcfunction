# 购买物品 - 验证金币并执行交易
# 前提: storage stardew:temp purchase_item 包含要购买的物品数据
# 前提: storage stardew:temp purchase_count 包含购买数量 (1或5)

# 读取物品价格和购买数量
execute store result score #purchase_price sd_temp run data get storage stardew:temp purchase_item.price
execute store result score #purchase_count sd_temp run data get storage stardew:temp purchase_count

# 计算总价格 = 单价 * 数量
scoreboard players operation #purchase_price sd_temp *= #purchase_count sd_temp

# 检查玩家金币是否足够
scoreboard players operation #player_gold sd_temp = @s sd_gold
scoreboard players operation #player_gold sd_temp -= #purchase_price sd_temp

# 如果金币不足,提示并返回
execute if score #player_gold sd_temp matches ..-1 run tellraw @s {"text":"金币不足!","color":"red"}
execute if score #player_gold sd_temp matches ..-1 run playsound minecraft:entity.villager.no master @s ~ ~ ~ 1 1
execute if score #player_gold sd_temp matches ..-1 run return fail

# 扣除金币
scoreboard players operation @s sd_gold -= #purchase_price sd_temp

# 给予物品 (循环购买数量次)
execute if score #purchase_count sd_temp matches 1.. run function stardew:shop/give_item_loop

# 播放物品获得音效
playsound minecraft:entity.item.pickup master @s ~ ~ ~ 0.8 1.0
playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 0.8 1.0