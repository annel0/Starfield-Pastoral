# Skill Experience TODO

## 已接入来源（运行时）
- Farming: `StardewCropBlock.harvest(...)` 收获成熟作物。
- Fishing: `FishingSessionManager`（原有）。
- Foraging: `WildTreeChopEvents`（倒树、树桩）+ `WildTreeSeedManager.shake(...)`（摇树掉种子）。
- Mining: `MinePickaxeEvents.onBlockBreak(...)`（石头/矿石/宝石矿）。
- Combat: `WeaponCombatEvents.onLivingDamagePost(...)`（击杀目标）。

## 精准对齐状态
- Farming:
  - 已按 `farming/xp/crop_xp_table.mcfunction` 逐作物精确映射到 Java 运行时（含别名兼容）。
- Mining:
  - 已按 `mining/break_stone.mcfunction` 对石头/矿石/宝石矿精确映射到 Java 运行时。
- Combat:
  - 已按 `combat/xp/monster_xp_table.mcfunction` 对 `sd_mob_* + sd_tier_*` 标签精确映射；并保留实体类型回退。

## 仍建议专项回归
- Global:
  - 经验来源优先级与防重复规则（多段伤害/多事件链）建议做一轮联机回归。
