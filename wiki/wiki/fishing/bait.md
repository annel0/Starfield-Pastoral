# 🪱 鱼饵系统

鱼饵可以装配在 **玻璃纤维钓竿** 及以上等级的钓竿中，用于提高上钩率或实现特殊效果。

## 咬钩时间公式

所有鱼饵共享一个基础效果——**缩短咬钩等待时间**。具体计算如下：

$$
\text{基础时间} = \min(600,\ \max(30000 - 250 \times \text{钓鱼等级}))\ \text{ms}
$$

在此基础上依次叠加：

| 修正因素 | 乘数 | 说明 |
|---------|------|------|
| 首次抛竿 | ×0.75 | 每次进入钓鱼点的第一竿 |
| 任意鱼饵 | ×0.5 | 所有鱼饵共享的基础加速 |
| <img src="/img/bait/wild_bait.png" class="item-icon"> [万能鱼饵](/wiki/fishing/bait#万能鱼饵-wild-bait) / <img src="/img/bait/challenge_bait.png" class="item-icon"> [挑战鱼饵](/wiki/fishing/bait#挑战鱼饵-challenge-bait) | 额外 ×0.75 | 在 ×0.5 基础上继续叠加 |
| <img src="/img/bait/deluxe_bait.png" class="item-icon"> [高级鱼饵](/wiki/fishing/bait#高级鱼饵-deluxe-bait) | 额外 ×0.66 | 在 ×0.5 基础上继续叠加 |
| <img src="/img/tackle/spinner.png" class="item-icon"> [旋式鱼饵](/wiki/fishing/tackle#旋式鱼饵-spinner)（渔具） | −5000 ms | 加法叠加，可搭配鱼饵使用 |
| <img src="/img/tackle/dressed_spinner.png" class="item-icon"> [精装旋式鱼饵](/wiki/fishing/tackle#精装旋式鱼饵-dressed-spinner)（渔具） | −10000 ms | 加法叠加，可搭配鱼饵使用 |

最终结果不低于 **500 ms**。

::: details 计算示例
钓鱼等级 5，使用高级鱼饵，非首次抛竿：
- 基础时间 = 30000 − 250×5 = 28750 ms
- ×0.5（鱼饵基础）= 14375 ms
- ×0.66（高级鱼饵额外）= 9487 ms
- 约 **9.5 秒**等待咬钩
:::

## 鱼饵保留机制

所有鱼饵在每次钓鱼后消耗 1 个。如果玩家拥有「保存」效果（Preserving），则有 **50%** 概率不消耗鱼饵。

---

## 鱼饵一览

### 鱼饵 Bait

<ItemCard name="鱼饵" nameEn="Bait" img="/img/bait/bait.png" category="鱼饵">

> *能让鱼更快上钩。要先挂在鱼杆上。*

最基础的鱼饵，提高鱼咬钩速度。

| 属性 | 数值 |
|------|------|
| 咬钩时间 | ×0.5（减半） |
| 售价 | 1g |
| 购买价格 | 5g（威利鱼店） |
| 获取途径 | 威利鱼店购买 / 蠕虫箱产出 |

</ItemCard>

### 高级鱼饵 Deluxe Bait

<ItemCard name="高级鱼饵" nameEn="Deluxe Bait" img="/img/bait/deluxe_bait.png" category="鱼饵">

> *使鱼更快咬钩，并且让「钓鱼条」更长。*

双重效果：加速上钩 + 增大钓鱼条。

| 属性 | 数值 |
|------|------|
| 咬钩时间 | ×0.5 × 0.66 = ×0.33（约减至三分之一） |
| 钓鱼条加长 | +12 像素 |
| 蟹笼加成 | 品质 +1 档，50% 概率双倍产出 |
| 售价 | 1g |
| 获取途径 | 高级蠕虫箱产出 / 钓鱼宝箱稀有掉落（权重 35，≥6 级） |

</ItemCard>

### 万能鱼饵 Wild Bait

<ItemCard name="万能鱼饵" nameEn="Wild Bait" img="/img/bait/wild_bait.png" category="鱼饵">

> *用莱纳斯的独特配方制作。有机会一次钓到两条鱼。*

独特的双鱼效果，大幅提升钓鱼收益。

| 属性 | 数值 |
|------|------|
| 咬钩时间 | ×0.5 × 0.75 = ×0.375 |
| 双鱼概率 | 25% + 日运气÷2（日运气 +0.1 时为 30%） |
| 蟹笼加成 | 25% 概率数量翻倍，垃圾率减半（20% → 10%） |
| 售价 | 15g |
| 获取途径 | 钓鱼宝箱稀有掉落（2~6 个，权重 35，≥4 级） |

::: info 双鱼公式
$$
P(\text{双鱼}) = \text{clamp}(0.25 + \frac{\text{dailyLuck}}{2},\ 0,\ 1)
$$
:::

</ItemCard>

### 魔法鱼饵 Magic Bait

<ItemCard name="魔法鱼饵" nameEn="Magic Bait" img="/img/bait/magic_bait.png" category="鱼饵">

> *能让你在所有类型的水中钓出所有季节、时间、天气的鱼。*

**无视季节、时间、天气限制**，在任意水域可钓到该地点的全部鱼种。

| 属性 | 数值 |
|------|------|
| 咬钩时间 | ×0.5（标准鱼饵速度） |
| 特殊效果 | 取消季节 / 时间 / 天气过滤 |
| 蟹笼加成 | 无视季节/天气限制 |
| 售价 | 1g |
| 获取途径 | 制饵机制作 |

::: warning 注意
魔法鱼饵不会改变钓鱼地点限制——海洋鱼仍然只能在海滩钓到。
:::

</ItemCard>

### 挑战鱼饵 Challenge Bait

<ItemCard name="挑战鱼饵" nameEn="Challenge Bait" img="/img/bait/challenge_bait.png" category="鱼饵">

> *"完美"钓鱼时可获得三条鱼。鱼每逃离"钓鱼条"一次，产量就减少一条。*

高风险高回报的鱼饵。小游戏中显示 3 条鱼目标。

| 属性 | 数值 |
|------|------|
| 咬钩时间 | ×0.5 × 0.75 = ×0.375 |
| 产出数量 | 最多 3 条（鱼每逃离钓鱼条 1 次，数量 −1） |
| 鱼体型加成 | 目标鱼体型 ×1.2 |
| 售价 | 1g |
| 获取途径 | 金色宝箱掉落（3~6 个，权重 45） |

| 钓鱼表现 | 获得数量 |
|---------|---------|
| 完美钓鱼（鱼从未离开钓鱼条） | 3 条 |
| 鱼逃脱 1 次 | 2 条 |
| 鱼逃脱 2 次及以上 | 1 条 |

</ItemCard>

### 针对性鱼饵 Targeted Bait

<ItemCard name="针对性鱼饵" nameEn="Targeted Bait" img="/img/bait/targeted_bait_base.png" category="鱼饵">

> *将鱼放入制饵机即可产出针对性鱼饵。*

专门针对某种鱼的鱼饵，大幅提高钓到目标鱼的几率，并附带鱼体型加成。

| 属性 | 数值 |
|------|------|
| 咬钩时间 | ×0.5（标准鱼饵速度） |
| 特殊效果 | 大幅增加目标鱼种出现率 |
| 鱼体型加成 | 匹配目标鱼时体型 ×1.2 |
| 获取途径 | 将任意鱼放入制饵机 |
| 命名规则 | 「XX鱼饵」（如「鲟鱼鱼饵」） |

</ItemCard>

### 磁铁 Magnet

<ItemCard name="磁铁" nameEn="Magnet" img="/img/bait/magnet.png" category="鱼饵">

> *增加钓鱼时发现宝藏的几率，但会降低鱼的咬钩率。*

装在鱼饵槽中使用，专门用于刷宝箱。

| 属性 | 数值 |
|------|------|
| 宝箱概率 | +15%（加法叠加到基础概率上） |
| 售价 | 15g |
| 获取途径 | 威利鱼店购买（5g） |

</ItemCard>

---

## 鱼饵获取途径汇总

| 鱼饵 | 购买 | 产出设备 | 宝箱掉落 | 其他 |
|------|------|---------|---------|------|
| <img src="/img/bait/bait.png" class="item-icon"> [鱼饵](#鱼饵-bait) | 威利鱼店 5g | 蠕虫箱 | 后备掉落（5~15 个） | — |
| <img src="/img/bait/deluxe_bait.png" class="item-icon"> [高级鱼饵](#高级鱼饵-deluxe-bait) | — | 高级蠕虫箱 | 稀有池（5 个，权重 35，≥6 级） | — |
| <img src="/img/bait/wild_bait.png" class="item-icon"> [万能鱼饵](#万能鱼饵-wild-bait) | — | — | 稀有池（2~6 个，权重 35，≥4 级） | — |
| <img src="/img/bait/magic_bait.png" class="item-icon"> [魔法鱼饵](#魔法鱼饵-magic-bait) | — | 制饵机 | — | — |
| <img src="/img/bait/challenge_bait.png" class="item-icon"> [挑战鱼饵](#挑战鱼饵-challenge-bait) | — | — | 金色池（3~6 个，权重 45） | — |
| <img src="/img/bait/targeted_bait_base.png" class="item-icon"> [针对性鱼饵](#针对性鱼饵-targeted-bait) | — | 制饵机（放入对应鱼） | — | — |
| <img src="/img/bait/magnet.png" class="item-icon"> [磁铁](#磁铁-magnet) | 威利鱼店 5g | — | 金色池（3~6 个，权重 30） | — |

### 相关设备合成配方

| 设备 | 解锁条件 | 材料 |
|------|---------|------|
| 蠕虫箱 | 钓鱼 4 级 | 15× 硬木 + 1× 金锭 + 1× 铁锭 |
| 高级蠕虫箱 | 钓鱼 8 级 | 1× 蠕虫箱 + 30× 苔藓 |
| 制饵机 | 钓鱼 6 级 | 3× 铁锭 + 3× 珊瑚 + 1× 海胆 |

---

## 鱼饵对比总表

| 鱼饵 | 咬钩时间倍率 | 特殊效果 | 蟹笼效果 | 推荐场景 |
|------|------------|---------|---------|---------|
| <img src="/img/bait/bait.png" class="item-icon"> [鱼饵](#鱼饵-bait) | ×0.5 | — | — | 日常钓鱼 |
| <img src="/img/bait/deluxe_bait.png" class="item-icon"> [高级鱼饵](#高级鱼饵-deluxe-bait) | ×0.33 | 钓鱼条 +12px | 品质+1，50%双倍 | 高难度鱼 |
| <img src="/img/bait/wild_bait.png" class="item-icon"> [万能鱼饵](#万能鱼饵-wild-bait) | ×0.375 | 25%~30% 双鱼 | 25%翻倍，垃圾率减半 | 刷金币 |
| <img src="/img/bait/magic_bait.png" class="item-icon"> [魔法鱼饵](#魔法鱼饵-magic-bait) | ×0.5 | 无视季节/时间/天气 | 无视季节/天气 | 收集图鉴 |
| <img src="/img/bait/challenge_bait.png" class="item-icon"> [挑战鱼饵](#挑战鱼饵-challenge-bait) | ×0.375 | 最多 3 条鱼 | 不适用 | 高手刷鱼 |
| <img src="/img/bait/targeted_bait_base.png" class="item-icon"> [针对性鱼饵](#针对性鱼饵-targeted-bait) | ×0.5 | 锁定目标+体型×1.2 | — | 刷特定鱼 |
| <img src="/img/bait/magnet.png" class="item-icon"> [磁铁](#磁铁-magnet) | ×0.5 | 宝箱率 +15% | — | 刷宝箱 |
