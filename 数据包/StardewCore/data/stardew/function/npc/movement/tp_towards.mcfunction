# 朝向目标并传送（宏函数 - 通用多NPC版本）
# @s = 任何npc实体
# 参数: target_x, target_y, target_z（通过storage传入）

# 使用facing命令朝向目标，然后向自己的前方移动0.15格
$execute facing $(target_x) $(target_y) $(target_z) run tp @s ^ ^ ^0.15
