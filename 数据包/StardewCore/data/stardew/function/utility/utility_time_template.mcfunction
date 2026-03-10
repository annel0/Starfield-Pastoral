## data/stardew/function/utility/utility_time_template.mcfunction
# 通用实用设施时间模板（拷贝并替换 `sd_template` 为你的设施前缀，例如 sd_keg）
# 说明：将本文件作为模板复制到具体设施文件夹，替换所有 `sd_template` 为你的设施名（如 sd_keg），并把函数名改为合适的路径。

# 合同（小结）
# - 输入：Interaction 实体（类型 interaction）代表设施主体；全局计分板 `Global sd_time` 表示当前游戏分钟；
# - 输出：为 interaction 实体维护以下计分板：
#   - sd_template_state : 0=空闲/1=工作中/2=已完成
#   - sd_template_type  : 配方类型 id（可选）
#   - sd_template_timer : 已累计工作分钟
#   - sd_template_max_time : 需要的总分钟数
#   - sd_template_last_time : 上次同步到 Global sd_time 的时间戳
#   - sd_anim_tick      : 动画时钟（可选，用于工作时的视觉动画）
#   - sd_anim_phase     : 动画阶段（可选，用于工作时的视觉动画）
# - 边界条件：支持跨天（Global sd_time 在 new_day 会被 reset），支持任意跳过（time wand / 菜单快进）——由 delta 计算和通用奖励 `sd_utility_bonus` 处理。

# 使用约定
# - 把 `sd_utility_active` 设为 1 表示该 interaction 实体正在执行某个配方并应在 new_day 获得奖励；完成后把它设为 0。
# - new_day 会把奖励写入实体级 `sd_utility_bonus`（分钟数），各设施的 work_tick 在自身逻辑中消费并清零该奖励。
# - 如需工作动画，可在 utility/tick.mcfunction 中添加动画函数调用（参考 furnace/animate_working.mcfunction）。

# ------------------------------
# 放置 / 初始化示例（复制到 place_<utility>.mcfunction）
# ------------------------------
# ...existing code... (放置模型/召唤 interaction/visual)
# 初始化计分板
execute as @e[tag=init_utility,tag=sd_template_interaction,distance=..2] run scoreboard players set @s sd_template_state 0
execute as @e[tag=init_utility,tag=sd_template_interaction,distance=..2] run scoreboard players set @s sd_template_type 0
execute as @e[tag=init_utility,tag=sd_template_interaction,distance=..2] run scoreboard players set @s sd_template_timer 0
execute as @e[tag=init_utility,tag=sd_template_interaction,distance=..2] run scoreboard players set @s sd_template_max_time 0
execute as @e[tag=init_utility,tag=sd_template_interaction,distance=..2] run scoreboard players set @s sd_template_last_time 0
# 默认不活跃
execute as @e[tag=init_utility,tag=sd_template_interaction,distance=..2] run scoreboard players set @s sd_utility_active 0

# ------------------------------
# 启动配方示例（复制并改名为 smelt_xxx 或 start_xxx）
# 说明：在玩家交互时调用，本示例假设已把交互实体标记为 sd_current_template
# ------------------------------
scoreboard players set @e[tag=sd_current_template] sd_template_state 1
scoreboard players set @e[tag=sd_current_template] sd_template_type 1
# 初始化已工作时间为 0，设置需要的总时间（示例：120 分钟）
scoreboard players set @e[tag=sd_current_template] sd_template_timer 0
scoreboard players set @e[tag=sd_current_template] sd_template_max_time 120
# 记录上次更新时间为当前游戏时间（用于支持时间跳过）
execute as @e[tag=sd_current_template] run scoreboard players operation @s sd_template_last_time = Global sd_time
# 标记为活跃以便 new_day 时获得奖励
execute as @e[tag=sd_current_template] run scoreboard players set @s sd_utility_active 1

# 可选：更新视觉、召唤产物展示、文本显示，并把视觉实体的 sd_template_id 设为 UUID[0]

# ------------------------------
# 工作 Tick 模板（复制到 utility 的 work_tick）
# 说明：在每 tick 或每个 utility tick 调用时执行，执行者为 interaction 实体 (@s)
# ------------------------------
# 0. 计算从上次更新到当前的时间差（分钟），支持跨天
scoreboard players operation #delta sd_temp = Global sd_time
execute if score Global sd_time >= @s sd_template_last_time run scoreboard players operation #delta sd_temp -= @s sd_template_last_time
execute if score Global sd_time < @s sd_template_last_time run scoreboard players set #daylen sd_temp 1200
execute if score Global sd_time < @s sd_template_last_time run scoreboard players operation #delta sd_temp += #daylen sd_temp
execute if score Global sd_time < @s sd_template_last_time run scoreboard players operation #delta sd_temp -= @s sd_template_last_time

# 1. 把 delta 累加到已工作时间
execute if score #delta sd_temp matches 1.. run scoreboard players operation @s sd_template_timer += #delta sd_temp

# 1.5 如果存在 new_day 给出的通用奖励（sd_utility_bonus），把奖励也加入已工作时间并清零
execute if score @s sd_utility_bonus matches 1.. run scoreboard players operation @s sd_template_timer += @s sd_utility_bonus
execute if score @s sd_utility_bonus matches 1.. run scoreboard players set @s sd_utility_bonus 0

# 2. 更新上次更新时间为当前 Global sd_time
execute run scoreboard players operation @s sd_template_last_time = Global sd_time

# 3. 检查是否完成（已工作时间 >= 需要的总时间）
# 请把下面的函数改为你的 complete 函数，例如 stardew:utility/keg/complete_keg
execute if score @s sd_template_timer >= @s sd_template_max_time run function stardew:utility/template/complete_template
execute if score @s sd_template_timer >= @s sd_template_max_time run return 1

# 4. 更新显示（可选）——把 sd_template_timer 与 sd_template_max_time 的差值写入某个文本实体
# 示例：把剩余时间存到 storage 并通过 text_display 显示（需要按你的视觉结构调整 selector）
scoreboard players operation @s sd_temp = @s sd_template_max_time
scoreboard players operation @s sd_temp -= @s sd_template_timer
execute store result storage stardew:temp time int 1 run scoreboard players get @s sd_temp

# ------------------------------
# 完成示例（复制到 complete_template.mcfunction）
# ------------------------------
# 1. 更新显示为已完成
execute as @e[type=text_display,tag=sd_template_time] if score @s sd_template_id = #current_id sd_template_id run data merge entity @s {text:'{"text":"已完成！","color":"green","bold":true}'}

# 2. 播放完成音效
execute at @s run playsound minecraft:block.anvil.use block @a ~ ~ ~ 0.5 1.5

# 3. 改变状态并清理活跃标记
scoreboard players set @s sd_template_state 2
scoreboard players set @s sd_utility_active 0

# ------------------------------
# 注：把文件中所有的 sd_template 替换为你的设施前缀（例如 sd_keg），并把函数路径改为你的功能路径。
# 如果你的设施使用不同类型的实体（非 interaction），请相应调整选择器。
# 模板中使用的临时/常用计分板：sd_temp（在 init 已定义），#delta / #daylen 为临时常量玩家项。
# 使用该模板可让 new_day 的统一奖励（sd_utility_bonus）对所有遵循该模式的 utility 自动生效。
