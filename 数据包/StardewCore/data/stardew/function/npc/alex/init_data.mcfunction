# Alex NPC数据初始化

# 设置基础信息
data modify storage stardew:npc alex.display_name set value '{"text":"亚历克斯","color":"#FFD700","bold":true}'
data modify storage stardew:npc alex.birthday set value {season:"summer",day:13}
data modify storage stardew:npc alex.profession set value "athlete"
data modify storage stardew:npc alex.home_location set value {x:100,y:64,z:200}

# 礼物偏好
data modify storage stardew:npc alex.loved_gifts set value ["minecraft:golden_apple","minecraft:cooked_beef","minecraft:cake"]
data modify storage stardew:npc alex.liked_gifts set value ["minecraft:bread","minecraft:cooked_chicken","minecraft:apple"]
data modify storage stardew:npc alex.disliked_gifts set value ["minecraft:poisonous_potato","minecraft:rotten_flesh"]
data modify storage stardew:npc alex.hated_gifts set value ["minecraft:spider_eye","minecraft:fermented_spider_eye"]

# 时间表数据
data modify storage stardew:npc alex.schedule.spring set value {}
data modify storage stardew:npc alex.schedule.spring."6" set value {location:{x:95,y:64,z:195},animation:"idle",action:"wake_up"}
data modify storage stardew:npc alex.schedule.spring."8" set value {location:{x:120,y:64,z:180},animation:"walk",action:"exercise"}
data modify storage stardew:npc alex.schedule.spring."12" set value {location:{x:110,y:64,z:200},animation:"idle",action:"rest"}
data modify storage stardew:npc alex.schedule.spring."18" set value {location:{x:100,y:64,z:200},animation:"idle",action:"home"}
data modify storage stardew:npc alex.schedule.spring."22" set value {location:{x:95,y:64,z:195},animation:"idle",action:"sleep"}

# 对话数据
data modify storage stardew:npc alex.dialogue.hearts_0_2 set value ['{"text":"嘿，你好啊！我是Alex。","color":"#2C1810"}','{"text":"我每天都在训练，为了成为最强的运动员！","color":"#2C1810"}']
data modify storage stardew:npc alex.dialogue.hearts_4_6 set value ['{"text":"你好朋友！今天感觉怎么样？","color":"#2C1810"}','{"text":"我觉得我们相处得不错，你觉得呢？","color":"#2C1810"}']
data modify storage stardew:npc alex.dialogue.hearts_8_10 set value ['{"text":"见到你总是让我很开心！","color":"#2C1810"}','{"text":"有你这样的好朋友真是太好了。","color":"#2C1810"}']

tellraw @a[tag=debug] {"text":"Alex NPC 数据已初始化","color":"aqua"}