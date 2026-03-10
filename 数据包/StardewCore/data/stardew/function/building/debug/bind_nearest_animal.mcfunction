# ================================================================
# 星露谷物语 - 给附近动物绑定建筑ID (Debug)
# ================================================================
# 用途：自动把最近的动物绑定到最近的建筑
# 调用：/execute at @e[type=marker,tag=stardew.building] run function stardew:building/debug/bind_nearest_animal

# 找到最近的动物
execute as @e[type=#stardew:animals,tag=stardew.animal,distance=..10,limit=1,sort=nearest] run function stardew:building/debug/bind_to_building
