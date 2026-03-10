# data/stardew/functions/tree/cooldown_feedback.mcfunction
# [执行者: 玩家]

# 1. 播放“弹刀”或“未命中”的沉闷音效
playsound minecraft:block.note_block.bass player @s ~ ~ ~ 0.5 0.5

# 2. [修正] 提示信息
# 移至聊天栏，防止被 UI 覆盖。颜色改为浅蓝 (Aqua) 以匹配雪花。
tellraw @s {"text":"❄ 冷却中...","color":"aqua"}