<script setup>
const fallCrops = [
  { name: '小白菜', nameEn: 'Bok Choy', img: '/img/crops/fall/bok_choy.png', seedImg: '/img/crops/fall/bok_choy_seeds.png', days: 4, regrow: null, sell: 80, link: '/wiki/crops/bok-choy' },
  { name: '茄子', nameEn: 'Eggplant', img: '/img/crops/fall/eggplant.png', seedImg: '/img/crops/fall/eggplant_seeds.png', days: 5, regrow: 5, sell: 60, link: '/wiki/crops/eggplant' },
  { name: '甜菜', nameEn: 'Beet', img: '/img/crops/fall/beet.png', seedImg: '/img/crops/fall/beet_seeds.png', days: 6, regrow: null, sell: 100, link: '/wiki/crops/beet' },
  { name: '蔓越莓', nameEn: 'Cranberry', img: '/img/crops/fall/cranberry.png', seedImg: '/img/crops/fall/cranberry_seeds.png', days: 7, regrow: 5, sell: 75, link: '/wiki/crops/cranberry' },
  { name: '苋菜', nameEn: 'Amaranth', img: '/img/crops/fall/amaranth.png', seedImg: '/img/crops/fall/amaranth_seeds.png', days: 7, regrow: null, sell: 150, link: '/wiki/crops/amaranth' },
  { name: '西蓝花', nameEn: 'Broccoli', img: '/img/crops/fall/broccoli.png', seedImg: '/img/crops/fall/broccoli_seeds.png', days: 7, regrow: 4, sell: 70, link: '/wiki/crops/broccoli' },
  { name: '洋蓟', nameEn: 'Artichoke', img: '/img/crops/fall/artichoke.png', seedImg: '/img/crops/fall/artichoke_seeds.png', days: 8, regrow: null, sell: 160, link: '/wiki/crops/artichoke' },
  { name: '葡萄', nameEn: 'Grape', img: '/img/crops/fall/grape.png', seedImg: '/img/crops/fall/grape_seeds.png', days: 10, regrow: 3, sell: 80, link: '/wiki/crops/grape' },
  { name: '山药', nameEn: 'Yam', img: '/img/crops/fall/yam.png', seedImg: '/img/crops/fall/yam_seeds.png', days: 10, regrow: null, sell: 160, link: '/wiki/crops/yam' },
  { name: '玫瑰仙子', nameEn: 'Fairy Rose', img: '/img/crops/fall/fairy_rose0.png', seedImg: '/img/crops/fall/fairy_rose_seeds.png', days: 12, regrow: null, sell: 290, link: '/wiki/crops/fairy-rose' },
  { name: '南瓜', nameEn: 'Pumpkin', img: '/img/crops/fall/pumpkin.png', seedImg: '/img/crops/fall/pumpkin_seeds.png', days: 13, regrow: null, sell: 320, link: '/wiki/crops/pumpkin' },
]
</script>

# 🍂 秋季作物

秋季共有 **11 种专属作物**可以种植，另外还有 3 种跨季作物（玉米、小麦、向日葵）在秋季继续生长。秋季拥有多种高价值作物。

## 总览

<CropTable :crops="fallCrops" />

---

## 蔬菜

### 小白菜 Bok Choy

<ItemCard name="小白菜" nameEn="Bok Choy" img="/img/crops/fall/bok_choy.png" seedImg="/img/crops/fall/bok_choy_seeds.png" season="fall" category="作物">

> *绿绿的菜叶丰满的根茎，健康又美味。*

秋季成熟最快的作物，仅需 4 天。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 1 - 1（共 4 天） |
| 再生 | ❌ |
| 售价 | 80g / 100g ⭐ / 120g ⭐⭐ / 160g ⭐⭐⭐ |
| 种子售价 | 12g |
| 能量 | 25 / 35 / 45 / 65 |
| 生命 | 11 / 15 / 20 / 29 |
| 农耕经验 | 14 |

</ItemCard>

### 茄子 Eggplant

<ItemCard name="茄子" nameEn="Eggplant" img="/img/crops/fall/eggplant.png" seedImg="/img/crops/fall/eggplant_seeds.png" season="fall" category="作物">

> *味道浓郁，营养丰富，是西红柿的近亲。*
>
> *炒着吃或者炖着吃味道都很不错。*

成熟后每 5 天再生一次。双格高度作物。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 1 - 2（共 5 天） |
| 再生 | ✅ 每 5 天 |
| 售价 | 60g / 75g ⭐ / 90g ⭐⭐ / 120g ⭐⭐⭐ |
| 种子售价 | 5g |
| 能量 | 20 / 28 / 36 / 52 |
| 生命 | 9 / 12 / 16 / 23 |
| 农耕经验 | 12 |

</ItemCard>

### 甜菜 Beet

<ItemCard name="甜菜" nameEn="Beet" img="/img/crops/fall/beet.png" seedImg="/img/crops/fall/beet_seeds.png" season="fall" category="作物">

> *香甜的根茎植物。*
>
> *它的叶子非常适合做沙拉。*

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 2 - 2（共 6 天） |
| 再生 | ❌ |
| 售价 | 100g / 125g ⭐ / 150g ⭐⭐ / 200g ⭐⭐⭐ |
| 种子售价 | 5g |
| 能量 | 30 / 42 / 54 / 78 |
| 生命 | 13 / 18 / 24 / 35 |
| 农耕经验 | 16 |

</ItemCard>

### 苋菜 Amaranth

<ItemCard name="苋菜" nameEn="Amaranth" img="/img/crops/fall/amaranth.png" seedImg="/img/crops/fall/amaranth_seeds.png" season="fall" category="作物">

> *远古文明广泛耕种的一种紫色谷物。*

需要用镰刀收割。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 2 - 2（共 7 天） |
| 再生 | ❌ |
| 收获方式 | 🔪 镰刀 |
| 售价 | 150g / 187g ⭐ / 225g ⭐⭐ / 300g ⭐⭐⭐ |
| 种子售价 | 17g |
| 能量 | 50 / 70 / 90 / 130 |
| 生命 | 22 / 31 / 40 / 58 |
| 农耕经验 | 21 |

</ItemCard>

### 洋蓟 Artichoke

<ItemCard name="洋蓟" nameEn="Artichoke" img="/img/crops/fall/artichoke.png" seedImg="/img/crops/fall/artichoke_seeds.png" season="fall" category="作物">

> *一种蓟科植物的芽。*
>
> *带有尖刺的外围叶子下，隐藏着一颗多肉而结实的内芯。*

| 属性 | 数值 |
|------|------|
| 生长阶段 | 2 - 2 - 2 - 2（共 8 天） |
| 再生 | ❌ |
| 售价 | 160g / 200g ⭐ / 240g ⭐⭐ / 320g ⭐⭐⭐ |
| 种子售价 | 7g |
| 能量 | 30 / 42 / 54 / 78 |
| 生命 | 13 / 18 / 24 / 35 |
| 农耕经验 | 22 |

</ItemCard>

### 山药 Yam

<ItemCard name="山药" nameEn="Yam" img="/img/crops/fall/yam.png" seedImg="/img/crops/fall/yam_seeds.png" season="fall" category="作物">

> *淀粉块茎植物，有很多食用方式。*

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 3 - 3 - 3（共 10 天） |
| 再生 | ❌ |
| 售价 | 160g / 200g ⭐ / 240g ⭐⭐ / 320g ⭐⭐⭐ |
| 种子售价 | 15g |
| 能量 | 45 / 63 / 81 / 117 |
| 生命 | 20 / 28 / 36 / 52 |
| 农耕经验 | 22 |

</ItemCard>

### 南瓜 Pumpkin

<ItemCard name="南瓜" nameEn="Pumpkin" img="/img/crops/fall/pumpkin.png" seedImg="/img/crops/fall/pumpkin_seeds.png" season="fall" category="作物" rarity="uncommon">

> *秋天的农作物，因为它香脆的种子以及甜美的果肉而深受喜爱。*
>
> *而且，南瓜壳还能用做节日饰品。*

秋季售价最高的蔬菜类作物。不可直接食用。双格高度。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 3 - 4 - 5（共 13 天） |
| 再生 | ❌ |
| 可食用 | ❌ |
| 售价 | 320g / 400g ⭐ / 480g ⭐⭐ / 640g ⭐⭐⭐ |
| 种子售价 | 25g |
| 农耕经验 | 31 |

</ItemCard>

### 西蓝花 Broccoli

<ItemCard name="西蓝花" nameEn="Broccoli" img="/img/crops/fall/broccoli.png" seedImg="/img/crops/fall/broccoli_seeds.png" season="fall" category="作物">

> *西蓝花的花球，微小的花蕾口感独特。*

成熟后每 4 天再生一次。种子不可出售。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 2 - 2（共 7 天） |
| 再生 | ✅ 每 4 天 |
| 售价 | 70g / 87g ⭐ / 105g ⭐⭐ / 140g ⭐⭐⭐ |
| 种子 | 不可出售 |
| 能量 | 63 / 88 / 113 / 163 |
| 生命 | 28 / 39 / 50 / 73 |
| 农耕经验 | 13 |

</ItemCard>

---

## 水果

### 蔓越莓 Cranberry

<ItemCard name="蔓越莓" nameEn="Cranberry" img="/img/crops/fall/cranberry.png" seedImg="/img/crops/fall/cranberry_seeds.png" season="fall" category="水果" rarity="rare">

> *这些红色的酸梅是传统的冬天食品。*

每次收获固定 **2 个**，且有 10% 概率额外 +1。成熟后每 5 天再生一次。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 2 - 2（共 7 天） |
| 再生 | ✅ 每 5 天 |
| 每次产量 | 固定 2 个 (+10% 概率额外 +1) |
| 售价 | 75g / 93g ⭐ / 112g ⭐⭐ / 150g ⭐⭐⭐ |
| 种子售价 | 60g |
| 能量 | 38 / 53 / 68 / 98 |
| 生命 | 17 / 23 / 30 / 44 |
| 农耕经验 | 14 |

**加工分类：** 水果 → 酿桶产果酒 / 罐头瓶产果酱

</ItemCard>

### 葡萄 Grape

<ItemCard name="葡萄" nameEn="Grape" img="/img/crops/fall/grape.png" seedImg="/img/crops/fall/grape_seeds.png" season="fall" category="水果">

> *一串的香甜水果。*

成熟后每 3 天再生一次。双格高度作物。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 3 - 4（共 10 天） |
| 再生 | ✅ 每 3 天 |
| 售价 | 80g / 100g ⭐ / 120g ⭐⭐ / 160g ⭐⭐⭐ |
| 种子售价 | 15g |
| 能量 | 38 / 53 / 68 / 98 |
| 生命 | 17 / 23 / 30 / 44 |
| 农耕经验 | 14 |

**加工分类：** 水果 → 酿桶产果酒 / 罐头瓶产果酱

</ItemCard>

---

## 花卉

### 玫瑰仙子 Fairy Rose

<ItemCard name="玫瑰仙子" nameEn="Fairy Rose" img="/img/crops/fall/fairy_rose0.png" seedImg="/img/crops/fall/fairy_rose_seeds.png" season="fall" category="花卉" rarity="rare">

> *传言这种花朵的香味会吸引小仙女而来。*

秋季最珍贵的花卉，有 6 种颜色变体。双格高度。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 4 - 4 - 3（共 12 天） |
| 再生 | ❌ |
| 颜色变体 | 6 种 |
| 售价 | 290g / 362g ⭐ / 435g ⭐⭐ / 580g ⭐⭐⭐ |
| 种子售价 | 50g |
| 能量 | 45 / 63 / 81 / 117 |
| 生命 | 20 / 28 / 36 / 52 |
| 农耕经验 | 29 |

</ItemCard>

---

## 跨季作物

以下作物也可在秋季种植或继续生长，详细数据请参阅对应季节页面：

| 作物 | 适种季节 | 详情 |
|------|---------|------|
| [玉米](/wiki/crops/summer#玉米-corn) | ☀️ 夏 + 🍂 秋 | 14 天成熟，每 4 天再生 |
| [小麦](/wiki/crops/summer#小麦-wheat) | ☀️ 夏 + 🍂 秋 | 4 天成熟，镰刀收割 |
| [向日葵](/wiki/crops/summer#向日葵-sunflower) | ☀️ 夏 + 🍂 秋 | 8 天成熟，双格高度 |
| [上古水果](/wiki/crops/spring#上古水果-ancient-fruit) | 🌸 春 + ☀️ 夏 + 🍂 秋 | 28 天成熟，每 7 天再生 |
