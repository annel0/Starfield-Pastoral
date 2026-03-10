# stardew:monsters/spawn/types/init_skeleton_stats
# 使用macro动态添加hp和atk tags

$tag @e[type=skeleton,tag=sd_mob_skeleton,tag=!sd_monster,limit=1,sort=nearest] add sd_hp_$(skeleton_hp)
$tag @e[type=skeleton,tag=sd_mob_skeleton,tag=!sd_monster,limit=1,sort=nearest] add sd_atk_$(skeleton_atk)
