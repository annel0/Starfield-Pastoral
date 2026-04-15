<script setup>
const springCrops = [
  { name: '胡萝卜', nameEn: 'Carrot', img: '/img/crops/spring/carrot.png', seedImg: '/img/crops/spring/carrot_seeds.png', days: 3, regrow: null, sell: 35, link: '/wiki/crops/carrot' },
  { name: '防风草', nameEn: 'Parsnip', img: '/img/crops/spring/parsnip.png', seedImg: '/img/crops/spring/parsnip_seeds.png', days: 4, regrow: null, sell: 35, link: '/wiki/crops/parsnip' },
  { name: '蒜', nameEn: 'Garlic', img: '/img/crops/spring/garlic.png', seedImg: '/img/crops/spring/garlic_seeds.png', days: 4, regrow: null, sell: 40, link: '/wiki/crops/garlic' },
  { name: '土豆', nameEn: 'Potato', img: '/img/crops/spring/potato.png', seedImg: '/img/crops/spring/potato_seeds.png', days: 6, regrow: null, sell: 80, link: '/wiki/crops/potato' },
  { name: '甘蓝菜', nameEn: 'Kale', img: '/img/crops/spring/kale.png', seedImg: '/img/crops/spring/kale_seeds.png', days: 6, regrow: null, sell: 110, link: '/wiki/crops/kale' },
  { name: '郁金香', nameEn: 'Tulip', img: '/img/crops/spring/tulip0.png', seedImg: '/img/crops/spring/tulip_seeds.png', days: 6, regrow: null, sell: 30, link: '/wiki/crops/tulip' },
  { name: '蓝爵', nameEn: 'Blue Jazz', img: '/img/crops/spring/blue_jazz0.png', seedImg: '/img/crops/spring/blue_jazz_seeds.png', days: 7, regrow: null, sell: 50, link: '/wiki/crops/blue-jazz' },
  { name: '草莓', nameEn: 'Strawberry', img: '/img/crops/spring/strawberry.png', seedImg: '/img/crops/spring/strawberry_seeds.png', days: 8, regrow: 4, sell: 120, link: '/wiki/crops/strawberry' },
  { name: '青豆', nameEn: 'Green Bean', img: '/img/crops/spring/green_bean.png', seedImg: '/img/crops/spring/green_bean_seeds.png', days: 10, regrow: 3, sell: 40, link: '/wiki/crops/green-bean' },
  { name: '咖啡豆', nameEn: 'Coffee Bean', img: '/img/crops/spring/coffee_bean.png', seedImg: '/img/crops/spring/coffee_bean_seeds.png', days: 10, regrow: 2, sell: 15, link: '/wiki/crops/coffee-bean' },
  { name: '花椰菜', nameEn: 'Cauliflower', img: '/img/crops/spring/cauliflower.png', seedImg: '/img/crops/spring/cauliflower_seeds.png', days: 12, regrow: null, sell: 175, link: '/wiki/crops/cauliflower' },
  { name: '大黄', nameEn: 'Rhubarb', img: '/img/crops/spring/rhubarb.png', seedImg: '/img/crops/spring/rhubarb_seeds.png', days: 13, regrow: null, sell: 220, link: '/wiki/crops/rhubarb' },
  { name: '上古水果', nameEn: 'Ancient Fruit', img: '/img/crops/spring/ancient_fruit.png', seedImg: '/img/crops/spring/ancient_fruit_seeds.png', days: 28, regrow: 7, sell: 550, link: '/wiki/crops/ancient-fruit' },
]
</script>

# 🌸 春季作物

春季共有 **13 种作物**可以种植，其中 4 种为可再生作物。

## 总览

<CropTable :crops="springCrops" />

---

## 蔬菜

### 防风草 Parsnip

<ItemCard name="防风草" nameEn="Parsnip" img="/img/crops/spring/parsnip.png" seedImg="/img/crops/spring/parsnip_seeds.png" season="spring" category="作物">

> *一种和胡萝卜很相似的春季块茎植物。*
>
> *它营养丰富且带有泥土的气息。*

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 1 - 1（共 4 天） |
| 再生 | ❌ |
| 售价 | 35g / 43g ⭐ / 52g ⭐⭐ / 70g ⭐⭐⭐ |
| 种子售价 | 5g |
| 能量 | 25 / 35 / 45 / 65 |
| 生命 | 11 / 15 / 20 / 29 |
| 农耕经验 | 8 |

**加工产品：** 防风草果汁（酿桶） · 防风草汤（烹饪）

</ItemCard>

### 蒜 Garlic

<ItemCard name="蒜" nameEn="Garlic" img="/img/crops/spring/garlic.png" seedImg="/img/crops/spring/garlic_seeds.png" season="spring" category="作物">

> *给菜肴增加一些风味。*
>
> *质量好的大蒜可能会特别辣。*

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 1 - 1（共 4 天） |
| 再生 | ❌ |
| 售价 | 40g / 60g ⭐ / 75g ⭐⭐ / 90g ⭐⭐⭐ |
| 种子售价 | 10g |
| 能量 | 20 / 28 / 36 / 52 |
| 生命 | 9 / 12 / 16 / 23 |
| 农耕经验 | 12 |

**加工产品：** 蒜果汁（酿桶）

</ItemCard>

### 胡萝卜 Carrot

<ItemCard name="胡萝卜" nameEn="Carrot" img="/img/crops/spring/carrot.png" seedImg="/img/crops/spring/carrot_seeds.png" season="spring" category="作物">

> *生长迅速、色彩鲜艳的块茎植物，可以当零食吃。*

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 0 - 1（共 3 天） |
| 再生 | ❌ |
| 售价 | 35g / 43g ⭐ / 52g ⭐⭐ / 70g ⭐⭐⭐ |
| 种子售价 | 5g |
| 农耕经验 | 8 |

**加工产品：** 胡萝卜果汁（酿桶）

</ItemCard>

### 土豆 Potato

<ItemCard name="土豆" nameEn="Potato" img="/img/crops/spring/potato.png" seedImg="/img/crops/spring/potato_seeds.png" season="spring" category="作物">

> *一种栽种范围很广的块茎植物。*

收获时有 20% 的概率额外获得一个土豆。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 2 - 2（共 6 天） |
| 再生 | ❌ |
| 额外掉落 | 20% 概率 +1 |
| 售价 | 80g / 100g ⭐ / 120g ⭐⭐ / 160g ⭐⭐⭐ |
| 种子售价 | 12g |
| 能量 | 25 / 35 / 45 / 65 |
| 生命 | 11 / 15 / 20 / 29 |
| 农耕经验 | 14 |

**加工产品：** 土豆果汁（酿桶）

</ItemCard>

### 甘蓝菜 Kale

<ItemCard name="甘蓝菜" nameEn="Kale" img="/img/crops/spring/kale.png" seedImg="/img/crops/spring/kale_seeds.png" season="spring" category="作物">

> *拿这些柔软的叶子蔬菜什锦盖饭或者做汤，味道好极了！*

春季唯一需要用镰刀收割的作物。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 2 - 1（共 6 天） |
| 再生 | ❌ |
| 收获方式 | 🔪 镰刀 |
| 售价 | 110g / 137g ⭐ / 165g ⭐⭐ / 220g ⭐⭐⭐ |
| 种子售价 | 17g |
| 农耕经验 | 17 |

**加工产品：** 甘蓝菜果汁（酿桶）

</ItemCard>

### 花椰菜 Cauliflower

<ItemCard name="花椰菜" nameEn="Cauliflower" img="/img/crops/spring/cauliflower.png" seedImg="/img/crops/spring/cauliflower_seeds.png" season="spring" category="作物" rarity="uncommon">

> *值钱，但是生长很慢。*
>
> *虽然它的颜色很浅，但是依然富含营养。*

春季售价最高的非再生作物。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 3 - 4 - 4（共 12 天） |
| 再生 | ❌ |
| 售价 | 175g / 218g ⭐ / 262g ⭐⭐ / 350g ⭐⭐⭐ |
| 种子售价 | 20g |
| 农耕经验 | 23 |

**加工产品：** 花椰菜果汁（酿桶）

</ItemCard>

### 青豆 Green Bean

<ItemCard name="青豆" nameEn="Green Bean" img="/img/crops/spring/green_bean.png" seedImg="/img/crops/spring/green_bean_seeds.png" season="spring" category="作物">

> *爽脆口感的多汁小豆子。*

成熟后每 3 天再生一次。青豆为双格高作物，有碰撞体积，玩家无法直接穿过，种植时需要注意留出通道。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 3 - 4（共 10 天） |
| 再生 | ✅ 每 3 天 |
| 售价 | 40g / 50g ⭐ / 60g ⭐⭐ / 80g ⭐⭐⭐ |
| 种子售价 | 15g |
| 农耕经验 | 9 |

**加工产品：** 青豆果汁（酿桶）

</ItemCard>

---

## 水果

### 大黄 Rhubarb

<ItemCard name="大黄" nameEn="Rhubarb" img="/img/crops/spring/rhubarb.png" seedImg="/img/crops/spring/rhubarb_seeds.png" season="spring" category="水果">

> *根茎极酸，但是加糖后是一种美味的甜品。*

不可直接食用。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 2 - 3 - 4 - 4（共 13 天） |
| 再生 | ❌ |
| 售价 | 220g / 275g ⭐ / 330g ⭐⭐ / 440g ⭐⭐⭐ |
| 种子售价 | 25g |
| 农耕经验 | 26 |

**加工产品：** 大黄果酒（酿桶） · 大黄派（烹饪）

</ItemCard>

### 草莓 Strawberry

<ItemCard name="草莓" nameEn="Strawberry" img="/img/crops/spring/strawberry.png" seedImg="/img/crops/spring/strawberry_seeds.png" season="spring" category="水果" rarity="rare">

> *香甜多汁的红色浆果。*

成熟后每 4 天再生一次。春季收益最高的作物之一。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 2 - 3（共 8 天） |
| 再生 | ✅ 每 4 天 |
| 售价 | 120g / 150g ⭐ / 180g ⭐⭐ / 240g ⭐⭐⭐ |
| 种子售价 | 25g |
| 能量 | 50 / 70 / 90 / 130 |
| 生命 | 22 / 31 / 40 / 58 |
| 农耕经验 | 18 |

**加工产品：** 草莓果酒（酿桶）

</ItemCard>

---

## 花卉

### 郁金香 Tulip

<ItemCard name="郁金香" nameEn="Tulip" img="/img/crops/spring/tulip0.png" seedImg="/img/crops/spring/tulip_seeds.png" season="spring" category="花卉">

> *最受欢迎的春天花朵。*
>
> *淡淡的芳香味。*

有 5 种颜色变体，收获时随机获得其中一种。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 1 - 2 - 2（共 6 天） |
| 再生 | ❌ |
| 颜色变体 | 5 种 |
| 售价 | 30g / 37g ⭐ / 45g ⭐⭐ / 60g ⭐⭐⭐ |
| 种子售价 | 5g |
| 能量 | 45 / 63 / 81 / 117 |
| 生命 | 20 / 28 / 36 / 52 |
| 农耕经验 | 7 |

</ItemCard>

### 蓝爵 Blue Jazz

<ItemCard name="蓝爵" nameEn="Blue Jazz" img="/img/crops/spring/blue_jazz0.png" seedImg="/img/crops/spring/blue_jazz_seeds.png" season="spring" category="花卉">

> *这种花朵为圆球状，以便吸引更多的蝴蝶而来。*

有 6 种颜色变体，收获时随机获得其中一种。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 2 - 2（共 7 天） |
| 再生 | ❌ |
| 颜色变体 | 6 种 |
| 售价 | 50g / 62g ⭐ / 75g ⭐⭐ / 100g ⭐⭐⭐ |
| 种子售价 | 7g |
| 农耕经验 | 10 |

</ItemCard>

---

## 跨季作物

### 咖啡豆 Coffee Bean

<ItemCard name="咖啡豆" nameEn="Coffee Bean" img="/img/crops/spring/coffee_bean.png" seedImg="/img/crops/spring/coffee_bean_seeds.png" season="multi" category="作物">

> *在春季和夏季种植。*
>
> *在小桶里放五颗咖啡豆可以制作咖啡。*

春 + 夏跨季作物，自身就是种子。每次收获固定获得 4 个咖啡豆，成熟后每 2 天再生一次。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 3 - 3 - 3（共 10 天） |
| 再生 | ✅ 每 2 天 |
| 每次产量 | 固定 4 个 |
| 适种季节 | 🌸 春 + ☀️ 夏 |
| 售价 | 15g / 18g ⭐ / 22g ⭐⭐ / 30g ⭐⭐⭐ |
| 种子 | 自身即种子 |
| 农耕经验 | 4 |

</ItemCard>

### 上古水果 Ancient Fruit

<ItemCard name="上古水果" nameEn="Ancient Fruit" img="/img/crops/spring/ancient_fruit.png" seedImg="/img/crops/spring/ancient_fruit_seeds.png" season="multi" category="水果" rarity="legendary">

> *千万年来冬眠中的水果。*

三季跨季作物，生长期极长但售价极高，成熟后每 7 天再生一次。种子不可出售。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 2 - 9 - 9 - 8（共 28 天） |
| 再生 | ✅ 每 7 天 |
| 适种季节 | 🌸 春 + ☀️ 夏 + 🍂 秋 |
| 售价 | 550g / 687g ⭐ / 825g ⭐⭐ / 1100g ⭐⭐⭐ |
| 种子 | 不可出售 |
| 农耕经验 | 38 |

**加工产品：** 上古水果果酒（酿桶）

</ItemCard>
