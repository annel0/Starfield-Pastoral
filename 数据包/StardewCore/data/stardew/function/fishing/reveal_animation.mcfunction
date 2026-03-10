# stardew:fishing/reveal_animation
# 播放钓鱼收获展示动画(逆时针绕玩家圆周运动:玩家后方→右侧→前方→左侧→后方)
# macro参数: cmd (custom_model_data)

# 在玩家位置生成item_display,使用anchored eyes固定高度,rotated ~ 0保持水平
# 初始translation在右后方(2.2,-0.2,-2.2),scale为0实现瞬间出现效果(Z负=后,正=前)
$execute anchored eyes positioned ^ ^ ^ rotated ~ 0 run summon item_display ^ ^ ^ {Tags:["sd_fish_reveal_display"],billboard:"fixed",item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(cmd)}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[2.2f,-0.2f,-2.2f],scale:[0f,0f,0f]}}

# 关键:让实体在玩家位置,继承玩家水平朝向(这样物品就以玩家为中心旋转,而不是以玩家前方为中心)
execute as @e[tag=sd_fish_reveal_display,limit=1] at @p rotated as @p run tp @s ~ ~1.62 ~ ~ 0

# 5阶段逆时针圆周轨道动画(围绕玩家,半径约2格):
# 调整后时间线:
# 1t-3t: phase1a - 从正后方快速飞到右后方(逆时针开始,3tick加速)
# 5t-8t: phase1b - 从右后方快速到正前方(4tick快速到中心)
# 10t-19t: phase1c - 在正前方停顿放大展示(10tick停留) + 粒子声音
# 21t-23t: phase2a - 从正前方继续逆时针到左后方(3tick)
# 25t-28t: phase2b - 从左后方飞回正后方远处消失(4tick)
schedule function stardew:fishing/reveal_anim_phase1a 1t
schedule function stardew:fishing/reveal_anim_phase1b 5t
schedule function stardew:fishing/reveal_anim_phase1c 10t
schedule function stardew:fishing/reveal_anim_effects 10t
schedule function stardew:fishing/reveal_anim_phase2a 21t
schedule function stardew:fishing/reveal_anim_phase2b 25t

# 30tick后清除实体
schedule function stardew:fishing/cleanup_reveal_display 30t
