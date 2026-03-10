# 防风草作物材质说明

所有材质文件已创建为1x1像素的占位符PNG，可以正常编译运行。

## 需要替换的材质文件 (16x16 PNG)

### 物品材质
- `textures/item/crops/spring/parsnip_seeds.png` - 防风草种子图标
- `textures/item/crops/spring/parsnip.png` - 防风草物品图标（普通品质）

### 品质星星叠加层（左上角星星）
- `textures/item/quality/silver_star.png` - 灰色星星（银星品质）
- `textures/item/quality/gold_star.png` - 金色星星（金星品质）
- `textures/item/quality/iridium_star.png` - 紫/粉色星星（铱星品质）

### 作物方块材质（Cross模型，5个生长阶段）
- `textures/block/crops/spring/parsnip_stage0.png` - 种子阶段
- `textures/block/crops/spring/parsnip_stage1.png` - 小苗阶段（第1天）
- `textures/block/crops/spring/parsnip_stage2.png` - 成长阶段（第2天）
- `textures/block/crops/spring/parsnip_stage3.png` - 接近成熟（第3天）
- `textures/block/crops/spring/parsnip_stage4.png` - 完全成熟（第4天，可收获）

## 材质要求
- 尺寸：16x16 像素
- 格式：PNG（支持透明通道）
- 方块材质使用Cross（十字交叉）模型渲染
- 品质星星应设计为左上角的小图标，与基础贴图叠加显示

## 映射文件结构

### Blockstate
```
blockstates/parsnip_crop.json → 5个阶段映射到5个模型
```

### 方块模型
```
models/block/crops/spring/parsnip_stage0-4.json → 使用 minecraft:block/cross 父模型
```

### 物品模型
```
models/item/parsnip.json → 基础模型 + custom_model_data overrides
models/item/parsnip_silver.json → layer0(作物) + layer1(银星)
models/item/parsnip_gold.json → layer0(作物) + layer1(金星)
models/item/parsnip_iridium.json → layer0(作物) + layer1(铱星)
```

所有映射文件均已正确配置，只需替换PNG材质文件即可。
