# 触发打开Pierre商店
# 由Pierre商店门口的interaction实体调用

# 找到附近3格内的玩家并为他们打开商店
execute as @a[distance=..3] run function stardew:shop/open_pierre

# 播放音效给附近玩家
execute as @a[distance=..3] at @s run playsound minecraft:block.wooden_door.open master @s ~ ~ ~ 1 1

# 重置interaction数据,允许下次交互
data remove entity @s interaction
data remove entity @s attack
