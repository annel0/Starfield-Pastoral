# data/stardew/functions/tools/cooldown/init.mcfunction
# 初始化工具冷却系统

# 冷却计分板
scoreboard objectives add sd_tool_cd dummy "工具冷却时间"
scoreboard objectives add sd_hoe_cd dummy "锄头冷却"
scoreboard objectives add sd_water_cd dummy "水壶冷却"
scoreboard objectives add sd_scythe_cd dummy "镰刀冷却"
scoreboard objectives add sd_axe_cd dummy "斧头冷却"
scoreboard objectives add sd_pickaxe_cd dummy "镐子冷却"

# Boss 血条用于显示冷却进度
bossbar add stardew:hoe_cooldown {"text":"🔨 锄头冷却中...","color":"gray"}
bossbar set stardew:hoe_cooldown color red
bossbar set stardew:hoe_cooldown style notched_6
bossbar set stardew:hoe_cooldown visible false

bossbar add stardew:water_cooldown {"text":"💧 水壶冷却中...","color":"blue"}
bossbar set stardew:water_cooldown color blue
bossbar set stardew:water_cooldown style notched_6
bossbar set stardew:water_cooldown visible false

bossbar add stardew:scythe_cooldown {"text":"⚔ 镰刀冷却中...","color":"yellow"}
bossbar set stardew:scythe_cooldown color yellow
bossbar set stardew:scythe_cooldown style notched_6
bossbar set stardew:scythe_cooldown visible false

bossbar add stardew:axe_cooldown {"text":"🪓 斧头冷却中...","color":"green"}
bossbar set stardew:axe_cooldown color green
bossbar set stardew:axe_cooldown style notched_6
bossbar set stardew:axe_cooldown visible false

bossbar add stardew:pickaxe_cooldown {"text":"⛏ 镐子冷却中...","color":"gray"}
bossbar set stardew:pickaxe_cooldown color white
bossbar set stardew:pickaxe_cooldown style notched_6
bossbar set stardew:pickaxe_cooldown visible false
