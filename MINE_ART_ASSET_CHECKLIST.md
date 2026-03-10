# 矿井美术/资源制作清单（对照原版即可开工）

> 这份文档回答你现在最关心的两件事：
> 1) **原版资源在哪里（你本机路径+文件名）**；
> 2) **你到底要做哪些东西（按优先级拆解到可执行任务）**。
>
> 范围：先聚焦“主矿井 1–120（SV Mines）”。火山/采石场矿井在后面单列。

---

## 0. 你现在的工作方式（最重要）

你现在要做的不是“一口气做完所有模型”。正确顺序是：

1. 先做 **地图方块材质（tilesheet → 方块贴图）**：让我们能在 MC 里搭出每张矿井模板的“墙/地/边角/装饰”。这些全部可以先用普通立方体方块，不需要 3D 模型。
2. 再做 **marker 方块**：让结构里能标出“可铺设点位/入口/电梯/宝箱”等，保证玩法能 1:1。
3. 再开始 **做结构**：按 TMX 模板照着搭。
4. **物品贴图**：你说你都能给我，那你按我下面的“需要的物品列表”去出图即可；模型可以后补。

你刚刚指出的点非常关键：原版的 `mine*.png` 是“乱但可用”的 tilesheet，地面绝对不止一种。

因此这份清单的正确目标不是“做 1 个地面方块完事”，而是：
- 从 TMX 里看原版 **实际用了多少种 Back 地面 tile**
- 把它们归类成“地面变体/边缘/角/墙体/装饰”等
- 在 MC 里用“多方块或 blockstate 变体”把这些视觉差异复刻出来

我已生成一份自动统计报告（从 `.tmx` 的 Back 层 CSV 直接统计）供你对照：`MINE_TMX_TILE_USAGE_REPORT.md`。

---

## 1. 原版资源定位（你本机已有的：精确路径）

### 1.1 矿井地图模板（TMX）

目录：`c:\Users\jk\Desktop\星露谷物语素材\Maps\Mines\`

- 主矿井模板：`1.tmx` ~ `60.tmx`（但注意：并不是每个层号都有同名模板文件）
- 结尾模板：`120.tmx`
- 采石场矿井：`77377.tmx`
- 火山（本期不做）：`VolcanoTemplate.tmx`、`Volcano_*.tmx`

### 1.2 矿井 tilesheet（TMX 引用的贴图源 PNG）

同目录：`c:\Users\jk\Desktop\星露谷物语素材\Maps\Mines\`

你现在已经有这些 png（文件名必须记牢）：

- 上层矿井基础：`mine.png`
- 上层矿井暗层：`mine_dark.png`
- 冰层矿井：`mine_frost.png`
- 冰层矿井暗层：`mine_frost_dark.png`
- 熔岩层（120 模板用）：`mine_lava.png`
- 熔岩层暗层：`mine_lava_dark.png`
- 采石场矿井：`mine_quarryshaft.png`
- 沙漠主题（当前只有 40.tmx 在用）：`mine_desert.png`
- 沙漠暗层：`mine_desert_dark.png`
- 火山：`volcano_dungeon.png`、`volcano_caldera.png`

危险矿井（后期/进阶复刻会用到）：
- `mine_dangerous.png`
- `mine_dark_dangerous.png`
- `mine_frost_dangerous.png` / `mine_frost_dark_dangerous.png`
- `mine_lava_dangerous.png` / `mine_lava_dark_dangerous.png`
- `mine_desert_dangerous.png` / `mine_desert_dark_dangerous.png`

> 说明：`*.tmx` 文件里 tileset 的 `<image source="mine"/>` 这种写法是不带扩展名的，对应同目录的 `mine.png`。

### 1.3 “哪个模板用哪个 tilesheet”（你对照做结构时就不会迷路）

我从你这批 `Mines/*.tmx` 里提取到的引用关系如下（这是“权威对照表”）：

- `mine.png`：`1.tmx, 2.tmx, 3.tmx, 4.tmx, 5.tmx, 6.tmx, 7.tmx, 8.tmx, 9.tmx, 10.tmx, 11.tmx, 12.tmx, 13.tmx, 14.tmx, 15.tmx, 16.tmx, 17.tmx, 18.tmx, 19.tmx, 20.tmx, 21.tmx, 22.tmx, 23.tmx, 24.tmx, 25.tmx, 26.tmx, 27.tmx, 28.tmx, 29.tmx, 41.tmx~60.tmx`
- `mine_dark.png`：`31.tmx~39.tmx`
- `mine_desert.png`：`40.tmx`
- `mine_lava.png`：`120.tmx`
- `mine_quarryshaft.png`：`77377.tmx`
- `volcano_dungeon.png`：`VolcanoTemplate.tmx` 与若干 `Volcano_*.tmx`

补充（很重要）：上面这张表是“从 TMX 的 `<image source=...>` 统计出来的”。
- 你目录里确实存在 `mine_frost*.png`（冰层）等 tilesheet，但当前这批 TMX 里很多模板仍然写的是 `source="mine"`。
- 这不代表冰层不需要做：主矿井 40–79 的视觉必须是冰层风格，你在 MC 里依然要准备一套 `mine_frost` 方块调色板；只是“模板编号 → 视觉 tilesheet”可能会受到矿区/层段影响，而不完全由 TMX 文件名决定。

**关键提醒（非常容易踩坑）**：
- 模板文件名是“地图编号”，不是“层号”。比如层号 80 会加载 `10` 号模板，所以你不会有 `80.tmx`。

### 1.4 物品图标（参考用；你会给自定义贴图）

你本机已看到：`c:\Users\jk\Desktop\星露谷物语素材\Maps\springobjects.png`（以及多语言变体）。

> 第一阶段我们不强制使用原版图标，但它能帮助你核对“原版矿井掉哪些东西”。

---

## 2. 你要做的东西（按优先级 + 交付物格式）

下面每一项都写了：
- **对照原版文件**：你应该打开哪个 TMX/PNG 参考
- **你要产出什么**：MC 里要新增哪些方块/贴图（模型可先不做）
- **完成标准**：做到什么程度算“可推进到下一步”

### P0（必须先做，做完就能开始搭结构）——“矿井方块调色板”

#### P0-1 上层矿井基础外观方块（对应 `mine.png`）
- 对照原版：
  - `c:\Users\jk\Desktop\星露谷物语素材\Maps\Mines\mine.png`
  - 任选一张模板辅助理解：`10.tmx`（复用率最高）
- 你要产出（按“原版实际用到的 tile”来做，下面是最低可用的拆分，不是只做 1 个地面）：
  - 地面变体（建议至少 6–12 个）：`mine_floor_a`...`mine_floor_l`
    - 用途：模拟原版地面在 TMX 里“同区域混用多个 tile”的效果。
    - 做法：从 `10.tmx` 的 Back 层里挑最常见的地面 tileId 做成变体（见统计报告）。
  - 边缘/收口（至少 4 个方向 + 角）：`mine_edge_n/e/s/w`、`mine_corner_ne/nw/se/sw`
  - 墙体/岩壁（至少 4–8 个）：`mine_wall_a`...（用于洞壁、立面、岩石纹理差异）
  - 装饰贴片（选做但很像原版）：`mine_decal_crack`、`mine_decal_debris`、`mine_decal_mud`
    - 说明：原版很多“脏/裂/碎石”来自不同 tile；在 MC 里可以做成单独的薄层装饰方块或结构专用装饰块。
- 完成标准：
  - 你能在 MC 里把 `10.tmx` 的房间外观大致搭出来（不要求一像素不差，但轮廓和材质风格一致）。

补充完成标准（更贴近“按原版”）：
- `10.tmx` 的同一块地面区域，你能用 6–12 种地面变体混铺出来，而不是“整片一张贴图”。

#### P0-2 暗层外观方块（对应 `mine_dark.png`）
- 对照原版：`...\Maps\Mines\mine_dark.png` 与模板 `31.tmx~39.tmx`
- 你要产出：与上层同结构的一套 dark 版本（地/墙/边/角）
- 完成标准：能搭 `31.tmx` 的外观。

#### P0-3 熔岩层外观方块（对应 `mine_lava.png`）
- 对照原版：`...\Maps\Mines\mine_lava.png` 与模板 `120.tmx`
- 你要产出：熔岩层的地/墙/边/角（至少 4–8 个方块）
- 完成标准：能搭 `120.tmx` 的外观。

#### P0-4 冰层外观方块（对应 `mine_frost.png`）
- 对照原版：`...\Maps\Mines\mine_frost.png`（以及暗层 `mine_frost_dark.png`）
- 你要产出：冰层的地/墙/边/角（建议和上层同结构命名，方便代码/资源管理）
- 完成标准：用于 40–79 的矿井外观能一眼看出“冰层”主题。

### P0（必须）——marker 方块（只要方块贴图，不要模型）

这些方块不追求好看，只追求你放进结构里一眼能看出来。

- `stone_spawn_marker`（红色）：可铺设点位（石头/矿点/宝石石头/刷怪/随机物品）
- `entry_marker`（绿色）：入口落点
- `elevator_marker`（蓝色）：电梯落点
- `chest_marker`（黄色）：宝箱落点（10/20/40/.../120）
- （可选）`diggable_marker`（紫色）：对应 TMX 里的 `Diggable`（用于 Duggy 条件刷怪）

完成标准：你搭完结构后，我能用代码扫描 marker，把点位导出/校验。

---

## 3. 结构制作清单（你应该先做哪些模板）

你要“按原版来”的正确理解是：**按模板编号做结构**，因为层号会映射到模板编号。

### 第一批（最值得先做）

- `10.tmx`：复用率最高，做完立刻收益最大
- `20.tmx`：会对应 20/60/100（而且宝箱层有坐标偏移细节）
- `120.tmx`：结尾层
- `1.tmx`：用来校验“上层矿井的风格/比例/标记密度”

每做完一张模板结构，你必须同时完成：
- 外观方块搭好（Back/Buildings/Front 的主要轮廓）
- marker 点位全放好（stone_spawn/entry/elevator/chest…）

---

## 4. 物品贴图交付清单（你直接给我 png 就行）

你不需要先做模型；只要贴图（png）+ 命名表。

### 4.1 必做（第一期矿井玩法会用到）

- 石头掉落相关：Stone、Coal
- 矿石：Copper Ore、Iron Ore、Gold Ore
- 晶洞：Geode、Frozen Geode、Magma Geode、Omni Geode
- 宝石（宝石富集石会用到）：Amethyst、Topaz、Aquamarine、Jade、Ruby、Emerald、Diamond

### 4.2 进阶（后续补齐更像原版）

- 怪物掉落：Slime、Bat Wing 等
- 戒指/武器/靴子（宝箱与特殊掉落会用到）：你给图即可，模型后补

交付格式建议：
- 给我一个文件夹：`mine_items/` 里面都是 png
- 再给我一个表（你随便用 txt/md）：`物品英文key -> png文件名`

---

## 5. 你卡住时就按这三条问我（最省时间）

1) “我现在做的是上层/暗层/熔岩层哪个？对应原版 png 是哪个？”（我会让你对照 1.2/1.3）
2) “这张模板我该先放哪些 marker？”（我会让你对照 2 和 3）
3) “这个物品原版叫什么/掉不掉？”（我会让你对照 springobjects 以及源码掉落表）
