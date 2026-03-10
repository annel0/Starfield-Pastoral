# stardew:monsters/spawn/types/init_skeleton
# 延迟1tick后初始化骷髅,添加Tags和属性
# 这样不会破坏骷髅的射箭AI

# 添加基础Tags
tag @e[type=skeleton,tag=!sd_monster_init,limit=1,sort=nearest] add sd_monster_init
tag @e[type=skeleton,tag=sd_monster_init,tag=!sd_mob_skeleton,limit=1,sort=nearest] add sd_mob_skeleton
tag @e[type=skeleton,tag=sd_mob_skeleton,tag=!sd_tier_2,limit=1,sort=nearest] add sd_tier_2

# 从storage读取hp和atk,使用macro动态添加tag
function stardew:monsters/spawn/types/init_skeleton_stats with storage stardew:temp

# 设置自定义属性 - 分开设置避免破坏AI
data modify entity @e[type=skeleton,tag=sd_mob_skeleton,tag=!sd_monster,limit=1,sort=nearest] DeathLootTable set value "stardew:monsters/skeleton"
data modify entity @e[type=skeleton,tag=sd_mob_skeleton,tag=!sd_monster,limit=1,sort=nearest] CustomNameVisible set value 1b
