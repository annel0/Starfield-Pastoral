# 鸡舍管理器开发任务清单（可直接执行）

> 目标：在不超出原版星露谷体验边界的前提下，实现“鸡舍管理器”GUI + 判定范围预览 + 成型/缺件检查。
>
> 约束：仅做“查看/提示/判定”，不做自动化代劳（无一键喂食/收集/抚摸）。

---

## 0. 范围定义（本阶段）

### In Scope
- 管理器 UI（单页）
- 鸡舍结构成型判定（✔/✖）
- 缺件列表（需求数量/当前数量）
- 范围预览（世界内线框 + 缺件点位）
- 建筑切换（上一栋/下一栋）
- 鸡舍基础摘要（容量、入住、门状态、干草容量/库存）

### Out of Scope（本阶段不做）
- 自动化操作按钮（一键喂食/收集/抚摸）
- NPC 商店购买 UI（玛尼）
- 完整动物 AI/日常行为重写
- 畜棚（Barn）管理器界面（后续复用鸡舍方案扩展）

---

## 1. 现有基础（可复用）

- 动物持久化：`src/main/java/com/stardew/craft/animal/data/AnimalWorldData.java`
- 建筑记录：`src/main/java/com/stardew/craft/animal/model/AnimalBuildingRecord.java`
- 建筑类型：`src/main/java/com/stardew/craft/animal/model/AnimalBuildingType.java`
- 动物调试命令：`src/main/java/com/stardew/craft/command/AnimalDebugCommand.java`
- 干草相关：`HayHopperBlock` / `FeedTroughBlock` / `AnimalWorldData` 的 hay 字段

---

## 2. 里程碑与任务分解

## M0 - 数据契约冻结（先做）

- [ ] 定义 UI 所需最小数据结构 `CoopManagerSnapshot`
  - 字段：buildingId、name、tier、isComplete、missingParts[]、capacity、members、hayStored、hayCap、doorOpen、range、bounds
- [ ] 定义缺件项结构 `MissingPartEntry`
  - 字段：partId、displayName、required、found、status
- [ ] 定义范围预览点位结构 `StructureMarker`
  - 字段：pos、markerType（MISSING/OK/BOUNDARY）、label
- [ ] 冻结网络载荷字段（避免 GUI 反复改协议）

**验收标准**
- 有一份固定字段清单；客户端无需读服务端内部对象即可渲染。

---

## M1 - 鸡舍结构判定服务

- [ ] 新增包：`com.stardew.craft.animal.validation`
- [ ] 新增类：`CoopStructureValidator`
  - 输入：managerPos + range + level
  - 输出：`ValidationResult`（complete + missingParts + markers + bounds）
- [ ] 新增类：`ValidationResult`
- [ ] 规则实现（先写死在类内，后续可配置化）
  - 必需组件：管理器本体、围栏/边界、门、饲料槽位、栖架、孵化器位（按 tier）
- [ ] 与 `AnimalBuildingType` 对齐 tier 条件（T1/T2/T3）

**验收标准**
- 同一范围多次校验结果稳定；缺件数与点位可复现。

---

## M2 - 管理器后端接口（服务端）

- [ ] 新增服务：`com.stardew.craft.animal.service.CoopManagerService`
  - 方法：`buildSnapshot(ServerPlayer, buildingId)`
  - 方法：`listOwnedCoops(ServerPlayer)`
  - 方法：`validateBuildingNow(ServerPlayer, buildingId)`
- [ ] 在 `AnimalWorldData` 增加只读查询帮助方法（避免 GUI 侧拼装业务）
  - `getOwnedBuildingsByFamily(owner, "coop")`
  - `getAnimalsByBuilding(buildingId)`（如未有）
- [ ] 失败场景错误码化（building not found / no permission / dimension mismatch）

**验收标准**
- 调试命令可拿到快照对象；异常会返回可读错误信息。

---

## M3 - 网络协议与客户端状态

- [ ] 新增 C2S：`RequestCoopManagerDataPacket`
- [ ] 新增 S2C：`CoopManagerDataPacket`
- [ ] 新增 C2S：`ToggleCoopRangePreviewPacket`
- [ ] 在 `PacketHandler` 注册上述包
- [ ] 新增客户端缓存：`CoopManagerClientState`
  - 当前 buildingId
  - 当前 snapshot
  - preview 开关
  - markers 列表

**验收标准**
- 打开 GUI 时能拉取数据；切换建筑时数据刷新正常。

---

## M4 - GUI 实现（单页，原版风格）

- [ ] 新增界面：`com.stardew.craft.client.screen.CoopManagerScreen`
- [ ] 控件（固定）
  - `btnPrevCoop`、`btnNextCoop`
  - `btnRangePreview`（查看范围）
  - `btnClose`
  - 缺件滚动列表 `missingList`
- [ ] 布局
  - 左侧：成型状态 + 缺件列表
  - 右侧：鸡舍摘要（容量/入住/门/干草）
  - 顶部：鸡舍名称 + tier + ✔/✖
- [ ] 状态显示
  - COMPLETE：绿色勾
  - INCOMPLETE：红叉 + 缺件高亮
- [ ] 文案 key 新增（`zh_cn.json` / `en_us.json`）

**验收标准**
- 三个状态可显示：无目标/未成型/已成型。
- 不出现超范围操作按钮。

---

## M5 - 世界范围预览渲染

- [ ] 新增渲染器：`com.stardew.craft.client.render.CoopRangePreviewRenderer`
- [ ] 订阅世界渲染事件（仅在 preview=true 时绘制）
- [ ] 绘制内容
  - 边界框：绿色（完整）/黄色（可用）
  - 缺件点位：红色方框 + 简短标签
  - 已满足点位：可选淡绿点
- [ ] ESC 或按钮再次点击关闭预览

**验收标准**
- 预览可开关，不卡顿，不污染其他维度渲染。

---

## M6 - 接入入口与调试闭环

- [ ] 新增打开 GUI 入口（建议先命令，后物品）
  - 命令：`/stardew coop_manager open [buildingId]`
- [ ] 可选新增管理器物品（后续）
- [ ] `AnimalDebugCommand` 增强
  - 输出 `ValidationResult` 摘要
  - 快速生成测试鸡舍结构（调试专用）

**验收标准**
- 不依赖开发者手动改 NBT，即可端到端测试。

---

## 3. 测试清单（逐条打勾）

### 功能测试
- [ ] 无鸡舍时打开 GUI，显示“无可管理鸡舍”
- [ ] 未成型鸡舍显示红叉 + 缺件列表
- [ ] 成型鸡舍显示绿勾
- [ ] 切换上一栋/下一栋数据一致
- [ ] 查看范围按钮可开关

### 边界测试
- [ ] 非所有者打开他人鸡舍（应拒绝）
- [ ] 跨维度请求（应拒绝）
- [ ] 建筑删除后 GUI 刷新不崩溃
- [ ] 大范围（64）预览帧率可接受

### 兼容测试
- [ ] 与现有 HUD（时间/钱币/提示消息）不冲突
- [ ] 与 Jade 显示不冲突
- [ ] 重新进档后状态一致

---

## 4. 任务优先级（建议执行顺序）

1. M0（数据契约）
2. M1（结构判定）
3. M2（服务端快照）
4. M3（网络）
5. M4（GUI）
6. M5（范围预览）
7. M6（入口与调试）

---

## 5. 实施时长预估（单人）

- M0: 0.5 天
- M1: 1~1.5 天
- M2: 0.5 天
- M3: 0.5 天
- M4: 1~1.5 天
- M5: 0.5~1 天
- M6: 0.5 天

**总计：约 5~7 天**

---

## 6. 首次开工建议（下一步）

- 先做 M0 + M1：把 `CoopStructureValidator` 和 `ValidationResult` 定下来。
- 这一步稳定后，GUI 就只是“画数据”，不会反复返工协议。
