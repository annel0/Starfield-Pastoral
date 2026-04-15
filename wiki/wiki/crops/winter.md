<script setup>
const winterCrops = [
  { name: '霜瓜', nameEn: 'Powder Melon', img: '/img/crops/winter/powder_melon.png', seedImg: '/img/crops/winter/powder_melon_seeds.png', days: 7, regrow: null, sell: 60, link: '/wiki/crops/powder-melon' },
]
</script>

# ❄️ 冬季作物

冬季只有 **1 种专属作物**——霜瓜。冬季大部分时间里田地被积雪覆盖，农耕活动有限。

## 总览

<CropTable :crops="winterCrops" />

---

## 霜瓜 Powder Melon

<ItemCard name="霜瓜" nameEn="Powder Melon" img="/img/crops/winter/powder_melon.png" seedImg="/img/crops/winter/powder_melon_seeds.png" season="winter" category="作物">

> *因表面结一层白霜而得名，瓜肉鲜脆清甜。*

冬季唯一的作物，7 天成熟。种子不可出售。

| 属性 | 数值 |
|------|------|
| 生长阶段 | 1 - 2 - 2 - 2（共 7 天） |
| 再生 | ❌ |
| 售价 | 60g / 75g ⭐ / 90g ⭐⭐ / 120g ⭐⭐⭐ |
| 种子 | 不可出售 |
| 能量 | 63 / 88 / 113 / 163 |
| 生命 | 28 / 39 / 50 / 73 |
| 农耕经验 | 12 |

</ItemCard>
