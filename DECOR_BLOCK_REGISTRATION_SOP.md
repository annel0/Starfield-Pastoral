# 新装饰方块注册流程与本次踩坑复盘

## 背景
本项目目标是与 Stardew Valley 做代码级 1:1 行为对齐。新增装饰方块不能只做到“能显示”，必须保证：

- 命名与资源路径一致
- 碰撞与模型严格对齐
- extension 机制与既有系统一致
- 透明渲染、掉落、拆除联动等行为与现有实现一致

本文件包含两部分：

1. 本次聊天中出现的错误复盘（反例）
2. 新装饰方块标准注册流程（正例）

---

## 第一部分：本次踩坑复盘（反例）

## 1. 只改了一半链路，命名与引用未全量同步

问题：注册名改了，但 blockstate、item model、lang、catalog、intake 等没有同步完，产生残留引用。

后果：

- 资源找不到
- 老命名和新命名并存，后续维护混乱

教训：命名变更必须做全链路一次性迁移，不能局部替换。

## 2. 贴图路径体系混用，出现紫黑贴图

问题：模型贴图引用和实际图集目录不一致，出现 deco 路径和 block/deco 路径混用。

后果：

- Missing texture
- 实机出现紫黑块

教训：模型中的纹理路径必须与最终资源目录、打包路径、atlas 采样路径三者一致。

## 3. 漏掉 cutout 注册，透明区域显示错误

问题：新装饰块未加入客户端 cutout 注册列表。

后果：

- alpha 区域显示异常

教训：所有带透明贴图的装饰块必须进入统一渲染层注册。

## 4. 碰撞逻辑被错误改成固定薄板

问题：为了临时修复，曾把墙饰碰撞改成固定薄板，偏离了模型 1:1 解析。

后果：

- 与既有行为不一致
- 用户可感知的碰撞错误

教训：碰撞必须来自模型或既有标准 shape 体系，不能临时硬编码替代。

## 5. 改了方块类，但 blockstate 没跟上 part 变体

问题：切换到 static extension 体系后，部分 blockstate 仍是单一 facing 变体，没有 part=main 和 part=extension。

后果：

- extension 被渲染成实体模型（不该出现）

教训：只要走 PART 体系，blockstate 必须显式声明 main 与 extension 分支，且 extension 指向空模型。

## 6. 审计范围不足，先盯墙饰后补 bonsai

问题：最初只盯 wall，没有第一时间全量覆盖 bonsai 与 window。

后果：

- 修复不完整
- 来回返工

教训：必须先做全量清单和批量审计，再动手改代码。

---

## 第二部分：新装饰方块标准注册流程（正例）

## 0. 先做清单，不要先写代码

先列出本批次全部对象：

- 注册名
- 模型文件
- 贴图文件
- 是否墙挂
- 是否可能跨格（X/Y/Z 超出 0..16）

必须先审计模型边界，再决定方块类。

## 1. 选择正确方块类型（决策表）

### 1.1 单格墙挂、无 extension

- 使用 MapDecorWallThinBlock 或 MapDecorWallSwitchBlock
- 碰撞来自模型变体解析

### 1.2 多格或超高（需要自动 extension）

- 非墙挂：MapDecorStaticBlock
- 墙挂：MapDecorWallStaticBlock

说明：MapDecorWallStaticBlock 继承 static 分片能力，并额外做墙面生存校验。

## 2. 模型与贴图规范

- 统一纹理路径前缀，禁止同批次内混用两套路径体系
- 模型坐标允许跨格，但必须与 PART 扩展体系配套
- extension 绝不使用可见模型

## 3. blockstate 规范

## 3.1 非 PART 类型（单格）

- 只定义 facing（以及 on/off 等业务状态）

## 3.2 PART 类型（必须）

必须包含：

- part=main,facing=...
- part=extension,facing=...

并且 extension 一律指向空模型：

- 使用“空元素模型”，但不能丢失粒子贴图

## 3.3 extension 粒子规范（强制）

extension 方块破坏粒子必须与 main 方块一致。禁止使用无粒子定义的通用空模型，否则会出现粒子与主方块不一致。

执行方式：

- extension 使用专用空模型（elements 为空）
- 该专用空模型的 `textures.particle` 必须指向对应 main 模型的粒子贴图
- 推荐路径：`stardewcraft:block/decor/extensions/<block_name>_extension_empty`

## 4. Java 注册规范

在 ModBlocks 中注册时：

- 传入正确的方块类
- 传入正确模型 id
- 墙挂多格对象必须走 MapDecorWallStaticBlock

在 ModItems 中保证 block item 同步注册。

## 5. 客户端渲染层规范

在客户端 setup 的 cutout 列表中补齐本批次透明装饰块。遗漏任何一个都会导致透明显示异常。

## 6. 语言与数据映射规范

同步更新：

- 语言文件键
- 装饰 catalog 映射
- intake 数据源映射

禁止保留旧命名残留引用。

## 7. 强制验收清单（每次提交前必须全过）

## 7.1 静态检查

- 搜索本批次注册名，确认 Java 与资源一一对应
- 搜索 part=extension，确认都映射空模型（针对 PART 体系对象）
- 抽查 extension 专用空模型，确认 `textures.particle` 与 main 模型粒子贴图一致
- 搜索 Missing texture 相关路径，确认无错位引用

## 7.2 编译检查

- 执行 gradle classes，必须成功

## 7.3 实机检查

逐个对象验证：

- 放置后外观正确
- 碰撞与模型一致
- extension 不显示实体模型
- extension 破坏粒子与 main 保持一致
- 从主块走到 extension 区域时碰撞连续，体感为一体
- 破坏主块与破坏 extension 时，联动销毁和掉落正确
- 破坏支撑墙面后，墙挂对象正确失效

---

## 第三部分：以后新增装饰的执行顺序（硬性）

1. 先做全量清单与边界审计
2. 再确定方块类选型
3. 再写 blockstate 与模型引用
4. 再做 Java 注册与 cutout 注册
5. 再补语言与数据映射
6. 最后跑编译与实机验收

禁止跳步，禁止先改一半再补。

---

## 附：本次最关键修正点

- 恢复墙饰 1:1 模型碰撞解析
- 增加墙挂 static 类型，支持自动 extension 与墙面生存校验
- 将需要 extension 的墙饰、墙挂 bonsai、window 切到 PART 体系
- 将上述对象 blockstate 的 extension 显式映射为空模型

该流程即日起作为装饰方块新增与重构的统一标准。