# ================================================================
# 清理旧的光源方块
# ================================================================
# 移除玩家离开后留下的 light block

# 找到所有不在 new 标记中的旧 light block marker
execute as @e[tag=stardew.light_block,tag=!stardew.light_block.new] at @s run function stardew:equipment/effects/rings/remove_single_light_block

# 将 new 标记转为普通标记
tag @e[tag=stardew.light_block.new] remove stardew.light_block.new
