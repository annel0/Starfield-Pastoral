# 设置系统常数
scoreboard players set #250 stardew_const 250
scoreboard players set #20 stardew_const 20
scoreboard players set #80 stardew_const 80
scoreboard players set #45 stardew_const 45

# 动画状态常数
# 1 = idle, 2 = walk

tellraw @a[tag=debug] {"text":"[StardewCore] 系统常数已设置","color":"gray"}