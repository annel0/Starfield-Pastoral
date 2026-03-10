# 商店系统 - 初始化
# 创建所有需要的scoreboards

scoreboard objectives add shop.page dummy "商店页码"
scoreboard objectives add shop.inv_page dummy "背包页码"
scoreboard objectives add shop.selected dummy "选中槽位"
scoreboard objectives add shop.click_cooldown dummy "点击冷却"
scoreboard objectives add shop.total_pages dummy "总页数"

# 注意: 使用现有的 sd_gold 作为金币系统

tellraw @a [{"text":"[商店系统] ","color":"gold"},{"text":"Scoreboards已初始化","color":"green"}]
