# 商店系统 (Shop System)

Stardew Valley 风格的商店系统,支持季节性商品、分页浏览、悬停高亮和购买交易。

## 📋 目录

- [功能特性](#功能特性)
- [文件结构](#文件结构)
- [使用方法](#使用方法)
- [配置说明](#配置说明)
- [技术实现](#技术实现)
- [待办事项](#待办事项)

---

## ✨ 功能特性

### 已实现功能
- ✅ **季节性商品系统**: 根据游戏季节显示不同商品
- ✅ **分页浏览**: 每页显示3个商品,支持翻页
- ✅ **悬停高亮**: 鼠标指向按钮/商品时显示发光效果
- ✅ **购买交易**: 验证金币、扣款、给予物品
- ✅ **UI显示**: 完整的商店UI界面
- ✅ **Interaction实体**: 可点击的按钮和商品槽

### 待实现功能
- ⏳ **动态loot路径**: 目前使用item_id条件分支,未来可改用宏函数
- ⏳ **商品图标显示**: 需要从loot table读取CMD并更新item_display
- ⏳ **多商店支持**: Robin、Clint、Willy等其他商店
- ⏳ **高级功能**: Shift批量购买、库存限制、特殊商品解锁

---

## 📁 文件结构

```
shop/
├── README.md                    # 本文档
├── init_pierre.mcfunction       # 初始化Pierre商店数据到storage
├── open_pierre.mcfunction       # 打开Pierre商店的入口函数
├── summon_ui_fixed.mcfunction   # 召唤商店UI和interaction实体
├── update_display.mcfunction    # 更新商品显示(名称、价格)
├── check_hover.mcfunction       # 检测鼠标悬停并添加高亮
├── raycast_step.mcfunction      # Raycast递归步进函数
├── handle_interaction.mcfunction # 处理interaction点击事件
├── close_shop.mcfunction        # 关闭商店并清理UI
├── close_for_player.mcfunction  # 玩家个体的关闭处理
├── page_up.mcfunction           # 上一页
├── page_down.mcfunction         # 下一页
├── purchase_slot_1.mcfunction   # 购买槽位1的商品
├── purchase_slot_2.mcfunction   # 购买槽位2的商品
├── purchase_slot_3.mcfunction   # 购买槽位3的商品
├── purchase.mcfunction          # 购买逻辑(验证金币、扣款)
├── give_item_by_id.mcfunction   # 根据item_id给予物品
├── pierre_inventory.json        # Pierre商品数据(JSON格式,仅供参考)
└── config/
    └── pierre.mcfunction        # Pierre商店配置(未使用)
```

---

## 🎮 使用方法

### 1. 初始化系统

确保在 `init.mcfunction` 中已添加以下计分板:
```mcfunction
scoreboard objectives add sd_in_shop dummy "商店状态"
scoreboard objectives add sd_shop_season dummy "商店季节"
scoreboard objectives add sd_shop_page dummy "商店页码"
scoreboard objectives add sd_temp dummy "临时变量"
```

### 2. 打开商店

#### 方法A: 直接命令
```mcfunction
execute as <player> run function stardew:shop/open_pierre
```

#### 方法B: 通过Interaction实体(推荐)
在Pierre商店门口放置一个interaction实体:
```mcfunction
# 在Pierre商店位置召唤interaction
summon interaction <x> <y> <z> {width:1f,height:2f,Tags:["shop_entry","pierre_shop"]}

# 在interact.mcfunction或类似tick函数中检测交互
execute as @e[type=interaction,tag=pierre_shop,nbt={interaction:{}}] at @s run function stardew:shop/trigger_pierre
```

创建 `shop/trigger_pierre.mcfunction`:
```mcfunction
# 找到附近的玩家并打开商店
execute as @a[distance=..3] run function stardew:shop/open_pierre

# 重置interaction数据
data remove entity @s interaction
```

### 3. 集成到主循环

在 `main.mcfunction` 中已添加:
```mcfunction
# 商店系统 tick
execute as @a[scores={sd_in_shop=1..}] run function stardew:shop/check_hover
execute as @a[scores={sd_in_shop=1..}] run function stardew:shop/handle_interaction
```

---

## ⚙️ 配置说明

### 商店数据结构

商店数据存储在 `storage stardew:shop pierre` 中:

```
pierre.spring[0]    # 春季第1页(商品0-2)
pierre.spring[1]    # 春季第2页(商品3-5)
pierre.spring[2]    # 春季第3页(商品6-8)
pierre.summer[0-3]  # 夏季4页
pierre.fall[0-3]    # 秋季4页
pierre.winter[0]    # 冬季1页
```

每个商品的数据格式:
```json
{
  "item_id": "parsnip_seeds",
  "loot_table": "stardew:items/seeds/crop_parsnip",
  "price": 20,
  "display_name": "防风草种子"
}
```

### 添加新商品

在 `init_pierre.mcfunction` 中添加:
```mcfunction
data modify storage stardew:shop pierre.spring[0] append value {
  item_id:"new_item",
  loot_table:"stardew:items/path/to/item",
  price:100,
  display_name:"新物品"
}
```

同时在 `give_item_by_id.mcfunction` 中添加:
```mcfunction
execute if data storage stardew:temp {item_id:"new_item"} run loot give @s loot stardew:items/path/to/item
```

### 修改季节

玩家的 `sd_shop_season` 计分板值:
- `1` = 春季
- `2` = 夏季
- `3` = 秋季
- `4` = 冬季

在 `open_pierre.mcfunction` 中可从全局季节系统读取。

---

## 🔧 技术实现

### UI坐标系统
- **原始设计位置**: 玩家在 (86, -54, 104) 朝北
- **实际运行位置**: 玩家在 (0, 64, 7) 朝南(stardew:interiors维度)
- **坐标转换公式**:
  1. 计算相对坐标: Relative = Original - (86, -54, 104)
  2. 旋转180°: Rotated = (-RelativeX, RelativeY, -RelativeZ)
  3. 最终坐标: Final = Rotated + (0, 64, 7) + offset

### Interaction实体位置
| 元素 | 位置 | 尺寸 | 标签 |
|------|------|------|------|
| 关闭按钮 | 1.375, 66.75, 9.1242 | 0.25×0.25 | `button_close` |
| 上一页 | 1.375, 65.875, 9.1242 | 0.25×0.25 | `button_page_up` |
| 下一页 | 1.375, 65, 9.1242 | 0.25×0.25 | `button_page_down` |
| 商品槽1 | -1.125, 66.75, 8.375 | 0.6×0.4 | `slot_1` |
| 商品槽2 | -1.125, 66.1875, 8.375 | 0.6×0.4 | `slot_2` |
| 商品槽3 | -1.125, 65.625, 8.375 | 0.6×0.4 | `slot_3` |

### 悬停检测
使用raycast从玩家眼睛位置向前0.1格递归检测,最大距离5格。
检测到的interaction实体会被添加 `glowing` 效果。

### 购买流程
1. 检测interaction的 `{interaction:{}}` 或 `{attack:{}}` NBT
2. 从 `storage stardew:temp current_page[index]` 读取商品数据
3. 验证玩家 `sd_gold` 是否足够
4. 扣除金币并通过 `loot give` 给予物品
5. 播放音效和显示提示

---

## 📝 待办事项 (TODOs)

### 高优先级
- [ ] 实现商品图标动态更新(从loot table读取CMD)
- [ ] 创建打开商店的interaction实体生成器
- [ ] 添加从时间系统读取当前季节的逻辑
- [ ] 完善返回位置存储(目前硬编码到主世界)

### 中优先级
- [ ] 添加其他商店(Robin、Clint、Willy等)
- [ ] 实现库存限制系统(有限库存商品)
- [ ] 添加特殊商品解锁条件(好感度、成就等)
- [ ] 实现NPC对话动态变化

### 低优先级
- [ ] Shift+点击批量购买
- [ ] 商店UI皮肤切换(不同商店不同外观)
- [ ] 购买历史记录
- [ ] 每日随机商品(墙纸、地板)

---

## 🐛 已知问题

1. **动态loot路径**: 目前使用大量 `execute if data` 条件分支来分发物品,未来Minecraft支持宏函数后可优化
2. **商品图标**: item_display的item无法通过NBT修改,需要使用 `loot replace entity` 命令(待实现)
3. **季节同步**: 需要与时间系统集成,目前默认为春季
4. **返回位置**: 关闭商店时硬编码传送到主世界,应存储玩家原位置

---

## 📚 参考资料

- **官方Wiki**: https://stardewvalleywiki.com/Pierre%27s_General_Store
- **Loot Tables**: `StardewCore/data/stardew/loot_table/items/seeds/`
- **UI设计**: 基于原版Stardew Valley商店界面
- **Custom Model Data**: 11100-11108 (UI组件)

---

## 🤝 贡献指南

如果要添加新商店:
1. 复制 `init_pierre.mcfunction` → `init_<shop_name>.mcfunction`
2. 修改storage路径为 `stardew:shop <shop_name>`
3. 创建 `open_<shop_name>.mcfunction`
4. 在 `give_item_by_id.mcfunction` 中添加新商品的loot分发逻辑
5. 更新本README文档

---

**最后更新**: 2025-12-30  
**版本**: v1.0.0 - 基础功能完成  
**作者**: StardewCraft Team
