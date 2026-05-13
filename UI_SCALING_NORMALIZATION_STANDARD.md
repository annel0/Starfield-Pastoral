# UI 缩放规格化标准

## 目标

这份标准用于修复 StardewCraft UI 在非 4x GUI scale 下的尺寸、点击区、贴图采样错位问题。

核心原则：视觉必须以原版 Stardew Valley 和当前已复刻的源码坐标为准。参考目录只能提供技术思路，不能提供视觉参数、布局比例、按钮位置、行高、面板尺寸或整体构图。

换句话说，要做的是“保留原版 UI，替换底层绘制单位”，不是重做一个相似功能的界面。

核心技术宗旨：把 `cursors.png` 这类大型合图逐步拆成独立 PNG，让 Minecraft 引擎按单个资源处理采样与缩放；UI 代码继续使用原版固定像素坐标和原有布局公式，只替换贴图来源。这样可以减少对超大 atlas 局部 UV 的依赖，让缩放效果更稳定，也让每个 UI 元素的资源边界、尺寸和调用关系更清楚。

之后所有 UI 改动都按这个宗旨推进：先拆出独立资源，再用等价 helper 接回原调用点；不借拆图机会重排界面、不改布局常量、不把参考工程参数搬进来。

## 这次样板失败的结论

错误做法：把参考实现里的面板宽度、行高、按钮大小、详情页宽度、物品尺寸直接搬进 `ShippingMenuScreen`。

实际后果：出货结算页不再像 SDV，原有横向长条面板变成窄面板，金币区、OK 按钮、加号按钮、行距和视觉重心全部偏离。

正确做法：保留 `ShippingMenuScreen` 现有源码的布局推导，例如 `centerY + px(-300 + i * 27 * 4)`、`categoryLabelsWidth = px(512)`、`drawTextureBoxNoShadow(..., px(104))` 这类 SDV 坐标关系；只把 `CURSORS` 上的局部 UV 抽成独立资源，并让抽出的资源按同一 SDV 像素尺寸绘制。

## 坐标体系

项目里同时存在三种单位，必须分清：

- SDV 像素：原版 Stardew Valley 源码和 atlas UV 使用的像素单位。
- MC GUI 像素：`GuiGraphics` 最终接收的屏幕坐标单位。
- 贴图源像素：PNG 文件真实尺寸。

迁移规则：

- 布局常量优先保留 SDV 像素，不改成参考工程的 MC GUI 常量。
- SDV 布局坐标只通过 `px(...)` 或 `StardewRenderMapping.ui(...)` 转一次。
- `pixelZoom = 4` 的贴图尺寸继续使用 `s4()` 或等价缩放，不因为拆图而改变视觉尺寸。
- 点击区必须继续使用和绘制相同的布局变量，不能单独重算。
- 拆出的 PNG 源尺寸可以变，但它在屏幕上占用的 SDV 尺寸不能变。
- 拆图后的 UI 调用点应直接使用原固定像素坐标和原缩放系数；helper 只负责绑定独立 PNG、绘制整张切片和必要的 tint，不负责布局。

## 贴图拆分规则

从 `cursors.png` 或其他 atlas 拆资源时，必须记录四个值：

| 字段 | 含义 |
| --- | --- |
| `sourceTexture` | 原 atlas 名，例如 `cursors.png`。 |
| `sourceRect` | 原 UV：`u, v, w, h`。 |
| `sdvDrawSize` | 原源码期望绘制尺寸，通常是 `w * 4`, `h * 4` 的 SDV pixelZoom 结果。 |
| `callSites` | 原来哪些 screen/helper 使用了这块资源。 |

拆分后的 helper 不应该发明新尺寸。示例：

```java
// 原逻辑
StardewGuiUtil.drawFromCursors(graphics, x, y, 408, 476, 9, 11, s4());

// 允许的迁移方向：视觉仍是 9x11 源图按 s4 绘制
ShippingTextures.drawCoin(graphics, x, y, s4());
```

不允许：

```java
// 错：把金币目标尺寸随手改成 16x20 MC GUI 像素
ShippingTextures.drawCoin(graphics, x, y, 16, 20);
```

## Helper 设计标准

新增 helper 应该服务于“资源命名”和“采样稳定”，而不是承载新布局。

推荐形态：

```java
public static void drawCoin(GuiGraphics graphics, int x, int y, float scale) {
    drawImage(graphics, COIN, x, y, 9, 11, scale);
}
```

如果要彻底摆脱 `pose().scale(...)`，也必须显式传入原来的目标尺寸：

```java
public static void drawCoin(GuiGraphics graphics, int x, int y, int targetW, int targetH) {
    graphics.blit(COIN, x, y, targetW, targetH, 0, 0, 9, 11, 9, 11);
}
```

调用方应使用原公式计算：

```java
int coinW = px(9 * 4);
int coinH = px(11 * 4);
ShippingTextures.drawCoin(graphics, coinX, coinY, coinW, coinH);
```

## ShippingMenuScreen 迁移标准

出货结算页是第一批迁移对象，但只能按以下顺序做：

1. 不改布局，只把一个小资源拆出来，例如金币图标。
2. 验证原 GUI scale 4x 视觉完全一致。
3. 验证 GUI scale 2/3 下只改善清晰度和采样，不改变构图。
4. 再迁移数字、点线、加号、OK、箭头。
5. 最后才考虑 panel 九宫格，但 panel 的目标宽高必须仍来自 `categoryLabelsWidth`、`px(104)`、`boxwidth`、`boxheight`。

Shipping 里必须保持的原版关系：

- 总结页 6 行保持原来的纵向节奏：`centerY + px(-300 + i * 27 * 4)`。
- 分类标签区域保持 `512` SDV 像素宽。
- 加号按钮仍位于分类长条右侧，不重排到新面板外的新位置。
- 物品预览框仍使用原 `293,360,24,24` 视觉比例。
- OK 按钮位置仍由 `totalWidth` 和 `itemAndPlusButtonWidth` 推导。
- 详情页宽高仍跟当前源码一致，除非先找到原版 SDV 对应源码证明需要修正。

## 验收标准

每迁移一个 screen，都要做这些检查：

- GUI scale 4 下截图应和迁移前几乎一致。
- GUI scale 2/3 下没有拉伸错位、采样模糊、点击区漂移。
- 只替换资源绘制方式，不改变 screen 构图。
- `git diff` 中布局常量不应大面积变化。
- `get_errors` 无错误。
- `./gradlew classes --console=plain` 通过。

视觉验收优先级高于“代码看起来更现代”。如果视觉不像 SDV，迁移就是失败。

## 推进顺序

推荐顺序：

1. 公共小部件：先迁移跨界面复用的独立按钮、箭头、图标，例如前后翻页箭头、关闭按钮、OK/Cancel、滚动条。
2. Shipping：金币、数字、点线、按钮这些小部件逐个迁移。
3. Shipping：panel 九宫格迁移，但保持原宽高公式。
4. 过夜其他 screen：沿用 Shipping 验证过的 helper。
5. Shop / Catalogue / Quest / Letter / Carpenter / Jukebox：优先迁移已有独立 PNG 或可从 `cursors.png` 精确切出的通用控件。
6. Bundle / Joja：先梳理原源码坐标，再迁移资源。
7. HUD / toast：单独制定标准，因为它们跟 screen 居中菜单不同。

当前已开始的全项目批次：

- 公共前后翻页箭头：`area_back_arrow.png` / `area_next_arrow.png`，源尺寸 `12x11`，已接入 Jukebox、LetterViewer、QuestLog、Carpenter。调用点保留原坐标、原 `s4()` 或 hover 缩放。
- Shipping 小部件：金币、点线、OK、前后箭头、金额数字已改为独立 PNG 绘制。
- LevelUp 小部件：OK、职业图标已改为独立 PNG 绘制。

下一批优先候选：

- `337,494,12,12` 关闭按钮：大量 screen 共享，但目前需要先从 `cursors.png` 精确切出独立 PNG。
- `421,459,11,12` / `421,472,11,12` 上下滚动箭头：Shop、FurnitureCatalogue、QuestLog 共享，需要先拆图。
- QuestLog 的 reward box、coin、new/done/dot/timed/object arrow：适合成组拆图，但必须先逐个确认源尺寸。

任何一步出现“视觉不像原版”，先回退该步，再调整标准。