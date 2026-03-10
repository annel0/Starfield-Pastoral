# data/stardew/functions/fishing/fight_loop_player_view.mcfunction
# 执行者：玩家，当前位置 = 鱼钩所在位置

# 双保险：如果玩家其实不在战斗，退出
execute unless entity @s[tag=is_fighting_fish] run return 0

# 1. 累加“鱼挣扎节奏”计数器
scoreboard players add @s sd_fish_shake 1

# 2. 根据难度决定“每隔多少 tick 才拉一次”
#    默认阈值 10 tick（难度低 → 拉得慢）
scoreboard players set @s sd_const 10
execute if score @s sd_final_difficulty matches 4..6 run scoreboard players set @s sd_const 7
execute if score @s sd_final_difficulty matches 7..9 run scoreboard players set @s sd_const 5
execute if score @s sd_final_difficulty matches 10.. run scoreboard players set @s sd_const 3

# 如果还没到阈值 → 先不拉
execute unless score @s sd_fish_shake >= @s sd_const run return 0

# 到阈值：重置计数器，执行一次“往外挣扎”
scoreboard players set @s sd_fish_shake 0

# 3. 真正移动鱼钩：沿玩家视线反方向，稍微拉远一点
execute as @e[type=fishing_bobber,distance=..0.5,limit=1] rotated as @s run tp @s ^ ^ ^-0.06

# 4. 播放一点挣扎声音（可以根据鱼种类以后再细化）
playsound minecraft:entity.salmon.flop player @s ~ ~ ~ 0.4 1.0
