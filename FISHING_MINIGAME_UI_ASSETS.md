# 钓鱼小游戏 UI 贴图清单（对齐 Stardew BobberBar）

本模组的 `FishingMinigameScreen` 会优先使用以下贴图来渲染“星露谷钓鱼条 UI”。

- 贴图放置路径：`src/main/resources/assets/stardewcraft/textures/gui/fishing/`
- 如果贴图不存在：会自动退回到“矩形 UI”（仍可测试逻辑）。

## 必需（建议先做这几张就能看到几乎完整 UI）

1) `bubble.png`
- 尺寸：`52 x 157`
- 原版来源：`Game1.mouseCursors` 的 `Rectangle(652, 1685, 52, 157)`
- 用途：背景气泡/边框装饰（半透明）

2) `track.png`
- 尺寸：`38 x 150`
- 原版来源：`Game1.mouseCursors` 的 `Rectangle(644, 1999, 38, 150)`
- 用途：竖向轨道背景

3) `bar_top.png`
- 尺寸：`9 x 2`
- 原版来源：`Game1.mouseCursors` 的 `Rectangle(682, 2078, 9, 2)`
- 用途：绿色条顶部

4) `bar_mid.png`
- 尺寸：`9 x 1`
- 原版来源：`Game1.mouseCursors` 的 `Rectangle(682, 2081, 9, 1)`
- 用途：绿色条中段（会被纵向拉伸）

5) `bar_bottom.png`
- 尺寸：`9 x 2`
- 原版来源：`Game1.mouseCursors` 的 `Rectangle(682, 2085, 9, 2)`
- 用途：绿色条底部

6) `fish.png`
- 尺寸：`20 x 20`
- 原版来源：`Game1.mouseCursors` 的 `Rectangle(614, 1840, 20, 20)`
- 用途：鱼图标

## 可选（有的话更像原版）

7) `fish_boss.png`
- 尺寸：`20 x 20`
- 原版来源：`Game1.mouseCursors` 的 `Rectangle(634, 1840, 20, 20)`（bossFish 偏移 +20）
- 用途：Boss 鱼图标（若不存在会回退到 `fish.png`）

8) `reel.png`
- 尺寸：`5 x 10`
- 原版来源：`Game1.mouseCursors` 的 `Rectangle(257, 1990, 5, 10)`
- 用途：卷线小图标（会旋转）

9) `treasure.png`
- 尺寸：`20 x 24`
- 原版来源：`Game1.mouseCursors` 的 `Rectangle(638, 1865, 20, 24)`
- 用途：宝箱图标（后续我们补 treasure 逻辑时启用）

10) `treasure_golden.png`
- 尺寸：`20 x 24`
- 原版来源：`Game1.mouseCursors_1_6` 的 `Rectangle(256, 51, 20, 24)`
- 用途：金宝箱图标（后续启用）

## 注意

- 以上贴图建议从你本地正版资源中自行裁剪导出（我们代码只引用路径，不会提供原版资源）。
- 当前实现会按原版的“2倍/4倍绘制比例”去缩放这些小贴图，再根据屏幕大小自适应缩放（fit）。
