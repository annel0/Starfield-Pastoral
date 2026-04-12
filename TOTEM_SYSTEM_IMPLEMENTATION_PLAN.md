# 传送图腾系统 - 完整实现规划

## 一、系统概述

严格复刻星露谷物语的传送图腾系统，移植到 Minecraft（NeoForge 1.21）中。

### 组件清单

| # | 组件 | 文件路径 | 说明 |
|---|------|---------|------|
| 1 | `TotemType` 枚举 | `block/utility/totem/TotemType.java` | Farm/Mountain/Beach 三种类型定义 |
| 2 | `TotemPoleBlock` | `block/utility/totem/TotemPoleBlock.java` | 图腾柱方块（3种，共享一个类） |
| 3 | `TotemPoleBlockEntity` | `blockentity/TotemPoleBlockEntity.java` | 存储名称、ID、激活状态、类型 |
| 4 | `TotemPoleTracker` | `totem/TotemPoleTracker.java` | SavedData：全局图腾柱注册表（ID→位置+名称） |
| 5 | `TeleportTotemItem` | `item/totem/TeleportTotemItem.java` | 消耗品传送图腾（3种，共享一个类） |
| 6 | `RainTotemItem` | `item/totem/RainTotemItem.java` | 求雨图腾 |
| 7 | `TotemPoleBlockEntityRenderer` | `client/render/TotemPoleBlockEntityRenderer.java` | 渲染柱体 + 浮动文字 |
| 8 | `TotemNamingScreen` | `client/gui/TotemNamingScreen.java` | 完全复刻SDV动物命名GUI |
| 9 | `OpenTotemNamingScreenPayload` | `network/payload/OpenTotemNamingScreenPayload.java` | S→C：打开命名GUI |
| 10 | `TotemNamingSubmitPayload` | `network/payload/TotemNamingSubmitPayload.java` | C→S：提交名称 |
| 11 | `TotemBindPayload` | `network/payload/TotemBindPayload.java` | C→S：绑定图腾到柱 |
| 12 | `SystemTotemManager` | `totem/SystemTotemManager.java` | 放置系统默认图腾柱（ID 0） |
| 13 | 注册项 | `ModBlocks`/`ModItems`/`ModBlockEntities`/`ModClientSetup`/`PacketHandler` | 注册所有新增内容 |
| 14 | 模型资源 | `resources/assets/stardewcraft/models/block/utility/totem_pole_*.json` | 从tmp_models复制并调整 |
| 15 | 纹理资源 | `resources/assets/stardewcraft/textures/block/utility/totem_pole_*.png` | activated/deactivated |
| 16 | 音效注册 | `ModSounds` + `sounds.json` | warrior, wand, rainsound, thunder |
| 17 | 本地化 | `en_us.json` + `zh_cn.json` | 所有方块/物品名称 |

---

## 二、TotemType 枚举

```java
public enum TotemType {
    FARM("farm", 0x55FF55, "农场"),              // 绿色文字
    MOUNTAIN("mountain", 0xFF5555, "山区"),       // 红色文字  
    BEACH("beach", 0x5555FF, "海滩");            // 蓝色文字
    
    String id;
    int textColor;            // 浮动文字颜色
    String displayNameKey;    // 本地化键
}
```

---

## 三、TotemPoleBlock

### 继承关系
`MapUtilityStaticBlock` (含 PART + FACING) → `TotemPoleBlock` implements `EntityBlock`

### BlockState 属性
- `PART` / `FACING`（继承自父类）
- `BooleanProperty ACTIVATED`（是否激活，影响纹理和亮度）

### 交互逻辑

| 操作 | 条件 | 行为 |
|------|------|------|
| 空手右键 | 非Shift | 打开命名GUI（发 `OpenTotemNamingScreenPayload`） |
| 空手Shift+右键 | — | 无操作 |
| 持同类型图腾Shift+右键 | 类型匹配 | 执行绑定：消耗1个图腾，将图腾柱ID写入剩余图腾的NBT |
| 持非匹配图腾右键 | 类型不匹配 | 显示红色提示"图腾类型不匹配" |

### 方块属性
- `lightLevel`：`state -> state.getValue(ACTIVATED) ? 15 : 0`
- 不可燃烧、硬度适中
- 掉落自身（仅MAIN部分）

### 系统柱特殊处理
- 系统柱（ID 0）不可破坏（`getDestroySpeed` 返回 -1 或检查 `isSystemPole`）
- 系统柱始终 `ACTIVATED = true`

---

## 四、TotemPoleBlockEntity

### 存储数据（NBT）
```
poleName: String    — 柱子名称（默认""）
poleId: int         — 全局唯一ID（0=系统柱，>0=玩家放置）
totemType: String   — "farm"/"mountain"/"beach"
activated: boolean  — 是否激活
systemPole: boolean — 是否为系统柱（不可破坏）
```

### 关键方法
- `setName(String)` — 设置名称，同步到 `TotemPoleTracker`，标记 `setChanged()`
- `activate()` / `deactivate()` — 切换激活状态，更新 BlockState
- `getNextId()` — 从 `TotemPoleTracker` 获取下一个可用ID
- 玩家放置时自动分配ID，注册到 Tracker

### 数据同步
- S→C 同步 `poleName` 和 `activated`（用于渲染器文字显示）
- 使用 `getUpdateTag()` / `handleUpdateTag()` 标准模式

---

## 五、TotemPoleTracker (SavedData)

### 数据结构
```java
Map<Integer, PoleEntry> poles;  // poleId → PoleEntry

record PoleEntry(
    BlockPos pos,
    String name, 
    TotemType type,
    boolean systemPole
)
```

### 关键方法
- `register(int id, PoleEntry)` — 注册图腾柱
- `unregister(int id)` — 移除（柱子被破坏时）
- `getNextId()` → int — 返回下一个可用ID（1起始）
- `getDefaultPole(TotemType)` → PoleEntry — 返回系统柱（ID 0对应类型）
- `getPole(int id)` → PoleEntry — 查找指定ID的柱子
- `getAllPoles(TotemType)` → List — 列出某类型所有柱子

### 系统柱默认位置

| 类型 | 坐标 | 朝向 |
|------|------|------|
| Farm | (135, -12, 136) | North |
| Mountain | (-290, -14, 256) | North |
| Beach | (-189, -14, -142) | North |

---

## 六、TeleportTotemItem (消耗品)

### 属性
- `TotemType type`
- `int boundPoleId`（NBT，默认-1表示未绑定→使用系统柱）
- `String boundPoleName`（NBT，显示用）

### 右键使用逻辑（严格复刻 SDV Object.performUseAction）

1. **检查**：`!player.canMove || eventUp || isFestival || ...` → 不可使用
2. **动画阶段**（SDV: 2000ms 动画 + totemWarp 回调）：
   - 播放 `warrior` 音效
   - 玩家面朝南（`faceDirection(2)`）
   - 玩家冻结 + 临时无敌（`-4000ms` 无敌计时器）
   - 停止背景音乐
   - 播放 2000ms 举起动画帧
   - 生成飘浮图腾图标粒子（3个，不同位置/速度/延迟）
   - 屏幕发光（`screenGlowOnce(sprinkleColor)`）
   - 生成闪光粒子（`addSprinklesToLocation 16x16 范围`）
3. **传送阶段**（SDV: totemWarp → totemWarpForReal 延迟1000ms）：
   - 生成 12 个 TemporaryAnimatedSprite(354) 在玩家周围
   - 播放 `wand` 音效
   - 屏幕淡出
   - 1000ms 后执行实际传送
   - 生成从右向左的扫光效果（x+8 到 x-8）
4. **实际传送**（totemWarpForReal）：
   - 查找绑定的图腾柱位置（从 `TotemPoleTracker` by ID）
   - 未绑定 → 使用系统柱位置
   - `player.teleportTo(pos)` 或跨维度传送
   - 恢复显示、取消无敌

### 颜色映射（SDV sprinkleColor）
- Farm: `Color.LimeGreen` (0x32CD32)
- Mountain: `Color.OrangeRed` (0xFF4500)
- Beach: `Color.LightBlue` (0xADD8E6)

### 物品名称显示
- 未绑定：`"农场传送图腾"` / `"山区传送图腾"` / `"海滩传送图腾"`
- 已绑定：`"农场传送图腾 - {柱名}"`

### 堆叠
- `stacksTo(999)`
- 同一绑定目标的图腾可堆叠（相同 boundPoleId → 相同 NBT → 可堆叠）

---

## 七、RainTotemItem

### 使用逻辑（严格复刻 SDV rainTotem）

1. **检查上下文**：
   - 检查 `AllowRainTotem`（当前位置是否允许）
   - 如不允许 → 显示红色消息 "此处无法使用"
   - 检查 `RainTotemAffectsContext` 重定向
2. **天气修改**：
   - 默认维度：检查明天是否节日，非节日则设明天天气为 Rain
   - 非默认维度：直接设该位置天气为 Rain
   - 调用 `WeatherManager.setTomorrowWeather("Rain")`
3. **动画效果**：
   - 播放 `thunder` 音效
   - 屏幕发光 SlateBlue
   - 玩家面朝南，播放 2000ms 动画帧
   - 生成 6 组云朵粒子（上方，不同运动方向/延迟）
   - 生成飘浮图腾图标粒子（向上飘 + 摇晃）
   - 2000ms 后播放 `rainsound` 音效
4. **消息**："暴风雨正在酝酿中..."（2000ms 延迟后显示）

---

## 八、TotemPoleBlockEntityRenderer

### 渲染内容
1. **模型渲染**：使用标准 `MapDecorStaticBlock` 的 GeoModel 渲染
2. **浮动文字**：在柱子上方渲染名称

### 文字渲染规格（用户要求）
- 位置：柱子顶部上方（使用 BubbleYHelper 类似逻辑）
- 颜色：Farm=绿色, Mountain=红色, Beach=蓝色（**加粗**）
- 无背景（`dropShadow` 模式或直接 `drawString` 无背景）
- **不使用 billboard 旋转**（固定朝向，与方块朝向一致）
- **两面可见**（正面 + 背面各渲染一次，背面镜像）

### 实现方式
```java
// 在 render() 中：
PoseStack pose = poseStack;
pose.pushPose();
// 根据 FACING 旋转
pose.translate(0.5, topY + 0.3, 0.5);
pose.mulPose(Axis.YP.rotationDegrees(facingAngle));

// 正面
pose.pushPose();
pose.scale(-0.025f, -0.025f, 0.025f);
font.drawInBatch(name, -font.width(name)/2f, 0, color, false, pose.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
pose.popPose();

// 背面（旋转180°）
pose.pushPose();
pose.mulPose(Axis.YP.rotationDegrees(180));
pose.scale(-0.025f, -0.025f, 0.025f);
font.drawInBatch(name, -font.width(name)/2f, 0, color, false, pose.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
pose.popPose();

pose.popPose();
```

---

## 九、TotemNamingScreen（完全复刻SDV NamingMenu）

### UI 布局（复刻 SDV NamingMenu.cs）
- 全屏覆盖 + 黑色半透明背景（`0.75f alpha`）
- 标题文字：居中上方卷轴文字（SDV `SpriteText.drawStringWithScrollCenteredAt`）
  - 对应MC：使用 Stardew 风格卷轴渲染
- 文本输入框：屏幕中央
- 确认按钮：输入框右侧（SDV 标准对勾按钮）
- 随机名称骰子按钮：确认按钮右侧

### 文本输入
- 复用 `AnimalPurchaseScreen` 的文本编辑逻辑：
  - `editName`, `editCursor`, `editingName`
  - 完整键盘处理：LEFT/RIGHT/HOME/END/BACKSPACE/DELETE/ENTER/ESC
  - `charTyped` 最大48字符
- 下划线渲染：聚焦时金色(`0xFFEADB8C`) + 渐变辉光，非聚焦时暗色(`0xAA8B8490`)
- 文字渲染：奶油色(`0xFFFFF7D0`)、0.90f缩放、加粗
- 光标：1px白色竖线，450ms闪烁

### 随机名称池
```java
TOTEM_NAMES_ZH = {"星光", "晨曦", "月影", "暮光", "雨露", "春风", "朝霞", "彩虹", "繁星", "流萤", "碧波", "晚霞", "微风", "云雀", "银河"}
TOTEM_NAMES_EN = {"Starlight", "Dawn", "Moonshadow", "Twilight", "Dewdrop", "Breeze", "Aurora", "Rainbow", "Constellation", "Firefly", "Ripple", "Dusk", "Zephyr", "Lark", "Galaxy"}
```

### 网络流程
1. 玩家空手右键图腾柱 → 服务端发 `OpenTotemNamingScreenPayload`（含当前名称、柱ID、类型）
2. 客户端打开 `TotemNamingScreen`
3. 玩家编辑名称，点确认 → 发 `TotemNamingSubmitPayload`（柱ID + 新名称）
4. 服务端更新 `TotemPoleBlockEntity` + `TotemPoleTracker`，广播更新

---

## 十、网络包

### OpenTotemNamingScreenPayload (S→C)
```
long blockPos       — 图腾柱位置
String currentName  — 当前名称
String totemType    — "farm"/"mountain"/"beach"
int poleId          — 柱ID
```

### TotemNamingSubmitPayload (C→S)
```
long blockPos       — 图腾柱位置
String newName      — 新名称
```

### TotemBindPayload (C→S)
不需要独立包——绑定逻辑直接在 `TotemPoleBlock.useItemOn` 的服务端处理。

---

## 十一、SystemTotemManager

### 职责
- 在 Stardew 维度加载时，检查系统图腾柱是否存在
- 不存在则放置（`setBlock` + 创建 BlockEntity）
- 系统柱 `poleId = 0`，`systemPole = true`，`activated = true`
- 系统柱不可破坏

### 触发时机
- `LevelEvent.Load` 或维度首次tick
- 仅在 Stardew 维度执行

### 系统柱三座

| TotemType | 坐标 | 默认名称 |
|-----------|------|---------|
| FARM | (135, -12, 136) | "农场" |
| MOUNTAIN | (-290, -14, 256) | "山区" |
| BEACH | (-189, -14, -142) | "海滩" |

---

## 十二、注册清单

### ModBlocks
```java
FARM_TOTEM_POLE = BLOCKS.register("farm_totem_pole", () -> new TotemPoleBlock(props, TotemType.FARM));
MOUNTAIN_TOTEM_POLE = BLOCKS.register("mountain_totem_pole", () -> new TotemPoleBlock(props, TotemType.MOUNTAIN));
BEACH_TOTEM_POLE = BLOCKS.register("beach_totem_pole", () -> new TotemPoleBlock(props, TotemType.BEACH));
```

### ModItems
```java
FARM_TOTEM_POLE_ITEM = blockItem("farm_totem_pole")
MOUNTAIN_TOTEM_POLE_ITEM = blockItem("mountain_totem_pole") 
BEACH_TOTEM_POLE_ITEM = blockItem("beach_totem_pole")

WARP_TOTEM_FARM = ITEMS.register("warp_totem_farm", () -> new TeleportTotemItem(props, TotemType.FARM));
WARP_TOTEM_MOUNTAIN = ITEMS.register("warp_totem_mountain", () -> new TeleportTotemItem(props, TotemType.MOUNTAIN));
WARP_TOTEM_BEACH = ITEMS.register("warp_totem_beach", () -> new TeleportTotemItem(props, TotemType.BEACH));
RAIN_TOTEM = ITEMS.register("rain_totem", () -> new RainTotemItem(props));
```

### ModBlockEntities
```java
TOTEM_POLE = BLOCK_ENTITIES.register("totem_pole", () -> 
    BlockEntityType.Builder.of(TotemPoleBlockEntity::new, 
        ModBlocks.FARM_TOTEM_POLE.get(), 
        ModBlocks.MOUNTAIN_TOTEM_POLE.get(), 
        ModBlocks.BEACH_TOTEM_POLE.get()
    ).build(null));
```

### ModClientSetup
```java
event.registerBlockEntityRenderer(ModBlockEntities.TOTEM_POLE.get(), TotemPoleBlockEntityRenderer::new);
```

### PacketHandler
```java
registrar.playToClient(OpenTotemNamingScreenPayload.TYPE, ...);
registrar.playToServer(TotemNamingSubmitPayload.TYPE, ...);
```

### ModSounds
```java
WARRIOR = register("warrior");
WAND = register("wand");
RAIN_SOUND = register("rainsound");
THUNDER = register("thunder");
```

---

## 十三、资源文件

### 模型文件
- 从 `tmp_models/3_1.json` → `assets/stardewcraft/models/block/utility/totem_pole_farm.json`
- 从 `tmp_models/3_2.json` → `assets/stardewcraft/models/block/utility/totem_pole_mountain.json`
- 从 `tmp_models/3_3.json` → `assets/stardewcraft/models/block/utility/totem_pole_beach.json`
- 纹理引用需更新为正确路径

### 纹理文件
- `3_1_activated.png` → `textures/block/utility/totem_pole_farm_activated.png`
- `3_1_deactivated.png` → `textures/block/utility/totem_pole_farm_deactivated.png`
- 同理 Mountain (3_2) / Beach (3_3)

### blockstate 文件
- 每种图腾柱需要 blockstate JSON，根据 `activated` + `facing` 选择模型

### 物品纹理
- `warp_totem_farm.png`, `warp_totem_mountain.png`, `warp_totem_beach.png`, `rain_totem.png`
- 需要从 SDV 精灵图中提取或创建

---

## 十四、实施顺序

1. ✅ TotemType 枚举
2. ✅ TotemPoleBlock (含 ACTIVATED 属性)
3. ✅ TotemPoleBlockEntity (NBT 存储 + 同步)
4. ✅ TotemPoleTracker (SavedData)
5. ✅ TeleportTotemItem (使用逻辑 + 绑定NBT)
6. ✅ RainTotemItem (天气修改 + 动画)
7. ✅ TotemPoleBlockEntityRenderer (模型 + 浮动文字)
8. ✅ 网络包 (OpenTotemNamingScreenPayload + TotemNamingSubmitPayload)
9. ✅ TotemNamingScreen (完全复刻SDV命名GUI)
10. ✅ SystemTotemManager (系统柱放置)
11. ✅ 注册（ModBlocks/ModItems/ModBlockEntities/ModClientSetup/PacketHandler/ModSounds）
12. ✅ 模型+纹理资源文件
13. ✅ 本地化
14. ✅ 编译验证
