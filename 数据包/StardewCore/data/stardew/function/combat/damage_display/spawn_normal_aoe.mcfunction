# 召唤AOE伤害显示（颜色略浅，表示溅射伤害）
# 使用宏函数接收伤害参数

# 参数: $(damage) - 伤害值
# 召唤在怪物上方1.2格，朝向玩家方向偏移0.3格
$summon text_display ^ ^1.2 ^0.3 {Tags:["sd_damage_display","sd_dmg_new"],alignment:"center",brightness:{sky:15,block:15},text:'{"text":"$(damage)","color":"gold","bold":true}',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.9f,0.9f,0.9f]},background:0,billboard:"center",interpolation_duration:0}

# 初始化计时器（从 0 开始，第一个 tick 会变成 1）
scoreboard players set @e[tag=sd_dmg_new,limit=1] sd_dmg_anim 0

# 移除临时标签
tag @e[tag=sd_dmg_new] remove sd_dmg_new
