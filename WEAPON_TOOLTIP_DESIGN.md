# 武器描述栏设计文档

## 设计原则

1. **视觉层次分明** - 重要信息突出，次要信息辅助
2. **图标+数值** - 用图标代替文字标签，简洁直观
3. **颜色编码** - 不同类型信息用不同颜色区分
4. **留白适当** - 不拥挤，有呼吸感

---

## 颜色规范

| 用途 | MC颜色代码 | 颜色 | 说明 |
|------|------------|------|------|
| 武器名称 (普通) | §f | 白色 | Lv.1-4 |
| 武器名称 (稀有) | §a | 绿色 | Lv.5-8 |
| 武器名称 (史诗) | §9 | 蓝色 | Lv.9-12 |
| 武器名称 (传说) | §5 | 紫色 | Lv.13-16 |
| 武器名称 (神话) | §6 | 金色 | Lv.17+ |
| 武器类型 | §7 | 灰色 | "剑" "匕首" "棍棒" |
| 伤害数值 | §c | 红色 | 伤害范围 |
| 正面属性 | §a | 绿色 | +速度, +防御 |
| 负面属性 | §c | 红色 | -速度 |
| 技能名称 | §e | 黄色 | 技能标题 |
| 技能描述 | §7 | 灰色 | 技能效果说明 |
| 技能数值 | §b | 青色 | 伤害百分比、冷却等 |
| 背景故事 | §8 | 深灰色 | 斜体描述文字 |

---

## 所需图标清单

### 属性图标 (8x8 或 9x9)
放置于 `textures/gui/weapon_skill/`

| 图标文件名 | 用途 | 设计说明 |
|------------|------|----------|
| `icon_damage.png` | 伤害 | 红色剑/刀刃形状 |
| `icon_speed.png` | 攻击速度 | 蓝色风/闪电形状 |
| `icon_defense.png` | 防御 | 银色盾牌形状 |
| `icon_weight.png` | 重量/击退 | 棕色重锤/砝码形状 |
| `icon_crit_chance.png` | 暴击率 | 黄色星星/爆炸形状 |
| `icon_crit_power.png` | 暴击伤害 | 橙色双星/火焰星形状 |
| `icon_cooldown.png` | 冷却时间 | 蓝色时钟形状 |
| `icon_level.png` | 等级 | 绿色向上箭头 |

### 武器类型图标 (8x8)
| 图标文件名 | 用途 |
|------------|------|
| `icon_type_sword.png` | 剑类标识 |
| `icon_type_dagger.png` | 匕首类标识 |
| `icon_type_club.png` | 棍棒类标识 |

### 技能图标 (16x16)
| 图标文件名 | 用途 |
|------------|------|
| `rusty_sword_1.png` | 生锈的剑 - 小技能「破伤一击」 |

---

## 描述栏布局设计

### 生锈的剑 (Rusty Sword) - 示例

```
┌─────────────────────────────────────┐
│  生锈的剑                            │  ← §f白色 武器名称
│  §7⚔ 剑  §8│  §7Lv.1                 │  ← 武器类型 + 等级
├─────────────────────────────────────┤
│                                     │
│  §c⚔ 2-5 伤害                        │  ← 伤害 (红色图标+数值)
│                                     │
│  §7══════════════════════════        │  ← 分隔线
│                                     │
│  §e⬡ 破伤一击  §7[右键]              │  ← 技能名(黄) + 触发方式(灰)
│  §7  挥出一击，使敌人获得            │
│  §7  「§c破伤风§7」效果              │
│                                     │
│  §8  ├ §b100% §7武器伤害             │  ← 伤害系数
│  §8  ├ §c+10% §7易伤 §8(3秒)         │  ← debuff效果
│  §8  └ §9⏱ 5秒 §7冷却                │  ← 冷却时间
│                                     │
│  §7══════════════════════════        │  ← 分隔线
│                                     │
│  §8§o"锈迹斑斑的旧剑，但在矿井      │  ← 背景故事 (深灰斜体)
│  §8§o 的黑暗中，它仍是你最可靠      │
│  §8§o 的伙伴。"                      │
│                                     │
└─────────────────────────────────────┘
```

---

## JSON组件格式 (MC 1.21)

```json
{
  "id": "stardew:rusty_sword",
  "components": {
    "minecraft:item_name": {
      "text": "生锈的剑",
      "color": "white"
    },
    "minecraft:lore": [
      {"text": ""},
      [
        {"text": "⚔ ", "color": "gray", "font": "stardew:icons"},
        {"text": "剑", "color": "gray"},
        {"text": " │ ", "color": "dark_gray"},
        {"text": "Lv.1", "color": "gray"}
      ],
      {"text": ""},
      [
        {"text": "⚔ ", "color": "red", "font": "stardew:icons"},
        {"text": "2-5", "color": "red"},
        {"text": " 伤害", "color": "gray"}
      ],
      {"text": ""},
      {"text": "════════════════════", "color": "dark_gray"},
      {"text": ""},
      [
        {"text": "⬡ ", "color": "yellow", "font": "stardew:icons"},
        {"text": "破伤一击", "color": "yellow"},
        {"text": "  [右键]", "color": "dark_gray"}
      ],
      [
        {"text": "  挥出一击，使敌人获得", "color": "gray"}
      ],
      [
        {"text": "  「", "color": "gray"},
        {"text": "破伤风", "color": "red"},
        {"text": "」效果", "color": "gray"}
      ],
      {"text": ""},
      [
        {"text": "  ├ ", "color": "dark_gray"},
        {"text": "100%", "color": "aqua"},
        {"text": " 武器伤害", "color": "gray"}
      ],
      [
        {"text": "  ├ ", "color": "dark_gray"},
        {"text": "+10%", "color": "red"},
        {"text": " 易伤 ", "color": "gray"},
        {"text": "(3秒)", "color": "dark_gray"}
      ],
      [
        {"text": "  └ ", "color": "dark_gray"},
        {"text": "⏱ ", "color": "blue", "font": "stardew:icons"},
        {"text": "5秒", "color": "blue"},
        {"text": " 冷却", "color": "gray"}
      ],
      {"text": ""},
      {"text": "════════════════════", "color": "dark_gray"},
      {"text": ""},
      {"text": "\"锈迹斑斑的旧剑，但在矿井", "color": "dark_gray", "italic": true},
      {"text": " 的黑暗中，它仍是你最可靠", "color": "dark_gray", "italic": true},
      {"text": " 的伙伴。\"", "color": "dark_gray", "italic": true}
    ],
    "minecraft:attribute_modifiers": {
      "modifiers": [
        {
          "type": "minecraft:generic.attack_damage",
          "amount": 2.5,
          "operation": "add_value",
          "slot": "mainhand",
          "id": "stardew:weapon_damage"
        },
        {
          "type": "minecraft:generic.attack_speed",
          "amount": -2.4,
          "operation": "add_value",
          "slot": "mainhand",
          "id": "stardew:weapon_speed"
        }
      ],
      "show_in_tooltip": false
    },
    "minecraft:custom_data": {
      "stardew": {
        "weapon_type": "sword",
        "weapon_id": "rusty_sword",
        "level": 1,
        "damage_min": 2,
        "damage_max": 5,
        "crit_chance": 0.02,
        "crit_power": 1.0,
        "defense": 0,
        "speed": 0,
        "weight": 0,
        "skill_1": "tetanus_strike",
        "skill_1_cooldown": 5
      }
    }
  }
}
```

---

## 自定义字体配置

需要创建自定义字体来显示图标：

### `assets/stardew/font/icons.json`
```json
{
  "providers": [
    {
      "type": "bitmap",
      "file": "stardew:gui/weapon_skill/icon_damage.png",
      "ascent": 7,
      "height": 9,
      "chars": ["⚔"]
    },
    {
      "type": "bitmap", 
      "file": "stardew:gui/weapon_skill/icon_speed.png",
      "ascent": 7,
      "height": 9,
      "chars": ["⚡"]
    },
    {
      "type": "bitmap",
      "file": "stardew:gui/weapon_skill/icon_defense.png", 
      "ascent": 7,
      "height": 9,
      "chars": ["🛡"]
    },
    {
      "type": "bitmap",
      "file": "stardew:gui/weapon_skill/icon_weight.png",
      "ascent": 7,
      "height": 9,
      "chars": ["⚖"]
    },
    {
      "type": "bitmap",
      "file": "stardew:gui/weapon_skill/icon_crit_chance.png",
      "ascent": 7,
      "height": 9,
      "chars": ["★"]
    },
    {
      "type": "bitmap",
      "file": "stardew:gui/weapon_skill/icon_crit_power.png",
      "ascent": 7,
      "height": 9,
      "chars": ["✦"]
    },
    {
      "type": "bitmap",
      "file": "stardew:gui/weapon_skill/icon_cooldown.png",
      "ascent": 7,
      "height": 9,
      "chars": ["⏱"]
    },
    {
      "type": "bitmap",
      "file": "stardew:gui/weapon_skill/icon_skill.png",
      "ascent": 7,
      "height": 9,
      "chars": ["⬡"]
    }
  ]
}
```

---

## 需要绘制的图标列表

### 属性图标 (9x9 像素)
1. `icon_damage.png` - 伤害图标（红色剑刃/交叉剑）
2. `icon_speed.png` - 速度图标（蓝色闪电/风）
3. `icon_defense.png` - 防御图标（银色盾牌）
4. `icon_weight.png` - 重量图标（棕色砝码）
5. `icon_crit_chance.png` - 暴击率图标（黄色星星）
6. `icon_crit_power.png` - 暴击伤害图标（橙色双星）
7. `icon_cooldown.png` - 冷却图标（蓝色时钟）
8. `icon_skill.png` - 技能图标（六边形标记）

### 技能图标 (16x16 像素)
1. `rusty_sword_1.png` - 「破伤一击」技能图标

---

## 图标Prompt汇总

### 属性图标 Prompts

**icon_damage.png:**
```
Pixel art icon, 9x9 pixels, two crossed swords forming an X shape, red metallic color with darker red outline, simple flat design, transparent background, game UI style
```

**icon_speed.png:**
```
Pixel art icon, 9x9 pixels, a lightning bolt or wind swirl symbol, cyan/light blue color with white highlight, simple flat design, transparent background, game UI style
```

**icon_defense.png:**
```
Pixel art icon, 9x9 pixels, a small shield shape, silver/gray metallic color with darker outline, simple flat design, transparent background, game UI style
```

**icon_weight.png:**
```
Pixel art icon, 9x9 pixels, a heavy weight or knockback arrow symbol, brown/bronze color with darker outline, simple flat design, transparent background, game UI style
```

**icon_crit_chance.png:**
```
Pixel art icon, 9x9 pixels, a four-pointed star or sparkle, bright yellow color with orange outline, simple flat design, transparent background, game UI style
```

**icon_crit_power.png:**
```
Pixel art icon, 9x9 pixels, a larger burst star or explosion symbol, orange color with red outline and yellow center, simple flat design, transparent background, game UI style
```

**icon_cooldown.png:**
```
Pixel art icon, 9x9 pixels, a simple clock face or hourglass, blue color with darker blue outline, simple flat design, transparent background, game UI style
```

**icon_skill.png:**
```
Pixel art icon, 9x9 pixels, a hexagon shape with inner glow, golden yellow color with orange outline, simple flat design, transparent background, game UI style
```

### 技能图标 Prompt

**rusty_sword_1.png (破伤一击):**
```
Pixel art game skill icon, 16x16 pixels, a rusty sword slashing with orange-brown rust particles flying off, small green poison/infection drops around the blade tip, dark scratched metal texture, simple fantasy RPG style, black outline, transparent background
```
