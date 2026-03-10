# 光线投射检测，用于高亮对话框中可点击的元素
# 检测最多3格距离

# 检测是否击中interaction实体
execute positioned ~-0.1 ~-0.1 ~-0.1 if entity @e[type=interaction,tag=dialogue_menu,dx=0,sort=nearest,limit=1] positioned ~0.1 ~0.1 ~0.1 run return run function stardew:dialogue/ray_hit

# 继续投射
execute if score @s stardew_ray_distance matches ..30 positioned ^ ^ ^0.1 run function stardew:dialogue/ray
