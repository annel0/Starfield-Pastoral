# 矿井系统实现计划

## 1. 维度坐标分配

```
维度名称：stardewcraft:stardew_mining

Y 坐标：固定 Y=64（所有层都在同一高度）
X 坐标：固定 X=0（房间以 X=0 为中心线）
Z 坐标：按层数分配

坐标公式：
- 0层（大厅）：中心点 (0, 64, 0)
- 第 N 层：中心点 (0, 64, N * 100)

示例：
- 大厅：Z = 0
- 1层：Z = 100
- 2层：Z = 200
- 40层：Z = 4000
- 80层：Z = 8000
- 120层：Z = 12000

房间尺寸：20×20 ~ 50×50（随机），间隔 100 格，绝对不重叠
```

---

## 2. 主题与石头映射

| 层数 | 主题 | 主石头 | Dark变体 | 装饰石 |
|------|------|--------|----------|--------|
| 1-39 | Earth | `earth_shale` | `dark_earth_shale` | `limestone`, `mossy_sandstone`, `cracked_slate` |
| 40-79 | Frost | `frost_gneiss` | `dark_frost_gneiss` | `banded_marble`, `salt_rock`, `cracked_slate` |
| 80-119 | Lava | `lava_basalt` | `dark_lava_basalt` | `scoria`, `cracked_slate` |
| 120 | Summit | 特殊 | - | - |

---

## 3. 矿石分布（对齐原版总数）

### 原版数据
- 原版每层石头数：约 30-100 个（stoneChance = 10%-30%）
- 原版矿石概率：2.9% 每个石头

### 我们的数据
- 我们每层石头数：约 300-1500 个（因为 20×20 ~ 50×50 的房间几乎全填满）
- 需要下调矿石概率，保证每层矿石总数 ≈ 原版

### 矿石概率重算

假设原版每层平均 60 个石头，矿石概率 2.9%，则每层平均矿石数 ≈ 1.7 个

我们每层平均石头数（按 35×35 房间估算，80% 填充率）：
- 35 × 35 × 0.8 ≈ 980 个石头

要保证每层矿石数 ≈ 1.7 个：
- 新概率 = 1.7 / 980 ≈ **0.17%**（约为原版的 1/17）

### 各矿种概率表

| 矿种 | 层数 | 原版概率 | 我们的概率 | 说明 |
|------|------|----------|------------|------|
| 铜矿 | 1-39 | 2.9% | 0.17% | Earth 段主矿 |
| 铁矿 | 40-79 | 2.9% | 0.17% | Frost 段主矿 |
| 金矿 | 80-119 | 2.9% | 0.17% | Lava 段主矿 |
| 铱矿 | 80-119 | 0.05% → 0.5% | 0.03% | Lava 段稀有矿（调高） |
| 煤矿 | 全层 | 1% | 0.06% | 全层通用 |

### 跨段出现规则（对齐原版）

| 矿种 | 可出现层数 | 概率变化 |
|------|-----------|----------|
| 铜矿 | 1-119 | 1-39正常，40+减半 |
| 铁矿 | 40-119 | 40-79正常，80+减半 |
| 金矿 | 80-119 | 80-119正常 |
| 铱矿 | 80-119 | 80-99低概率，100-119正常 |
| 煤矿 | 1-119 | 全层一致 |

---

## 4. 宝石/稀有石头概率

| 类型 | 原版概率 | 我们的概率 |
|------|----------|------------|
| 彩色宝石石 | 0.8% | 0.05% |
| 钻石石 | 0.05% | 0.003% |
| 棱彩碎片石 | 0.05% | 0.003% |
| 晶洞 | 按主题 | 按主题（待定） |

---

## 5. 洞窟生成规则

每层生成 1-3 个小洞窟：
- 洞窟大小：3×3 ~ 6×6
- 洞窟内容：
  - 空气（挖空区域）
  - 可能包含：木桶、箱子、煤堆
  - 可能包含：怪物巢（刷怪点）

生成算法：
1. 在房间内随机选 1-3 个点（避开中心安全区）
2. 以该点为中心，用简单圆形/椭圆形挖空
3. 在洞窟边缘放装饰石
4. 在洞窟内随机放容器

---

## 6. 楼层生成流程

```java
generateFloor(int floorNumber) {
    // 1. 确定房间尺寸（20-50 随机）
    int size = random.nextInt(31) + 20; // 20-50
    
    // 2. 确定主题
    Theme theme = getThemeForFloor(floorNumber);
    boolean isDark = random.nextDouble() < 0.15; // 15% Dark 层
    
    // 3. 计算中心坐标
    int centerX = 0;
    int centerY = 64;
    int centerZ = floorNumber * 100;
    
    // 4. 生成外壳（mine_barrier）
    generateShell(centerX, centerY, centerZ, size);
    
    // 5. 填充主石头
    fillWithStone(centerX, centerY, centerZ, size, theme, isDark);
    
    // 6. 生成矿石（按概率替换石头）
    generateOres(centerX, centerY, centerZ, size, floorNumber, theme);
    
    // 7. 生成洞窟
    generateCaves(centerX, centerY, centerZ, size, theme);
    
    // 8. 生成中心安全区（梯子/电梯台座）
    generateCenterArea(centerX, centerY, centerZ, floorNumber);
    
    // 9. 记录 stonesLeft（用于梯子概率）
    countStones();
}
```

---

## 7. 大厅结构

### 导入方式
- 你用 Litematica 导出 `.litematic` 文件
- 放到 `src/main/resources/data/stardewcraft/structures/mine_entrance.litematic`
- 我用代码读取并放置到 (0, 64, 0)

### 大厅必须包含
- 电梯方块位置（玩家交互入口）
- 返回主世界的出口
- 安全区（不刷怪）

---

## 8. 待实现的 Java 类

### 核心类
1. `MiningDimension` - 维度注册与配置
2. `MineFloorGenerator` - 楼层生成器
3. `MineFloorData` - 楼层数据（stonesLeft、是否生成梯子等）
4. `MiningManager` - 管理玩家进度、电梯解锁等

### 方块类
1. `MineBarrierBlock` - 不可破坏外壳（已注册，需加逻辑）
2. `ElevatorBlock` - 电梯交互（已注册，需加逻辑）
3. `MineLadderBlock` - 下楼梯子（需注册）
4. `MineBarrelBlock` - 木桶容器（需注册）

### 事件处理
1. `MiningBlockBreakHandler` - 处理挖石头 → 梯子概率
2. `MiningMobSpawnHandler` - 处理刷怪逻辑

---

## 9. 下一步行动

1. [ ] 你：用 Litematica 建好大厅，导出 `.litematic` 给我
2. [ ] 我：注册维度 `stardewcraft:stardew_mining`
3. [ ] 我：实现 `MineFloorGenerator` 楼层生成
4. [ ] 我：实现梯子概率公式
5. [ ] 我：实现电梯系统

---

## 附录：物品 ID 对照（星露谷 → 我们）

| 星露谷 Object ID | 名称 | 我们的方块 |
|------------------|------|-----------|
| 751 | 铜矿 | `earth/frost/lava_copper_ore` |
| 290 | 铁矿 | `earth/frost/lava_iron_ore` |
| 764 | 金矿 | `earth/frost/lava_gold_ore` |
| 765 | 铱矿 | `earth/frost/lava_iridium_ore` |
| 668/670 | 普通石头 | `earth_shale` / `frost_gneiss` / `lava_basalt` |
