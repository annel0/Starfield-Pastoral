# 龙牙利刃测试 - 给予玩家龙牙利刃

loot give @s loot stardew:items/weapon/dragontooth_shiv

tellraw @s [{"text":"[测试] ","color":"green","bold":true},{"text":"已给予 ","color":"white"},{"text":"龙牙利刃","color":"#DC143C","bold":true}]
tellraw @s [{"text":"  ","color":"white"},{"text":"右键","color":"yellow","bold":true},{"text":" - 刀锋之舞 (5连击，每次50%伤害，6秒冷却)","color":"gray"}]
tellraw @s [{"text":"  ","color":"white"},{"text":"Shift+右键","color":"yellow","bold":true},{"text":" - 暴击涌动 (+50%暴击率，暴击伤害×4，6秒持续，12秒冷却)","color":"gray"}]
