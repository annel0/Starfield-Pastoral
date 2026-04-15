# BundleScreen.java ↔ SDV JunimoNoteMenu.cs 逐行对齐修复计划

> 源文件: `源文件/StardewValley.Menus/JunimoNoteMenu.cs` (1826行)
> 源文件: `源文件/StardewValley.Menus/Bundle.cs` (400行)
> 目标文件: `src/.../communitycenter/client/BundleScreen.java` (~1092行)

---

## Fix #1 — 删除SDV不存在的"奖励物品预览"

**SDV原版**: 详情页只有 bundle icon (872,88) + name label。没有任何奖励物品图标。
奖励通过 **presentButton** (总览页) → `openRewardsMenu()` → `ItemGrabMenu` 领取。

**我们的错误**: 在 `renderBundleDetail()` 里 (1000,88) 绘制了 `rewardStack` + tooltip。
这是完全凭空捏造的功能。

**修复**:
- 删除 `BundleClaimRewardPayload.parseRewardString(def.rewardString())` 渲染
- 删除 rewardStack tooltip 代码
- 保留 presentButton 在总览页的行为

---

## Fix #2 — ingredient slot hover 高亮方式改为贴图切换

**SDV原版** `performHoverAction()` L1288-1303:
```csharp
if (heldItem != null) {
    foreach (var c2 in ingredientSlots) {
        if (c2.bounds.Contains(x,y) && CanBePartiallyOrFullyDonated(heldItem)
            && (partialDonationItem == null || c2.item == partialDonationItem))
        {
            c2.sourceRect.X = 530;  // ← 高亮贴图
            c2.sourceRect.Y = 262;
        } else {
            c2.sourceRect.X = 512;  // ← 默认贴图
            c2.sourceRect.Y = 244;
        }
    }
}
```

**我们的错误**: 用 alpha 0.5→1.0 切换。SDV 根本不用 alpha 区分空/hover。

**修复**:
- 空slot始终用 **(512, 244, 18, 18)** 全亮
- hover时 + 手持匹配物品 → 切换到 **(530, 262, 18, 18)** (高亮版)
- 删除 alpha 0.5 逻辑
- SDV 的 draw() 中: 空slot和filled slot 都用 `c.draw(b, color, depth)` 先画背景，
  再用 `c.drawItem(b, 4, 4)` 画物品偏移4,4 screen px

SDV draw() 对 slot 的完整逻辑 (L1420-1440):
```csharp
foreach (var c in ingredientSlots) {
    float alpha_mult = 1f;
    if (partialDonationItem != null && c.item != partialDonationItem) alpha_mult = 0.25f;
    if (c.item == null || (partialDonationItem != null && c.item == partialDonationItem))
        c.draw(b, (fromGameMenu ? Color.LightGray*0.5f : Color.White) * alpha_mult, 0.89f);
    c.drawItem(b, 4, 4, alpha_mult);
}
```
→ 只有 **没物品** 或 **是partialDonation物品** 的slot才画背景！filled slot不画背景只画物品！

---

## Fix #3 — ingredient list 阴影贴图

**SDV原版** draw() L1442-1458:
```csharp
for (int i = 0; i < ingredientList.Count; i++) {
    bool completed = (i < bundle?.ingredients?.Count && bundle.ingredients[i].completed);
    if (!completed)
        b.Draw(Game1.shadowTexture,
            new Vector2(c2.Center.X - shadowTexture.Width*4/2 - 4, c2.Center.Y + 4),
            shadowBounds, White*alpha, 0, Zero, 4f, None, 0.1f);
    if (c2.item != null && c2.visible)
        c2.item.drawInMenu(b, pos, scale/4f, 1f, 0.9f, StackDraw, White*(completed?0.25f:alpha), drawShadow:false);
}
```

**我们的缺失**: 完全没画阴影 blob。

**修复**:
- 在 `IngredientListEntry.draw()` 中，未完成物品下方增加一个椭圆阴影 sprite
- SDV用专门的 `Game1.shadowTexture` (小小的椭圆阴影 12×6 px)
- 我们可以用一个小的半透明黑色椭圆 fill 或者导入shadow贴图

---

## Fix #4 — item 移入使用 MC 原生 tooltip

**用户需求**: 鼠标移到 ingredientList / ingredientSlot 的物品上时，弹出MC原生 tooltip。

**SDV原版**:
- `hoveredItem = inventory.hover(x, y, heldItem)` → 只对**库存物品**显示 tooltip
- `ingredientList` hover → 只设 `hoverText = c.hoverText` (纯文本名字)
- `ingredientSlots` hover → 无 tooltip

**我们的实现**:
- ingredientList 已有 `g.renderTooltip(font, displayName, mouseX, mouseY)` — 简单文本
- 改为: 如果该物品有 ItemStack，直接用 `g.renderTooltip(font, stack, mouseX, mouseY)` 显示MC原生物品tooltip（含属性、描述等）

---

## Fix #5 — 完成动画重写

**SDV原版** `checkIfBundleIsComplete()` L1061-1118:
1. 所有slot填满 → 把heldItem退回库存
2. `communityCenter.bundles[index]` 全部设true
3. `screenSwipe = new ScreenSwipe(0, -1f, -1, width, height)` — 屏幕水平擦除
4. `currentPageBundle.completionAnimation(this, true, 400)` — 延迟400ms后:
   - `takeDownBundleSpecificPage()` — 关闭详情页回到总览
   - orb sprite 播15帧动画(50ms/帧)
   - sparkle 粒子效果(`Utility.sparkleWithinArea()`)
   - "dwop" 音效
5. `canClick = false`
6. `bundleRewards[index] = true`
7. 检查所有bundle是否完成 → `markAreaAsComplete()`

**我们的错误**: 白色全屏闪光 600ms，没有 screenSwipe，没有关闭详情页。

**修复**:
- 删除白色闪光
- 完成时:
  1. 播放 "dwop" 音效
  2. `specificBundlePage = false + clear slots/list` (模拟 takeDownBundleSpecificPage)
  3. 标记 orb 为 complete (animFrame=14)
  4. 刷新 present button 状态
- 屏幕擦除和sparkle可简化 — MC没有对应的 ScreenSwipe 概念，
  可以用一个从左到右的黑色遮罩动画代替

---

## Fix #6 — tempSprites 渲染系统

**SDV原版**:
- `ingredientDepositAnimation()`: (530,244,18,18) 6帧×50ms holdLastFrame + "cowboy_monsterhit" 结束音效
- `completionAnimation()`: sparkle 粒子 (Utility.sparkleWithinArea)
- `shake(1)`: 两个叶子粒子 (TAS 50)
- overview + detail 页都绘制 `tempSprites`

**修复**:
- 添加 `List<TempSprite>` 到 BundleScreen
- 每帧 tick 更新 + 过期移除
- `TempSprite` class: x,y,u,v,w,h,frames,interval,timer,currentFrame,holdLast,lifetime
- deposit 成功后添加 slot 处的6帧动画
- overview 页的 tempSprites 绘制

---

## Fix #7 — canClick 防护

**SDV原版** `update()` L1244-1251:
```csharp
if (screenSwipe != null) {
    canClick = false;
    if (screenSwipe.update(time)) {
        screenSwipe = null;
        canClick = true;
    }
}
```
所有 click handler 顶部检查: `if (!canClick) return;`

**修复**:
- 添加 `boolean canClick = true` 字段
- 完成动画期间 `canClick = false`
- 动画结束后 `canClick = true`
- `mouseClicked()` 顶部加 `if (!canClick) return false;`

---

## Fix #8 — HighlightObjects 库存物品高亮

**SDV原版**:
```csharp
public bool HighlightObjects(Item item) {
    if (currentPageBundle != null && currentPageBundle.depositsAllowed) {
        if (currentPageBundle.IsValidItemForThisIngredientDescription(item, ...))
            return true;
    }
    return false;
}
```
不匹配的库存物品灰暗显示。

**修复**:
- 在 detail 页渲染库存 slot 时，对不匹配当前 bundle 的物品降低 alpha
- MC 没有原生 HighlightObjects，需要在 `renderSlot()` 或通过 mixin 实现
- 最简单方案: override `AbstractContainerScreen.renderSlot()` 对不匹配的 slot 叠加半透明灰色遮罩

---

## 实施顺序

1. ✏️ Fix #1 — 删除虚构的奖励预览 (最简单)
2. ✏️ Fix #7 — canClick 防护 (简单)
3. ✏️ Fix #2 — slot hover 贴图切换 (中等)
4. ✏️ Fix #4 — MC原生tooltip (简单)
5. ✏️ Fix #5 — 完成动画重写 (中等)
6. ✏️ Fix #6 — tempSprites (较大)
7. ✏️ Fix #3 — ingredient list 阴影 (小)
8. ✏️ Fix #8 — HighlightObjects (中等, 可能需要额外class)
