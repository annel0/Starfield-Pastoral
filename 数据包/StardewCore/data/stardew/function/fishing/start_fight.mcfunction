# data/stardew/functions/fishing/start_fight.mcfunction
# [执行者: 玩家]
# 触发条件：咬钩期间按下 Shift

# 1. 设置战斗状态
tag @s add is_fighting_fish

# 2. 初始化
scoreboard players set @s sd_fish_progress 500
scoreboard players set @s sd_fish_phase 1
scoreboard players set @s sd_fishing_tick 1200
scoreboard players set @s sd_fish_shake 0

# 3. 根据鱼种决定力量 (Difficulty) - 扩展到 1-15 档位
# 默认随机 (防止未定义鱼种)
execute store result score @s sd_fish_power_fish run random value 1..5

# --- 🗑️ 垃圾与资源 (ID 40xxx) ---
# 难度: 1 (极易)
execute if score @s sd_fish_type matches 40000..40999 run scoreboard players set @s sd_fish_power_fish 1

# --- 🌸 春季 (Spring) ---
# 小嘴鲈鱼 (41000): 简单
execute if score @s sd_fish_type matches 41000 run execute store result score @s sd_fish_power_fish run random value 2..3
# 西鲱 (41010): 中等
execute if score @s sd_fish_type matches 41010 run execute store result score @s sd_fish_power_fish run random value 5..6
# 鲶鱼 (41020): 极难 (雨天BOSS)
execute if score @s sd_fish_type matches 41020 run execute store result score @s sd_fish_power_fish run random value 11..13
# 太阳鱼 (41030): 简单
execute if score @s sd_fish_type matches 41030 run execute store result score @s sd_fish_power_fish run random value 2..3
# 鳀鱼 (41040): 极易
execute if score @s sd_fish_type matches 41040 run scoreboard players set @s sd_fish_power_fish 1
# 沙丁鱼 (41050): 简单
execute if score @s sd_fish_type matches 41050 run execute store result score @s sd_fish_power_fish run random value 1..3
# 大头鱼 (41060): 简单偏中
execute if score @s sd_fish_type matches 41060 run execute store result score @s sd_fish_power_fish run random value 3..4
# 鲤鱼 (41070): 极易 (发呆鱼)
execute if score @s sd_fish_type matches 41070 run scoreboard players set @s sd_fish_power_fish 1
# 大比目鱼 (41080): 中等
execute if score @s sd_fish_type matches 41080 run execute store result score @s sd_fish_power_fish run random value 4..6
# 鳗鱼 (41090): 困难 (雨夜)
execute if score @s sd_fish_type matches 41090 run execute store result score @s sd_fish_power_fish run random value 8..10
# [传说] 绯红鱼 (41990): 传说级
execute if score @s sd_fish_type matches 41990 run execute store result score @s sd_fish_power_fish run random value 13..15

# --- 🔥 夏季 (Summer) ---
# 虹鳟鱼 (42000): 简单偏中
execute if score @s sd_fish_type matches 42000 run execute store result score @s sd_fish_power_fish run random value 4..5
# 罗非鱼 (42010): 简单偏中
execute if score @s sd_fish_type matches 42010 run execute store result score @s sd_fish_power_fish run random value 4..5
# 红鲻鱼 (42020): 简单
execute if score @s sd_fish_type matches 42020 run execute store result score @s sd_fish_power_fish run random value 3..4
# 狗鱼 (42030): 中等
execute if score @s sd_fish_type matches 42030 run execute store result score @s sd_fish_power_fish run random value 5..7
# 金枪鱼 (42040): 困难
execute if score @s sd_fish_type matches 42040 run execute store result score @s sd_fish_power_fish run random value 7..9
# 鲟鱼 (42050): 困难偏难
execute if score @s sd_fish_type matches 42050 run execute store result score @s sd_fish_power_fish run random value 8..10
# 河豚 (42060): 困难 (行为诡异)
execute if score @s sd_fish_type matches 42060 run execute store result score @s sd_fish_power_fish run random value 9..11
# 章鱼 (42070): 极难 (触手怪)
execute if score @s sd_fish_type matches 42070 run execute store result score @s sd_fish_power_fish run random value 10..12
# 超级海参 (42080): 极难
execute if score @s sd_fish_type matches 42080 run execute store result score @s sd_fish_power_fish run random value 11..13
# 多拉多鱼 (42090): 中等
execute if score @s sd_fish_type matches 42090 run execute store result score @s sd_fish_power_fish run random value 5..7
# [传说] 琵琶鱼 (42990): 传说级
execute if score @s sd_fish_type matches 42990 run execute store result score @s sd_fish_power_fish run random value 13..15

# --- 🍂 秋季 (Fall) ---
# 三文鱼 (43000): 中等
execute if score @s sd_fish_type matches 43000 run execute store result score @s sd_fish_power_fish run random value 5..7
# 虎纹鳟鱼 (43010): 中等偏难
execute if score @s sd_fish_type matches 43010 run execute store result score @s sd_fish_power_fish run random value 6..8
# 大嘴鲈鱼 (43020): 简单偏中
execute if score @s sd_fish_type matches 43020 run execute store result score @s sd_fish_power_fish run random value 4..6
# 红鲷鱼 (43030): 简单偏中
execute if score @s sd_fish_type matches 43030 run execute store result score @s sd_fish_power_fish run random value 4..5
# 海参 (43040): 简单
execute if score @s sd_fish_type matches 43040 run execute store result score @s sd_fish_power_fish run random value 2..4
# 大眼鱼 (43050): 中等
execute if score @s sd_fish_type matches 43050 run execute store result score @s sd_fish_power_fish run random value 4..6
# 午夜鲤鱼 (43060): 中等
execute if score @s sd_fish_type matches 43060 run execute store result score @s sd_fish_power_fish run random value 3..5
# 海鳗 (43070): 困难
execute if score @s sd_fish_type matches 43070 run execute store result score @s sd_fish_power_fish run random value 6..8
# [传说] 鮟鱇鱼 (43990): 传说级
execute if score @s sd_fish_type matches 43990 run execute store result score @s sd_fish_power_fish run random value 12..15

# --- ❄️ 冬季 (Winter) ---
# 河鲈 (44000): 简单
execute if score @s sd_fish_type matches 44000 run execute store result score @s sd_fish_power_fish run random value 3..5
# 鱿鱼 (44010): 困难
execute if score @s sd_fish_type matches 44010 run execute store result score @s sd_fish_power_fish run random value 6..8
# 青花鱼 (44020): 中等
execute if score @s sd_fish_type matches 44020 run execute store result score @s sd_fish_power_fish run random value 4..6
# 蛇齿单线鱼 (44030): 极难 (新人劝退)
execute if score @s sd_fish_type matches 44030 run execute store result score @s sd_fish_power_fish run random value 8..10
# [传说] 冰川鱼 (44990): 传说级 (最强)
execute if score @s sd_fish_type matches 44990 run execute store result score @s sd_fish_power_fish run random value 13..16

# --- 🔮 特殊 (Special) ---
# 幽灵鱼 (45000): 中等
execute if score @s sd_fish_type matches 45000 run execute store result score @s sd_fish_power_fish run random value 4..6
# 石鱼 (45010): 困难 (沉重)
execute if score @s sd_fish_type matches 45010 run execute store result score @s sd_fish_power_fish run random value 5..7
# 冰柱鱼 (45020): 困难
execute if score @s sd_fish_type matches 45020 run execute store result score @s sd_fish_power_fish run random value 7..9
# 岩浆鳗鱼 (45030): 极难 (非传说最强)
execute if score @s sd_fish_type matches 45030 run execute store result score @s sd_fish_power_fish run random value 9..11
# 沙鱼 (45040): 中等
execute if score @s sd_fish_type matches 45040 run execute store result score @s sd_fish_power_fish run random value 4..6
# 蝎鲤 (45050): 困难
execute if score @s sd_fish_type matches 45050 run execute store result score @s sd_fish_power_fish run random value 7..9

# --- 🌍 通用 (Common) ---
# 鲂鱼 (46000): 简单 (C级)
execute if score @s sd_fish_type matches 46000 run execute store result score @s sd_fish_power_fish run random value 2..3
# 鳟鱼 (46010): 简单 (C级)
execute if score @s sd_fish_type matches 46010 run execute store result score @s sd_fish_power_fish run random value 2..3
# 比目鱼 (46030): 简单偏中 (B级)
execute if score @s sd_fish_type matches 46030 run execute store result score @s sd_fish_power_fish run random value 3..5
# 木跃鱼 (46040): 简单偏中 (B级 - 特殊区域)
execute if score @s sd_fish_type matches 46040 run execute store result score @s sd_fish_power_fish run random value 4..6


# ==================================================
# 难度计算逻辑 (保持不变)
# ==================================================
# 获取玩家竿力 (默认为1)
execute store result score @s sd_fish_power_player run data get entity @s SelectedItem.components."minecraft:custom_data".fish_power
execute unless score @s sd_fish_power_player matches 1.. run scoreboard players set @s sd_fish_power_player 1

# 最终难度 = 鱼力 - 竿力
scoreboard players operation @s sd_final_difficulty = @s sd_fish_power_fish
scoreboard players operation @s sd_final_difficulty -= @s sd_fish_power_player
# 难度保底为 1
execute if score @s sd_final_difficulty matches ..0 run scoreboard players set @s sd_final_difficulty 1

# 启动 Bossbar
bossbar set stardew:fishing name {"text":"钓鱼中... 按住 Shift 拉线","color":"white"}
bossbar set stardew:fishing color white
bossbar set stardew:fishing style progress
bossbar set stardew:fishing max 1000
bossbar set stardew:fishing value 500
bossbar set stardew:fishing visible true
bossbar set stardew:fishing players @s

# 音效
playsound minecraft:entity.player.splash.high_speed player @s ~ ~ ~ 1 1.5

function stardew:fishing/utils/check_tackle

# 如果是声呐浮标，显示详细信息
execute if score @s sd_tackle_id matches 5001 run tellraw @s ["",{"text":"[声呐探测] ","color":"blue"},{"text":"鱼种ID: ","color":"gray"},{"score":{"name":"@s","objective":"sd_fish_type"},"color":"aqua"},{"text":" | 力量: ","color":"gray"},{"score":{"name":"@s","objective":"sd_fish_power_fish"},"color":"red"}]

# 如果没有声呐，只显示普通提示
execute unless score @s sd_tackle_id matches 5001 run tellraw @s {"text":"有什么东西咬钩了...","color":"yellow"}