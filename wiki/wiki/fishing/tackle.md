# 🪝 渔具与浮标

渔具（Tackle）可以装配在 **铱制钓竿**（1 槽）及 **高级铱制钓竿**（2 槽）中。所有渔具耐久统一为 **20 次使用**，用完后自动消失。

---

## 浮标类

### 品质浮标 Quality Bobber

<ItemCard name="品质浮标" nameEn="Quality Bobber" img="/img/tackle/quality_bobber.png" category="渔具">

> *提高钓到鱼的品质。*

| 属性 | 数值 |
|------|------|
| 效果 | 鱼品质 **+1 档**（普通→银星，银星→金星，金星→铱星） |
| 耐久 | 20 次 |
| 购买价格 | 2,000g（威利鱼店） |
| 售价 | 300g |
| 合成配方 | 无（仅商店购买） |

</ItemCard>

### 铅制浮标 Lead Bobber

<ItemCard name="铅制浮标" nameEn="Lead Bobber" img="/img/tackle/lead_bobber.png" category="渔具">

> *为你的"钓鱼条"添加了重量。阻止它在触底的时候反弹。*

| 属性 | 数值 |
|------|------|
| 反弹系数 | 每个铅制浮标 bounceMult = 0.1（默认反弹强度仅 10%） |
| 叠加效果 | 2 个 = 0.2（20%反弹），几乎不弹 |
| 耐久 | 20 次 |
| 购买价格 | 200g（威利鱼店） |
| 售价 | 150g |
| 合成配方 | 无（仅商店购买） |

</ItemCard>

### 软木塞浮标 Cork Bobber

<ItemCard name="软木塞浮标" nameEn="Cork Bobber" img="/img/tackle/cork_bobber.png" category="渔具">

> *稍微增加你的「钓鱼条」的大小。*

| 属性 | 数值 |
|------|------|
| 钓鱼条加长 | **+24 像素** / 个（可叠加） |
| 等级 0 基础条长 | 96 px → 装备后 120 px |
| 等级 10 基础条长 | 176 px → 装备后 200 px |
| 耐久 | 20 次 |
| 购买价格 | 750g（威利鱼店） |
| 售价 | 250g |
| 合成配方 | 无（仅商店购买） |

</ItemCard>

### 声纳浮漂 Sonar Bobber

<ItemCard name="声纳浮漂" nameEn="Sonar Bobber" img="/img/tackle/sonar_bobber.png" category="渔具">

> *在钓到鱼之前显示上钩的鱼种。*

| 属性 | 数值 |
|------|------|
| 效果 | 小游戏开始前在 HUD 显示鱼种名称 |
| 耐久 | 20 次 |
| 购买价格 | **无法购买** |
| 售价 | 250g |
| 获取途径 | **仅限钓鱼宝箱**稀有掉落（权重 25，需钓鱼 ≥6 级） |

::: warning 获取方式
声纳浮漂不在任何商店中出售，只能从钓鱼宝箱的稀有掉落池获取。建议使用寻宝者渔具 + 磁铁提高宝箱率。
:::

</ItemCard>

### 陷阱浮标 Trap Bobber

<ItemCard name="陷阱浮标" nameEn="Trap Bobber" img="/img/tackle/trap_bobber.png" category="渔具">

> *让你没在拉钩的时候，鱼会逃跑的慢一些。*

鱼离开钓鱼条时，进度条下降速率从默认 **0.003/帧** 降低。

| 数量 | 逃脱速率 | 相比默认 |
|------|---------|---------|
| 0（无） | 0.003/帧 | 基准 |
| 1 个 | 0.002/帧 | 减少 33% |
| 2 个 | 0.0015/帧 | 减少 50% |
| 3 个 | 0.001/帧 | 减少 67%（上限） |

| 属性 | 数值 |
|------|------|
| 耐久 | 20 次 |
| 购买价格 | 500g（威利鱼店） |
| 售价 | 200g |
| 合成配方 | 钓鱼 6 级解锁：1× 铜锭 + 10× 树液 |

</ItemCard>

---

## 钩子类

### 倒刺钩 Barbed Hook

<ItemCard name="倒刺钩" nameEn="Barbed Hook" img="/img/tackle/barbed_hook.png" category="渔具">

> *上钩的鱼更不容易跑掉。对速度慢、弱小的鱼最有效。*

当鱼在钓鱼条内时，会触发重力加速效果使钓鱼条自动跟随鱼的位置。

| 属性 | 数值 |
|------|------|
| 重力减免 | 鱼在条内时重力乘数从 ×0.6 降至 **×0.3** |
| 自动跟随速度 | +0.2（第 1 个），之后每个 +0.05 |
| 附加重力减轻 | 第 2 个起每个额外 ×0.9 |
| 耐久 | 20 次 |
| 购买价格 | 1,000g（威利鱼店） |
| 售价 | 500g |
| 合成配方 | 钓鱼 8 级解锁：1× 铜锭 + 1× 铁锭 + 1× 金锭 |

::: tip 适用场景
倒刺钩最适合对付平滑型（smooth）和下沉型（sinker）鱼类——这些鱼移动缓慢，倒刺钩的自动跟随能完美锁定。对冲刺型（dart）鱼效果较差。
:::

</ItemCard>

---

## 旋饵类

### 旋式鱼饵 Spinner

<ItemCard name="旋式鱼饵" nameEn="Spinner" img="/img/tackle/spinner.png" category="渔具">

> *它的形状会使它在水中打转。稍稍提升钓鱼时的上钩率。*

| 属性 | 数值 |
|------|------|
| 咬钩时间 | **−5000 ms**（与鱼饵乘数叠加后再减） |
| 耐久 | 20 次 |
| 购买价格 | 500g（威利鱼店） |
| 售价 | 250g |
| 合成配方 | 钓鱼 6 级解锁：2× 铁锭 |

</ItemCard>

### 精装旋式鱼饵 Dressed Spinner

<ItemCard name="精装旋式鱼饵" nameEn="Dressed Spinner" img="/img/tackle/dressed_spinner.png" category="渔具">

> *金属片和彩色飘带构成的景象会吸引来鱼。增加钓鱼时的上钩率。*

| 属性 | 数值 |
|------|------|
| 咬钩时间 | **−10000 ms**（旋式鱼饵的 2 倍效果） |
| 耐久 | 20 次 |
| 购买价格 | 1,000g（威利鱼店） |
| 售价 | 500g |
| 合成配方 | 钓鱼 8 级解锁：2× 铁锭 + 1× 布料 |

</ItemCard>

---

## 特殊类

### 寻宝者 Treasure Hunter

<ItemCard name="寻宝者" nameEn="Treasure Hunter" img="/img/tackle/curiosity_lure.png" category="渔具">

> *在收集宝藏的时候鱼是不会逃走的。也可以稍微增加找到宝藏的几率。*

| 属性 | 数值 |
|------|------|
| 宝箱概率 | **+5%** / 个（加法叠加到宝箱公式中） |
| 收集宝箱时 | 鱼不会逃跑（进度条冻结） |
| 耐久 | 20 次 |
| 购买价格 | 750g（威利鱼店） |
| 售价 | 250g |
| 合成配方 | 钓鱼 7 级解锁：2× 金锭 |

</ItemCard>

### 珍稀诱钩 Curiosity Lure

<ItemCard name="珍稀诱钩" nameEn="Curiosity Lure" img="/img/tackle/curiosity_lure.png" category="渔具">

> *可以提高钓到稀有鱼的几率。*

| 属性 | 数值 |
|------|------|
| 效果 | 提高稀有鱼出现概率 |
| 耐久 | 20 次 |
| 购买价格 | **无法购买** |
| 售价 | 500g |
| 获取途径 | 特殊途径（非商店、非合成） |

::: warning 注意
珍稀诱钩不在威利鱼店中出售，也没有合成配方。
:::

</ItemCard>

---

## 渔具获取途径汇总

| 渔具 | 购买价格 | 合成配方 | 解锁等级 |
|------|---------|---------|---------|
| <img src="/img/tackle/quality_bobber.png" class="item-icon"> [品质浮标](#品质浮标-quality-bobber) | 2,000g | — | — |
| <img src="/img/tackle/lead_bobber.png" class="item-icon"> [铅制浮标](#铅制浮标-lead-bobber) | 200g | — | — |
| <img src="/img/tackle/cork_bobber.png" class="item-icon"> [软木塞浮标](#软木塞浮标-cork-bobber) | 750g | — | — |
| <img src="/img/tackle/sonar_bobber.png" class="item-icon"> [声纳浮漂](#声纳浮漂-sonar-bobber) | **无法购买** | — | 宝箱掉落（≥6 级） |
| <img src="/img/tackle/trap_bobber.png" class="item-icon"> [陷阱浮标](#陷阱浮标-trap-bobber) | 500g | 1×铜锭 + 10×树液 | 钓鱼 6 |
| <img src="/img/tackle/barbed_hook.png" class="item-icon"> [倒刺钩](#倒刺钩-barbed-hook) | 1,000g | 1×铜锭 + 1×铁锭 + 1×金锭 | 钓鱼 8 |
| <img src="/img/tackle/spinner.png" class="item-icon"> [旋式鱼饵](#旋式鱼饵-spinner) | 500g | 2×铁锭 | 钓鱼 6 |
| <img src="/img/tackle/dressed_spinner.png" class="item-icon"> [精装旋式鱼饵](#精装旋式鱼饵-dressed-spinner) | 1,000g | 2×铁锭 + 1×布料 | 钓鱼 8 |
| <img src="/img/tackle/curiosity_lure.png" class="item-icon"> [寻宝者](#寻宝者-treasure-hunter) | 750g | 2×金锭 | 钓鱼 7 |
| <img src="/img/tackle/curiosity_lure.png" class="item-icon"> [珍稀诱钩](#珍稀诱钩-curiosity-lure) | **无法购买** | — | 特殊途径 |

---

## 钓鱼条大小公式

渔具对钓鱼条的影响遵循统一公式：

$$
\text{钓鱼条长度} = (96 + \text{钓鱼等级} \times 8) + \text{渔具加成} + \text{鱼饵加成}
$$

| 来源 | 加成 |
|------|------|
| 基础 | 96 px |
| 每级钓鱼 | +8 px |
| <img src="/img/tackle/cork_bobber.png" class="item-icon"> [软木塞浮标](#软木塞浮标-cork-bobber) | +24 px / 个 |
| <img src="/img/bait/deluxe_bait.png" class="item-icon"> [高级鱼饵](/wiki/fishing/bait#高级鱼饵-deluxe-bait) | +12 px |

::: details 不同等级的钓鱼条长度
| 等级 | 裸装 | +软木塞 | +软木塞+高级鱼饵 |
|------|------|---------|----------------|
| 0 | 96 px | 120 px | 132 px |
| 3 | 120 px | 144 px | 156 px |
| 5 | 136 px | 160 px | 172 px |
| 8 | 160 px | 184 px | 196 px |
| 10 | 176 px | 200 px | 212 px |
:::

---

## 渔具对比总表

| 渔具 | 类型 | 核心数值 | 价格 | 推荐场景 |
|------|------|---------|------|---------|
| <img src="/img/tackle/quality_bobber.png" class="item-icon"> [品质浮标](#品质浮标-quality-bobber) | 浮标 | 品质 +1 档 | 2,000g | 刷金币、铱星鱼 |
| <img src="/img/tackle/lead_bobber.png" class="item-icon"> [铅制浮标](#铅制浮标-lead-bobber) | 浮标 | 反弹 ×0.1 | 200g | 下沉型鱼 |
| <img src="/img/tackle/cork_bobber.png" class="item-icon"> [软木塞浮标](#软木塞浮标-cork-bobber) | 浮标 | 钓鱼条 +24px | 750g | 通用新手 |
| <img src="/img/tackle/sonar_bobber.png" class="item-icon"> [声纳浮漂](#声纳浮漂-sonar-bobber) | 浮标 | 显示鱼种 | 宝箱限定 | 收集图鉴 |
| <img src="/img/tackle/trap_bobber.png" class="item-icon"> [陷阱浮标](#陷阱浮标-trap-bobber) | 浮标 | 逃脱 −33% | 500g | 高难度鱼 |
| <img src="/img/tackle/barbed_hook.png" class="item-icon"> [倒刺钩](#倒刺钩-barbed-hook) | 钩子 | 重力 ×0.3 + 自动跟随 | 1,000g | 慢速鱼 |
| <img src="/img/tackle/spinner.png" class="item-icon"> [旋式鱼饵](#旋式鱼饵-spinner) | 旋饵 | 咬钩 −5s | 500g | 刷鱼量 |
| <img src="/img/tackle/dressed_spinner.png" class="item-icon"> [精装旋式鱼饵](#精装旋式鱼饵-dressed-spinner) | 旋饵 | 咬钩 −10s | 1,000g | 刷鱼量 |
| <img src="/img/tackle/curiosity_lure.png" class="item-icon"> [寻宝者](#寻宝者-treasure-hunter) | 特殊 | 宝箱 +5%，不逃鱼 | 750g | 刷宝箱 |
| <img src="/img/tackle/curiosity_lure.png" class="item-icon"> [珍稀诱钩](#珍稀诱钩-curiosity-lure) | 特殊 | 稀有鱼率↑ | 特殊获取 | 传说鱼 |
