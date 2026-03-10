
# 星露谷物语战斗系统数据分析

本文档记录了从星露谷物语源代码中提取的战斗系统数据，用于设计MC模组的伤害映射系统。

---

## 1. 武器系统

### 1.1 武器类型常量
来源: [MeleeWeapon.cs](MeleeWeapon.cs)

| 类型 | 常量值 | 说明 |
|------|--------|------|
| `stabbingSword` | 0 | 刺击剑（自动转换为防御剑） |
| `dagger` | 1 | 匕首 |
| `club` | 2 | 锤子/棍棒 |
| `defenseSword` | 3 | 防御剑 |

### 1.2 武器属性

武器的核心属性（来自 `MeleeWeapon.cs`）：

```csharp
public readonly NetInt minDamage = new NetInt();     // 最小伤害
public readonly NetInt maxDamage = new NetInt();     // 最大伤害
public readonly NetInt speed = new NetInt();         // 速度（正值快，负值慢）
public readonly NetInt addedPrecision = new NetInt(); // 精准度加成
public readonly NetInt addedDefense = new NetInt();   // 防御加成
public readonly NetInt addedAreaOfEffect = new NetInt(); // 范围效果加成
public readonly NetFloat knockback = new NetFloat();  // 击退力
public readonly NetFloat critChance = new NetFloat(); // 暴击率
public readonly NetFloat critMultiplier = new NetFloat(); // 暴击伤害倍率
```

### 1.3 默认击退值

```csharp
public virtual float defaultKnockBackForThisType(int type)
{
    switch (type)
    {
        case 1:  // 匕首
            return 0.5f;
        case 0:  // 刺击剑
        case 3:  // 防御剑
            return 1f;
        case 2:  // 锤子
            return 1.5f;
        default:
            return -1f;
    }
}
```

### 1.4 暴击系统

**基础暴击率**: 0.02 (2%)

**匕首暴击加成**:
```csharp
float effectiveCritChance = critChance.Value;
if (type.Value == 1)  // 匕首
{
    effectiveCritChance += 0.005f;      // +0.5% 基础加成
    effectiveCritChance *= 1.12f;       // 再乘以1.12倍
}
```

**基础暴击伤害倍率**: 3.0x

**暴击伤害计算**:
```csharp
damageAmount = crit ? ((int)((float)damageAmount * critMultiplier)) : damageAmount;
```

### 1.5 特殊能力冷却时间

```csharp
public const int defenseCooldownTime = 1500;  // 防御剑格挡冷却 1.5秒
public const int daggerCooldownTime = 3000;   // 匕首连刺冷却 3秒
public const int clubCooldownTime = 6000;     // 锤子砸地冷却 6秒
```

**匕首连刺**: 快速刺4次 (`daggerHitsLeft = 4`)

### 1.6 速度影响

```csharp
public const int millisecondsPerSpeedPoint = 40;  // 每点速度减少40ms
public const int defaultSpeed = 400;               // 基础攻击间隔400ms
public const int baseClubSpeed = -8;               // 锤子基础速度-8
```

攻击间隔计算:
```csharp
swipeSpeed = (float)(400 - speed.Value * 40) - who.addedSpeed * 40f;
swipeSpeed *= 1f - who.buffs.WeaponSpeedMultiplier;
```

---

## 2. 弹弓系统

来源: [Slingshot.cs](Slingshot.cs)

### 2.1 弹弓类型

```csharp
public const int basicDamage = 5;           // 基础伤害
public const string basicSlingshotId = "32";    // 基础弹弓
public const string masterSlingshotId = "33";   // 大师弹弓
public const string galaxySlingshotId = "34";   // 银河弹弓
```

### 2.2 弹弓伤害倍率

```csharp
float damageMod = ((text == "33") ? 2f : ((!(text == "34")) ? 1f : 4f));
// 基础弹弓: 1x
// 大师弹弓: 2x
// 银河弹弓: 4x
```

### 2.3 弹药伤害

```csharp
public virtual int GetAmmoDamage(Object ammunition)
{
    return ammunition?.QualifiedItemId switch
    {
        "(O)388" => 2,   // 木材
        "(O)390" => 5,   // 石头
        "(O)378" => 10,  // 铜矿石
        "(O)380" => 20,  // 铁矿石
        "(O)384" => 30,  // 金矿石
        "(O)382" => 15,  // 煤炭
        "(O)386" => 50,  // 铱矿石
        "(O)441" => 20,  // 爆炸弹药
        _ => 1,          // 其他
    };
}
```

### 2.4 弹弓最终伤害计算

```csharp
(int)(damageMod * (float)(damage + Game1.random.Next(-(damage / 2), damage + 2)) * (1f + who.buffs.AttackMultiplier))
```

---

## 3. 伤害计算公式

来源: [GameLocation.cs](GameLocation.cs) `damageMonster` 方法

### 3.1 基础伤害计算

```csharp
// 1. 随机基础伤害
damageAmount = Game1.random.Next(minDamage, maxDamage + 1);

// 2. 暴击判定
if (Game1.random.NextDouble() < (double)(critChance + (float)who.LuckLevel * (critChance / 40f)))
{
    crit = true;
    damageAmount = (int)((float)damageAmount * critMultiplier);
}

// 3. 玩家攻击力加成
damageAmount = Math.Max(1, damageAmount + who.Attack * 3);

// 4. 职业加成
if (who.professions.Contains(24))  // Fighter 战士
{
    damageAmount = (int)Math.Ceiling((float)damageAmount * 1.1f);  // +10%
}
if (who.professions.Contains(26))  // Brute 野蛮人
{
    damageAmount = (int)Math.Ceiling((float)damageAmount * 1.15f); // +15%
}
if (crit && who.professions.Contains(29))  // Desperado 神枪手
{
    damageAmount = (int)((float)damageAmount * 2f);  // 暴击伤害翻倍
}
```

### 3.2 完整伤害公式

```
最终伤害 = max(1, (基础伤害 + 玩家攻击力 × 3) × 暴击倍率 × 职业加成 - 怪物韧性)
```

---

## 4. 怪物系统

来源: [Monster.cs](Monster.cs)

### 4.1 怪物基础属性

```csharp
public readonly NetInt damageToFarmer = new NetInt();    // 对玩家伤害
public readonly NetIntDelta health = new NetIntDelta();  // 当前生命值
public readonly NetInt maxHealth = new NetInt();         // 最大生命值
public readonly NetInt resilience = new NetInt();        // 韧性/护甲
public readonly NetInt slipperiness = new NetInt(2);     // 滑动程度
public readonly NetInt experienceGained = new NetInt();  // 击杀经验
public readonly NetDouble jitteriness = new NetDouble(); // 抖动程度
public readonly NetDouble missChance = new NetDouble();  // 闪避率
public readonly NetBool isGlider = new NetBool();        // 是否飞行
public readonly NetBool mineMonster = new NetBool();     // 是否矿井怪物
```

### 4.2 怪物数据解析

怪物数据格式（从 `Data/Monsters`）:
```
健康值/对玩家伤害/.../飞行/.../掉落物/韧性/抖动/追踪距离/速度/闪避率/矿井怪物/经验值/显示名
```

```csharp
protected void parseMonsterInfo(string name)
{
    string[] monsterInfo = DataLoader.Monsters(Game1.content)[name].Split('/');
    Health = Convert.ToInt32(monsterInfo[0]);          // 索引0: 生命值
    MaxHealth = Health;
    DamageToFarmer = Convert.ToInt32(monsterInfo[1]);  // 索引1: 对玩家伤害
    isGlider.Value = Convert.ToBoolean(monsterInfo[4]); // 索引4: 是否飞行
    resilience.Value = Convert.ToInt32(monsterInfo[7]); // 索引7: 韧性
    jitteriness.Value = Convert.ToDouble(monsterInfo[8]); // 索引8: 抖动
    base.speed = Convert.ToInt32(monsterInfo[10]);      // 索引10: 速度
    missChance.Value = Convert.ToDouble(monsterInfo[11]); // 索引11: 闪避率
    mineMonster.Value = Convert.ToBoolean(monsterInfo[12]); // 索引12: 矿井怪物
    ExperienceGained = Convert.ToInt32(monsterInfo[13]); // 索引13: 经验值
}
```

### 4.3 怪物受伤计算

```csharp
public virtual int takeDamage(int damage, int xTrajectory, int yTrajectory, 
    bool isBomb, double addedPrecision, Farmer who)
{
    // 伤害减去韧性，最少造成1点伤害
    int actualDamage = Math.Max(1, damage - resilience.Value);
    
    // 闪避判定
    if (Game1.random.NextDouble() < missChance.Value - missChance.Value * addedPrecision)
    {
        actualDamage = -1;  // 闪避成功
    }
    else
    {
        Health -= actualDamage;
        // ... 击退和音效处理
    }
    return actualDamage;
}
```

### 4.4 困难模式怪物增强

```csharp
public virtual void BuffForAdditionalDifficulty(int additional_difficulty)
{
    // 伤害增强
    if (DamageToFarmer != 0)
    {
        DamageToFarmer = (int)((float)DamageToFarmer * (1f + (float)additional_difficulty * 0.25f));
        int target = 20 + (additional_difficulty - 1) * 20;
        if (DamageToFarmer < target)
        {
            DamageToFarmer = (int)Utility.Lerp(DamageToFarmer, target, 0.5f);
        }
    }
    
    // 生命值增强
    MaxHealth = (int)((float)MaxHealth * (1f + (float)additional_difficulty * 0.5f));
    int target = 500 + (additional_difficulty - 1) * 300;
    if (MaxHealth < target)
    {
        MaxHealth = (int)Utility.Lerp(MaxHealth, target, 0.5f);
    }
    Health = MaxHealth;
    
    // 韧性增强
    resilience.Value += additional_difficulty * resilience.Value;
}
```

### 4.5 到达矿井底部后的怪物增强

```csharp
if (maxTimesReachedMineBottom() >= 1 && mineMonster.Value)
{
    resilience.Value += resilience.Value / 2;      // 韧性+50%
    missChance.Value *= 2.0;                       // 闪避率翻倍
    Health += Game1.random.Next(0, Health);        // 生命值随机增加
    DamageToFarmer += Game1.random.Next(0, DamageToFarmer / 2);  // 伤害随机增加
}
```

---

## 5. 玩家生命值系统

来源: [Farmer.cs](Farmer.cs), [LevelUpMenu.cs](LevelUpMenu.cs)

### 5.1 初始生命值

```csharp
public int health = 100;     // 当前生命值
public int maxHealth = 100;  // 最大生命值
```

### 5.2 战斗等级提升生命值

每提升1级战斗等级，最大生命值+5
```csharp
// 来自 LevelUpMenu.RevalidateHealth()
for (int i = 1; i <= farmer.GetUnmodifiedSkillLevel(4); i++)  // 4 = 战斗技能
{
    if (!farmer.newLevels.Contains(new Point(4, i)) && i != 5 && i != 10)
    {
        expected_max_health += 5;  // 每级+5，但5级和10级选职业不加
    }
}
```

### 5.3 职业加成

```csharp
// Fighter 战士 (职业24) - 战斗等级5时可选
public const int fighter = 24;
// 效果: maxHealth += 15, 攻击伤害+10%

// Defender 防御者 (职业27) - 战斗等级10时可选（需要先选战士）
public const int defender = 27;
// 效果: maxHealth += 25

// Scout 侦察兵 (职业25) - 战斗等级5时可选
public const int scout = 25;
// 效果: 暴击率+50%

// Acrobat 杂技师 (职业28) - 战斗等级10时可选（需要先选侦察兵）
public const int acrobat = 28;
// 效果: 特殊技能冷却减半

// Desperado 神枪手 (职业29) - 战斗等级10时可选（需要先选侦察兵）
public const int desperado = 29;
// 效果: 暴击伤害翻倍

// Brute 野蛮人 (职业26) - 战斗等级10时可选（需要先选战士）
public const int brute = 26;
// 效果: 攻击伤害+15%
```

职业生命值加成代码:
```csharp
public void getImmediateProfessionPerk(int whichProfession)
{
    switch (whichProfession)
    {
        case 24:  // Fighter 战士
            Game1.player.maxHealth += 15;
            break;
        case 27:  // Defender 防御者
            Game1.player.maxHealth += 25;
            break;
    }
    Game1.player.health = Game1.player.maxHealth;
}
```

### 5.4 其他生命值来源

- `qiCave` 邮件标记: +25 最大生命值

### 5.5 最大生命值计算公式

```
最大生命值 = 100 (基础)
           + 战斗等级 × 5 (排除5级和10级)
           + 15 (如果有战士职业)
           + 25 (如果有防御者职业)
           + 25 (如果有qiCave标记)
```

**最大可能生命值**:
- 战斗10级: 100 + 8×5 = 140
- 加战士: 140 + 15 = 155
- 加防御者: 155 + 25 = 180
- 加qiCave: 180 + 25 = **205**

### 5.6 耐力系统

```csharp
public const int startingStamina = 270;            // 初始耐力
public readonly NetInt maxStamina = new NetInt(270); // 最大耐力
private readonly NetFloat netStamina = new NetFloat(270f); // 当前耐力
```

---

## 6. 玩家受伤系统

来源: [Farmer.cs](Farmer.cs) `takeDamage` 方法

### 6.1 受伤公式

```csharp
public void takeDamage(int damage, bool overrideParry, Monster damager)
{
    // 1. 伤害浮动 (±12.5%)
    damage += Game1.random.Next(Math.Min(-1, -damage / 8), Math.Max(1, damage / 8));
    
    // 2. 获取防御值
    int defense = buffs.Defense;
    if (stats.Get("Book_Defense") != 0)
    {
        defense++;  // 防御之书+1防御
    }
    
    // 3. 防御值衰减（当防御值 >= 伤害的50%时）
    if ((float)defense >= (float)damage * 0.5f)
    {
        defense -= (int)((float)defense * (float)Game1.random.Next(3) / 10f);
        // 随机减少0%, 10%, 或20%的防御值
    }
    
    // 4. 最终伤害计算
    damage = Math.Max(1, damage - defense);  // 最少受到1点伤害
    
    // 5. 扣除生命值
    health = Math.Max(0, health - damage);
}
```

### 6.2 防御值来源

- 武器的 `addedDefense` 属性
- 戒指附魔
- Buff效果
- 防御之书 (`Book_Defense`)

### 6.3 特殊防护

**Yoba之戒 (Ring 524)**:
```csharp
// 生命值越低，触发几率越高
if (isWearingRing("524") && !hasBuff("21") && 
    Game1.random.NextDouble() < (0.9 - (double)((float)health / 100f)) / (double)(3 - LuckLevel / 10) 
    + ((health <= 15) ? 0.2 : 0.0))
{
    // 触发无敌状态
    applyBuff("21");
}
```

**凤凰戒指 (Ring 863)** - 每日复活:
```csharp
if (health <= 0 && GetEffectsOfRingMultiplier("863") > 0 && !hasUsedDailyRevive.Value)
{
    health = (int)Math.Min(maxHealth, (float)maxHealth * 0.5f + (float)GetEffectsOfRingMultiplier("863"));
    hasUsedDailyRevive.Value = true;
}
```

### 6.4 格挡系统

使用防御剑特殊技能时可格挡:
```csharp
bool playerParryable = CurrentTool is MeleeWeapon 
    && ((MeleeWeapon)CurrentTool).isOnSpecial 
    && ((MeleeWeapon)CurrentTool).type.Value == 3;  // 防御剑

if (playerParryable)
{
    playNearbySoundAll("parry");
    damager.parried(damage, this);  // 怪物被弹开
}
```

### 6.5 无敌时间

```csharp
public const int millisecondsInvincibleAfterDamage = 1200;  // 受伤后无敌1.2秒
currentTemporaryInvincibilityDuration = 1200 + GetEffectsOfRingMultiplier("861") * 400;
// Ring 861 每个+0.4秒无敌时间
```

---

## 7. 经验值系统

来源: [Farmer.cs](Farmer.cs)

### 7.1 技能经验需求

```csharp
public static int checkForLevelGain(int oldXP, int newXP)
{
    // 等级对应的累计经验值
    // Level 1: 100
    // Level 2: 380
    // Level 3: 770
    // Level 4: 1300
    // Level 5: 2150
    // Level 6: 3300
    // Level 7: 4800
    // Level 8: 6900
    // Level 9: 10000
    // Level 10: 15000
}
```

### 7.2 战斗经验获取

击杀怪物时获得怪物的 `experienceGained` 值的战斗经验。

---

## 8. MC模组伤害映射建议

### 8.1 生命值映射

| 星露谷 | MC建议值 | 说明 |
|--------|----------|------|
| 100 HP (初始) | 20 HP (10❤) | 标准MC生命值 |
| 200 HP (满级) | 40 HP (20❤) | 需要增加额外生命值 |

**映射比例**: 1 星露谷HP ≈ 0.2 MC HP (1:5)

### 8.2 伤害映射

**怪物伤害**: 根据源代码，怪物伤害范围约为 5-50+

| 星露谷伤害 | MC建议伤害 |
|------------|------------|
| 5-10 | 1-2 |
| 11-20 | 3-4 |
| 21-35 | 5-7 |
| 36-50+ | 8-10+ |

**映射比例**: 约 5:1

### 8.3 武器伤害

基于 `minDamage` 和 `maxDamage`，建议按武器等级划分:
- 初期武器 (1-15伤害): MC 1-3伤害
- 中期武器 (15-40伤害): MC 4-8伤害  
- 后期武器 (40-80伤害): MC 9-16伤害
- 顶级武器 (80+伤害): MC 17+伤害

### 8.4 防御值

星露谷防御直接减少伤害值，MC可以考虑:
- 使用护甲值系统
- 或者作为伤害减免百分比

---

## 9. 数据文件位置

游戏数据文件通常位于:
- `Data/Weapons` - 武器数据
- `Data/Monsters` - 怪物数据
- `Data/Boots` - 靴子/防具数据

这些数据可以通过 `DataLoader` 类加载。
