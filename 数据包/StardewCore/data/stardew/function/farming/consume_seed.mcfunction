# data/stardew/function/farming/consume_seed.mcfunction
# 消耗玩家手中的种子
# 执行者：玩家 (@s, gamemode=!creative,gamemode=!spectator)
# 注意：必须在玩家位置执行 (at @s)

# 直接从主手清除1个物品
execute at @s run item modify entity @s weapon.mainhand stardew:consume_one
