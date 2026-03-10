# stardew:monsters/drops/loot_table.mcfunction
# 统一的掉落表系统（基于标签）

# 调试信息
tellraw @a[distance=..10] {"text":"[DEBUG] 怪物死亡，准备掉落","color":"yellow"}

# 根据掉落标签给予物品
execute if entity @s[tag=sd_drop_slime] run tellraw @a[distance=..10] {"text":"[DEBUG] 史莱姆掉落","color":"aqua"}
execute if entity @s[tag=sd_drop_slime] run function stardew:monsters/drops/types/slime
execute if entity @s[tag=sd_drop_bug_meat] run tellraw @a[distance=..10] {"text":"[DEBUG] 虫肉掉落","color":"aqua"}
execute if entity @s[tag=sd_drop_bug_meat] run function stardew:monsters/drops/types/bug_meat
execute if entity @s[tag=sd_drop_bat_wing] run function stardew:monsters/drops/types/bat_wing
execute if entity @s[tag=sd_drop_bone] run function stardew:monsters/drops/types/bone
execute if entity @s[tag=sd_drop_stone] run function stardew:monsters/drops/types/stone

# 稀有掉落（基于层数）
execute if entity @s[tag=sd_drop_solar] run function stardew:monsters/drops/types/solar_essence
execute if entity @s[tag=sd_drop_void] run function stardew:monsters/drops/types/void_essence

# 给予经验
function stardew:monsters/drops/give_xp

