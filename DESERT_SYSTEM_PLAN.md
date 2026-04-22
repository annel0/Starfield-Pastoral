# 沙漠系统实现规划

## 概述

沙漠区域是社区中心金库（CC Vault，`ccVault` flag）完成后解锁的共享世界区域。  
包含：沙漠地图（共享 schem）、公交传送、Sandy NPC + Oasis 商店、骷髅洞穴入口、沙漠商人。

---

## 一、沙漠区域放置

### 1.1 Schem 放置策略

沙漠 schem 是**多人共享**的世界级结构，不随玩家创建，在世界首次加载时放置一次。

**参考系统**：`StardewValleyMapBootstrap` 主世界 schem 放置

**方案**：在 `STARDEW_VALLEY` 维度内选定远离主地图的固定坐标放置沙漠 schem。

| 项目 | 值 |
|---|---|
| 维度 | `ModDimensions.STARDEW_VALLEY` |
| 放置 Y | 64（与主世界一致） |
| 放置坐标 | 距主地图中心较远处（例如 X+1500, Z+0），需根据 schem 尺寸调整避免重叠 |
| 生物群系 | `stardewcraft:calico_desert`（已注册） |

### 1.2 放置时机与流程

```
ServerStartedEvent
  └─ DesertMapBootstrap.ensurePlaced(ServerLevel)
      ├─ 检查 DesertSavedData.isPlaced() → 已放置则跳过
      ├─ 加载 data/stardewcraft/structures/desert/main.schem
      ├─ 解析 NBT 获取尺寸 (width, height, length)
      ├─ 计算放置原点（固定偏移，居中对齐）
      ├─ 分帧放置（复用 StardewValleyMapBootstrap 的分帧策略）
      │   ├─ BLOCK_BUDGET_PER_TICK = 120,000
      │   └─ TIME_BUDGET_MS = 10
      └─ DesertSavedData.markPlaced(hash, origin, bounds)
```

### 1.3 关键类

| 类 | 职责 |
|---|---|
| `DesertMapBootstrap` | 加载 schem、计算坐标、分帧放置、版本管理 |
| `DesertSavedData` | SavedData 子类，记录是否已放置、schem hash、原点坐标 |

### 1.4 生物群系处理

沙漠 schem 放置后，区域内的 chunk 需要被标记为 `calico_desert` 生物群系，  
以便钓鱼系统（`FishingDataManager`）和文物掉落（`ArtifactDropService`）正确识别沙漠位置。

**方案**：schem 放置完成后，遍历覆盖范围内的所有 chunk section，  
调用 `chunk.getSection(sectionIndex).getBiomes().set(x, y, z, desertBiome)` 强制覆写生物群系。

---

## 二、公交传送系统

### 2.1 交互流程

```
玩家右键「公交站牌方块」
  └─ 服务端检查 ccVault flag
      ├─ 未解锁 → 发送提示消息"公交线路尚未修复"
      └─ 已解锁 → 发送 OpenDesertBusPayload
          └─ 客户端弹出 StardewConfirmDialogScreen
              ├─ 选项1: "前往沙漠（500G）"
              ├─ 选项2: "取消"
              └─ 玩家确认 → 发送 DesertBusSelectPayload
                  └─ 服务端处理：
                      ├─ 扣 500G（检查余额）
                      ├─ 播放公交音效
                      ├─ 黑屏过渡（发送 FadePayload）
                      └─ 传送到沙漠到达点
```

### 2.2 反向传送（沙漠 → 公交站）

沙漠内放置另一个传送方块，同样右键交互：
- 免费返回
- 相同黑屏 + 音效
- 传送到公交站坐标 `(85.5, -12.0, 223.5)` in STARDEW_VALLEY

### 2.3 需要的类

| 类 | 类型 | 职责 |
|---|---|---|
| `DesertBusBlock` | Block | 公交站牌方块，右键触发 |
| `OpenDesertBusPayload` | S→C Payload | 打开确认 UI |
| `DesertBusSelectPayload` | C→S Payload | 玩家选择传送 |
| 客户端 handler | Client | 复用 `StardewConfirmDialogScreen`（同矿车模式） |

### 2.4 音效

| 音效 | 时机 | 对应原版 |
|---|---|---|
| 公交引擎声 | 确认传送时 | `busDriveOff` |
| 到达音效 | 传送完成后 | 简单的刹车/到达音 |

### 2.5 沙漠方尖碑

在 `WarpDestinations.java` 中追加沙漠目的地（解锁后可用）：

```java
register(new WarpDestination(
    "desert", "stardewcraft.warp.desert", "stardewcraft.warp.desert.desc",
    1_000_000,  // 100万G（最高级传送）
    desertArrivalX, desertArrivalY, desertArrivalZ,
    ModDimensions.STARDEW_VALLEY
));
```

需要在传送魔杖 UI 增加对 `ccVault` flag 的 gating 检查。

---

## 三、Sandy NPC + Oasis 商店

### 3.1 Sandy NPC

| 项目 | 说明 |
|---|---|
| NPC ID | `sandy` |
| 位置 | Oasis 商店内部（interior subspace） |
| 美术资源 | 已有 |
| 系统 | 复用现有 NPC 系统（NpcDataManager + StardewNpcEntity） |
| 日程 | 固定站在商店内，不需要复杂日程 |
| 好感度 | 标准好感度系统，可送礼/对话 |

### 3.2 Oasis 商店（Interior Subspace）

使用现有 interior 系统接入：

| 项目 | 值 |
|---|---|
| Schem 文件 | `data/stardewcraft/structures/interior/sandy_house.schem` |
| Interior 原点 | 在 interior 区域分配新坐标（例如 `(14720, 70, 14720)`） |
| 入口门户 | 沙漠 schem 中 Oasis 大门位置放置 `PORTAL_TRIGGER` |
| 出口门户 | Interior 内部放置 `PORTAL_TRIGGER` 指向沙漠外部 |

**InteriorSubspaceManager 修改**：
- 增加 `SANDY_HOUSE` 注册项
- `LAYOUT_VERSION` 递增至 30

### 3.3 商店系统

| 商店 | 类型 | 内容 |
|---|---|---|
| Sandy's Oasis | 金币购买 | 星星果种子、大黄种子、仙人掌种子、沙漠专属装饰 |
| 沙漠商人 | 物物交换 | 异域物品兑换（不收金币） |

**Sandy 商店注册**：在 `ShopRegistry` 中添加 `sandy_shop`  
**沙漠商人注册**：在 `ShopRegistry` 中添加 `desert_trader`（交换模式）

---

## 四、沙漠商人

### 4.1 位置

沙漠户外，Oasis 门口附近固定位置。  
可实现为：
- 方案 A：NPC 实体（`desert_trader`），固定站位不移动
- 方案 B：功能方块（商人摊位），右键打开商店

**推荐方案 B**，简单可靠，避免 NPC 实体在多人中的同步问题。

### 4.2 交换商品参考（原版）

沙漠商人特色是**以物易物**，不收金币。  
商品每周轮换（基于星期几），参考 SDV Shops.json `DesertTrade`。

| 星期 | 出售物品 | 交换材料 |
|---|---|---|
| 周一 | 魔法糖冰棒 | 1×虹鳟鱼 |
| 周二 | 芋头种子 | 3×宝石甜莓 |
| 周三 | 万象晶球 | 5×精炼石英 |
| 周四 | 沙漠图腾 | 3×椰子 |
| 周五 | 咖啡豆 | 1×钻石 |
| 周六/日 | 随机稀有物品 | 根据物品定 |

> 具体商品表可后期细调，先搭建交换商店框架。

---

## 五、骷髅洞穴入口

### 5.1 概述

骷髅洞穴入口位于沙漠 schem 中的特定位置（洞穴口）。  
玩家右键进入 → 传送到骷髅洞穴矿井维度。

### 5.2 接入方式

骷髅洞穴矿井系统由用户单独开发中，此处只需要：

1. **入口方块**：沙漠 schem 中放置一个传送触发方块
2. **传送逻辑**：右键 → 检查是否持有骷髅钥匙（Skull Key，矿洞 120 层奖励）→ 传送到骷髅洞穴第 1 层
3. **出口传送**：骷髅洞穴内梯子到 0 层 → 返回沙漠入口

### 5.3 解锁条件

| 条件 | 说明 |
|---|---|
| 金库献祭完成 | `ccVault` flag（到达沙漠的前提） |
| 骷髅钥匙 | 矿洞 120 层奖励物品（检查背包） |

---

## 六、实现优先级

### Phase 1：核心区域（本次）

| 序号 | 任务 | 依赖 | 复杂度 |
|---|---|---|---|
| 1.1 | `DesertMapBootstrap` + `DesertSavedData` | schem 文件 | 中 |
| 1.2 | 沙漠 schem 放置 + 生物群系覆写 | 1.1 | 中 |
| 1.3 | `DesertBusBlock` + 传送 UI + 网络包 | 无 | 中 |
| 1.4 | 沙漠到达点 + 返回传送方块 | 1.2 | 低 |
| 1.5 | 沙漠方尖碑（WarpDestinations 追加） | 1.2 | 低 |

### Phase 2：NPC 与商店

| 序号 | 任务 | 依赖 | 复杂度 |
|---|---|---|---|
| 2.1 | Sandy NPC 注册（npc profile JSON） | NPC 系统 | 低 |
| 2.2 | Sandy's Oasis interior schem 放置 | interior 系统 | 中 |
| 2.3 | Sandy 商店商品注册 | ShopRegistry | 低 |
| 2.4 | 沙漠商人方块 + 交换商店 | ShopRegistry | 中 |

### Phase 3：骷髅洞穴接入

| 序号 | 任务 | 依赖 | 复杂度 |
|---|---|---|---|
| 3.1 | 骷髅洞穴入口方块 + 钥匙检查 | 矿井系统 | 低 |
| 3.2 | 骷髅洞穴 ↔ 沙漠双向传送 | 3.1 | 低 |

### Phase 4：锦上添花（可选/后期）

| 任务 | 说明 |
|---|---|
| 沙漠节庆 | 春 15-17 日特殊活动（骆驼赛跑、21 点等） |
| 骆驼互动 | 装饰性 NPC |
| 隐藏宝藏 | 秘密笔记联动 |
| Calico Jack 小游戏 | 21 点纸牌 |

---

## 七、文件清单

### 新增 Java 类

```
src/main/java/com/stardew/craft/desert/
├── DesertMapBootstrap.java        # schem 加载 + 分帧放置
├── DesertSavedData.java           # 放置状态持久化
├── DesertConstants.java           # 坐标常量、到达点等
└── DesertBiomeWriter.java         # 放置后覆写生物群系

src/main/java/com/stardew/craft/block/desert/
└── DesertBusBlock.java            # 公交站牌传送方块

src/main/java/com/stardew/craft/network/payload/
├── OpenDesertBusPayload.java      # S→C: 打开传送确认 UI
└── DesertBusSelectPayload.java    # C→S: 确认传送
```

### 新增资源文件

```
src/main/resources/data/stardewcraft/structures/desert/
└── main.schem                     # 沙漠区域结构文件

src/main/resources/data/stardewcraft/structures/interior/
└── sandy_house.schem              # Sandy 的 Oasis 商店内部

src/main/resources/data/stardewcraft/npc/profiles/
└── sandy.json                     # Sandy NPC 配置
```

### 需修改的现有文件

| 文件 | 修改内容 |
|---|---|
| `ModBlocks.java` | 注册 `DESERT_BUS_BLOCK` |
| `PacketHandler.java` | 注册 desert bus 网络包 |
| `WarpDestinations.java` | 追加沙漠方尖碑目的地 |
| `InteriorSubspaceManager.java` | 追加 Sandy's Oasis 内部，`LAYOUT_VERSION++` |
| `ShopRegistry.java` | 注册 Sandy 商店 + 沙漠商人商店 |
| `CCStoryFlags.java` | 若需要额外 flag（可选） |
| `lang/zh_cn.json` + `en_us.json` | 新增翻译条目 |

---

## 八、关键坐标预留

> 以下坐标为预估值，需根据实际 schem 尺寸调整。

| 位置 | 坐标（STARDEW_VALLEY 维度） | 说明 |
|---|---|---|
| 沙漠 schem 原点 | (1500, 64, 0) | 距主地图远端，避免重叠 |
| 沙漠到达点 | schem 内公交停靠位置 | 玩家传送到此 |
| 沙漠返回传送点 | schem 内公交站牌位置 | 返回主世界公交站 |
| Oasis 入口（exterior） | schem 内大门位置 | `PORTAL_TRIGGER` |
| Oasis 内部（interior） | (14720, 70, 14720) | interior subspace |
| 骷髅洞穴入口 | schem 内洞穴口位置 | 传送到矿井维度 |
| 主世界公交站牌 | (85.5, -12.0, 223.5) 附近 | 公交站已有坐标 |

---

## 九、技术要点

### 9.1 多人共享

沙漠区域对所有玩家共享，不像农场那样 per-player。  
这意味着：
- schem 只放置一次
- `DesertSavedData` 是全局的（不按 UUID 分）
- 公交传送的 ccVault flag 是 per-player 的 → 每个玩家独立解锁

### 9.2 大型 Schem 性能

如果 schem 很大（> 500万方块），采用与主世界相同的分帧策略：
- 首次放置分摊到多个 tick
- 放置期间玩家无法进入沙漠区域（或显示"加载中"提示）
- 放置完成后写入 SavedData，后续重启不再重复放置

### 9.3 公交传送黑屏

参考已有的跨维度传送模式（`CrossDimensionTeleporter`）：
- 发送一个 fade-out payload → 客户端渐黑
- 等待 ~1 秒
- 执行传送
- 发送 fade-in payload → 客户端渐亮
- 播放到达音效
