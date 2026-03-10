# 绑定AJ模型到阿比盖尔NPC
# @s = AJ root entity (刚召唤的)
# 执行位置 = 村民位置

# 1. 添加visual标签
tag @s add npc.abigail.visual

# 2. 复制村民的NPC ID
scoreboard players operation @s stardew.npc.id = @e[type=villager,tag=npc.abigail,tag=npc.new,limit=1,sort=nearest] stardew.npc.id

# 3. 立即播放idle动画
function animated_java:abigail/animations/pause_all
function animated_java:abigail/animations/idle/play

# 4. 调试信息
