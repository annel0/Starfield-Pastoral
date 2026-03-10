# ================================================================
# 星露谷物语 - 尝试抚摸动物
# ================================================================
# 用途：玩家右键+潜行+空手时，射线检测前方的动物并抚摸
# 调用：从 detect_interaction 调用
# @s = 玩家
# ================================================================

# 清除所有动物的处理标记
tag @e[type=#stardew:animals,tag=stardew.animal.just_petted] remove stardew.animal.just_petted

# 射线检测：寻找前方3格内的星露谷动物
# 使用标签确保每次只抚摸一只动物
execute anchored eyes positioned ^ ^ ^0.5 as @e[type=#stardew:animals,tag=stardew.animal,tag=!stardew.animal.just_petted,distance=..1,limit=1,sort=nearest] run function stardew:animal/interact/pet_animal_as_animal
execute anchored eyes positioned ^ ^ ^1.0 unless entity @e[type=#stardew:animals,tag=stardew.animal.just_petted,distance=..5] as @e[type=#stardew:animals,tag=stardew.animal,tag=!stardew.animal.just_petted,distance=..1,limit=1,sort=nearest] run function stardew:animal/interact/pet_animal_as_animal
execute anchored eyes positioned ^ ^ ^1.5 unless entity @e[type=#stardew:animals,tag=stardew.animal.just_petted,distance=..5] as @e[type=#stardew:animals,tag=stardew.animal,tag=!stardew.animal.just_petted,distance=..1,limit=1,sort=nearest] run function stardew:animal/interact/pet_animal_as_animal
execute anchored eyes positioned ^ ^ ^2.0 unless entity @e[type=#stardew:animals,tag=stardew.animal.just_petted,distance=..5] as @e[type=#stardew:animals,tag=stardew.animal,tag=!stardew.animal.just_petted,distance=..1,limit=1,sort=nearest] run function stardew:animal/interact/pet_animal_as_animal
execute anchored eyes positioned ^ ^ ^2.5 unless entity @e[type=#stardew:animals,tag=stardew.animal.just_petted,distance=..5] as @e[type=#stardew:animals,tag=stardew.animal,tag=!stardew.animal.just_petted,distance=..1,limit=1,sort=nearest] run function stardew:animal/interact/pet_animal_as_animal
execute anchored eyes positioned ^ ^ ^3.0 unless entity @e[type=#stardew:animals,tag=stardew.animal.just_petted,distance=..5] as @e[type=#stardew:animals,tag=stardew.animal,tag=!stardew.animal.just_petted,distance=..1,limit=1,sort=nearest] run function stardew:animal/interact/pet_animal_as_animal
