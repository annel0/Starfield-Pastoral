# 给予物品并播放特效（根据玩家标签区分）
# 在动画展示时调用（第6tick）

# 普通宝箱
execute as @a[tag=sd_treasure_common] run function stardew:fishing/treasure_chest/give_common

# 稀有宝箱
execute as @a[tag=sd_treasure_rare] run function stardew:fishing/treasure_chest/give_rare

# 史诗宝箱
execute as @a[tag=sd_treasure_epic] run function stardew:fishing/treasure_chest/give_epic
