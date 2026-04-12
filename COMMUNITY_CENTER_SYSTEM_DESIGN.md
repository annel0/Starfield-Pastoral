# 社区中心献祭系统 — 完整设计文档

## 一、原版数据结构（来自源文件）

### 1.1 Bundles.json 格式

文件路径：`源文件/Content/Data/Bundles.json`

每条记录格式：
```
"区域名/bundleID": "内部名/奖励字符串/物品列表/颜色/需完成数/贴图覆盖/显示名"
```

字段索引（来自 `Bundle.cs` 常量）：
- `[0]` = 内部名（如 "Spring Crops"）
- `[1]` = 奖励（如 "O 465 20" 表示物品类型+ID+数量）
- `[2]` = 材料列表（每 3 个一组：`物品ID 数量 品质`，空格分隔）
- `[3]` = 颜色索引（0-6）
- `[4]` = 需完成的材料槽数（可选，默认=全部）
- `[5]` = 贴图覆盖（可选，`纹理路径:索引` 或纯索引）
- `[6]` = 显示名

### 1.2 颜色映射（来自 `Bundle.getColorFromColorIndex()`）

| 索引 | 颜色 | 常量名 |
|------|------|--------|
| 0 | Green (Lime) | Color_Green |
| 1 | Purple (DeepPink) | Color_Purple |
| 2 | Orange | Color_Orange |
| 3 | Yellow (Orange) | Color_Yellow |
| 4 | Red | Color_Red |
| 5 | Blue (LightBlue) | Color_Blue |
| 6 | Teal (Cyan) | Color_Teal |

### 1.3 奖励字符串格式（来自 `Bundle.getReward()` → `Utility.getItemFromStandardTextDescription()`）

- `O 465 20` → 物品(Object) ID=465，数量=20
- `BO 10 1` → 大型加工品(BigCraftable) ID=10，数量=1
- `R 517 1` → 戒指(Ring) ID=517，数量=1

### 1.4 完整 Bundle 数据

#### Pantry（食品储藏室）— Area 0

| Bundle ID | 名称 | 材料 | 颜色 | 需完成数 | 奖励 |
|-----------|------|------|------|---------|------|
| 0 | 春季作物 | 防风草×1, 绿豆×1, 花椰菜×1, 土豆×1 | Green(0) | 4 | 春季种子×20 (O 465 20) |
| 1 | 夏季作物 | 番茄×1, 热辣椒×1, 蓝莓×1, 甜瓜×1 | Yellow(3) | 4 | 速效生长肥料×1 (O 621 1) |
| 2 | 秋季作物 | 玉米×1, 茄子×1, 南瓜×1, 山药×1 | Orange(2) | 4 | 蜂房×1 (BO 10 1) |
| 3 | 高品质作物 | 防风草×5(金), 甜瓜×5(金), 南瓜×5(金), 玉米×5(金) | Teal(6) | 3/4 | 洒水器×1 (BO 15 1) |
| 4 | 动物 | 大鸡蛋×1, 大牛奶×1, 大鸡蛋(棕)×1, 山羊奶×1, 毛线×1, 鸭蛋×1 | Red(4) | 5/6 | 奶酪压制机×1 (BO 16 1) |
| 5 | 工匠 | 松露油×1, 布料×1, 山羊芝士×1, 芝士×1, 蜂蜜×1, 果冻×1, 苹果酱×1, 杏桃酱×1, 橙子酱×1, 桃子酱×1, 石榴酱×1, 樱桃酱×1 | Purple(1) | 6/12 | 酒桶×1 (BO 12 1) |

#### Crafts Room（工艺室）— Area 1

| Bundle ID | 名称 | 材料 | 颜色 | 需完成数 | 奖励 |
|-----------|------|------|------|---------|------|
| 13 | 春季采集 | 水仙花×1, 蒲公英×1, 韭葱×1, 黄水仙×1 | Green(0) | 4 | 春季种子×30 (O 495 30) |
| 14 | 夏季采集 | 葡萄×1, 香味浆果×1, 甜豌豆×1 | Yellow(3) | 3 | 夏季种子×30 (O 496 30) |
| 15 | 秋季采集 | 普通蘑菇×1, 野梅子×1, 榛子×1, 黑莓×1 | Orange(2) | 4 | 秋季种子×30 (O 497 30) |
| 16 | 冬季采集 | 冬根×1, 水晶果×1, 雪山药×1, 番红花×1 | Teal(6) | 4 | 冬季种子×30 (O 498 30) |
| 17 | 建筑 | 木材×99, 木材×99, 石头×99, 硬木×10 | Red(4) | 4 | 木炭窑×1 (BO 114 1) |
| 19 | 异国采集 | 椰子×1, 仙人掌果×1, 洞穴胡萝卜×1, 红蘑菇×1, 紫蘑菇×1, 枫糖浆×1, 橡树脂×1, 松焦油×1, 海莲子×1 | Purple(1) | 5/9 | 秋季种子×5 (O 235 5) |

#### Fish Tank（鱼缸）— Area 2

| Bundle ID | 名称 | 材料 | 颜色 | 需完成数 | 奖励 |
|-----------|------|------|------|---------|------|
| 6 | 河鱼 | 鲶鱼×1, 太阳鱼×1, 鲤鱼×1, 虎鳟×1 | Teal(6) | 4 | 高级鱼饵×30 (O DeluxeBait 30) |
| 7 | 湖鱼 | 大口鲈鱼×1, 鲤鱼×1, 大头鱼×1, 鳐鱼×1 | Green(0) | 4 | 诱饵桶×1 (O 687 1) |
| 8 | 海鱼 | 沙丁鱼×1, 金枪鱼×1, 红鲷鱼×1, 鳗鱼×1 | Blue(5) | 4 | 蟹笼×5 (O 690 5) |
| 9 | 夜间垂钓 | 鲶鱼×1, 大嘴鲈鱼×1, 鳗鱼×1 | Purple(1) | 3 | 小夜灯戒指×1 (R 517 1) |
| 10 | 特色鱼 | 河豚×1, 鬼鱼×1, 沙鱼×1, 木跳鱼×1 | Red(4) | 4 | 炸玉米饼×5 (O 242 5) |
| 11 | 蟹笼 | 海螺×1, 龙虾×1, 螃蟹×1, 小龙虾×1, 鹦鹉螺×1, 虾×1, 蜗牛×1, 海螺×1, 牡蛎×1, 贻贝×1 | Purple(1) | 5/10 | 蟹笼×3 (O 710 3) |

#### Boiler Room（锅炉房）— Area 3

| Bundle ID | 名称 | 材料 | 颜色 | 需完成数 | 奖励 |
|-----------|------|------|------|---------|------|
| 20 | 铁匠 | 铜锭×1, 铁锭×1, 金锭×1 | Orange(2) | 3 | 熔炉×1 (BO 13 1) |
| 21 | 地质学家 | 石英×1, 火玛瑙×1, 冰晶石×1, 萤石×1 | Purple(1) | 4 | 万象晶球×5 (O 749 5) |
| 22 | 冒险家 | 史莱姆液×99, 蝙蝠翅膀×10, 太阳精华×1, 虚空精华×1 | Purple(1) | 2/4 | 小磁铁戒指×1 (R 518 1) |

#### Vault（金库）— Area 4

| Bundle ID | 名称 | 材料 | 颜色 | 需完成数 | 奖励 |
|-----------|------|------|------|---------|------|
| 23 | 2,500 金 | 金币 2500 | Red(4) | 1 | 巧克力蛋糕×3 (O 220 3) |
| 24 | 5,000 金 | 金币 5000 | Orange(2) | 1 | 速效生长肥料×30 (O 369 30) |
| 25 | 10,000 金 | 金币 10000 | Yellow(3) | 1 | 避雷针×1 (BO 9 1) |
| 26 | 25,000 金 | 金币 25000 | Purple(1) | 1 | 水晶复制机×1 (BO 21 1) |

金库的材料格式特殊：`-1 金额 金额`（`-1` 表示金币，非物品）。金库 Bundle 在 UI 中不显示材料槽，而是显示购买按钮。

#### Bulletin Board（公告栏）— Area 5

| Bundle ID | 名称 | 材料 | 颜色 | 需完成数 | 奖励 |
|-----------|------|------|------|---------|------|
| 31 | 厨师 | 枫糖浆×1, 炒蛋×1, 松露×1, 热辣椒×1, 面包×1, 青豆×1 | Red(4) | 6 | 粉色蛋糕×3 (O 221 3) |
| 32 | 田野研究 | 紫蘑菇×1, 鹦鹉螺化石×1, 铁矿石×1(???) , 兔子脚×1 | Blue(5) | 4 | 回收机×1 (BO 20 1) |
| 33 | 魔法师 | 橡树脂×1, 太阳精华×1, 兔子脚×1, 石榴×1 | Purple(1) | 4 | 金南瓜×5 (O 336 5) |
| 34 | 染料 | 红蘑菇×1, 向日葵×1, 鸭子毛×1, 水晶球×1, 海蓝宝石×1, 红卷心菜×1 | Teal(6) | 6 | 种子生产器×1 (BO 25 1) |
| 35 | 饲料 | 小麦×10, 干草×10, 苹果×3 | Yellow(3) | 3 | 干草料斗×1 (BO 104 1) |

#### Abandoned Joja Mart（废弃 Joja 商场）— Area 6

| Bundle ID | 名称 | 材料 | 颜色 | 需完成数 | 奖励 |
|-----------|------|------|------|---------|------|
| 36 | 失踪的 | 高品质葡萄酒(银)×1, 恐龙蛋黄酱×1, 棱晶碎片×1, 远古种子×5(金), 虚空蛋黄酱×1(金?), 鱼露×1(???) | Purple(1) | 5/6 | 无（解锁电影院） |

### 1.5 房间（Area）定义

来自 `CommunityCenter.cs`：

| Area ID | 常量名 | 名称 | 地图 Tile 区域 (x,y,w,h) |
|---------|--------|------|--------------------------|
| 0 | AREA_Pantry | Pantry（食品储藏室） | (0, 0, 22, 11) |
| 1 | AREA_CraftsRoom | Crafts Room（工艺室） | (0, 12, 21, 17) |
| 2 | AREA_FishTank | Fish Tank（鱼缸） | (35, 4, 9, 9) |
| 3 | AREA_BoilerRoom | Boiler Room（锅炉房） | (52, 9, 16, 12) |
| 4 | AREA_Vault | Vault（金库） | (45, 0, 15, 9) |
| 5 | AREA_Bulletin | Bulletin Board（公告栏） | (22, 13, 28, 9) |
| 6 | AREA_AbandonedJojaMart | Abandoned Joja Mart | 独立位置 |
| 7 | AREA_Bulletin2 | 公告栏附属区域 | (44, 10, 6, 3) |
| 8 | AREA_JunimoHut | Junimo 小屋 | (22, 4, 13, 9) |

### 1.6 Junimo Note（卷轴）位置

来自 `CommunityCenter.getNotePosition()`：

| Area | 坐标 (tile) | 图层 |
|------|------------|------|
| 0 (Pantry) | (14, 5) | Buildings |
| 1 (Crafts Room) | (14, 23) | Buildings |
| 2 (Fish Tank) | (40, 10) | Buildings |
| 3 (Boiler Room) | (63, 14) | Buildings |
| 4 (Vault) | (55, 6) | Buildings |
| 5 (Bulletin) | (46, 11) | Front |

---

## 二、房间解锁逻辑

来自 `CommunityCenter.shouldNoteAppearInArea(int area)`：

```
Area 1 (Crafts Room)    → 初始即可见（无条件）
Area 0 (Pantry)         → 已完成 Bundle 总数 > 0
Area 2 (Fish Tank)      → 已完成 Bundle 总数 > 0
Area 3 (Boiler Room)    → 已完成 Bundle 总数 > 1
Area 5 (Bulletin Board) → 已完成 Bundle 总数 > 2
Area 4 (Vault)          → 已完成 Bundle 总数 > 3
Area 6 (Abandoned Joja) → 需要已看过事件 191393
```

注意：这里的"已完成 Bundle 总数"是 `numberOfCompleteBundles()`，判断逻辑是遍历 `bundles` 字典，检查每个 bundle 的 bool 数组是否全 true。

---

## 三、房间完成判定与奖励

### 3.1 Bundle 完成判定

来自 `JunimoNoteMenu.checkIfBundleIsComplete()`：

```
已填充的材料槽数 ≥ bundle.numberOfIngredientSlots → 该 Bundle 完成
```

当一个 Bundle 完成时：
1. 标记 `bundles[bundleIndex]` 所有槽位为 true
2. 标记 `bundleRewards[bundleIndex] = true`
3. 调用 `checkForNewJunimoNotes()` 看是否解锁新房间
4. 播放屏幕切换动画
5. 全服广播聊天消息

### 3.2 房间完成判定

当该房间内所有 Bundle 都完成后：
1. 调用 `markAreaAsComplete(whichArea)` → `areasComplete[area] = true`
2. 调用 `areaCompleteReward(whichArea)` → 发放邮件奖励

### 3.3 区域完成奖励邮件

来自 `CommunityCenter.doAreaCompleteReward()`：

| Area | 邮件标记 | 游戏含义 |
|------|---------|---------|
| 0 (Pantry) | `ccPantry` | 温室修复 |
| 1 (Crafts Room) | `ccCraftsRoom` | 采石场桥修复 |
| 2 (Fish Tank) | `ccFishTank` | 潘妮闪光淘盘 |
| 3 (Boiler Room) | `ccBoilerRoom` | 矿车系统修复 |
| 4 (Vault) | `ccVault` | 沙漠巴士修复 |
| 5 (Bulletin) | `ccBulletin` + `ccBulletinThankYou` | 友谊奖励 |

### 3.4 所有房间完成

`areAllAreasComplete()` 检查 `areasComplete[0..5]` 全为 true 后：
- 地图切换为 `CommunityCenter_Refurbished`
- 触发 Junimo 告别舞蹈
- 加载 Junimo 小屋（Area 8）
- 添加鱼缸家具

---

## 四、物品匹配逻辑

### 4.1 BundleIngredientDescription（来自 `BundleIngredientDescription.cs`）

```csharp
public struct BundleIngredientDescription {
    string id;           // 物品 ID（字符串），若为 category 则为 null
    string preservesId;  // 加工品原料 ID（腌制/果酱等）
    int? category;       // 负数：类别匹配（-4=鱼, -5=蛋, -6=牛奶...）
    int stack;           // 需求数量
    int quality;         // 最低品质 (0=普通, 1=银, 2=金, 3=铱)
    bool completed;      // 是否已完成
}
```

构造时：如果物品 ID 是负数 → 视为 `category`，否则视为具体物品 `id`。

### 4.2 物品匹配（来自 `Bundle.IsValidItemForThisIngredientDescription()`）

```
1. item == null 或 ingredient 已完成 → false
2. ingredient.quality > item.Quality → false
3. 如果 ingredient.preservesId != null → 检查加工品匹配
4. 如果 ingredient.category 有值 → 检查 item.Category == category
5. 否则 → 检查 item ID 精确匹配 ingredient.id
```

### 4.3 金币 Bundle（Vault）

金库的材料是 `-1 金额 金额`，-1 表示金币。UI 不显示材料槽，而是显示一个购买按钮，直接扣钱完成。来自 `JunimoNoteMenu.setUpBundleSpecificPage()`：当 `whichArea == 4` 时，不创建 ingredientSlots，而是创建 `purchaseButton`。

---

## 五、部分捐赠（Partial Donation）

来自 `JunimoNoteMenu.HandlePartialDonation()`：

当玩家背包中的物品数量不够一个材料槽的需求，但全背包凑起来够的情况下，可以**分多次**放入同一个材料槽。

关键数据：
- `partialDonationItem` — 当前正在分次捐赠的物品
- `partialDonationComponents` — 已贡献的物品列表（用于退回）
- `currentPartialIngredientDescriptionIndex` — 正在填的材料槽索引

流程：
1. `CanBePartiallyOrFullyDonated()` 检查背包+手持总量 ≥ 需求
2. 每次放入物品时，`partialDonationItem.Stack` 累加
3. 当 `partialDonationItem.Stack ≥ 需求` 时，自动调用 `tryToDepositThisItem()` 完成该槽
4. 可以右键逐个取回部分捐赠的物品
5. 关闭 Bundle 页面时自动退回所有部分捐赠

---

## 六、修复动画/演出系统

来自 `CommunityCenter.UpdateWhenCurrentLocation()`，4 阶段状态机：

### Phase 0: firstPause

- 持续 1000ms
- 冻结玩家控制
- 如果玩家在社区中心，朝向下方、跳跃、抖动、显示惊讶帧

### Phase 1: junimoAppear

- 持续 3000ms
- 每 tick 40% 概率在该房间矩形内随机位置生成临时 Junimo
- 播放 `tinyWhip` 音效 + 白色星星粒子
- 清除灯光图层

### Phase 2: junimoDance（restore 准备）

- 屏幕白光渐亮（`screenGlowOnce`）
- 播放 `wind` 音效，音量随白光强度增加
- 玩家抖动加剧
- 显示 Junimo 文字消息（`getMessageForAreaCompletion()`）
  - 消息内容根据已完成房间数 1-6 不同
- 等待 5200ms

### Phase 3: restore

- 移除所有临时 Junimo
- 调用 `loadArea(restoreAreaIndex)` — **关键：覆盖方块**
- 白屏退去
- 播放 `wand` 音效 + `junimoStarSong` 音乐
- 闪白 `flashAlpha = 1`
- 玩家停止抖动
- 延迟后：Junimo 返回小屋取星星 → 贴到牌匾上

### loadArea() 方块覆盖逻辑

来自 `CommunityCenter.loadArea()`：
```
1. 获取房间矩形区域 getAreaBounds(area)
2. 加载修复版地图 Maps\CommunityCenter_Refurbished
3. 用 ApplyMapOverride() 将修复版地图的该区域覆盖到当前地图
4. 遍历覆盖区域，处理灯光属性
5. 按概率在空位添加白色粒子效果
6. 特殊处理：
   - Area 5 → 同时加载 Area 7（公告栏附属区域）
   - Area 2 → 添加鱼缸家具
```

---

## 七、Junimo 告别舞蹈

当所有 6 个房间完成后，`_isWatchingJunimoGoodbye = true`：

1. `junimoGoodbyeDance()` — 6 个 Junimo 放到固定位置 (23-28, 11-12)
2. 镜头移动到玩家位置，5 秒后开始
3. `startGoodbyeDance()` — 所有 Junimo 说再见
4. `endGoodbyeDance()` — 所有 Junimo 逐渐消失
5. 3.6 秒后 `loadJunimoHut()` — 加载 Area 8（Junimo 小屋），播放 `wand` 音效 + 白闪
6. 显示全局消息："祝尼魔们回来了"

---

## 八、Junimo 实体（来自 `Junimo.cs`）

### 8.1 基本属性

- 继承 `NPC`，但 `IsVillager = false`
- 精灵图：`Characters/Junimo`，16×16 像素
- 缩放：0.75
- 速度：3
- 可穿透农夫
- 不呼吸动画

### 8.2 颜色系统

场景中常驻 Junimo 的颜色由 `friendly` 状态和 `whichArea` 决定。

临时 Junimo（修复演出中生成的）有随机颜色：
- 99% 概率：8 种基色之一（Red, Goldenrod, Yellow, Lime, Cyan-ish, Blue, MediumPurple, Salmon）
- 1% 中的 1%：彩虹色（颜色随时间变化）

### 8.3 行为

- `holdingStar` — 举着一颗星星（完成房间后去牌匾贴星）
- `holdingBundle` — 举着一个彩色 Bundle 球（完成 Bundle 后跑回小屋）
- `temporaryJunimo` — 临时生成的（修复演出用，不存档）
- `sayingGoodbye` — 告别舞蹈中，头顶显示心形
- `stayPut` — 不移动（卷轴旁站立）
- 路径导航到目标 → 寻路（使用 `PathFinder`）

### 8.4 渲染特效

- 当 `holdingStar` 为 true 时，头上画一颗旋转的黄色星星
- 当 `holdingBundle` 为 true 时，头上画一个颜色匹配的 Bundle 球
- 透明度 `alpha` 可渐变（`fadeAway()`, `fadeBack()`）
- `sayingGoodbye` 时头顶交替显示心形和文字气泡

---

## 九、漏领奖励系统

来自 `CommunityCenter.checkForMissedRewards()`：

当某个房间完成后，如果玩家没有从 UI 中领走 Bundle 的单独奖品：
1. 遍历所有 `bundleRewards`，找到已标记但未领取的
2. 按区域聚合所有漏领奖品
3. 放入 `missedRewardsChest`（一个隐形箱子）
4. 在社区中心大厅 (22, 10) 显示一个箱子 Tile
5. 玩家点击该 Tile → 打开 `ItemGrabMenu` 领取

---

## 十、星星牌匾

来自 `CommunityCenter.draw()`：

在社区中心大厅绘制 0-6 颗星星，每颗星位置固定：

| 星星 # | 世界坐标 |
|--------|---------|
| 0 | (2136, 324) |
| 1 | (2136, 364) |
| 2 | (2096, 384) |
| 3 | (2056, 364) |
| 4 | (2056, 324) |
| 5 | (2096, 308) |

使用鼠标光标纹理中的 7×7 像素星星图标，4 倍缩放渲染。

---

## 十一、JunimoNoteMenu GUI 布局

来自 `JunimoNoteMenu.cs`：

### 11.1 两页模式

1. **房间总览页**（`specificBundlePage = false`）：
   - 背景纹理：`JunimoNote.png` 的 (0,0,320,180)，4 倍缩放 = 1280×720
   - 显示该房间所有 Bundle 图标（动画球体，颜色对应）
   - 房间名称居中显示
   - 如有可领奖励，显示礼物图标（`presentButton`）
   - 如从游戏菜单打开，显示左右翻页按钮（`areaNextButton`, `areaBackButton`）

2. **Bundle 详情页**（`specificBundlePage = true`）：
   - 背景纹理：`JunimoNote.png` 的 (320,0,320,180)，4 倍缩放
   - 上方：Bundle 图标 + 名称
   - 中间行（Y=364 区域）：所需材料图标列表
   - 下方行（Y=540 区域）：材料提交槽位
   - 再下方：玩家背包（`InventoryMenu`）
   - 可接受物品高亮显示

### 11.2 Bundle 球体位置

来自 `getBundleLocationFromNumber()`：

| Bundle 序号 | 偏移 (X, Y) |
|------------|-------------|
| 0 | (592, 136) |
| 1 | (392, 384) |
| 2 | (784, 388) |
| 3 | (304, 252) |
| 4 | (892, 252) |
| 5 | (588, 276) |
| 6 | (588, 380) |
| 7 | (440, 164) |
| 8 | (776, 164) |

### 11.3 Bundle 球体动画

来自 `Bundle.cs` 构造函数：

- 纹理：`JunimoNote.png`
- 源矩形：`(bundleColor × 256 % 512, 244 + bundleColor × 256 / 512 × 16, 16, 16)`
- 3 帧动画，每帧 70ms，来回播放（`pingPong = true`）
- 完成后：播放 15 帧完成动画
- 当前季节名包含在 Bundle 名中时自动抖动

### 11.4 材料槽布局

来自 `addRectangleRowsToList()`，根据材料数量自动排列：

| 数量 | 布局 |
|------|------|
| 1-4 | 单行居中 |
| 5 | 上 3 下 2 |
| 6 | 上 3 下 3 |
| 7 | 上 4 下 3 |
| 8 | 上 4 下 4 |
| 9 | 上 5 下 4 |
| 10 | 上 5 下 5 |
| 11 | 上 6 下 5 |
| 12 | 上 6 下 6 |

每个槽位 72×72 像素，间距 12 像素。

### 11.5 巫师加密文字

初次进入社区中心时，所有文字都是加密的（`scrambledText = true`）。收到巫师的信后解锁（`canReadJunimoText` 邮件标记）。未解锁时文字显示为 `"???"`，使用 `junimoText` 字体渲染。

---

## 十二、couldThisIngredienteBeUsedInABundle — 背包高亮

来自 `CommunityCenter.couldThisIngredienteBeUsedInABundle()`：

维护一个 `bundlesIngredientsInfo` 字典：
- Key = 物品的 QualifiedItemId（如 `"(O)24"`）或 Category 字符串（如 `"-4"`）
- Value = `List<List<int>>` → `[bundleIndex, 需求数量, 需求品质]`

检查逻辑：
1. 物品不是大型加工品
2. 按 QualifiedItemId 查找匹配项，品质 ≥ 需求品质 → true
3. 按 Category 查找匹配项，品质 ≥ 需求品质 → true

原版用此方法在背包中给可用于 Bundle 的物品画一个特殊高亮框（紫色？）。

---

## 十三、地图版本

原版有 3 张社区中心地图：

| 文件 | 用途 |
|------|------|
| `CommunityCenter_Ruins.tmx` | 初始废墟版（默认加载） |
| `CommunityCenter_Refurbished.tmx` | 完全修复版（逐房间覆盖，或全部完成后整体切换） |
| `CommunityCenter_Joja.tmx` | Joja 路线（成为 Joja 会员后替换） |

地图切换逻辑（来自 `updateMap()` 和 `TransferDataFromSavedLocation()`）：
- Joja 会员：`mapPath = "Maps\CommunityCenter_Joja"`, `warehouse = true`
- 所有房间完成：`mapPath = "Maps\CommunityCenter_Refurbished"`

---

## 十四、MC 模组实现方案

### 14.1 项目现有系统概要

| 系统 | 模式 | 关键类 |
|------|------|--------|
| 室内亚空间 | 星露谷维度远坐标区域 + `.schem` 结构 + `Interaction` 传送门 | `InteriorSubspaceManager` |
| 结构加载 | `.schem` / NBT 格式，`StructureLoader` 解析 | `StructureLoader` |
| 维度 | `stardew_valley` 维度，预烘焙 `.mca` + 分帧 schem 放置 | `StardewValleyMapBootstrap` |
| 动画实体 | GeckoLib (`geckolib-neoforge-1.21.1`) | `StardewNpcEntity`, `BaseCoopAnimalEntity` |
| 数据加载 | `SimpleJsonResourceReloadListener` + 手动 Gson | `NpcDataManager`, `FishingDataManager` |
| 玩家数据 | `PlayerStardewData` + `PlayerDataManager(SavedData)` + NBT | `PlayerStardewDataAPI` |
| 全局数据 | `SavedData` (如 `MuseumDonationData`) | 直接挂在 overworld DataStorage |
| 菜单/GUI | `AbstractContainerMenu` + `AbstractContainerScreen` | `ModMenuTypes`, `ModClientSetup` |
| 网络 | `PacketHandler` 注册 payload | 自有 payload 类 |
| 邮件 | `MailSystem` / `MailService` / `MailRegistry` | 已有系统 |
| 物品映射 | SDV 数字 ID → mod item path（仅烹饪有） | `vanilla_cooking_ingredient_map.json` |
| 方块注册 | `ModBlocks.BLOCKS` DeferredRegister | `ModBlocks` |
| 方块实体 | `ModBlockEntities.BLOCK_ENTITIES` DeferredRegister | `ModBlockEntities` |
| 菜单注册 | `ModMenuTypes.MENU_TYPES` DeferredRegister | `ModMenuTypes` |
| 商店 | `ShopRegistry` 硬编码 | `ShopRegistry` |

### 14.2 包结构

```
com.stardew.craft.communitycenter/
├── CommunityCenterSystem.java                // @EventBusSubscriber, 注册数据加载器+事件
├── data/
│   ├── BundleDataManager.java                // SimpleJsonResourceReloadListener 解析 bundles.json
│   ├── BundleDefinition.java                 // 单个 Bundle 定义 (record)
│   ├── BundleIngredient.java                 // 单个材料需求 (record)
│   ├── BundleAreaDefinition.java             // 房间定义 (record)
│   └── BundleItemResolver.java               // SDV ID → mod ResourceLocation 解析
├── state/
│   ├── CommunityCenterSavedData.java          // extends SavedData, 全局持久化
│   └── CommunityCenterProgress.java           // 运行时进度查询 API
├── area/
│   └── CommunityCenterAreaLoader.java         // 房间局部方块覆盖（废墟→修复）
├── menu/
│   ├── BundleMenu.java                        // extends AbstractContainerMenu
│   └── BundleMenuProvider.java                // 打开菜单的入口逻辑
├── client/
│   └── BundleScreen.java                      // extends AbstractContainerScreen, 献祭 UI
├── block/
│   ├── JunimoNoteBlock.java                   // 祝尼魔卷轴方块
│   ├── JunimoNoteBlockEntity.java             // 卷轴方块实体（GeckoLib 动画）
│   └── StarPlaqueBlock.java                   // 星星牌匾方块（0-6 星状态）
├── entity/
│   ├── JunimoEntity.java                      // 祝尼魔实体（GeckoLib）
│   ├── JunimoGeoModel.java                    // GeckoLib 模型
│   └── JunimoGeoRenderer.java                 // GeckoLib 渲染器
├── cutscene/
│   └── AreaRestoreCutscene.java               // 修复演出状态机（4 阶段）
└── network/
    ├── BundleDepositPayload.java              // C→S: 提交物品
    ├── BundleSyncPayload.java                 // S→C: 同步进度
    └── RestoreCutscenePayload.java            // S→C: 白屏/冻结/音效
```

### 14.3 数据模型

#### BundleDefinition

```java
public record BundleDefinition(
    int bundleId,                          // 0, 1, 2 ... 36
    String areaName,                       // "Pantry", "Crafts Room", ...
    int areaId,                            // 0-6
    String internalName,                   // "Spring Crops"
    String displayNameKey,                 // 国际化 key
    String rewardString,                   // 奖励描述（解析为 mod 物品）
    List<BundleIngredient> ingredients,     // 材料列表
    int color,                             // 0-6
    int requiredCount                      // 需完成的材料数
) {}
```

#### BundleIngredient

```java
public record BundleIngredient(
    @Nullable String itemId,              // mod 物品 ResourceLocation，金币 bundle 为 null
    int category,                          // 负数=类别匹配（-1=金币），0=指定物品
    int stack,                             // 需求数量
    int quality                            // 最低品质 (0=普通, 1=银, 2=金, 3=铱)
) {}
```

#### CommunityCenterSavedData

```java
public class CommunityCenterSavedData extends SavedData {
    // bundleId → boolean[]（每个材料槽位的完成状态）
    private final Map<Integer, boolean[]> bundles;
    // bundleId → 奖励是否可领取
    private final Map<Integer, Boolean> bundleRewards;
    // areaId → 房间是否已完成
    private final boolean[] areasComplete = new boolean[7];
    // 修复演出状态
    private int restoreAreaIndex = -1;
    private int restorePhase = -1;
    private int restoreTimer = 0;
}
```

### 14.4 室内空间方案

社区中心室内需要加入 `InteriorSubspaceManager` 的结构清单：

```java
// 在 InteriorSubspaceManager 中新增：
private static final String CC_RUINS_STRUCTURE_PATH = "data/stardewcraft/structures/interior/community_center_ruins.schem";
private static final BlockPos CC_ORIGIN = new BlockPos(18816, 70, 17088);  // 新的远坐标，不与现有室内冲突
private static final BlockPos CC_INDOOR_SPAWN_OFFSET = new BlockPos(32, 1, 12);
private static final BlockPos CC_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(31, 1, 12);
```

#### 需制作的 .schem 文件

| 文件 | 用途 | 说明 |
|------|------|------|
| `community_center_ruins.schem` | 废墟版室内 | 初始加载，包含所有 6 个房间区域 |
| `cc_area_0_pantry.schem` | 食品储藏室修复片段 | 覆盖指定区域方块 |
| `cc_area_1_crafts.schem` | 工艺室修复片段 | 同上 |
| `cc_area_2_fishtank.schem` | 鱼缸修复片段 | 同上 |
| `cc_area_3_boiler.schem` | 锅炉房修复片段 | 同上 |
| `cc_area_4_vault.schem` | 金库修复片段 | 同上 |
| `cc_area_5_bulletin.schem` | 公告栏修复片段 | 同上 |
| `cc_area_8_junimohut.schem` | Junimo 小屋（全部完成后） | 覆盖大厅中央区域 |

#### 区域覆盖机制 — CommunityCenterAreaLoader

项目目前**不存在**区域局部覆盖机制。需要新建：

```java
public final class CommunityCenterAreaLoader {
    // 房间修复 schem 片段路径
    private static final Map<Integer, String> AREA_REPAIR_SCHEMATICS = Map.of(
        0, "data/stardewcraft/structures/interior/cc_area_0_pantry.schem",
        1, "data/stardewcraft/structures/interior/cc_area_1_crafts.schem",
        // ...
    );
    // 每个片段相对于 CC_ORIGIN 的偏移
    private static final Map<Integer, BlockPos> AREA_OFFSETS = Map.of(
        0, new BlockPos(0, 0, 0),     // Pantry 在结构左上角
        1, new BlockPos(0, 0, 12),    // Crafts Room 在 Pantry 下方
        // ...具体偏移需根据 schem 设计确定
    );

    public static void loadArea(ServerLevel level, int areaId) {
        String path = AREA_REPAIR_SCHEMATICS.get(areaId);
        BlockPos target = CC_ORIGIN.offset(AREA_OFFSETS.get(areaId));
        StructureLoader.loadAndPlace(level, path, target);
    }
}
```

### 14.5 Junimo 实体方案

#### 模型与动画

使用 GeckoLib，参考项目现有 `BaseCoopAnimalEntity` 模式：

| 文件 | 用途 |
|------|------|
| `assets/stardewcraft/geo/junimo.geo.json` | 3D 模型（球体+星形天线） |
| `assets/stardewcraft/animations/junimo.animation.json` | 动画（idle/walk/jump/dance/fadeAway） |
| `assets/stardewcraft/textures/entity/junimo.png` | 纹理（参考原版 `Characters/Junimo.png`） |

#### 实体类

```java
public class JunimoEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_AREA = ...;
    private static final EntityDataAccessor<Integer> DATA_COLOR = ...;
    private static final EntityDataAccessor<Boolean> DATA_HOLDING_STAR = ...;
    private static final EntityDataAccessor<Boolean> DATA_TEMPORARY = ...;
    
    // GeckoLib 动画控制器
    // idle → walk → dance → fadeAway
}
```

#### 颜色

模型使用白色纹理 + 运行时 tint 着色（GeckoLib 支持 `RenderProvider` 颜色覆盖），或为每种颜色准备单独纹理。

### 14.6 JunimoNote 卷轴方块

每个待献祭房间放一个发光互动方块：

```java
public class JunimoNoteBlock extends BaseEntityBlock {
    // 右键交互 → 打开 BundleMenu
    // 关联的 BlockEntity 存储 areaId
    // 发光 + 粒子效果（彩色火焰粒子）
    // 房间完成后该方块被移除（由 CommunityCenterAreaLoader.loadArea() 覆盖掉）
}
```

GeckoLib 动画可选：静态方块 + 粒子效果可能更简单，效果也足够。

### 14.7 修复演出

```java
public final class AreaRestoreCutscene {
    // 服务端 tick 驱动的状态机
    // Phase 0: 冻结玩家 (50 ticks ≈ 1秒)
    //   → 发 RestoreCutscenePayload(FREEZE) 给客户端
    // Phase 1: 生成临时 Junimo (150 ticks ≈ 3秒)
    //   → 在房间区域内随机生成 JunimoEntity(temporary=true)
    // Phase 2: 白屏渐亮 (260 ticks ≈ 5.2秒)
    //   → 发 RestoreCutscenePayload(WHITE_FLASH) 给客户端
    //   → 播放风声音效（音量渐增）
    // Phase 3: 覆盖方块 + 结束 (100 ticks ≈ 2秒)
    //   → CommunityCenterAreaLoader.loadArea()
    //   → 移除所有临时 Junimo
    //   → 播放 wand 音效
    //   → 发 RestoreCutscenePayload(FLASH_END) 给客户端
    //   → 解冻玩家
}
```

客户端收到 payload 后：
- `FREEZE` → 禁用移动输入
- `WHITE_FLASH` → `GuiGraphics.fill(0, 0, w, h, 0xFFFFFFFF)` 通过 alpha 渐变
- `FLASH_END` → flash alpha = 1 快速衰减

### 14.8 GUI 纹理

需要从原版 `JunimoNote.png` 参考制作 MC 版纹理：

| 区域 | 原版纹理位置 | 用途 |
|------|------------|------|
| (0,0,320,180) | 房间总览页背景 | 羊皮纸风格带叶子装饰 |
| (320,0,320,180) | Bundle 详情页背景 | 同上但布局不同 |
| (color*256%512, 244+color*256/512*16, 16,16) | Bundle 球体动画帧 | 7 种颜色 × 3+15 帧 |
| (512,244,18,18) | 空材料槽 | 圆形空位 |
| (530,244,18,18) | 已填充材料槽 | 圆形填充态 |
| (530,262,18,18) | 悬停态材料槽 | 高亮态 |
| (517,286,65,20) | 购买按钮（Vault 用） | 金币图标+文字 |
| (517,266,4,17) | Bundle 名称背景左端 | 3 段式横条 |
| (520,266,1,17) | Bundle 名称背景中段 | 可拉伸 |
| (524,266,4,17) | Bundle 名称背景右端 | 3 段式横条 |
| (548,262,18,20) | 礼物图标动画 | 4 帧 |

### 14.9 物品映射

现有 `vanilla_cooking_ingredient_map.json` 覆盖了大部分 Bundle 所需物品。Bundle 系统的 `BundleItemResolver` 需要：
1. 复用此映射
2. 补充缺失物品（大型加工品 BO、戒指 R 等）
3. 支持 category 匹配（负数 ID → `IStardewItem.getItemCategory()`）
4. 支持金币类型（`-1` → 扣钱而非物品）

### 14.10 与现有系统的集成

| 集成点 | 操作 | 文件 |
|-------|------|------|
| `InteriorSubspaceManager` | 注册社区中心室内结构 + 传送门 | 现有文件修改 |
| `ModBlocks` | 注册 `JUNIMO_NOTE_BLOCK`, `STAR_PLAQUE_BLOCK` | 现有文件修改 |
| `ModBlockEntities` | 注册 `JUNIMO_NOTE` block entity | 现有文件修改 |
| `ModEntities` | 注册 `JUNIMO` entity type | 现有文件修改 |
| `ModMenuTypes` | 注册 `BUNDLE_MENU` | 现有文件修改 |
| `ModClientSetup` | 注册 `BundleScreen` | 现有文件修改 |
| `PacketHandler` | 注册 `BundleDepositPayload`, `BundleSyncPayload`, `RestoreCutscenePayload` | 现有文件修改 |
| `MailRegistry` | 添加 6 封区域奖励邮件 | 现有文件修改 |
| `StardewCraft` 构造函数 | 确保所有注册器 `.register(modEventBus)` | 如果新增了 DeferredRegister |

### 14.11 GUI 纹理资产方案

**核心原则**：所有 UI 界面必须与星露谷物语原版保证一模一样，不得有任何视觉差异。所有 GUI 纹理直接从原版 `JunimoNote.png` 截取使用，不做任何修改。

#### 纹理文件

| 源文件 | 项目路径 | 说明 |
|-------|---------|------|
| `源文件/Content/LooseSprites/JunimoNote.png` | `assets/stardewcraft/textures/gui/junimo_note.png` | 完整纹理图集，直接复制，原样使用 |

原版纹理图集是一张包含所有 Bundle UI 元素的 sprite sheet，所有 UV 坐标严格对照原版 `JunimoNoteMenu.cs` 和 `Bundle.cs` 中的硬编码值。

#### UV 坐标映射表（来自原版源码，精确到像素）

**页面背景：**

| 元素 | 源矩形 (x, y, w, h) | 缩放 | 显示尺寸 | 代码位置 |
|------|---------------------|------|---------|---------|
| 房间总览页背景 | (0, 0, 320, 180) | ×4 | 1280×720 | `draw()` |
| Bundle 详情页背景 | (320, 0, 320, 180) | ×4 | 1280×720 | `draw()` |

**Bundle 球体动画：**

| 元素 | 源矩形计算方式 | 帧数 | 间隔 |
|------|--------------|------|------|
| 球体帧 | `(bundleColor × 256 % 512, 244 + bundleColor × 256 / 512 × 16, 16, 16)` | 3 帧 pingPong | 70ms |
| 完成动画 | 同起始位置向右偏移 | 15 帧 holdLastFrame | 50ms |

颜色索引 0-6 对应 7 种颜色球体，每种占 256px 宽条带，交替排列在 Y=244 和 Y=260 行。

**Bundle 详情页大图标：**

| 元素 | 源矩形计算方式 | 说明 |
|------|--------------|------|
| Bundle 详情图标 | `(bundleIndex × 32 % textureWidth, 180 + 32 × (bundleIndex × 32 / textureWidth), 32, 32)` | 每个 Bundle 独立 32×32 图标 |

**材料槽：**

| 状态 | 源矩形 (x, y, w, h) | 用途 |
|------|---------------------|------|
| 空槽位 | (512, 244, 18, 18) | 圆形空位 |
| 已填充（存放动画起始） | (530, 244, 18, 18) | 存放物品后 6 帧动画起点 |
| 悬停态 | (530, 262, 18, 18) | 鼠标悬停时的高亮 |

存放动画：从 (530, 244) 开始，6 帧，每帧 50ms，`holdLastFrame = true`，结束音效 `"cowboy_monsterhit"`。

**Bundle 名称标签（三段式横条）：**

| 部分 | 源矩形 (x, y, w, h) | 渲染方式 |
|------|---------------------|---------|
| 左端 | (517, 266, 4, 17) | 固定 ×4 |
| 中段（可拉伸） | (520, 266, 1, 17) | 水平拉伸至文字宽度 |
| 右端 | (524, 266, 4, 17) | 固定 ×4 |

文字渲染在标签上方，使用 `dialogueFont`，带 3 层阴影（偏移 (2,2)、(0,2)、(2,0)），主色 `textColor * 0.9f`。

**其他 UI 元素：**

| 元素 | 源矩形 (x, y, w, h) | 用途 |
|------|---------------------|------|
| 购买按钮 (Vault) | (517, 286, 65, 20) | 金库房间的金币购买按钮 |
| 礼物图标 | (548, 262, 18, 20) | 4 帧动画，70ms，可领奖时显示 |
| 翻页箭头 (→) | Cursors (365, 495, 12, 11) | 从游戏菜单打开时的区域切换 |
| 翻页箭头 (←) | Cursors (352, 495, 12, 11) | 同上 |
| 返回按钮 | Cursors tileSheet #44 | 详情页返回总览 |

### 14.12 SDV → MC 渲染逐行复刻指南

**核心原则**：原版 `JunimoNoteMenu.cs` 的 `draw()` 方法（约 120 行渲染代码）必须逐行翻译为 MC `BundleScreen.render()` 方法，保证每一个 `SpriteBatch.Draw()` 调用都有对应的 `GuiGraphics.blit()` 调用，使用完全相同的 UV 坐标。

#### 14.12.1 坐标系统映射

| SDV 概念 | SDV 值 | MC 对应 | 转换方式 |
|---------|-------|---------|---------|
| `pixelZoom` | 固定 `4` | 动态 `guiScale` | `s4() = 4.0f / guiScale` |
| 菜单基础尺寸 | 320×180 sprite px | 同值 | — |
| 菜单显示尺寸 | 1280×720 screen px | `ui(1280) × ui(720)` | `ui(n) = Math.round(n / guiScale)` |
| 菜单定位 | `viewport.W/2 - 640, viewport.H/2 - 360` | `(width - ui(1280))/2, (height - ui(720))/2` | MC Screen 的 `width/height` 已经是 GUI 单位 |

关键：SDV 所有坐标都是**屏幕像素**（已乘 4），MC 的 `GuiGraphics` 坐标是 **GUI 单位**（会被 guiScale 再放大到屏幕像素）。因此 SDV 坐标必须除以 `guiScale` 才能在 MC 中产生相同的屏幕像素位置。

#### 14.12.2 SpriteBatch.Draw → GuiGraphics.blit 翻译规则

**SDV 原版调用模式：**
```csharp
b.Draw(noteTexture,
    new Vector2(xPositionOnScreen, yPositionOnScreen),  // 目标位置（屏幕像素）
    new Rectangle(0, 0, 320, 180),                       // 源矩形（纹理像素）
    Color.White,                                         // 颜色/透明度
    0f,                                                  // 旋转
    Vector2.Zero,                                        // 原点
    4f,                                                  // 缩放
    SpriteEffects.None,                                  // 翻转
    0.1f);                                               // 图层深度
```

**MC 逐行翻译：**
```java
g.pose().pushPose();
g.pose().translate(menuX, menuY, 0.1f);  // 目标位置 (GUI单位) + 图层深度→Z轴
g.pose().scale(s4(), s4(), 1.0f);         // SDV的 scale 4 → MC动态缩放
g.blit(JUNIMO_NOTE_TEXTURE,
    0, 0,           // pose 已设置位置，这里从 0,0 开始
    0, 0,           // 源 UV (u, v) = SDV sourceRect.X, .Y
    320, 180,        // 源尺寸 (w, h) = SDV sourceRect.Width, .Height
    TEXTURE_WIDTH, TEXTURE_HEIGHT);  // 纹理图集总尺寸
g.pose().popPose();
```

**或使用项目已有工具方法（推荐）：**
```java
// 对于 Cursors 上的元素
StardewGuiUtil.drawFromCursors(g, x, y, u, v, w, h, s4());

// 对于 JunimoNote 纹理上的元素，需新增类似工具方法
drawFromJunimoNote(g, x, y, u, v, w, h, s4());
```

#### 14.12.3 颜色/透明度映射

| SDV 用法 | MC 翻译 | 注意事项 |
|---------|---------|---------|
| `Color.White` | 不设颜色（默认白） | — |
| `Color.White * 0.5f` | `g.setColor(1, 1, 1, 0.5f)` | **用后必须** `g.setColor(1,1,1,1)` 还原 |
| `Color.LightGray * 0.5f` | `g.setColor(0.83f, 0.83f, 0.83f, 0.5f)` | 从游戏菜单打开时的槽位半透明 |
| `Game1.textShadowColor` | `0x3B3022` (SDV 暗色) | 文字阴影色 |
| `Game1.textColor * 0.9f` | `0x5B5045` alpha 0.9 | 主文字色 |

#### 14.12.4 文字渲染映射

SDV 使用 `SpriteText` 和 `Game1.dialogueFont` 两种字体：

| SDV 方法 | 用途 | MC 翻译 |
|---------|------|---------|
| `SpriteText.drawStringHorizontallyCenteredAt(b, text, x, y)` | 房间标题 | `g.drawCenteredString(font, text, x, y, color)` |
| `SpriteText.drawStringWithScrollCenteredAt(b, text, x, y)` | 底部奖励名称（带卷轴背景） | 需自绘卷轴背景 + `g.drawCenteredString()` |
| `b.DrawString(dialogueFont, text, pos + shadow, shadowColor)` ×3 + 主色 | Bundle名称（3层阴影） | `g.drawString(font, text, x+2, y+2, shadowColor)` ×3 + 主色 |
| `SpriteText.drawString(b, text, x, y, ..., junimoText: true)` | 加密祝尼魔文字 | 自行实现乱码字体或直接显示 `"???"` |

#### 14.12.5 draw() 方法完整翻译清单

**房间总览页（`specificBundlePage = false`）：**

```
原版绘制顺序 → MC 对应
1. 背景暗化                    → g.fill(0, 0, w, h, 0x80000000)
2. noteTexture (0,0,320,180)  → blit 房间总览背景
3. 房间名称居中                → drawCenteredString()
4. (若加密) 祝尼魔文字 + return → 显示 "???" 乱码文本
5. 遍历 bundles → bundle.draw() → 每个 Bundle 球体 sprite 的当前帧
6. presentButton?.draw()       → 礼物图标动画
7. tempSprites 遍历绘制        → 临时粒子效果
8. 翻页箭头 (fromGameMenu)     → drawFromCursors 箭头
9. 底部奖励名称卷轴             → 自绘卷轴 + 文字
10. 关闭按钮                   → 右上角 X
11. 鼠标光标                   → MC 自动处理
```

**Bundle 详情页（`specificBundlePage = true`）：**

```
1. noteTexture (320,0,320,180)  → blit 详情页背景
2. Bundle 详情大图标 (32×32)     → blit 大图标
3. Bundle 名称标签（三段式）     → 左端 + 中段拉伸 + 右端 + 文字×4层
4. 返回按钮                     → drawFromCursors
5. 购买按钮 (Vault 时)          → blit 购买按钮 + 金币显示
6. tempSprites                  → 存放动画粒子
7. ingredientSlots 遍历         → 材料槽位（空/填充/悬停态）
   - 部分捐赠时非当前槽 alpha=0.25
   - 物品图标居中绘制 (偏移 4,4)
8. ingredientList 遍历          → 所需材料列表
   - 未完成的画阴影
   - item.drawInMenu() ×4 scale
   - 已完成的 alpha=0.25
9. inventory.draw()             → 玩家背包 36 格
10. 底部奖励名称                → 同总览页
11. 关闭按钮 + 鼠标             → 同上
12. heldItem?.drawInMenu()      → 手持物品跟随鼠标
13. tooltip / hoverText         → drawToolTip / drawHoverText
```

#### 14.12.6 物品绘制差异

SDV 的 `item.drawInMenu()` 直接从物品的 sprite sheet 切片绘制，包含品质星标、数量文字。MC 中：

| SDV | MC | 差异处理 |
|-----|-----|---------|
| `item.drawInMenu(b, pos, scale)` | `g.renderItem(stack, x, y)` + `g.renderItemDecorations(font, stack, x, y)` | MC 会自动画数量和耐久条 |
| 品质星标 (银/金/铱) | MC 无原生品质概念 | 需自绘品质星覆盖层（如果 mod 物品有品质属性） |
| 阴影 `Game1.shadowTexture` | — | 用 `g.setColor()` 压暗或 PoseStack 偏移绘制暗版 |

#### 14.12.7 Bundle 球体动画状态机

原版 `TemporaryAnimatedSprite` 的行为需在 MC 端用 `tick` 计数器模拟：

```java
// 每个 Bundle 的动画状态
private int animFrame = 1;       // 当前帧 (0-2 for idle, 0-14 for completion)
private int animTimer = 0;       // 帧时间累积 (ms)
private boolean pingPong = true; // 来回播放
private boolean animPaused = true;
private float rotation = 0;      // 抖动旋转角
private float maxShake = 0;      // 抖动幅度

// tick 更新
void tickAnimation(float partialTick) {
    if (!animPaused) {
        animTimer += deltaMs;
        if (animTimer >= interval) {
            animTimer = 0;
            advanceFrame();  // pingPong 来回 or holdLastFrame
        }
    }
    // 抖动衰减
    if (maxShake > 0) {
        rotation += (shakeLeft ? -1 : 1) * (PI / 200f);
        if (abs(rotation) >= maxShake) shakeLeft = !shakeLeft;
        maxShake = max(0, maxShake - 0.0007669904f);
    }
}
```

绘制球体时用 `g.pose().mulPose(Axis.ZP.rotation(rotation))` 应用抖动旋转。

### 14.13 实现阶段建议

#### Phase 0：美术准备
- ~~制作 Junimo 3D 模型和 GeckoLib 动画~~ ✅ 已完成
- 复制 `JunimoNote.png` 到 `textures/gui/junimo_note.png`（直接使用原版资产）
- 制作 `community_center_ruins.schem`（废墟版室内）
- 制作 6 个修复片段 schem

#### Phase 1：数据层 + 持久化
- `BundleDataManager` + `BundleDefinition` + JSON 数据文件
- `BundleItemResolver`（SDV ID → mod 物品映射）
- `CommunityCenterSavedData`
- `CommunityCenterProgress` API

#### Phase 2：室内空间 + 方块
- `InteriorSubspaceManager` 注册社区中心
- `CommunityCenterAreaLoader` 局部覆盖系统
- `JunimoNoteBlock` + `BlockEntity`
- `StarPlaqueBlock`

#### Phase 3：GUI（逐行复刻原版 JunimoNoteMenu.cs）
- `BundleMenu` + `BundleScreen`（房间总览+详情两页）
- 物品匹配逻辑（精确复刻 `IsValidItemForThisIngredientDescription`）
- 金库购买逻辑
- Bundle 球体动画状态机
- 材料槽交互（拖放/Shift+点击/部分捐赠）
- 三段式名称标签 + 3 层文字阴影
- 部分捐赠支持

#### Phase 4：实体 + 演出
- ~~`JunimoEntity`（GeckoLib 动画）~~ ✅ 已完成
- `AreaRestoreCutscene` 状态机
- 网络包（白屏/冻结/音效）
- 告别舞蹈序列

#### Phase 5：集成 + polish
- 邮件奖励
- 背包高亮
- 漏领奖励箱子
- 音效
- 巫师加密文字解锁流程
