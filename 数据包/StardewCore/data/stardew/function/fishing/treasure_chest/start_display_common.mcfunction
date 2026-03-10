# 启动普通宝箱展示（立即清空副手，开始动画）
# macro参数: minecraft:custom_model_data

# 立即清空副手（避免显示太久）
item replace entity @s weapon.offhand with air

# 播放动画
$function stardew:fishing/treasure_chest/display_animation {cmd:$(minecraft:custom_model_data)}

# 6tick后播放粒子效果并给予物品
schedule function stardew:fishing/treasure_chest/give_common 6t
