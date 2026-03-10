# data/stardew/function/ui/actionbar_bars.mcfunction
# 根据当前值生成各个进度条的字符串，存储到 storage

# 生命值条（暂时固定，后面再做动态）
data modify storage stardew:ui Bar.HP set value '{"text":"▮▮▮▮▮▮▮▮▮▮","color":"red"}'

# 能量条（暂时固定，后面再做动态）
data modify storage stardew:ui Bar.Energy set value '[{"text":"▮▮▮▮▮▮▮▮","color":"gold"},{"text":"▯▯","color":"dark_gray"}]'

# 特殊进度条（根据 SpecialBars 生成）
execute if score #special_bars sd_temp matches 0 run data modify storage stardew:ui Bar.Special set value '{"text":"▯▯▯▯▯▯▯▯▯▯","color":"dark_gray"}'
execute if score #special_bars sd_temp matches 1 run data modify storage stardew:ui Bar.Special set value '[{"text":"▮","color":"special_color"},{"text":"▯▯▯▯▯▯▯▯▯","color":"dark_gray"}]'
execute if score #special_bars sd_temp matches 2 run data modify storage stardew:ui Bar.Special set value '[{"text":"▮▮","color":"special_color"},{"text":"▯▯▯▯▯▯▯▯","color":"dark_gray"}]'
execute if score #special_bars sd_temp matches 3 run data modify storage stardew:ui Bar.Special set value '[{"text":"▮▮▮","color":"special_color"},{"text":"▯▯▯▯▯▯▯","color":"dark_gray"}]'
execute if score #special_bars sd_temp matches 4 run data modify storage stardew:ui Bar.Special set value '[{"text":"▮▮▮▮","color":"special_color"},{"text":"▯▯▯▯▯▯","color":"dark_gray"}]'
execute if score #special_bars sd_temp matches 5 run data modify storage stardew:ui Bar.Special set value '[{"text":"▮▮▮▮▮","color":"special_color"},{"text":"▯▯▯▯▯","color":"dark_gray"}]'
execute if score #special_bars sd_temp matches 6 run data modify storage stardew:ui Bar.Special set value '[{"text":"▮▮▮▮▮▮","color":"special_color"},{"text":"▯▯▯▯","color":"dark_gray"}]'
execute if score #special_bars sd_temp matches 7 run data modify storage stardew:ui Bar.Special set value '[{"text":"▮▮▮▮▮▮▮","color":"special_color"},{"text":"▯▯▯","color":"dark_gray"}]'
execute if score #special_bars sd_temp matches 8 run data modify storage stardew:ui Bar.Special set value '[{"text":"▮▮▮▮▮▮▮▮","color":"special_color"},{"text":"▯▯","color":"dark_gray"}]'
execute if score #special_bars sd_temp matches 9 run data modify storage stardew:ui Bar.Special set value '[{"text":"▮▮▮▮▮▮▮▮▮","color":"special_color"},{"text":"▯","color":"dark_gray"}]'
execute if score #special_bars sd_temp matches 10.. run data modify storage stardew:ui Bar.Special set value '{"text":"▮▮▮▮▮▮▮▮▮▮","color":"special_color"}'

# 设置特殊进度条的颜色
execute if score @s sd_special_type matches 0 run data modify storage stardew:ui Bar.SpecialColor set value "dark_gray"
execute if score @s sd_special_type matches 1 run data modify storage stardew:ui Bar.SpecialColor set value "yellow"
execute if score @s sd_special_type matches 2 run data modify storage stardew:ui Bar.SpecialColor set value "aqua"

# 特殊进度条标签
execute if score @s sd_special_type matches 0 run data modify storage stardew:ui Bar.SpecialLabel set value "    "
execute if score @s sd_special_type matches 1 run data modify storage stardew:ui Bar.SpecialLabel set value "蓄力 "
execute if score @s sd_special_type matches 2 run data modify storage stardew:ui Bar.SpecialLabel set value "钓鱼 "

# 特殊进度条后缀
execute if score @s sd_special_type matches 0 run data modify storage stardew:ui Bar.SpecialSuffix set value "    "
execute if score @s sd_special_type matches 1 if score @s sd_charge_ready matches 0 run data modify storage stardew:ui Bar.SpecialSuffix set value '""'
execute if score @s sd_special_type matches 1 if score @s sd_charge_ready matches 1 run data modify storage stardew:ui Bar.SpecialSuffix set value '"  ✔"'
execute if score @s sd_special_type matches 2 run data modify storage stardew:ui Bar.SpecialSuffix set value '"s"'
