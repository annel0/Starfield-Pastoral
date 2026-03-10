# 对话系统初始化
# 在主init文件中调用

# 创建对话系统需要的计分板
scoreboard objectives add stardew_dialogue_open dummy
scoreboard objectives add stardew_dialogue_close dummy
scoreboard objectives add stardew_dialogue_select dummy
scoreboard objectives add stardew_ray_distance dummy

# 提示信息
tellraw @a[tag=debug] {"text":"[StardewCore] 对话系统已初始化","color":"green"}
