# data/stardew/function/ui/actionbar.mcfunction
# 显示 actionbar 状态栏：生命值 | 能量 | 特殊进度

# 1. 获取玩家生命值
execute store result score #hp sd_temp run scoreboard players get @s sd_health
execute store result score #max_hp sd_temp run scoreboard players get @s sd_max_health

# 1.1 计算生命值条格数（0-10格）
scoreboard players operation #hp_bars sd_temp = #hp sd_temp
scoreboard players set #10 sd_temp 10
scoreboard players operation #hp_bars sd_temp *= #10 sd_temp
execute if score #max_hp sd_temp matches 1.. run scoreboard players operation #hp_bars sd_temp /= #max_hp sd_temp
execute if score #hp_bars sd_temp matches 11.. run scoreboard players set #hp_bars sd_temp 10
execute if score #hp_bars sd_temp matches ..-1 run scoreboard players set #hp_bars sd_temp 0

# 1.2 计算生命值百分比（0-100）用于颜色判断
scoreboard players operation #hp_percent sd_temp = #hp sd_temp
scoreboard players set #100 sd_temp 100
scoreboard players operation #hp_percent sd_temp *= #100 sd_temp
execute if score #max_hp sd_temp matches 1.. run scoreboard players operation #hp_percent sd_temp /= #max_hp sd_temp

# 1.3 根据百分比设置生命值条颜色类型
# 0 = 红色 (>50%), 1 = 金色 (10-50%), 2 = 深红色 (<10%)
scoreboard players set #hp_color sd_temp 0
execute if score #hp_percent sd_temp matches ..50 run scoreboard players set #hp_color sd_temp 1
execute if score #hp_percent sd_temp matches ..9 run scoreboard players set #hp_color sd_temp 2

# 2. 获取玩家能量值和最大能量
execute store result score #energy sd_temp run scoreboard players get @s sd_energy
execute store result score #max_energy sd_temp run scoreboard players get @s sd_max_energy

# 2.1 计算能量条格数（0-10格）
scoreboard players operation #energy_bars sd_temp = #energy sd_temp
scoreboard players set #10 sd_temp 10
scoreboard players operation #energy_bars sd_temp *= #10 sd_temp
execute if score #max_energy sd_temp matches 1.. run scoreboard players operation #energy_bars sd_temp /= #max_energy sd_temp
execute if score #energy_bars sd_temp matches 11.. run scoreboard players set #energy_bars sd_temp 10
execute if score #energy_bars sd_temp matches ..-1 run scoreboard players set #energy_bars sd_temp 0

# 2.2 计算能量百分比（0-100）用于颜色判断
scoreboard players operation #energy_percent sd_temp = #energy sd_temp
scoreboard players set #100 sd_temp 100
scoreboard players operation #energy_percent sd_temp *= #100 sd_temp
execute if score #max_energy sd_temp matches 1.. run scoreboard players operation #energy_percent sd_temp /= #max_energy sd_temp

# 2.3 根据百分比设置能量条颜色类型
# 0 = 绿色 (>50%), 1 = 黄色 (10-50%), 2 = 红色 (<10%)
scoreboard players set #energy_color sd_temp 0
execute if score #energy_percent sd_temp matches ..50 run scoreboard players set #energy_color sd_temp 1
execute if score #energy_percent sd_temp matches ..9 run scoreboard players set #energy_color sd_temp 2

# 3. 读取特殊进度条（由其他系统设置，如蓄力、钓鱼）
# 如果没有特殊进度，重置为 0
execute unless score @s sd_special_type matches 1.. run scoreboard players set @s sd_special_value 0
execute unless score @s sd_special_type matches 1.. run scoreboard players set @s sd_special_max 0
execute unless score @s sd_special_type matches 1.. run scoreboard players set @s sd_special_type 0

# 3.5 矿洞层数显示（当在矿洞且没有其他特殊进度时）
# sd_special_type = 3 表示矿洞层数显示
execute if entity @s[nbt={Dimension:"stardew:mine"}] if score @s sd_special_type matches 0 run scoreboard players set @s sd_special_type 3
execute if score @s sd_special_type matches 3 run scoreboard players operation @s sd_special_value = @s sd_mine_floor
execute if score @s sd_special_type matches 3 run scoreboard players set @s sd_special_max 100

# 4. 存储到 storage 用于宏函数
execute store result storage stardew:ui ActionBar.HP int 1 run scoreboard players get #hp sd_temp
execute store result storage stardew:ui ActionBar.MaxHP int 1 run scoreboard players get #max_hp sd_temp
execute store result storage stardew:ui ActionBar.HPBars int 1 run scoreboard players get #hp_bars sd_temp
execute store result storage stardew:ui ActionBar.HPColor int 1 run scoreboard players get #hp_color sd_temp
execute store result storage stardew:ui ActionBar.Energy int 1 run scoreboard players get #energy sd_temp
execute store result storage stardew:ui ActionBar.MaxEnergy int 1 run scoreboard players get #max_energy sd_temp
execute store result storage stardew:ui ActionBar.EnergyBars int 1 run scoreboard players get #energy_bars sd_temp
execute store result storage stardew:ui ActionBar.EnergyColor int 1 run scoreboard players get #energy_color sd_temp
execute store result storage stardew:ui ActionBar.SpecialValue int 1 run scoreboard players get @s sd_special_value
execute store result storage stardew:ui ActionBar.SpecialMax int 1 run scoreboard players get @s sd_special_max
execute store result storage stardew:ui ActionBar.SpecialType int 1 run scoreboard players get @s sd_special_type
execute store result storage stardew:ui ActionBar.Gold int 1 run scoreboard players get @s sd_gold

# 计算剩余时间（SpecialRemain = SpecialMax - SpecialValue，不能为负）
# 注意：钓鱼的 SpecialValue 本身就是剩余 tick，不需要再减
execute if score @s sd_special_type matches 1 run scoreboard players operation #special_remain sd_temp = @s sd_special_max
execute if score @s sd_special_type matches 1 run scoreboard players operation #special_remain sd_temp -= @s sd_special_value
execute if score @s sd_special_type matches 2 run scoreboard players operation #special_remain sd_temp = @s sd_special_value

execute if score #special_remain sd_temp matches ..-1 run scoreboard players set #special_remain sd_temp 0

# 如果是钓鱼（type=2），转换为秒数（tick / 20）
execute if score @s sd_special_type matches 2 run scoreboard players operation #special_remain sd_temp /= #20 sd_const

execute store result storage stardew:ui ActionBar.SpecialRemain int 1 run scoreboard players get #special_remain sd_temp

# 计算进度条格数（0-10格，SpecialValue * 10 / SpecialMax）
# 注意：钓鱼是递减的，所以钓鱼的 SpecialValue 就是剩余时间，格数也是从满到空
scoreboard players operation #special_bars sd_temp = @s sd_special_value
scoreboard players set #10 sd_temp 10
scoreboard players operation #special_bars sd_temp *= #10 sd_temp
execute if score @s sd_special_max matches 1.. run scoreboard players operation #special_bars sd_temp /= @s sd_special_max
execute if score #special_bars sd_temp matches 11.. run scoreboard players set #special_bars sd_temp 10
execute if score #special_bars sd_temp matches ..-1 run scoreboard players set #special_bars sd_temp 0

execute store result storage stardew:ui ActionBar.SpecialBars int 1 run scoreboard players get #special_bars sd_temp

# 矿洞层数存储
execute store result storage stardew:ui ActionBar.MineFloor int 1 run scoreboard players get @s sd_mine_floor

# 渲染（统一使用 actionbar_render，内部根据 sd_special_type 分发）
function stardew:ui/actionbar_render with storage stardew:ui ActionBar

# 5. 用完后重置特殊进度类型（每 tick 都需要重新设置）
scoreboard players set @s sd_special_type 0
