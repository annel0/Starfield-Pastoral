# data/stardew/functions/fishing/fight_loop.mcfunction
# 执行者：鱼钩 (fishing_bobber)

# 附近没有正在战斗的玩家 → 不动
execute unless entity @e[type=player,tag=is_fighting_fish,distance=..32,limit=1] run return 0

# 找最近的战斗玩家，让“他”在当前鱼钩位置执行子逻辑
execute as @e[type=player,tag=is_fighting_fish,distance=..32,sort=nearest,limit=1] at @s run function stardew:fishing/fight_loop_player_view
