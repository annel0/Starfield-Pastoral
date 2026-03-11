# Stardew Valley 怪物最小动作规格（排除火山与沙漠）

## 原版美术资产都在哪里

1. 怪物贴图主目录：`Content/Characters/Monsters/`
- 代码通常用 `"Characters\\Monsters\\<MonsterName>"` 加载。
- 入口见各怪物 `reloadSprite()`，例如 `源文件/StardewValley.Monsters/GreenSlime.cs`、`源文件/StardewValley.Monsters/Bat.cs`。

2. 特效与阴影：
- 阴影常用 `Game1.shadowTexture`。
- 投射物/死亡特效主要由代码生成（`TemporaryAnimatedSprite`、`BasicProjectile`）。

## 你这次真正要做的动作数量（最小版）

说明：
- 下面是按“可开工外包”的最小动作，不是把源码里的所有关键帧都拆成独立动画。
- 很多怪物在原版没有独立攻击动作，伤害来自位移碰撞或投射物逻辑。
- `death` 在本项目里默认是可选项：可直接复用 MC `LivingEntity` 通用死亡流程。

| 怪物组 | 建议动作数 | 最小动作清单 | 备注 |
|---|---:|---|---|
| 小史莱姆组（Green/Frost/Sludge） | 3 | idle, move, hit | 攻击靠跳扑位移与碰撞，不必单独 attack clip |
| 大史莱姆（Big Slime） | 3 | idle, move, hit | 可选加 1 个 split；death 默认走 MC 通用 |
| 蝙蝠组（Bat/Frost/Lava） | 3 | fly_idle, fly_move, hit | 扑击是位移逻辑，不必单独 attack clip |
| 虫组（Bug/ArmoredBug/Grub/Fly） | 3 | idle, move, hit | ArmoredBug 只需额外“受击弹开反馈” |
| Duggy | 4 | hidden, emerge, move_or_attack, hit | 这是少数需要 emerge 的怪 |
| Dust Spirit | 3 | idle, move, hit | |
| 幽灵组（Ghost/Carbon/Putrid） | 3 | float_idle, float_move, hit | Putrid 额外腐化粒子可后做 |
| 岩蟹组（Rock/Lava/Truffle） | 4 | shell_idle, shell_move, body_move, hit | 壳态和破壳态建议至少两种移动表现 |
| 石魔像（RockGolem） | 3 | idle, move, hit | |
| 暗影组（Brute/Shaman/Sniper） | 4 | idle, move, attack_or_cast, hit | 三者可共用骨架后分支 |
| Metal Head | 3 | idle, move, hit | |
| Skeleton | 4 | idle, move, attack, hit | |
| Leaper | 4 | idle, move, leap, hit | |
| Blue Squid | 3 | float_idle, float_move, hit | |
| Squid Kid | 4 | float_idle, float_move, cast, hit | 投射物发射动作建议单独 1 个 |
| Pepper Rex（DinoMonster） | 4 | idle, move, breath_attack, hit | 喷火动作建议单独 1 个 |

## 史莱姆组结论（你问的重点）

小史莱姆你完全可以先按 **3 个动作** 开工：
1. `idle`
2. `move`
3. `hit`

如果要再加一个，就加 `jump_attack`；`death` 可以先不做，直接用 MC 通用死亡。

## 为什么 `death` 可以不做

1. `LivingEntity` 自带通用死亡流程（倒地旋转/死亡计时/移除）。
2. 对玩法没有硬依赖，省掉 `death` 不会影响伤害结算和掉落。
3. 外包第一批资产先把战斗读感做出来（idle/move/hit/attack），性价比最高。

## 位移参考系（你问的“扑过去怎么做”）

结论：**默认不要做 root motion 位移**，动画只做形变，位移交给代码。

推荐做法：
1. GeckoLib 动画里根骨骼保持原地，不在动画里推实体坐标。
2. 怪物扑击/追击/冲刺距离由 AI 代码控制（速度、加速度、轨迹、碰撞）。
3. 动画只表达“前摇/发力/收招”，不要承担真实位移。

只有一种例外：
1. 你明确要做纯演出镜头时，才临时用 root motion。
2. 正常战斗实机不建议，否则碰撞和同步会很痛苦。

## 行为真值入口（简化版）

1. 通用战斗流程：`源文件/StardewValley/GameLocation.cs`
2. 矿井刷怪与怪物选择：`源文件/StardewValley.Locations/MineShaft.cs`
3. 史莱姆行为：`源文件/StardewValley.Monsters/GreenSlime.cs`
4. 你要判断“有没有独立攻击动画”，重点看各怪的 `behaviorAtGameTick()` 和 `updateAnimation()`。
