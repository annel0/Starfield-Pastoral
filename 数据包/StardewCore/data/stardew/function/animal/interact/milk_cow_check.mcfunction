# ================================================================
# 星露谷物语 - 挤奶桶交互处理
# ================================================================
# 用途：检测玩家用挤奶桶右键牛,收集牛奶
# 调用：从 tools/main_handler.mcfunction 调用

# @s = 玩家,手持挤奶桶(CMD 8101)

# 在玩家视线方向检测牛
execute anchored eyes positioned ^ ^ ^0.5 as @e[type=cow,distance=..2,limit=1,sort=nearest,tag=stardew.animal] if score @s stardew.animal.type matches 201 run function stardew:animal/interact/try_milk_cow
execute anchored eyes positioned ^ ^ ^1.0 unless entity @e[type=cow,tag=stardew.just_milked,distance=..5] as @e[type=cow,distance=..2,limit=1,sort=nearest,tag=stardew.animal] if score @s stardew.animal.type matches 201 run function stardew:animal/interact/try_milk_cow
execute anchored eyes positioned ^ ^ ^1.5 unless entity @e[type=cow,tag=stardew.just_milked,distance=..5] as @e[type=cow,distance=..2,limit=1,sort=nearest,tag=stardew.animal] if score @s stardew.animal.type matches 201 run function stardew:animal/interact/try_milk_cow
execute anchored eyes positioned ^ ^ ^2.0 unless entity @e[type=cow,tag=stardew.just_milked,distance=..5] as @e[type=cow,distance=..2,limit=1,sort=nearest,tag=stardew.animal] if score @s stardew.animal.type matches 201 run function stardew:animal/interact/try_milk_cow
