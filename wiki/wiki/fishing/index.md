# 钓鱼系统

星野牧歌完整还原了星露谷物语的钓鱼系统，包括钓鱼小游戏、鱼饵与渔具、传说鱼、宝箱等全部机制。

## 核心数据

- **84 种鱼类**（含传说鱼、蟹笼捕捞、特殊鱼类）
- **7 种鱼饵**（含针对性鱼饵、磁铁）
- **10 种渔具**（浮标 / 旋饵 / 钩子 / 特殊）
- **5 种钓竿**

## 钓竿

| 钓竿 | 鱼饵槽 | 渔具槽 | 购买价格 | 购买地点 | 说明 |
|------|--------|--------|---------|---------|------|
| 竹钓竿 | — | — | — | 初始拥有 | 基础钓竿 |
| 训练竿 | — | — | 25g | 威利鱼店 | 强制普通品质，练习用 |
| 玻璃纤维钓竿 | ✅ | — | 1,800g | 威利鱼店 | 可装鱼饵 |
| 铱制钓竿 | ✅ | 1 | 7,500g | 威利鱼店 | 可装鱼饵 + 渔具 |
| 高级铱制钓竿 | ✅ | 2 | — | 特殊获取 | 可装鱼饵 + 两个渔具 |

::: warning 训练竿
训练竿不能装鱼饵和渔具，且钓到的鱼**强制普通品质**（完美钓鱼也不会提升品质）。适合新手练习小游戏操作。
:::

## 品质与售价

所有鱼类的品质售价遵循统一倍率：

| 品质 | 倍率 |
|------|------|
| 普通 | ×1.0 |
| 银星 ⭐ | ×1.25 |
| 金星 ⭐⭐ | ×1.5 |
| 铱星 ⭐⭐⭐ | ×2.0 |

## 经验值公式

| 结果 | 经验值 |
|------|--------|
| 成功钓鱼 | $10 + \text{鱼难度值}$ |
| 钓鱼失败（鱼逃脱） | 2 XP |
| 钓到非鱼物品（垃圾） | 3 XP |

## 能量消耗

每次抛竿消耗：$8.0 - \text{钓鱼等级} \times 0.1$

| 等级 | 能量 |
|------|------|
| 0 | 8.0 |
| 5 | 7.5 |
| 10 | 7.0 |

## 难度等级

鱼类的钓鱼难度范围为 0~110，决定了小游戏中鱼的行为速度和移动模式：

| 难度范围 | 等级 | 代表鱼类 |
|---------|------|---------|
| 0~25 | 简单 | 鲤鱼(15)、太阳鱼(24)、鲱鱼(25) |
| 26~50 | 中等 | 沙丁鱼(30)、鲷鱼(40)、大嘴鲈鱼(50) |
| 51~80 | 困难 | 鳗鱼(70)、鲟鱼(78)、鲶鱼(78) |
| 81~110 | 传说 | 蛇齿单线鱼(85)、绯红鱼(95)、传说之鱼(110) |

## 鱼类行为模式

小游戏中鱼的移动方式由行为类型决定：

| 类型 | 编号 | 特征 |
|------|------|------|
| 混合型 | 0 | 常规游动，最常见 |
| 平滑型 | 1 | 匀速移动，偶尔冲刺 |
| 冲刺型 | 2 | 快速冲刺，motionMultiplier = 20× |
| 漂浮型 | 3 | 偏向水面停留 |
| 下沉型 | 4 | 偏向底部停留 |

## 蟹笼系统

蟹笼放置在水中后每天检查一次，需要装鱼饵才能工作。

| 参数 | 数值 |
|------|------|
| 基础垃圾率 | 20% |
| 高级/万能鱼饵 | 垃圾率减半（10%） |
| 水手职业（Mariner） | 垃圾率 0%（100% 鱼类） |
| 诱鱼大师（Luremaster） | 鱼饵无消耗 |
| 万能鱼饵 | 25% 概率数量翻倍 |
| 高级鱼饵 | 品质 +1 档，50% 概率双倍产出 |
| 魔法鱼饵 | 无视季节/天气限制 |

---

## 详细页面

<div class="nav-grid stagger">
  <a href="/wiki/fishing/minigame" class="nav-card glass-card">
    <span class="nav-card-icon">🎮</span>
    <div class="nav-card-title">钓鱼小游戏</div>
    <div class="nav-card-desc">小游戏机制、完美钓鱼、宝箱系统</div>
  </a>
  <a href="/wiki/fishing/bait" class="nav-card glass-card">
    <span class="nav-card-icon">🪱</span>
    <div class="nav-card-title">鱼饵系统</div>
    <div class="nav-card-desc">6 种鱼饵 + 针对性鱼饵的效果与获取</div>
  </a>
  <a href="/wiki/fishing/tackle" class="nav-card glass-card">
    <span class="nav-card-icon">🪝</span>
    <div class="nav-card-title">渔具与浮标</div>
    <div class="nav-card-desc">10 种渔具的效果与搭配策略</div>
  </a>
  <a href="/wiki/fishing/ocean" class="nav-card glass-card">
    <span class="nav-card-icon">🌊</span>
    <div class="nav-card-title">海洋鱼类</div>
    <div class="nav-card-desc">海滩可钓取的全部鱼类</div>
  </a>
  <a href="/wiki/fishing/river" class="nav-card glass-card">
    <span class="nav-card-icon">🏞️</span>
    <div class="nav-card-title">河流鱼类</div>
    <div class="nav-card-desc">城镇与森林河流的鱼类</div>
  </a>
  <a href="/wiki/fishing/lake" class="nav-card glass-card">
    <span class="nav-card-icon">🏔️</span>
    <div class="nav-card-title">湖泊鱼类</div>
    <div class="nav-card-desc">山顶湖泊可钓取的鱼类</div>
  </a>
  <a href="/wiki/fishing/legendary" class="nav-card glass-card">
    <span class="nav-card-icon">🏆</span>
    <div class="nav-card-title">传说鱼</div>
    <div class="nav-card-desc">每条只能钓一次的顶级鱼类</div>
  </a>
</div>
