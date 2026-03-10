# 肥料系统配置文档

## 📦 已创建的Loot Table文件 (10个)

### 品质肥料系列 (Quality Fertilizer)
提高作物品质几率

| 文件名 | 中文名 | CMD | 购买价 | 售价 | 等级 | 效果 |
|--------|--------|-----|--------|------|------|------|
| basic_fertilizer.json | 初级肥料 | 4001 | 100g | 50g | 1 | 稍微提高品质几率 |
| quality_fertilizer.json | 高级肥料 | 4002 | 150g | 75g | 2 | 提高品质几率 |
| deluxe_fertilizer.json | 顶级肥料 | 4003 | 350g | 175g | 3 | 无普通品质作物 |

### 生长激素系列 (Speed-Gro)
加快作物生长速度

| 文件名 | 中文名 | CMD | 购买价 | 售价 | 等级 | 效果 |
|--------|--------|-----|--------|------|------|------|
| speed_gro.json | 生长激素 | 4004 | 100g | 50g | 1 | 生长速度+10% |
| deluxe_speed_gro.json | 高级生长激素 | 4005 | 150g | 75g | 2 | 生长速度+25% |
| hyper_speed_gro.json | 顶级生长激素 | 4006 | 350g | 175g | 3 | 生长速度+33% |

### 保湿土壤系列 (Retaining Soil)
隔夜保持湿润

| 文件名 | 中文名 | CMD | 购买价 | 售价 | 等级 | 效果 |
|--------|--------|-----|--------|------|------|------|
| basic_retaining_soil.json | 初级保湿土壤 | 4007 | 100g | 50g | 1 | 33%几率保湿 |
| quality_retaining_soil.json | 高级保湿土壤 | 4008 | 150g | 75g | 2 | 66%几率保湿 |
| deluxe_retaining_soil.json | 顶级保湿土壤 | 4009 | 150g | 75g | 3 | 100%保湿 |

### 树肥 (Tree Fertilizer)
加速野树生长

| 文件名 | 中文名 | CMD | 购买价 | 售价 | 等级 | 效果 |
|--------|--------|-----|--------|------|------|------|
| tree_fertilizer.json | 树肥 | 4010 | 10g | 5g | 1 | 野树快速生长 |

---

## 🎨 Custom Model Data 映射 (carrot_on_a_stick)

```json
{ "predicate": { "custom_model_data": 4001 }, "model": "item/stardew/fertilizer/basic_fertilizer" },
{ "predicate": { "custom_model_data": 4002 }, "model": "item/stardew/fertilizer/quality_fertilizer" },
{ "predicate": { "custom_model_data": 4003 }, "model": "item/stardew/fertilizer/deluxe_fertilizer" },
{ "predicate": { "custom_model_data": 4004 }, "model": "item/stardew/fertilizer/speed_gro" },
{ "predicate": { "custom_model_data": 4005 }, "model": "item/stardew/fertilizer/deluxe_speed_gro" },
{ "predicate": { "custom_model_data": 4006 }, "model": "item/stardew/fertilizer/hyper_speed_gro" },
{ "predicate": { "custom_model_data": 4007 }, "model": "item/stardew/fertilizer/basic_retaining_soil" },
{ "predicate": { "custom_model_data": 4008 }, "model": "item/stardew/fertilizer/quality_retaining_soil" },
{ "predicate": { "custom_model_data": 4009 }, "model": "item/stardew/fertilizer/deluxe_retaining_soil" },
{ "predicate": { "custom_model_data": 4010 }, "model": "item/stardew/fertilizer/tree_fertilizer" }
```

---

## 📝 Custom Data 结构

所有肥料物品包含以下custom_data:
```json
{
  "stardew_item": 1,
  "item_type": "fertilizer",
  "fertilizer_type": "quality|speed|retaining|tree",
  "fertilizer_level": 1|2|3,
  "sd_price": 售价金额
}
```

### fertilizer_type 类型说明:
- `quality` - 品质肥料 (影响收获时作物品质)
- `speed` - 生长激素 (减少生长天数)
- `retaining` - 保湿土壤 (隔夜保持湿润)
- `tree` - 树肥 (加速野树生长)

### fertilizer_level 等级说明:
- `1` - 初级 (Basic)
- `2` - 高级 (Quality/Deluxe)
- `3` - 顶级 (Deluxe/Hyper)

---

## 🛒 Pierre商店售卖时间 (官方Wiki数据)

### 第一年春季15日后解锁:
- 初级肥料 (100g)
- 生长激素 (100g)
- 初级保湿土壤 (100g)

### 第二年开始解锁:
- 高级肥料 (150g)
- 高级生长激素 (150g)
- 高级保湿土壤 (150g)

### 特殊获取 (不在商店售卖):
- 顶级肥料 - 齐先生的核桃房兑换
- 顶级生长激素 - 齐先生的核桃房兑换
- 顶级保湿土壤 - 姜岛商人兑换
- 树肥 - 采集7级解锁配方

---

## 🎨 需要建模的部分

你需要在资源包中创建以下模型文件:

```
assets/minecraft/models/item/stardew/fertilizer/
├── basic_fertilizer.json
├── quality_fertilizer.json
├── deluxe_fertilizer.json
├── speed_gro.json
├── deluxe_speed_gro.json
├── hyper_speed_gro.json
├── basic_retaining_soil.json
├── quality_retaining_soil.json
├── deluxe_retaining_soil.json
└── tree_fertilizer.json
```

### 建议的视觉风格 (参考Wiki外观):
- **初级肥料**: 棕色粉末袋
- **高级肥料**: 深绿色粉末袋
- **顶级肥料**: 紫色发光粉末袋

- **生长激素**: 绿色液体瓶
- **高级生长激素**: 深绿色液体瓶
- **顶级生长激素**: 亮绿色发光液体瓶

- **初级保湿土壤**: 浅棕色土袋
- **高级保湿土壤**: 深棕色土袋
- **顶级保湿土壤**: 紫棕色发光土袋

- **树肥**: 小树苗图标+肥料袋

---

## 🚧 待实现功能

### 1. 施肥功能
- [ ] 创建 `farming/fertilizer/apply.mcfunction`
- [ ] 检测右键耕地使用肥料
- [ ] 存储肥料数据到crop marker或单独marker

### 2. 耕地视觉效果
- [ ] 在耕地方块上添加不同颜色的粒子/display实体
- [ ] 品质肥料: 棕色粒子
- [ ] 生长激素: 绿色粒子
- [ ] 保湿土壤: 蓝色粒子

### 3. 生长加速
- [ ] 修改 `crops/planting/*/plant.mcfunction`
- [ ] 种植时检查肥料并计算 sd_max_crop_age
- [ ] 公式: 原天数 × (1 - 加速百分比)

### 4. 品质提升
- [ ] 修改 `farming/harvest.mcfunction`
- [ ] 根据fertilizer_level调整品质随机数阈值
- [ ] 顶级肥料: 移除普通品质判定

### 5. 保湿效果
- [ ] 修改 `time/new_day.mcfunction`
- [ ] 在重置sd_watered前检查保湿土壤
- [ ] 根据等级判定保留几率

### 6. 添加到商店
- [ ] 修改 `shop/init_pierre.mcfunction`
- [ ] 春季15日后: 初级系列
- [ ] 第二年: 高级系列

---

## 📊 品质提升数据 (根据Wiki)

### 无肥料 (耕种等级0):
- 普通: 97% | 银星: 2% | 金星: 1% | 铱星: 0%

### 初级肥料 (耕种等级0):
- 普通: 88% | 银星: 8% | 金星: 4% | 铱星: 0%

### 高级肥料 (耕种等级0):
- 普通: 78% | 银星: 14% | 金星: 8% | 铱星: 0%

### 顶级肥料 (耕种等级0):
- 普通: 0% | 银星: 84% | 金星: 10% | 铱星: 6%

**注意**: 品质几率会随耕种等级提升而增加!

---

## ✅ 当前进度

- [x] 创建10个肥料loot_table文件
- [x] 配置CMD映射 (4001-4010)
- [x] 添加custom_data结构
- [x] 设置官方中文翻译名称
- [ ] 创建资源包模型
- [ ] 实现施肥功能
- [ ] 实现耕地视觉特效
- [ ] 实现生长/品质/保湿逻辑
- [ ] 添加到Pierre商店
