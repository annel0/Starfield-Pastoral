# data/stardew/function/ui/actionbar_render.mcfunction
# 优化版：根据能量格数分发到不同的渲染函数
# sd_special_type: 0=空, 1=蓄力, 2=钓鱼, 3=矿洞

# 普通UI（sd_special_type 0/1/2）使用 energy bar 文件
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 0 run function stardew:ui/energy/bar_0 with storage stardew:ui ActionBar
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 1 run function stardew:ui/energy/bar_1 with storage stardew:ui ActionBar
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 2 run function stardew:ui/energy/bar_2 with storage stardew:ui ActionBar
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 3 run function stardew:ui/energy/bar_3 with storage stardew:ui ActionBar
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 4 run function stardew:ui/energy/bar_4 with storage stardew:ui ActionBar
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 5 run function stardew:ui/energy/bar_5 with storage stardew:ui ActionBar
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 6 run function stardew:ui/energy/bar_6 with storage stardew:ui ActionBar
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 7 run function stardew:ui/energy/bar_7 with storage stardew:ui ActionBar
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 8 run function stardew:ui/energy/bar_8 with storage stardew:ui ActionBar
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 9 run function stardew:ui/energy/bar_9 with storage stardew:ui ActionBar
execute unless score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 10.. run function stardew:ui/energy/bar_10 with storage stardew:ui ActionBar

# 矿洞UI（sd_special_type=3）使用 mine bar 文件
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 0 run function stardew:ui/mine/bar_0 with storage stardew:ui ActionBar
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 1 run function stardew:ui/mine/bar_1 with storage stardew:ui ActionBar
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 2 run function stardew:ui/mine/bar_2 with storage stardew:ui ActionBar
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 3 run function stardew:ui/mine/bar_3 with storage stardew:ui ActionBar
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 4 run function stardew:ui/mine/bar_4 with storage stardew:ui ActionBar
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 5 run function stardew:ui/mine/bar_5 with storage stardew:ui ActionBar
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 6 run function stardew:ui/mine/bar_6 with storage stardew:ui ActionBar
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 7 run function stardew:ui/mine/bar_7 with storage stardew:ui ActionBar
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 8 run function stardew:ui/mine/bar_8 with storage stardew:ui ActionBar
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 9 run function stardew:ui/mine/bar_9 with storage stardew:ui ActionBar
execute if score @s sd_special_type matches 3 if score #energy_bars sd_temp matches 10.. run function stardew:ui/mine/bar_10 with storage stardew:ui ActionBar
