# 采集系统

星野牧歌中有丰富的野外采集物散布在各个区域，按季节轮换刷新。采集是早期获取金币和能量的重要途径。

## 核心机制

### 采集方式

野外采集物以方块形式生成在世界中，**右键或破坏**均可采集。每次采集固定获得 **7 点采集经验**。

### 品质系统

采集品同样有四种品质：<span class="sc-badge sc-badge-green">普通</span> <span class="sc-badge sc-badge-blue">银星</span> <span class="sc-badge sc-badge-purple">金星</span> <span class="sc-badge sc-badge-amber">铱星</span>

品质由采集技能等级决定：
- **金星概率** = 采集等级 / 30
- **银星概率** = 采集等级 / 15
- **植物学家职业**：所有采集品自动为铱星品质

### 采集者职业

拥有「采集者」职业时，每次采集有 **20%** 概率获得双倍收获。

### 售价公式

采集品的品质售价遵循统一倍率：

| 品质 | 倍率 |
|------|------|
| 普通 | ×1.0 |
| 银星 ⭐ | ×1.25 |
| 金星 ⭐⭐ | ×1.5 |
| 铱星 ⭐⭐⭐ | ×2.0 |

### 刷新区域

采集物按区域自然刷新，每日每区域随机刷新 **1~4 个**，最多同时存在 **6 个**。系统每天会尝试最多 **30 次**放置每个物品，放置位置需在指定矩形范围内。

::: tip 刷新条件
- **城镇 / 森林 / 山地**：必须在草方块上方、且能看到天空
- **海滩**：任何坚实方块上方即可，无需草方块
- 占用位置为空气或可替换植物（短草、高草、蕨类、小花等）
:::

---

## 刷新区域详细坐标

### 🏘️ 城镇 Town

| 子区域 | X 范围 | Z 范围 |
|--------|--------|--------|
| 区域 1 | 19 ~ 159 | 193 ~ 221 |
| 区域 2 | 21 ~ 51 | 96 ~ 112 |
| 区域 3 | -18 ~ -1 | 69 ~ 80 |

**地面要求**：草方块 + 可见天空

| 物品 | 季节 | 生成概率 |
|------|------|---------|
| <img src="/img/forage/daffodil.png" class="item-icon"> [黄水仙](/wiki/foraging/spring#黄水仙-daffodil) | 春 | 90% |
| <img src="/img/forage/sweet_pea.png" class="item-icon"> [甜豌豆](/wiki/foraging/summer#甜豌豆-sweet-pea) | 夏 | 90% |
| <img src="/img/forage/blackberry.png" class="item-icon"> [黑莓](/wiki/foraging/fall#黑莓-blackberry) | 秋 | 60% |
| <img src="/img/forage/crocus.png" class="item-icon"> [番红花](/wiki/foraging/winter#番红花-crocus) | 冬 | 70% |
| <img src="/img/forage/holly.png" class="item-icon"> [冬青树](/wiki/foraging/winter#冬青树-holly) | 冬 | 50% |
| <img src="/img/forage/crystal_fruit.png" class="item-icon"> [水晶果](/wiki/foraging/winter#水晶果-crystal-fruit) | 冬 | 10% |

### 🌲 森林 Cindersap Forest

| 子区域 | X 范围 | Z 范围 |
|--------|--------|--------|
| 区域 1 | 134 ~ 197 | -194 ~ -110 |
| 区域 2 | 231 ~ 252 | -138 ~ -114 |
| 区域 3 | 221 ~ 302 | -11 ~ 35 |

**地面要求**：草方块 + 可见天空

| 物品 | 季节 | 生成概率 |
|------|------|---------|
| <img src="/img/forage/wild_horseradish.png" class="item-icon"> [野山葵](/wiki/foraging/spring#野山葵-wild-horseradish) | 春 | 90% |
| <img src="/img/forage/dandelion.png" class="item-icon"> [蒲公英](/wiki/foraging/spring#蒲公英-dandelion) | 春 | 90% |
| <img src="/img/forage/spice_berry.png" class="item-icon"> [香味浆果](/wiki/foraging/summer#香味浆果-spice-berry) | 夏 | 60% |
| <img src="/img/forage/sweet_pea.png" class="item-icon"> [甜豌豆](/wiki/foraging/summer#甜豌豆-sweet-pea) | 夏 | 90% |
| <img src="/img/forage/blackberry.png" class="item-icon"> [黑莓](/wiki/foraging/fall#黑莓-blackberry) | 秋 | 90% |
| <img src="/img/forage/crocus.png" class="item-icon"> [番红花](/wiki/foraging/winter#番红花-crocus) | 冬 | 90% |
| <img src="/img/forage/crystal_fruit.png" class="item-icon"> [水晶果](/wiki/foraging/winter#水晶果-crystal-fruit) | 冬 | 90% |
| <img src="/img/forage/holly.png" class="item-icon"> [冬青树](/wiki/foraging/winter#冬青树-holly) | 冬 | 50% |

### ⛰️ 山地 Mountain

| 子区域 | X 范围 | Z 范围 |
|--------|--------|--------|
| 区域 1 | -196 ~ -239 | 161 ~ 188 |
| 区域 2 | -245 ~ -207 | 233 ~ 263 |
| 区域 3 | -324 ~ -292 | 289 ~ 309 |
| 区域 4 | -105 ~ -72 | 294 ~ 312 |

**地面要求**：草方块 + 可见天空

| 物品 | 季节 | 生成概率 |
|------|------|---------|
| <img src="/img/forage/leek.png" class="item-icon"> [韭葱](/wiki/foraging/spring#韭葱-leek) | 春 | 70% |
| <img src="/img/forage/wild_horseradish.png" class="item-icon"> [野山葵](/wiki/foraging/spring#野山葵-wild-horseradish) | 春 | 50% |
| <img src="/img/forage/spice_berry.png" class="item-icon"> [香味浆果](/wiki/foraging/summer#香味浆果-spice-berry) | 夏 | 50% |
| <img src="/img/forage/sweet_pea.png" class="item-icon"> [甜豌豆](/wiki/foraging/summer#甜豌豆-sweet-pea) | 夏 | 80% |
| <img src="/img/forage/wild_plum.png" class="item-icon"> [野梅](/wiki/foraging/fall#野梅-wild-plum) | 秋 | 40% |
| <img src="/img/forage/hazelnut.png" class="item-icon"> [榛子](/wiki/foraging/fall#榛子-hazelnut) | 秋 | 90% |
| <img src="/img/forage/crystal_fruit.png" class="item-icon"> [水晶果](/wiki/foraging/winter#水晶果-crystal-fruit) | 冬 | 85% |
| <img src="/img/forage/crocus.png" class="item-icon"> [番红花](/wiki/foraging/winter#番红花-crocus) | 冬 | 90% |
| <img src="/img/forage/holly.png" class="item-icon"> [冬青树](/wiki/foraging/winter#冬青树-holly) | 冬 | 50% |

### 🏖️ 海滩 Beach

| 子区域 | X 范围 | Z 范围 | 备注 |
|--------|--------|--------|------|
| 区域 1 | -293 ~ -192 | -182 ~ -139 | — |
| 区域 2 | -376 ~ -326 | -148 ~ -173 | 权重 ×1.5 |

**地面要求**：任何坚实方块（无需草方块，无需可见天空）

| 物品 | 季节 | 生成概率 |
|------|------|---------|
| <img src="/img/forage/coral.png" class="item-icon"> 珊瑚 | 全年 | 80% |
| <img src="/img/forage/sea_urchin.png" class="item-icon"> 海胆 | 全年 | 50% |
| <img src="/img/forage/rainbow_shell.png" class="item-icon"> 彩虹贝壳 | 夏 | 50% |
| <img src="/img/forage/nautilus_shell.png" class="item-icon"> 鹦鹉螺 | 冬 | 80% |

---

## 按季节浏览

<div class="nav-grid stagger">
  <a href="/wiki/foraging/spring" class="nav-card glass-card">
    <span class="nav-card-icon">🌸</span>
    <div class="nav-card-title">春季采集</div>
    <div class="nav-card-desc">野山葵、黄水仙、韭葱、蒲公英、大葱</div>
  </a>
  <a href="/wiki/foraging/summer" class="nav-card glass-card">
    <span class="nav-card-icon">☀️</span>
    <div class="nav-card-title">夏季采集</div>
    <div class="nav-card-desc">香味浆果、甜豌豆、蕨菜</div>
  </a>
  <a href="/wiki/foraging/fall" class="nav-card glass-card">
    <span class="nav-card-icon">🍂</span>
    <div class="nav-card-title">秋季采集</div>
    <div class="nav-card-desc">野梅、榛子、黑莓</div>
  </a>
  <a href="/wiki/foraging/winter" class="nav-card glass-card">
    <span class="nav-card-icon">❄️</span>
    <div class="nav-card-title">冬季采集</div>
    <div class="nav-card-desc">冬根、水晶果、番红花、冬青树、雪山药</div>
  </a>
</div>

---

## 特殊产地

### 🍄 蘑菇

蘑菇类物品不支持品质系统，不会在上述四大区域自然刷新。

| 物品 | 售价 | 能量 | 来源标签 | 出现季节 |
|------|------|------|---------|---------|
| <img src="/img/forage/common_mushroom.png" class="item-icon"> 普通蘑菇 | 40g | 38 | 矿洞 | 春、秋 |
| <img src="/img/forage/red_mushroom.png" class="item-icon"> 红蘑菇 | 75g | -50 | ⚠️ 矿洞 / 洞穴 | 夏、秋 |
| <img src="/img/forage/purple_mushroom.png" class="item-icon"> 紫蘑菇 | 250g | 125 | 矿洞 | 全季节 |
| <img src="/img/forage/morel.png" class="item-icon"> 羊肚菌 | 150g | 20 | 洞穴 | 仅春季 |
| <img src="/img/forage/chanterelle.png" class="item-icon"> 鸡油菌 | 160g | 75 | 洞穴 | 仅秋季 |
| <img src="/img/forage/magma_cap.png" class="item-icon"> 熔岩菇 | 400g | 175 | 特殊 | 生长在熔岩池旁 |

### 🏜️ 沙漠物品

| 物品 | 售价 | 能量 | 出现季节 |
|------|------|------|---------|
| <img src="/img/forage/coconut.png" class="item-icon"> 椰子 | 100g | 不可食用 | 全季节 |
| <img src="/img/forage/cactus_fruit.png" class="item-icon"> 仙人掌果子 | 75g | 38 | 全季节 |

### ⛏️ 矿洞物品

| 物品 | 售价 | 能量 | 出现季节 |
|------|------|------|---------|
| <img src="/img/forage/cave_carrot.png" class="item-icon"> 山洞萝卜 | 25g | 18 | 全季节 |
| <img src="/img/forage/winter_root.png" class="item-icon"> [冬根](/wiki/foraging/winter#冬根-winter-root) | 70g | 25 | 仅冬季 |

### 🌿 秘密森林物品

| 物品 | 售价 | 能量 | 出现季节 |
|------|------|------|---------|
| <img src="/img/forage/fiddlehead_fern.png" class="item-icon"> [蕨菜](/wiki/foraging/summer#蕨菜-fiddlehead-fern) | 90g | 25 | 仅夏季 |
| <img src="/img/forage/ginger.png" class="item-icon"> 姜 | 60g | 25 | 全季节 |
