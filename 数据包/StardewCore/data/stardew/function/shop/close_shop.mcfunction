# 关闭商店 - 清除UI(不传送玩家)

# 播放关闭音效
execute as @a[scores={sd_in_shop=1..}] at @s run playsound minecraft:block.chest.close master @s ~ ~ ~ 1 1

# 重置玩家商店状态
scoreboard players set @a[scores={sd_in_shop=1..}] sd_in_shop 0
scoreboard players set @a[scores={sd_in_shop=1..}] sd_shop_season 0
scoreboard players set @a[scores={sd_in_shop=1..}] sd_shop_page 0

# 播放关闭动画 (动画完成后自动kill实体)
function stardew:shop/animate_close