# 获取所有武器（测试用）

# 清除旧的生锈的剑（避免持有旧版本）
clear @s minecraft:carrot_on_a_stick[minecraft:custom_data~{weapon_type:"sword"}]

# 给予新版本的武器
loot give @s loot stardew:items/weapon/rusty_sword

tellraw @s {"text":"[Debug] 已清除旧武器并获得新版生锈的剑！","color":"green"}
tellraw @s {"text":"提示：新武器有 weapon_range: 3.0 属性","color":"yellow"}
