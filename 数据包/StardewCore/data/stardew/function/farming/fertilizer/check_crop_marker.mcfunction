# data/stardew/functions/farming/fertilizer/check_crop_marker.mcfunction
# 检查是否有肥料标记(允许对空耕地和有作物的耕地施肥)

# 检查是否已经施过肥 (marker在当前位置上方0.375格,和作物同高)
execute positioned ~ ~0.375 ~ if entity @e[type=marker,tag=sd_fertilizer_marker,dx=0,dy=0,dz=0,limit=1] run tellraw @p {"text":"这块耕地已经施过肥了!","color":"yellow"}
execute positioned ~ ~0.375 ~ if entity @e[type=marker,tag=sd_fertilizer_marker,dx=0,dy=0,dz=0,limit=1] run return fail

# 没有施肥,可以施肥(无论是否有作物)
function stardew:farming/fertilizer/apply_to_farmland
