# 建筑管理器 GUI 重设计方案

> 适用于：鸡舍管理器（CoopManagerScreen）& 畜棚管理器（BarnManagerScreen）  
> 两者共享同一套 `BuildingManagerScreen` 基类，仅通过参数区分类型

---

## 一、设计理念

**"Robin 的蓝图台"** — 将管理器界面设计为 Robin 木匠铺蓝图桌的体验。  
玩家交互管理器方块时，打开的不是 debug 面板，而是一张铺在木桌上的 **建筑蓝图**。

核心风格关键词：
- **SDV 原生感**：使用 `drawDialogueBoxFrame` 木质边框 + `drawTextureBox` 子面板
- **蓝图纸质感**：主内容区用暖米色底 `#F5E6C8`，略带纸纹噪点
- **信息层次清晰**：标题区、状态区、需求区、操作区四段式布局
- **动效克制但灵动**：开合动画 + 进度条填充 + 按钮悬停呼吸

---

## 二、整体布局

### 尺寸与缩放

```
基础设计帧：480 × 320 像素（Figma 坐标系）
UI_SCALE = 自适应（目标在屏幕上占 ~60% 宽度）
最终像素：约 480 × 320（在 GUI scale=2 下）
```

### 四段式垂直布局

```
┌──────────────────────────────────────────────────┐
│  ██ drawDialogueBoxFrame 木质外边框 ██            │
│  ┌────────────────────────────────────────────┐  │
│  │  [A] 标题栏  —  建筑名 + 阶段徽章          │  │
│  ├────────────────────────────────────────────┤  │
│  │                                            │  │
│  │  [B] 状态全景区                             │  │
│  │  ┌──────────┐  ┌────────────────────────┐  │  │
│  │  │ 建筑缩略图 │  │ 容量条 / 动物头像列表  │  │  │
│  │  │ (Tier图示) │  │ 当前空间 / 设施状态    │  │  │
│  │  └──────────┘  └────────────────────────┘  │  │
│  │                                            │  │
│  ├──── ─ ─ ─ ─  水平分隔线 ─ ─ ─ ─ ─────────┤  │
│  │                                            │  │
│  │  [C] 升级需求区 / 已满级祝贺区              │  │
│  │  需求项 checklist（图标+文字+进度条）        │  │
│  │                                            │  │
│  ├────────────────────────────────────────────┤  │
│  │  [D] 操作按钮栏                             │  │
│  │  [ 🔨 建造/升级 ]  [ 🏚️ 拆除 ]  [ 📦 搬迁 ] │  │
│  └────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────┘
```

---

## 三、各区域详细设计

### [A] 标题栏（y: 0 ~ 40）

**背景**：无独立背景，融入外框顶部木质区域

**内容**：
```
左侧：建筑类型图标（32×32）
  - 鸡舍：从 cursors.png 截取鸡舍缩略图，或用纯代码绘制简笔小屋轮廓
  - 畜棚：同理
中间：建筑名称（SDV SpriteText 风格粗体，金色 #FFD46A）
  - "鸡舍" / "畜棚"（未建造时）
  - "大鸡舍" / "大畜棚"（T2）
  - "豪华鸡舍" / "豪华畜棚"（T3）
右侧：阶段徽章
  - 未建造：灰色空心圆 ○
  - T1：铜色实心星 ★（#CD7F32）
  - T2：银色实心星 ★（#C0C0C0）
  - T3：金色实心星 ★（#FFD700），带呼吸发光特效
```

**阶段徽章绘制**（纯几何）：
```java
// 用 cursors.png 的星星素材 (338,400,8,8) 或手绘五角星
// T3 时添加 sin 呼吸 alpha 脉冲：alpha = 0.7 + 0.3 * sin(tick * 0.08)
```

**水平分隔线**：使用 `StardewGuiUtil.drawHorizontalPartition()` — tile 4/6/7

---

### [B] 状态全景区（y: 44 ~ 160）

左右分两列，中间用 `drawVerticalPartitionSmall()` 分隔。

#### 左列：建筑缩略图（120 × 100）

**用纯代码几何绘制**的建筑等级示意图，风格为 SDV 像素画：

```
未建造 (Tier 0)：
  ┌─ ─ ─ ─┐      虚线轮廓
  │  ？？  │      灰色问号
  └─ ─ ─ ─┘      #808080, 50% alpha

T1 小型：
  ╱‾‾‾‾╲
  │ 🚪 │         棕色小屋，单扇门
  └────┘          填充 #8B6914

T2 中型：
  ╱‾‾‾‾‾‾‾╲
  │  🚪🪟 │      更宽，加窗户
  └───────┘       填充 #A0792C

T3 豪华型：
  ╱‾‾‾‾‾‾‾‾‾╲
  │ 🚪🪟🪟⭐│    最宽，双窗+星标
  └──────────┘    填充 #B8912E + 金边描边
```

具体实现：用 `graphics.fill()` 画矩形墙体 + 三角形屋顶（通过多行 fill 模拟），颜色按 tier 变化。

**升级箭头动画**（非满级时）：
- 缩略图下方显示 `↑ 升级至 T{n+1}` 文字
- 使用 cursors.png `(421,459,11,12)` 上箭头图标
- 箭头做 `translateY` 上下浮动动画：`y += 2 * sin(tick * 0.12)`

#### 右列：建筑实时信息（200 × 100）

**容量指示器**（核心亮点）：
```
动物容量：3 / 4
[■■■□] ← 每格代表一个动物槽位
```

- 每个槽位是一个 20×20 的小方格
- 已占用槽位：显示对应动物类型的 16×16 像素图标（复用 `animal_query/icon_*.png`）
- 空槽位：灰色虚线框 `#606060`，30% alpha
- 槽位排列：每行 4 个，最多 3 行（对应 T3 的 12 容量）
- **新增动物时的入场动画**：图标从透明淡入 + 轻微弹跳（200ms ease-out）

**空间尺寸**：
```
📐 空间：宽5 × 长7 × 高4  (内部 140 格)
```
- 用 `font.drawString` 渲染
- 颜色：满足要求 `#8FEA8F` 绿 / 不满足 `#E68B8B` 红
- 尺寸值不足时数字闪烁（alpha 在 0.5~1.0 之间 sin 波动）

**设施简报**（图标行）：
```
🥣×2  📦×1  🐣×1  🔥×0
食槽    漏斗   孵化器  加热器
```
- 每个设施用 16×16 物品图标（`renderItem`）+ `×数量` 文字
- 满足需求：白色数字
- 未满足：红色数字 + 图标半透明闪烁

---

### [C] 升级需求区（y: 168 ~ 252）

用 `drawTextureBoxNoShadow()` 绘制一个内嵌面板（浅色九宫格边框）。

#### 三种状态：

**状态 1：可升级（有缺件）**

标题行：`"升级至 {目标名称} 需要："` — 白色

需求清单（每行 24px 高）：
```
每行格式：
[物品图标 16×16] [需求名称]  [进度条 80×8] [当前/需求]  [✓/✗]

示例：
🥣 饲料槽        [████████░░] 2/3    ✗
📦 干草漏斗      [██████████] 1/1    ✓
🚪 门            [██████████] 2/2    ✓
📐 内部空间      [██████░░░░] 89/140  ✗
🧱 密封外壳      [██████████] ✓      ✓
```

**进度条绘制**（几何）：
```java
// 底框：2px 圆角矩形 #3A3228
graphics.fill(x, y, x+80, y+8, 0xFF3A3228);
// 填充：
//   满足时：渐变绿 #4CAF50 → #81C784
//   未满足：渐变橙 #FF9800 → #FFB74D
int fillW = (int)(80.0 * current / required);
graphics.fill(x+1, y+1, x+1+fillW, y+7, fulfilled ? 0xFF4CAF50 : 0xFFFF9800);
// 高光线（顶部1px）：
graphics.fill(x+1, y+1, x+1+fillW, y+2, 0x40FFFFFF);
```

**状态 2：全部满足，可以建造**

整个需求区变为绿色调：
```
   ✨ 所有条件已满足！✨
   
   [  🔨  立即建造  🔨  ]    ← 按钮在此处额外显示一个高亮版本
```
- 文字金色 `#FFD46A`，带 sin 波 alpha 呼吸
- 星星特效：用 cursors.png 星星精灵 `(338,400,8,8)` 在左右两侧做旋转+缩放动画

**状态 3：已满级（T3）**

```
  ⭐ 已达到最高等级 ⭐
  
  "你的{建筑名}已经是最好的了！"
  
  动物容量：12 / 12（全金色文字）
```
- 底部渐变光晕效果：中心 `#FFD700` 40% alpha 向四周衰减
- 星星围绕文字做缓慢轨道旋转（每 3 秒一圈）

---

### [D] 操作按钮栏（y: 258 ~ 310）

三个并排按钮，每个 120×36：

```
[ 🔨 建造/升级 ]    [ 🏚️ 拆 除 ]    [ 📦 搬 迁 ]
```

#### 按钮视觉设计

**不使用 MC 原版 Button**，改用自绘 SDV 风格按钮：

```
正常态：
  drawTextureBox(cursors 384,373,18,18) 九宫格边框
  内部填充 #5A4726 棕色
  文字居中，白色 #FFFFFF

悬停态：
  边框亮度 +20%
  内部填充 #7A6236
  文字变为金色 #FFD46A
  缩放 1.0 → 1.05（50ms ease-out）
  播放 SMALL_SELECT 音效

按下态：
  内部填充 #3A2716
  文字白色
  缩放 1.05 → 0.97（30ms）
  播放 BUTTON1 音效

禁用态：
  整体灰度化
  文字 #808080
  alpha 0.5
```

#### 按钮图标

用 cursors.png 精灵：
- 建造/升级：`锤子` — 从 cursors.png 提取（或用 `ModSounds.HAMMER` 对应的视觉符号）
  - 代码绘制备选：画一个 8×8 像素的锤头+把手
- 拆除：`红色 ✗` — cursors.png `(337,494,12,12)` close 按钮红化
- 搬迁：`箭头方块` — cursors.png `(352,495,12,11)` back arrow + `(365,495,12,11)` forward arrow 组合

---

## 四、确认对话框（覆盖层）

点击「拆除」或「搬迁」时，不切换模式，而是弹出一个 **居中悬浮对话框**。

### 布局
```
┌─────────────────────────────────────┐
│  drawDialogueBoxFrame 小型木框      │
│                                     │
│    ⚠️  确认拆除鸡舍？                │
│                                     │
│    此操作将移除建筑等级。            │
│    建筑内所有设施将保留。            │
│                                     │
│    🐔 当前绑定动物：3 只             │  ← 有动物时红色警告
│    ⛔ 请先移走所有动物               │
│                                     │
│    [  ✓ 确认  ]    [  ✗ 取消  ]     │
│                                     │
└─────────────────────────────────────┘
```

### 视觉细节
- 背景遮罩：全屏 `#000000` 40% alpha
- 对话框入场：从 scale 0.85 → 1.0 的弹入动画（180ms cubic ease-out）
- 退场：scale 1.0 → 0.9 + alpha 1.0 → 0.0（120ms）
- 确认按钮：同 [D] 区按钮风格，绿色调 `#4A7A3A`
- 取消按钮：同 [D] 区按钮风格，红色调 `#7A3A3A`
- 有绑定动物时确认按钮禁用 + 动物数量红色闪烁

---

## 五、动画系统

### 5.1 开屏动画（打开 GUI 时）

```
时间线（总 400ms）：

0ms  → 外框从 scale(0.8) + alpha(0) 开始
       cubic ease-out 展开至 scale(1.0) + alpha(1.0)
       
100ms → [A] 标题栏从顶部 translateY(-20) 滑入
         阶段徽章 rotate(0°→360°) 旋转入场

200ms → [B] 左列缩略图从左 translateX(-30) 滑入
         [B] 右列信息从右 translateX(+30) 滑入
         
250ms → [C] 需求区从 alpha(0) 淡入
         进度条从 width(0) 动画填充至实际宽度（300ms ease-out）
         
350ms → [D] 按钮从底部 translateY(+15) 滑入
```

### 5.2 进度条填充动画

需求区的每根进度条在打开 GUI 时做 **从左到右的填充动画**：
```java
float progress = Math.min(1.0f, (ticksSinceOpen - entryDelay) / 15.0f); // 15tick = 750ms
float eased = 1.0f - (1.0f - progress) * (1.0f - progress); // ease-out quadratic
int fillW = (int)(maxWidth * eased * (current / (float)required));
```

每行错开 3tick 开始填充，形成级联瀑布效果。

### 5.3 槽位动画

容量区的动物槽位做 **逐个入场**：
```
每个槽位间隔 2tick 出现
出现时：scale 0 → 1.2 → 1.0 (bounce)
         alpha 0 → 1.0
         duration: 150ms
```

### 5.4 循环动画

持续播放的微动效（保持界面活力）：

| 元素 | 动画 | 参数 |
|------|------|------|
| T3 金星徽章 | alpha 呼吸 | `0.7 + 0.3 * sin(tick * 0.08)` |
| 升级箭头 | Y 浮动 | `2 * sin(tick * 0.12)` |
| 不满足数值 | alpha 闪烁 | `0.5 + 0.5 * sin(tick * 0.15)` |
| "全部满足" 星星 | 旋转+缩放 | `rotate(tick * 2°), scale(0.9 + 0.1 * sin(tick * 0.1))` |
| 空槽位虚线框 | alpha 脉搏 | `0.2 + 0.1 * sin(tick * 0.06)` |

### 5.5 按钮悬停动画

```java
// approach 插值（同 AnimalQueryScreen 模式）
private float hoverScale = 1.0f;
private static final float HOVER_TARGET = 1.05f;
private static final float HOVER_SPEED = 0.15f;

void tick() {
    float target = isHovered ? HOVER_TARGET : 1.0f;
    hoverScale += (target - hoverScale) * HOVER_SPEED;
}
```

---

## 六、音效设计

| 事件 | 音效 | 来源 |
|------|------|------|
| 打开管理器 | `DOOR_CREAK` | 木门吱呀声，契合木匠铺主题 |
| 悬停按钮 | `SMALL_SELECT` | SDV 小选择音 |
| 点击建造/升级 | `HAMMER` | 锤子敲击，成功建造的反馈 |
| 建造成功反馈 | `NEW_RECIPE` | 叮～学习新配方的愉悦音 |
| 点击拆除 | `TRASHCANLID` | 翻盖声，暗示破坏性操作 |
| 确认拆除执行 | `EXPLOSION`（低音量 0.3） | 短促爆炸，提示不可逆 |
| 点击搬迁 | `BACKPACK_IN` | 收纳声 |
| 弹出确认框 | `BIG_SELECT` | 重要选择提示 |
| 关闭确认框/取消 | `BIG_DESELECT` | 取消回退声 |
| 关闭管理器 | `DOOR_CREAK_REVERSE` | 关门声 |
| 进度条填满瞬间 | `SHINY4`（每条各一次） | 闪光完成音 |
| 条件全部满足 | `JINGLE1` | 喜悦铃声 |
| 尝试禁用操作 | `CANCEL` | 短促否定音 |

### 音效播放原则
- 悬停音限频：同一按钮 300ms 内不重复播放
- 进度条完成音延迟播放，与填充动画同步
- 所有 GUI 音效走 `player.playSound()` 本地播放，不广播

---

## 七、颜色系统

### 配色方案（SDV 暖色调木质主题）

| 用途 | 颜色 | Hex |
|------|------|-----|
| 标题文字 | 星露金 | `#FFD46A` |
| 正文 | 暖白 | `#F0EDE3` |
| 次要文字 | 灰米 | `#BFB8A8` |
| 满足状态 | 草绿 | `#8FEA8F` |
| 不满足状态 | 珊瑚红 | `#E68B8B` |
| 进度条满 | 森绿 | `#4CAF50` |
| 进度条未满 | 琥珀橙 | `#FF9800` |
| 按钮正常底 | 深木棕 | `#5A4726` |
| 按钮悬停底 | 浅木棕 | `#7A6236` |
| 内嵌面板底 | 羊皮纸 | `#F5E6C8` 20% alpha |
| 警告文字 | 亮红 | `#FF6B6B` |
| T1 铜星 | 铜 | `#CD7F32` |
| T2 银星 | 银 | `#C0C0C0` |
| T3 金星 | 金 | `#FFD700` |

---

## 八、资产需求分析

### 完全复用已有资产（无需新建）

| 资产 | 来源 | 用途 |
|------|------|------|
| `menu_tiles.png` | `StardewGuiUtil.drawDialogueBoxFrame()` | 主框架 |
| `cursors.png (384,373,18,18)` | `drawTextureBoxNoShadow()` | 子面板边框 |
| `cursors.png (337,494,12,12)` | Close ✗ 按钮 | 拆除图标 |
| `cursors.png (421,459,11,12)` | 上箭头 | 升级提示 |
| `cursors.png (128,256,64,64)` | OK 大按钮 | 确认对话框 |
| `animal_query/icon_*.png` ×11 | 所有动物头像 | 容量槽位 |
| `animal_query/heart_empty.png` | 心形 | 可选用于好感度预览 |
| `gold_icon.png` | 金币 | 可选用于未来价格显示 |
| 各种 `ModSounds.*` | 音效 | 全部已注册 |

### 纯代码几何绘制（无需贴图）

| 元素 | 绘制方式 |
|------|----------|
| 建筑缩略图 | `graphics.fill()` 矩形组合 — 墙体+屋顶+门+窗 |
| 进度条 | `graphics.fill()` 双层矩形（底框+填充+高光） |
| 星形徽章 | `drawFromCursors` 小星星 or `graphics.fill()` 菱形近似 |
| 容量空槽 | `graphics.fill()` 虚线边框（4段短线组合） |
| 水平/垂直分隔 | `StardewGuiUtil.drawHorizontalPartition()` |
| 按钮 | `drawTextureBox` 九宫格 + `graphics.fill()` 内填充 |
| 确认框遮罩 | `graphics.fill()` 全屏半透明黑 |

### 需要新建的资产：无

所有视觉效果通过 **已有精灵图集 + 几何绘制 + 动画** 实现。

---

## 九、技术实现架构

### 类结构

```
client/gui/
  BuildingManagerScreen.java     ← 新基类（替代 CoopManagerScreen + BarnManagerScreen）
  BuildingManagerAnimator.java   ← 动画状态机（入场、悬停、循环）
  BuildingManagerWidgets.java    ← 自绘按钮、进度条、槽位格组件
```

### Screen 生命周期

```java
class BuildingManagerScreen extends AbstractContainerScreen<CoopManagerMenu> {
    
    // 常量
    static final int PANEL_W = 480, PANEL_H = 320;
    
    // 动画状态
    long openTick;           // GUI 打开时的 tick
    float entryProgress;     // 0→1 入场进度
    float[] barProgress;     // 每根进度条的填充进度
    float[] slotAlpha;       // 每个槽位的入场 alpha
    
    // 悬停追踪
    int hoveredButton = -1;  // -1=none, 0=build, 1=demolish, 2=relocate
    float[] btnScale;        // 每个按钮的 approach 缩放
    long lastHoverSoundTick; // 悬停音效限频
    
    // 确认框
    boolean showConfirm;
    ConfirmType confirmType;
    float confirmScale;      // 弹入缩放
    float confirmAlpha;      // 淡入透明度
    
    @Override
    protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
        // 1. 全屏淡色遮罩
        // 2. drawDialogueBoxFrame 外框（带 entry scale 动画）
        // 3. 绘制 [A][B][C][D] 四区域
        // 4. 如果 showConfirm → 绘制确认覆盖层
    }
    
    @Override
    protected void containerTick() {
        updateEntryAnimation();
        updateBarAnimations();
        updateSlotAnimations();
        updateButtonHovers();
        updateConfirmAnimation();
    }
}
```

### 自绘按钮（不用 MC Button）

```java
// 在 renderBg 中自行绘制按钮矩形
// 在 mouseClicked 中手动 hitTest
// 优点：完全控制视觉效果，无 MC widget 样式干扰

record StardewButton(int x, int y, int w, int h, Component label, Runnable action) {
    boolean isHovered(int mx, int my) { ... }
    void render(GuiGraphics g, float scale, boolean hovered, boolean active) {
        // drawTextureBox 边框
        // fill 内部
        // drawCenteredString 文字
    }
}
```

### 建筑类型参数化

```java
enum BuildingFamily {
    COOP("coop", "鸡舍", new String[]{"鸡舍","大鸡舍","豪华鸡舍"},
         0xFFA0792C, /* 屋顶色 */ animalIcons_coop),
    BARN("barn", "畜棚", new String[]{"畜棚","大畜棚","豪华畜棚"},
         0xFF8B6914, /* 屋顶色 */ animalIcons_barn);
    
    final String id;
    final String baseName;
    final String[] tierNames;
    final int roofColor;
    final ResourceLocation[] animalIcons;
}
```

---

## 十、Menu 数据扩展建议

当前 `CoopManagerMenu` 的 23 个 DataSlot 已经足够支撑新 GUI 的所有信息展示。

需要额外考虑的：
- **动物列表**：当前只同步 `boundAnimalCount`（int），新 GUI 的容量槽位需要知道每个动物的类型。
  - 方案 A：新增 DataSlot 同步每个槽位的动物类型 ID（最多 12 个 int，编码为 enum ordinal）
  - 方案 B：客户端从 `AnimalWorldData` 的客户端缓存读取（如果已有同步机制）
  - 方案 C：仅显示数量 + 汇总图标（如"🐔×3 🦆×1"），不显示每个槽位 — 实现简单且信息充足
  - **推荐方案 C**，后续需要再升级到 A

---

## 十一、交互流程图

```
打开管理器
    │
    ▼
[入场动画 400ms]
    │
    ▼
┌─ 主界面 ──────────────────────┐
│                                │
│  查看状态信息 ←───────────┐    │
│       │                   │    │
│       ├─→ 点击 [建造/升级]  │    │
│       │      │             │    │
│       │      ├─ 可执行 → 发包 → 成功音 → 刷新界面 ─┐
│       │      └─ 不可执行 → CANCEL 音效              │
│       │                                             │
│       ├─→ 点击 [拆除] ─→ 弹出确认框 ────────────┐  │
│       │                                         │  │
│       └─→ 点击 [搬迁] ─→ 弹出确认框 ──────┐    │  │
│                                            │    │  │
│  ┌─ 确认框 ──────────── ←──────────────────┘    │  │
│  │  [确认] → 发包 → 成功 → 关闭GUI              │  │
│  │  [取消] → 关闭确认框 → 回主界面 ──────────────┘  │
│  └──────────────────────────────────────────────────┘
│                                │
│  ESC / 右键 → 关闭 GUI        │
└────────────────────────────────┘
```

---

## 十二、与现有系统集成

| 项目 | 处理方式 |
|------|----------|
| `CoopManagerMenu` / `BarnManagerMenu` | **保留不变**，Screen 层重做 |
| `ModMenuTypes` 注册 | 不变 |
| `CoopManagerBlock.openMenu()` | 不变，仍创建同一个 Menu |
| `MenuScreens.register()` | 改绑定新的 `BuildingManagerScreen::new` |
| 旧 `CoopManagerScreen` / `BarnManagerScreen` | 删除，由统一基类替代 |
| 翻译 key | 新增 + 保留已有（兼容） |

---

## 十三、预期效果总结

| 维度 | 旧版 | 新版 |
|------|------|------|
| 视觉风格 | 黑底白字 debug 面板 | SDV 木质蓝图桌，暖色调 |
| 背景/边框 | `graphics.fill` 纯色 | `drawDialogueBoxFrame` + 九宫格 |
| 信息密度 | 文字堆砌 | 图标+进度条+槽位，视觉层次 |
| 按钮 | MC 原版灰色 Button | 自绘 SDV 棕色按钮+悬停特效 |
| 动画 | 无 | 入场级联+进度条填充+呼吸+弹跳 |
| 音效 | 无 | 11 种情境音效 |
| 确认对话框 | 同页面切换模式 | 悬浮弹出框+遮罩 |
| 代码复用 | Coop/Barn 两份 copy-paste | 统一 BuildingManagerScreen |
