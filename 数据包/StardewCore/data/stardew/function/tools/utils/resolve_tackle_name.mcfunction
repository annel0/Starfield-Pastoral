# data/stardew/functions/tools/utils/resolve_tackle_name.mcfunction
# 输入: score @s sd_temp (渔具ID)
# 输出: storage stardew:temp TackleName (对应的 JSON 文本组件)

# 0. 默认/空 (ID=0)
execute if score @s sd_temp matches 0 run data modify storage stardew:temp TackleName set value {"text":"空","color":"dark_gray","italic":false}

# 1. 声呐浮标 (5001)
execute if score @s sd_temp matches 5001 run data modify storage stardew:temp TackleName set value {"text":"声呐浮标","color":"blue","italic":false}

# 2. 优质浮标 (5002)
execute if score @s sd_temp matches 5002 run data modify storage stardew:temp TackleName set value {"text":"优质浮标","color":"yellow","italic":false}

# ==========================================
# 以后要加新渔具，只需要在这里加一行即可！
# ==========================================
# execute if score @s sd_temp matches 5003 run data modify storage stardew:temp TackleName set value {"text":"软木塞浮标","color":"green","italic":false}
# execute if score @s sd_temp matches 5004 run data modify storage stardew:temp TackleName set value {"text":"寻宝者","color":"gold","italic":false}