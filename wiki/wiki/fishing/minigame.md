# 🎮 钓鱼小游戏

当鱼上钩后，会进入钓鱼小游戏界面。你需要操控「钓鱼条」追踪鱼的位置来成功钓上鱼。

## 基本操作

- **按住鼠标左键**：钓鱼条上升（重力 = −0.25/帧）
- **松开鼠标左键**：钓鱼条下落（重力 = +0.25/帧）
- 将钓鱼条保持在鱼的图标上方，进度条会上涨；偏离时进度条下降
- 进度条满（≥1.0）即钓上，归零（≤0.0）则鱼逃脱

### 游戏常数

| 参数 | 数值 |
|------|------|
| 浮标轨道高度 | 548 像素 |
| 钓鱼条轨道高度 | 568 像素 |
| UI 缩放比例 | 0.7 |
| 帧间隔 | ~16 ms |

---

## 钓鱼条大小

钓鱼条的长度由以下公式决定：

$$
\text{条长} = (96 + \text{钓鱼等级} \times 8) + \text{渔具加成} + \text{鱼饵加成}\ (\text{px})
$$

| 来源 | 加成 |
|------|------|
| 基础 | 96 px |
| 每级钓鱼 | +8 px |
| <img src="/img/tackle/cork_bobber.png" class="item-icon"> [软木塞浮标](/wiki/fishing/tackle#软木塞浮标-cork-bobber) | +24 px / 个 |
| <img src="/img/bait/deluxe_bait.png" class="item-icon"> [高级鱼饵](/wiki/fishing/bait#高级鱼饵-deluxe-bait) | +12 px |

| 等级 | 裸装 | +软木塞 | +软木塞+高级鱼饵 |
|------|------|---------|----------------|
| 0 | 96 px | 120 px | 132 px |
| 5 | 136 px | 160 px | 172 px |
| 10 | 176 px | 200 px | 212 px |

---

## 进度条机制

### 捕获进度

| 状态 | 速率 | 说明 |
|------|------|------|
| 鱼在钓鱼条内 | **+0.002/帧** | 每帧（16ms）增加 0.2% |
| 鱼离开钓鱼条 | **−0.003/帧** | 默认逃脱速率 |
| 使用 <img src="/img/tackle/trap_bobber.png" class="item-icon"> [陷阱浮标](/wiki/fishing/tackle#陷阱浮标-trap-bobber)（1个） | −0.002/帧 | 逃脱减缓 33% |
| 使用 <img src="/img/tackle/trap_bobber.png" class="item-icon"> [陷阱浮标](/wiki/fishing/tackle#陷阱浮标-trap-bobber)（2个） | −0.0015/帧 | 逃脱减缓 50% |

- 进度 ≥ 1.0 → 捕获成功
- 进度 ≤ 0.0 → 鱼逃脱

### 重力与按键

| 条件 | 重力值 |
|------|--------|
| 按住鼠标 | −0.25（上升） |
| 松开鼠标 | +0.25（下降） |
| 鱼在条内（无 <img src="/img/tackle/barbed_hook.png" class="item-icon"> [倒刺钩](/wiki/fishing/tackle#倒刺钩-barbed-hook)） | 重力 ×0.6 |
| 鱼在条内（有 <img src="/img/tackle/barbed_hook.png" class="item-icon"> [倒刺钩](/wiki/fishing/tackle#倒刺钩-barbed-hook)） | 重力 ×0.3 |

<img src="/img/tackle/lead_bobber.png" class="item-icon"> [铅制浮标](/wiki/fishing/tackle#铅制浮标-lead-bobber)效果：钓鱼条触底时反弹强度 = bounceMultiplier × 默认反弹。1 个铅制浮标 = 0.1（几乎不弹）。

---

## 难度机制

每种鱼都有一个 **难度值**（0~110），影响鱼的移动速度和方向变化频率：

| 难度范围 | 等级 | 代表鱼类 |
|---------|------|---------|
| 0~25 | 简单 | 鲤鱼(15)、太阳鱼(24)、鲱鱼(25) |
| 26~50 | 中等 | 沙丁鱼(30)、鲷鱼(40)、比目鱼(50) |
| 51~80 | 困难 | 鳗鱼(70)、鲟鱼(78)、鲶鱼(78) |
| 81~110 | 传说 | 蛇齿单线鱼(85)、绯红鱼(95)、传说之鱼(110) |

### 鱼类行为模式

鱼的运动类型决定了小游戏中的移动规律：

| 类型 | 编号 | 特征 | 机制细节 |
|------|------|------|---------|
| 混合型 | 0 | 常规游动 | 无特殊加速度 |
| 平滑型 | 1 | 匀速滑动，偶尔冲刺 | 可能突然移动 −100~−51 或 50~101 像素（概率与难度相关） |
| 冲刺型 | 2 | 快速冲刺 | motionMultiplier = 20.0，加速度按难度平方缩放 |
| 漂浮型 | 3 | 偏向顶部 | 加速度 +0.01/帧，范围 [−1.5, +1.5] |
| 下沉型 | 4 | 偏向底部 | 加速度 −0.01/帧，范围 [−1.5, +1.5] |

### 鱼的移动触发公式

$$
P(\text{改变方向}) = \frac{\text{difficulty} \times \text{motionMultiplier}}{4000}\ (\text{每帧})
$$

触发后目标位置偏移量：
$$
\text{percent} = \frac{\min(99,\ \text{difficulty} + \text{random}(10, 44))}{100}
$$

---

## 鱼体型与品质

### 体型计算

$$
\text{fishSize} = \max(0.2,\ \text{castPower}) \times \frac{1 + \lfloor\text{fishingLevel}/2\rfloor + \text{random}(0,\ \max(6,\ 1 + \lfloor\text{fishingLevel}/2\rfloor))}{5.0}
$$

- 如果使用匹配的针对性鱼饵或挑战鱼饵：体型 ×1.2
- 最终 ±10% 随机波动
- 体型值 clamp 到 [0, 1]

### 初始品质（由体型决定）

| 体型范围 | 品质 |
|---------|------|
| < 0.33 | 普通（0） |
| 0.33 ~ 0.65 | 银星（1） |
| ≥ 0.66 | 金星（2） |

### 品质提升链

品质会依次受到以下因素提升：

| 步骤 | 条件 | 效果 |
|------|------|------|
| 1 | 装备 <img src="/img/tackle/quality_bobber.png" class="item-icon"> [品质浮标](/wiki/fishing/tackle#品质浮标-quality-bobber) | 品质 +1（上限铱星） |
| 2 | 训练竿 | 强制品质 = 0（覆盖一切） |
| 3 | 完美钓鱼且品质 ≥ 金星 | 品质 → 铱星 |
| 3 | 完美钓鱼且品质 ≥ 银星 | 品质 → 金星 |

**品质等级**：0 = 普通，1 = 银星，2 = 金星，3 = 铱星

::: details 最优品质组合示例
目标：铱星品质
1. 高抛竿力度（castPower 接近 1.0）→ 高体型 → 初始金星
2. 品质浮标 → 金星 +1 = 铱星
3. 或者：初始银星 + 完美钓鱼 → 金星 + 品质浮标 → 铱星
:::

---

## 完美钓鱼

如果在整个钓鱼过程中，鱼 **始终没有离开过钓鱼条**，则判定为「完美钓鱼」：

- 品质额外提升（见上方品质提升链）
- 搭配挑战鱼饵时获得满额 3 条鱼

---

## 宝箱系统

### 宝箱出现概率

$$
P(\text{宝箱}) = 0.15 + \text{luckBuff} \times 0.005 + \text{fishingLevel} \times 0.005 + \frac{\text{dailyLuck}}{2} + P_\text{海盗} + P_\text{磁铁} + P_\text{寻宝者}
$$

| 因素 | 加成 |
|------|------|
| 基础概率 | 15% |
| 幸运 Buff | +0.5% / 级 |
| 钓鱼等级 | +0.5% / 级 |
| 日运气 | dailyLuck ÷ 2 |
| 海盗职业 | +15% |
| <img src="/img/bait/magnet.png" class="item-icon"> [磁铁](/wiki/fishing/bait#磁铁-magnet) | +15% |
| <img src="/img/tackle/curiosity_lure.png" class="item-icon"> [寻宝者](/wiki/fishing/tackle#寻宝者-treasure-hunter)渔具 | +5% / 个 |

::: details 概率计算示例
钓鱼 10 级 + 海盗职业 + 寻宝者 + 日运气 0.1：
- 0.15 + 0.05 + 0.05 + 0.15 + 0.05 = **0.45（45%）**+ dailyLuck/2
:::

### 金色宝箱

钓鱼等级 ≥ 10 时有机会出现金色宝箱：

$$
P(\text{金色}) = \text{clamp}(0.25 + \text{dailyLuck},\ 0,\ 1)
$$

金色宝箱的掉落池更加珍贵（钻石、棱彩碎片等）。

### 宝箱捕获机制

宝箱出现后需要将钓鱼条移到宝箱图标上收集：

| 状态 | 速率 |
|------|------|
| 钓鱼条覆盖宝箱 | +1.35% / 帧 |
| 钓鱼条未覆盖宝箱 | −1.0% / 帧 |
| 捕获完成 | 进度 ≥ 100% |

::: tip 寻宝者渔具
装备寻宝者渔具后，收集宝箱期间鱼的进度条不会下降，可以放心全力收集宝箱。
:::

---

### 宝箱掉落表

宝箱掉落分为 **普通池**、**稀有池**（15% 概率触发）和 **金色池**（金色宝箱 50% 概率使用）。

每次开启宝箱，从首次 100% 掉落开始，随后每轮以衰减系数继续掷骰：
- 普通宝箱衰减：×0.4（每轮 40% 概率继续）
- 金色宝箱衰减：×0.6（每轮 60% 概率继续）

#### 普通掉落池（11 种）

| 物品 | 数量 | 权重 | 概率 |
|------|------|------|------|
| 煤炭 | 3~8 | 140 | 19.3% |
| 铜矿石 | 2~7 | 120 | 16.6% |
| 铁矿石 | 2~6 | 100 | 13.8% |
| 石头 | 5~15 | 80 | 11.0% |
| <img src="/img/minerals/quartz.png" class="item-icon"> 石英 | 1~4 | 60 | 8.3% |
| 晶洞 | 1~2 | 55 | 7.6% |
| <img src="/img/minerals/earth_crystal.png" class="item-icon"> 大地水晶 | 1~3 | 45 | 6.2% |
| <img src="/img/minerals/frozen_tear.png" class="item-icon"> 冰泪石 | 1~3 | 35 | 4.8% |
| <img src="/img/minerals/fire_quartz.png" class="item-icon"> 火石英 | 1~3 | 35 | 4.8% |
| 冰封晶洞 | 1~2 | 35 | 4.8% |
| 岩浆晶洞 | 1~2 | 25 | 3.4% |

**权重总和：730**，概率 = 权重 ÷ 730

#### 稀有掉落池（15% 概率触发）

| 物品 | 数量 | 权重 | 最低等级 |
|------|------|------|---------|
| 金矿石 | 1~3 | 90 | 0 |
| <img src="/img/minerals/diamond.png" class="item-icon"> 钻石 | 1~2 | 50 | 0 |
| <img src="/img/minerals/amethyst.png" class="item-icon"> 紫水晶 | 1~3 | 45 | 0 |
| <img src="/img/minerals/topaz.png" class="item-icon"> 黄玉 | 1~3 | 45 | 0 |
| 万象晶洞 | 1 | 45 | 0 |
| <img src="/img/minerals/jade.png" class="item-icon"> 翡翠 | 1~3 | 40 | 0 |
| <img src="/img/minerals/aquamarine.png" class="item-icon"> 海蓝宝石 | 1~2 | 35 | 0 |
| <img src="/img/minerals/ruby.png" class="item-icon"> 红宝石 | 1~2 | 35 | 0 |
| <img src="/img/minerals/emerald.png" class="item-icon"> 绿宝石 | 1~2 | 35 | 0 |
| <img src="/img/bait/wild_bait.png" class="item-icon"> [万能鱼饵](/wiki/fishing/bait#万能鱼饵-wild-bait) | 2~6 | 35 | 4 |
| <img src="/img/bait/deluxe_bait.png" class="item-icon"> [高级鱼饵](/wiki/fishing/bait#高级鱼饵-deluxe-bait) | 5 | 35 | 6 |
| <img src="/img/tackle/sonar_bobber.png" class="item-icon"> [声纳浮漂](/wiki/fishing/tackle#声纳浮漂-sonar-bobber) | 1 | 25 | 6 |
| 鱼汤 | 1 | 16 | 2 |
| 虾鸡尾酒 | 1 | 14 | 2 |
| 古董/化石类 | 各 1 | 1~10 | 2 |
| 恐龙蛋 | 1 | 1 | 6 |

::: info 古董文物
稀有池包含大量古董和化石，每种权重 5~10，需钓鱼 ≥2 级：骨骼尾巴、鹦鹉螺化石、两栖类化石、古代玩偶、精灵首饰、口香糖棒、装饰扇、古代宝剑、生锈汤匙/马刺/齿轮、鸡雕像、远古种子、石器、干燥海星、船锚、玻璃碎片、骨笛、石斧、稀有光碟等。恐龙蛋权重仅 1，为最稀有掉落。
:::

#### 金色掉落池（金色宝箱限定）

| 物品 | 数量 | 权重 | 概率 | 最低等级 |
|------|------|------|------|---------|
| <img src="/img/minerals/diamond.png" class="item-icon"> 钻石 | 2~5 | 100 | 26.2% | 0 |
| <img src="/img/bait/challenge_bait.png" class="item-icon"> [挑战鱼饵](/wiki/fishing/bait#挑战鱼饵-challenge-bait) | 3~6 | 45 | 11.8% | 0 |
| 仙尘 | 3~6 | 40 | 10.5% | 0 |
| 铱矿石 | 2~6 | 40 | 10.5% | 6 |
| <img src="/img/minerals/prismatic_shard.png" class="item-icon"> 棱彩碎片 | 1~2 | 35 | 9.2% | 0 |
| <img src="/img/bait/magnet.png" class="item-icon"> [磁铁](/wiki/fishing/bait#磁铁-magnet) | 3~6 | 30 | 7.9% | 0 |
| 金色动物饼干 | 1 | 20 | 5.2% | 8 |
| 铱锭 | 1~2 | 18 | 4.7% | 8 |
| 鱼汤 | 1~2 | 18 | 4.7% | 2 |
| 虾鸡尾酒 | 1~2 | 16 | 4.2% | 2 |
| 放射性矿石 | 1~3 | 15 | 3.9% | 8 |
| 放射性锭 | 1 | 8 | 2.1% | 10 |

**权重总和：382**（不含等级不足时被过滤的项），概率 = 权重 ÷ 总和

#### 后备掉落

如果玩家钓鱼等级不满足所有掉落池条件，回退到后备掉落：

| 物品 | 数量 |
|------|------|
| 鱼饵 | 5~15 |

---

## 经验值

| 结果 | 经验值公式 |
|------|----------|
| 成功钓鱼 | $10 + \text{鱼难度值}$ |
| 钓鱼失败 | 2 XP |
| 钓到非鱼物品（垃圾） | 3 XP |

::: details 经验值示例
| 鱼 | 难度 | 成功经验 |
|----|------|---------|
| 鲤鱼 | 15 | 25 XP |
| 比目鱼 | 50 | 60 XP |
| 章鱼 | 95 | 105 XP |
| 传说之鱼 | 110 | 120 XP |
:::

---

## 能量消耗

每次抛竿消耗能量：

$$
\text{能量消耗} = 8.0 - \text{钓鱼等级} \times 0.1
$$

| 等级 | 能量消耗 |
|------|---------|
| 0 | 8.0 |
| 5 | 7.5 |
| 10 | 7.0 |
