<script setup>
const summerCrops = [
  { name: '小麦', nameEn: 'Wheat', img: '/img/crops/summer/wheat.png', seedImg: '/img/crops/summer/wheat_seeds.png', days: 4, regrow: null, sell: 25, link: '/wiki/crops/wheat' },
  { name: '辣椒', nameEn: 'Hot Pepper', img: '/img/crops/summer/hot_pepper.png', seedImg: '/img/crops/summer/hot_pepper_seeds.png', days: 5, regrow: 3, sell: 40, link: '/wiki/crops/hot-pepper' },
  { name: '萝卜', nameEn: 'Radish', img: '/img/crops/summer/radish.png', seedImg: '/img/crops/summer/radish_seeds.png', days: 6, regrow: null, sell: 90, link: '/wiki/crops/radish' },
  { name: '金皮西葫芦', nameEn: 'Summer Squash', img: '/img/crops/summer/summer_squash.png', seedImg: '/img/crops/summer/summer_squash_seeds.png', days: 6, regrow: 3, sell: 45, link: '/wiki/crops/summer-squash' },
  { name: '虞美人花', nameEn: 'Poppy', img: '/img/crops/summer/poppy0.png', seedImg: '/img/crops/summer/poppy_seeds.png', days: 7, regrow: null, sell: 140, link: '/wiki/crops/poppy' },
  { name: '向日葵', nameEn: 'Sunflower', img: '/img/crops/summer/sunflower.png', seedImg: '/img/crops/summer/sunflower_seeds.png', days: 8, regrow: null, sell: 80, link: '/wiki/crops/sunflower' },
  { name: '夏季亮片', nameEn: 'Summer Spangle', img: '/img/crops/summer/summer_spangle0.png', seedImg: '/img/crops/summer/summer_spangle_seeds.png', days: 8, regrow: null, sell: 90, link: '/wiki/crops/summer-spangle' },
  { name: '红叶卷心菜', nameEn: 'Red Cabbage', img: '/img/crops/summer/red_cabbage.png', seedImg: '/img/crops/summer/red_cabbage_seeds.png', days: 9, regrow: null, sell: 260, link: '/wiki/crops/red-cabbage' },
  { name: '西红柿', nameEn: 'Tomato', img: '/img/crops/summer/tomato.png', seedImg: '/img/crops/summer/tomato_seeds.png', days: 11, regrow: 4, sell: 60, link: '/wiki/crops/tomato' },
  { name: '啤酒花', nameEn: 'Hops', img: '/img/crops/summer/hops.png', seedImg: '/img/crops/summer/hops_seeds.png', days: 11, regrow: 1, sell: 25, link: '/wiki/crops/hops' },
  { name: '甜瓜', nameEn: 'Melon', img: '/img/crops/summer/melon.png', seedImg: '/img/crops/summer/melon_seeds.png', days: 12, regrow: null, sell: 250, link: '/wiki/crops/melon' },
  { name: '蓝莓', nameEn: 'Blueberry', img: '/img/crops/summer/blueberry.png', seedImg: '/img/crops/summer/blueberry_seeds.png', days: 13, regrow: 4, sell: 50, link: '/wiki/crops/blueberry' },
  { name: '杨桃', nameEn: 'Starfruit', img: '/img/crops/summer/starfruit.png', seedImg: '/img/crops/summer/starfruit_seeds.png', days: 13, regrow: null, sell: 750, link: '/wiki/crops/starfruit' },
  { name: '玉米', nameEn: 'Corn', img: '/img/crops/summer/corn.png', seedImg: '/img/crops/summer/corn_seeds.png', days: 14, regrow: 4, sell: 50, link: '/wiki/crops/corn' },
]
</script>

# ☀️ 夏季作物

夏季共有 **14 种作物**可以种植，其中 6 种为可再生作物。夏季也是高价值作物最集中的季节。

## 总览

<CropTable :crops="summerCrops" />

---

## 蔬菜

### 西红柿 Tomato

<ItemCard name="西红柿" nameEn="Tomato" img="/img/crops/summer/tomato.png" seedImg="/img/crops/summer/tomato_seeds.png" season="summer" category="作物">

> *口感丰富，味道略有些强烈。*
>
> *西红柿有非常广泛的烹饪用途。*

成熟后每 4 天再生一次。双格高度作物。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 2 - 3 - 3 - 3（共 11 天） |
| 再生 | ✅ 每 4 天 |
| 售价 | 60g / 75g ⭐ / 90g ⭐⭐ / 120g ⭐⭐⭐ |
| 种子售价 | 12g |
| 能量 | 20 / 28 / 36 / 52 |
| 生命 | 9 / 12 / 16 / 23 |
| 农耕经验 | 12 |

</ItemCard>

### 辣椒 Hot Pepper

<ItemCard name="辣椒" nameEn="Hot Pepper" img="/img/crops/summer/hot_pepper.png" seedImg="/img/crops/summer/hot_pepper_seeds.png" season="summer" category="水果">

> *超级辣，稍甜。*

夏季生长最快的再生作物，5 天成熟后每 3 天再生一次。归类为水果。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 1 - 2（共 5 天） |
| 再生 | ✅ 每 3 天 |
| 售价 | 40g / 50g ⭐ / 60g ⭐⭐ / 80g ⭐⭐⭐ |
| 种子售价 | 10g |
| 能量 | 13 / 18 / 23 / 33 |
| 生命 | 5 / 8 / 10 / 14 |
| 农耕经验 | 9 |

</ItemCard>

### 萝卜 Radish

<ItemCard name="萝卜" nameEn="Radish" img="/img/crops/summer/radish.png" seedImg="/img/crops/summer/radish_seeds.png" season="summer" category="作物">

> *新鲜清爽的块茎蔬菜，生吃的时候加一点胡椒。*

| 属性 | 数值 |
|------|------|
| 生长阶段 | 2 - 1 - 2 - 1（共 6 天） |
| 再生 | ❌ |
| 售价 | 90g / 112g ⭐ / 135g ⭐⭐ / 180g ⭐⭐⭐ |
| 种子售价 | 10g |
| 能量 | 45 / 63 / 81 / 117 |
| 生命 | 20 / 28 / 36 / 52 |
| 农耕经验 | 15 |

</ItemCard>

### 红叶卷心菜 Red Cabbage

<ItemCard name="红叶卷心菜" nameEn="Red Cabbage" img="/img/crops/summer/red_cabbage.png" seedImg="/img/crops/summer/red_cabbage_seeds.png" season="summer" category="作物">

> *常用来做沙拉或者凉拌卷心菜。*
>
> *由于土地条件不同，颜色可能是紫色或者蓝色或者黄绿色。*

| 属性 | 数值 |
|------|------|
| 生长阶段 | 2 - 2 - 2 - 3（共 9 天） |
| 再生 | ❌ |
| 售价 | 260g / 325g ⭐ / 390g ⭐⭐ / 520g ⭐⭐⭐ |
| 种子售价 | 25g |
| 能量 | 75 / 105 / 135 / 195 |
| 生命 | 33 / 47 / 60 / 87 |
| 农耕经验 | 28 |

</ItemCard>

### 金皮西葫芦 Summer Squash

<ItemCard name="金皮西葫芦" nameEn="Summer Squash" img="/img/crops/summer/summer_squash.png" seedImg="/img/crops/summer/summer_squash_seeds.png" season="summer" category="作物">

> *未成熟时收获的金皮西葫芦嫩瓜。*

成熟后每 3 天再生一次。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 2 - 2（共 6 天） |
| 再生 | ✅ 每 3 天 |
| 售价 | 45g / 56g ⭐ / 67g ⭐⭐ / 90g ⭐⭐⭐ |
| 种子售价 | 11g |
| 能量 | 63 / 88 / 113 / 163 |
| 生命 | 28 / 39 / 50 / 73 |
| 农耕经验 | 9 |

</ItemCard>

---

## 水果

### 甜瓜 Melon

<ItemCard name="甜瓜" nameEn="Melon" img="/img/crops/summer/melon.png" seedImg="/img/crops/summer/melon_seeds.png" season="summer" category="水果" rarity="uncommon">

> *凉爽香甜的夏日食品。*

夏季高价值水果，双格高度作物。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 3 - 4 - 4（共 12 天） |
| 再生 | ❌ |
| 售价 | 250g / 312g ⭐ / 375g ⭐⭐ / 500g ⭐⭐⭐ |
| 种子售价 | 20g |
| 能量 | 113 / 158 / 203 / 293 |
| 生命 | 50 / 71 / 91 / 131 |
| 农耕经验 | 27 |

**加工分类：** 水果 → 酿桶产果酒 / 罐头瓶产果酱

</ItemCard>

### 蓝莓 Blueberry

<ItemCard name="蓝莓" nameEn="Blueberry" img="/img/crops/summer/blueberry.png" seedImg="/img/crops/summer/blueberry_seeds.png" season="summer" category="水果" rarity="rare">

> *一种据说拥有多种益处的浆果。*
>
> *蓝莓拥有最高浓度的营养。*

每次收获固定 **3 个**，且有 2% 概率额外 +1。成熟后每 4 天再生一次。双格高度作物。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 4 - 4 - 4（共 13 天） |
| 再生 | ✅ 每 4 天 |
| 每次产量 | 固定 3 个 (+2% 概率额外 +1) |
| 售价 | 50g / 62g ⭐ / 75g ⭐⭐ / 100g ⭐⭐⭐ |
| 种子售价 | 20g |
| 能量 | 25 / 35 / 45 / 65 |
| 生命 | 11 / 15 / 20 / 29 |
| 农耕经验 | 10 |

**加工分类：** 水果 → 酿桶产果酒 / 罐头瓶产果酱

</ItemCard>

### 杨桃 Starfruit

<ItemCard name="杨桃" nameEn="Starfruit" img="/img/crops/summer/starfruit.png" seedImg="/img/crops/summer/starfruit_seeds.png" season="summer" category="水果" rarity="legendary">

> *美味多汁的热带水果。*
>
> *稍甜，夹着酸味。*

全季节单次售价最高的作物。双格高度。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 2 - 4 - 3 - 4（共 13 天） |
| 再生 | ❌ |
| 售价 | 750g / 937g ⭐ / 1125g ⭐⭐ / 1500g ⭐⭐⭐ |
| 种子售价 | 100g |
| 能量 | 125 / 175 / 225 / 325 |
| 生命 | 56 / 78 / 101 / 146 |
| 农耕经验 | 43 |

**加工分类：** 水果 → 酿桶产果酒 / 罐头瓶产果酱

</ItemCard>

---

## 花卉

### 虞美人花 Poppy

<ItemCard name="虞美人花" nameEn="Poppy" img="/img/crops/summer/poppy0.png" seedImg="/img/crops/summer/poppy_seeds.png" season="summer" category="花卉">

> *虞美人花颜色鲜艳，有药用价值。*

有 3 种颜色变体，收获时随机获得其中一种。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 2 - 2（共 7 天） |
| 再生 | ❌ |
| 颜色变体 | 3 种 |
| 售价 | 140g / 175g ⭐ / 210g ⭐⭐ / 280g ⭐⭐⭐ |
| 种子售价 | 25g |
| 能量 | 45 / 63 / 81 / 117 |
| 生命 | 20 / 28 / 36 / 52 |
| 农耕经验 | 20 |

</ItemCard>

### 夏季亮片 Summer Spangle

<ItemCard name="夏季亮片" nameEn="Summer Spangle" img="/img/crops/summer/summer_spangle0.png" seedImg="/img/crops/summer/summer_spangle_seeds.png" season="summer" category="花卉">

> *一种热带花朵，生长于潮湿的夏日季节。*
>
> *拥有芬香刺鼻的味道。*

有 6 种颜色变体，收获时随机获得其中一种。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 3 - 2（共 8 天） |
| 再生 | ❌ |
| 颜色变体 | 6 种 |
| 售价 | 90g / 112g ⭐ / 135g ⭐⭐ / 180g ⭐⭐⭐ |
| 种子售价 | 12g |
| 能量 | 45 / 63 / 81 / 117 |
| 生命 | 20 / 28 / 36 / 52 |
| 农耕经验 | 15 |

</ItemCard>

---

## 跨季作物

### 啤酒花 Hops

<ItemCard name="啤酒花" nameEn="Hops" img="/img/crops/summer/hops.png" seedImg="/img/crops/summer/hops_seeds.png" season="summer" category="作物">

> *带苦味刺鼻的花朵，用于酿酒。*

再生速度最快的作物，成熟后 **每天** 都能收获一次。双格高度的藤架类作物，有碰撞体积，玩家无法直接穿过。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 4 - 4（共 11 天） |
| 再生 | ✅ 每 1 天 |
| 售价 | 25g / 31g ⭐ / 37g ⭐⭐ / 50g ⭐⭐⭐ |
| 种子售价 | 15g |
| 能量 | 45 / 63 / 81 / 117 |
| 生命 | 20 / 28 / 36 / 52 |
| 农耕经验 | 6 |

</ItemCard>

### 小麦 Wheat

<ItemCard name="小麦" nameEn="Wheat" img="/img/crops/summer/wheat.png" seedImg="/img/crops/summer/wheat_seeds.png" season="multi" category="作物">

> *被广泛种植的谷物之一。*
>
> *能做成制作蛋糕和面包的上好面粉。*

夏 + 秋跨季作物，4 天极速成熟。不可直接食用，需要用镰刀收割。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 1 - 1（共 4 天） |
| 再生 | ❌ |
| 收获方式 | 🔪 镰刀 |
| 适种季节 | ☀️ 夏 + 🍂 秋 |
| 售价 | 25g / 31g ⭐ / 37g ⭐⭐ / 50g ⭐⭐⭐ |
| 种子售价 | 2g |
| 可食用 | ❌ |
| 农耕经验 | 6 |

</ItemCard>

### 玉米 Corn

<ItemCard name="玉米" nameEn="Corn" img="/img/crops/summer/corn.png" seedImg="/img/crops/summer/corn_seeds.png" season="multi" category="作物">

> *最常见的庄稼之一。*
>
> *甜甜的新鲜玉米棒是夏天最好的小吃。*

夏 + 秋跨季作物，成熟后每 4 天再生一次。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 2 - 4 - 4 - 4（共 14 天） |
| 再生 | ✅ 每 4 天 |
| 适种季节 | ☀️ 夏 + 🍂 秋 |
| 售价 | 50g / 62g ⭐ / 75g ⭐⭐ / 100g ⭐⭐⭐ |
| 种子售价 | 37g |
| 能量 | 25 / 35 / 45 / 65 |
| 生命 | 11 / 15 / 20 / 29 |
| 农耕经验 | 10 |

</ItemCard>

### 向日葵 Sunflower

<ItemCard name="向日葵" nameEn="Sunflower" img="/img/crops/summer/sunflower.png" seedImg="/img/crops/summer/sunflower_seeds.png" season="multi" category="花卉">

> *一直被误会为向着太阳转动。*

夏 + 秋跨季作物。双格高度。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 3 - 2（共 8 天） |
| 再生 | ❌ |
| 适种季节 | ☀️ 夏 + 🍂 秋 |
| 售价 | 80g / 100g ⭐ / 120g ⭐⭐ / 160g ⭐⭐⭐ |
| 种子售价 | 50g |
| 能量 | 45 / 63 / 81 / 117 |
| 生命 | 20 / 28 / 36 / 52 |
| 农耕经验 | 5 |

</ItemCard>
