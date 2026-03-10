# 渔具/鱼饵「完全复刻SV」对照清单（打勾版）

> 目标：逐项做到 **行为/数值/叠加/触发时机/消耗规则** 与 SV 一模一样。
>
> 说明：
> - ✅ = 已对齐并已落地 + 终端 `gradlew build` 验证通过
> - ⬜ = 还没做/需要进一步对照
> - ⚠️ = 已有实现但需再核对 SV 细节（避免“看起来差不多”）
>
---

## 鱼饵（Bait，stackable）

- [✅] `stardewcraft:bait`（普通鱼饵）
  - ✅ 已落地：按 SV 逻辑“任意鱼饵都会让咬钩时间 ×0.5”。
  - ✅ 已落地：成功/失败都会消耗 1 个鱼饵（后续仅需补齐 Preserving 附魔 50% 保留）。

- [✅] `stardewcraft:magnet`（磁铁鱼饵）
  - 已落地：宝箱概率 `+0.15`（等同 SV `(O)703` 分支）。
  - 核对点：是否还应影响其它宝箱相关逻辑（例如宝箱生成后内容/出现频率/金宝箱）。

- [✅] `stardewcraft:wild_bait`（野生鱼饵）
  - ✅ 已落地：咬钩时间：`×0.5`（任意鱼饵）再 `×0.75`（Wild/Challenge 分支）=> 总 `×0.375`（对齐 SV）。
  - ✅ 已落地：成功钓到鱼时有概率额外多 1 条（总计 2 条），概率 `25% + dailyLuck/2`（对齐 SV；per-player daily luck）。
  - ✅ 已落地：仍只消耗 1 个鱼饵（SV：消耗在 `doneFishing(consumeBaitAndTackle:true)` 中统一处理）。

- [✅] `stardewcraft:magic_bait`（魔法鱼饵）
  - ✅ 已落地：选鱼过滤中跳过季节/天气/时间限制（仍保留水域/水深/群系等基础规则），对齐 SV usingMagicBait 行为。

- [✅] `stardewcraft:deluxe_bait`（高级鱼饵）
  - ✅ 已落地：BobberBar 高度 `+12px`（与 SV 一致）。
  - ✅ 已落地：咬钩时间：`×0.5`（任意鱼饵）再 `×0.66`（Deluxe 分支）=> 总 `×0.33`（对齐 SV）。
  - ✅ 已落地：消耗规则与普通鱼饵一致（SV：不因 Deluxe Bait 改变消耗，仅 Preserving 附魔影响）。

- [✅] `stardewcraft:challenge_bait`（挑战鱼饵）
  - ✅ 已落地：咬钩时间：`×0.5`（任意鱼饵）再 `×0.75`（Wild/Challenge 分支）=> 总 `×0.375`（对齐 SV）。
  - ✅ 已落地：小游戏内启用 Challenge 计数：初始 3；鱼离开绿条时计数 -1；计数归零时重置进度；成功时按剩余计数给予 1-3 条鱼（对齐 SV）。

---

## 渔具（Tackle，non-stackable，可双槽叠加）

- [⬜] `stardewcraft:spinner`（旋式鱼钩）
  - ✅ 已落地：按 SV `calculateTimeUntilFishingBite` 逻辑减少“最大咬钩等待时间”上限：每个 `-5000ms`（可双槽叠加）。
  - ✅ 已落地：与鱼饵/钓鱼等级共同作用，终端 `gradlew ... classes` 编译通过。

- [✅] `stardewcraft:dressed_spinner`（华丽旋式鱼钩）
  - ✅ 已落地：按 SV `calculateTimeUntilFishingBite` 逻辑减少“最大咬钩等待时间”上限：每个 `-10000ms`（可双槽叠加）。
  - ✅ 已落地：与 Spinner 可同时装备并累加（两者共同降低上限），终端 `gradlew ... classes` 编译通过。

- [✅] `stardewcraft:trap_bobber`（陷阱浮标）
  - 已落地：按 SV 递减公式得到 `escapeLossPerTick`，并通过启动包下发到客户端小游戏生效。

- [✅] `stardewcraft:cork_bobber`（软木浮标）
  - 已落地：每个 `+24px`（支持双槽叠加）。

- [✅] `stardewcraft:lead_bobber`（铅制浮标）
  - 已落地：底部反弹速度衰减（按计数缩放），更贴近 SV。

- [✅] `stardewcraft:treasure_hunter`（寻宝者）
  - 已落地：按 SV 计数叠加：每个 `+0.05` 宝箱概率（`0.15/3`），两槽可叠加。
  - 待核对：是否还应影响金宝箱判定或宝箱出现/内容。

- [✅] `stardewcraft:barbed_hook`（倒刺钩）
  - 已落地：客户端 BobberBar 内吸附/重力逻辑按 SV 方式落地（支持计数）。

- [✅] `stardewcraft:curiosity_lure`（好奇诱饵）
  - 已落地：改为影响“选鱼权重”（对 `chance < 0.25` 的候选做线性抬底）。
  - 待核对：SV 的 `CuriosityLureBuff`（若 spawn 数据自带 override）是否需要支持。

- [✅] `stardewcraft:quality_bobber`（品质浮标）
  - 已落地：fishSize→初始品质→Quality Bobber→Perfect catch 的升级流程，并与现有 `QualityHelper`（NBT key=`Quality`, 0-3）统一。
  - 待核对：与职业/技能/完美钓鱼的叠加顺序是否完全一致。

- [✅] `stardewcraft:sonar_bobber`（声呐浮标）
  - 已落地：服务端在开小游戏时下发“是否装备声呐 + 当前上钩鱼的物品ID”，客户端小游戏 UI 显示该鱼的图标。
  - 备注：由于不引入/拷贝 SV 原版贴图，本实现用简易面板替代原版气泡框；功能与触发时机一致。

---

## 全局规则（容易漏，必须逐项对照）

- [✅] 鱼饵消耗规则（成功/失败/宝箱/双鱼）
  - ✅ 已落地：成功/失败/即时结算（垃圾/海藻等 skipMinigame）都会消耗 1 个鱼饵；Wild/Challenge 多鱼也仍只消耗 1 个。
  - ⚠️ 已落地：Preserving 50% 保留逻辑已接入，但当前以钓竿 CustomData `StardewFishingRod.Preserving=true` 作为开关（后续可再接入真正的“附魔/精通/锻造”来源）。

- [✅] 渔具耐久（uses）与断裂/消耗时机
  - ✅ 已落地：成功钓到“需要小游戏的鱼”才会消耗渔具耐久；垃圾/海藻等 skipMinigame 不消耗渔具耐久。
  - ✅ 已落地：uses 改为“剩余次数递减到 0 断裂”，默认最大 uses=20（后续若发现某些渔具有不同最大值再细分）。

- [⬜] 海盗职业（Pirate profession id=9）对宝箱概率 `+0.15`
  - 现状：服务端宝箱概率处写了 TODO；需要接入职业系统后与 SV 一致。

- [⚠️] 金色宝箱（Mastery + 0.25 + team average daily luck）
  - ✅ 已落地：概率已改为 `0.25 + dailyLuck`（per-player 下用玩家 daily luck 近似 AverageDailyLuck，并做 clamp）。
  - ⚠️ 仍需后续：Mastery 触发门槛目前仍暂用 `fishingLevel >= 10` 近似（等接入真正 mastery 系统后再完全对齐）。

- [⬜] 选鱼逻辑：Magic Bait / Curiosity Lure / Targeted bait（若未来加入）
  - 确保条件跳过、权重计算、候选过滤完全一致。

---

## 变更记录（手动补充）

- 2026-01-10：Curiosity Lure 从“经验加成”改为“低概率鱼权重抬底”；Treasure Hunter 改为可叠加计数；终端 build 通过。
