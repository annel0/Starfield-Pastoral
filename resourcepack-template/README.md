# OhoDavi's Anime Portraits for StardewCraft
# OhoDavi 动漫风肖像 - StardewCraft 资源包

> **原作者 / Original Author**: [OhoDavi](https://www.nexusmods.com/stardewvalley/mods/1839)
> **原始版本 / Original Version**: 1.6.4 (Content Patcher mod for Stardew Valley)
> **转制 / Converted for**: StardewCraft (NeoForge 1.21 Minecraft mod)

本资源包包含 OhoDavi 绘制的动漫风格 NPC 肖像，已转换为 StardewCraft 资源包格式。
包含 96 个角色肖像（含季节/场景变体），所有默认样式均已包含在内。

This resource pack contains OhoDavi's anime-style NPC portraits, converted for StardewCraft.
Includes 96 character portraits (with seasonal/location variants).

## 安装方法 / How to Install

1. 将整个文件夹复制到 `.minecraft/resourcepacks/` 目录
2. 在游戏中 `选项 → 资源包` 启用即可
3. 如需自定义修改，可在此基础上编辑 `assets/stardewcraft/textures/portraits/` 中的图片

## 肖像格式 / Portrait Format

每个肖像文件是一张 **sprite sheet**（精灵表），由 64×64 像素的格子组成。
排列方式为 **2 列 × N 行**（宽度固定 128px，高度 = 行数 × 64px）。

表情索引从左到右、从上到下编号：
```
┌────────┬────────┐
│  #0    │  #1    │  64×64 each
├────────┼────────┤
│  #2    │  #3    │
├────────┼────────┤
│  #4    │  #5    │
│  ...   │  ...   │
└────────┴────────┘
  128px wide total
```

- **#0** = 默认/中性表情（neutral）
- **#1** = 开心/高兴（happy）
- **#2** = 悲伤（sad）
- **#3** = 独特表情 1
- **#4** = 独特表情 2
- **#5+** = 更多表情（数量因角色而异）

你可以自由增减行数，游戏会自动检测图片尺寸。
但宽度**必须是 128px（2列）或 64px（1列）**。

## 可替换的肖像文件 / Available Portrait Files

将下方列出的文件名放入 `assets/stardewcraft/textures/portraits/` 即可覆盖对应角色。

### 可婚角色 / Bachelors & Bachelorettes

| 角色 NPC | 文件名 | 尺寸 | 表情数 |
|----------|--------|------|--------|
| Abigail | `abigail.png` | 128×320 | 10 |
| Abigail (Beach) | `abigail_beach.png` | 128×320 | 10 |
| Abigail (Winter) | `abigail_winter.png` | 128×320 | 10 |
| Alex | `alex.png` | 128×384 | 12 |
| Alex (Beach) | `alex_beach.png` | 128×320 | 10 |
| Alex (Winter) | `alex_winter.png` | 128×384 | 12 |
| Elliott | `elliott.png` | 128×320 | 10 |
| Elliott (Beach) | `elliott_beach.png` | 128×320 | 10 |
| Elliott (Winter) | `elliott_winter.png` | 128×320 | 10 |
| Emily | `emily.png` | 128×256 | 8 |
| Emily (Beach) | `emily_beach.png` | 128×256 | 8 |
| Emily (Winter) | `emily_winter.png` | 128×256 | 8 |
| Haley | `haley.png` | 128×448 | 14 |
| Haley (Beach) | `haley_beach.png` | 128×448 | 14 |
| Haley (Winter) | `haley_winter.png` | 128×448 | 14 |
| Harvey | `harvey.png` | 128×384 | 12 |
| Harvey (Beach) | `harvey_beach.png` | 128×320 | 10 |
| Harvey (Winter) | `harvey_winter.png` | 128×384 | 12 |
| Leah | `leah.png` | 128×320 | 10 |
| Leah (Beach) | `leah_beach.png` | 128×256 | 8 |
| Leah (Winter) | `leah_winter.png` | 128×320 | 10 |
| Maru | `maru.png` | 128×320 | 10 |
| Maru (Beach) | `maru_beach.png` | 128×320 | 10 |
| Maru (Hospital) | `maru_hospital.png` | 128×192 | 6 |
| Maru (Winter) | `maru_winter.png` | 128×320 | 10 |
| Penny | `penny.png` | 128×448 | 14 |
| Penny (Beach) | `penny_beach.png` | 128×448 | 14 |
| Penny (Winter) | `penny_winter.png` | 128×448 | 14 |
| Sam | `sam.png` | 128×384 | 12 |
| Sam (Beach) | `sam_beach.png` | 128×384 | 12 |
| Sam (Joja) | `sam_jojamart.png` | 128×384 | 12 |
| Sam (Winter) | `sam_winter.png` | 128×384 | 12 |
| Sebastian | `sebastian.png` | 128×320 | 10 |
| Sebastian (Beach) | `sebastian_beach.png` | 128×256 | 8 |
| Sebastian (Winter) | `sebastian_winter.png` | 128×320 | 10 |
| Shane | `shane.png` | 128×384 | 12 |
| Shane (Beach) | `shane_beach.png` | 128×384 | 12 |
| Shane (Joja) | `shane_jojamart.png` | 128×384 | 12 |
| Shane (Winter) | `shane_winter.png` | 128×384 | 12 |

### 镇民 / Townfolk

| 角色 NPC | 文件名 | 尺寸 | 表情数 |
|----------|--------|------|--------|
| Caroline | `caroline.png` | 128×128 | 4 |
| Caroline (Beach) | `caroline_beach.png` | 128×128 | 4 |
| Caroline (Winter) | `caroline_winter.png` | 128×128 | 4 |
| Clint | `clint.png` | 128×256 | 8 |
| Clint (Beach) | `clint_beach.png` | 128×128 | 4 |
| Clint (Winter) | `clint_winter.png` | 128×256 | 8 |
| Demetrius | `demetrius.png` | 128×256 | 8 |
| Demetrius (Winter) | `demetrius_winter.png` | 128×256 | 8 |
| Evelyn | `evelyn.png` | 128×128 | 4 |
| Evelyn (Winter) | `evelyn_winter.png` | 128×128 | 4 |
| George | `george.png` | 128×128 | 4 |
| George (Winter) | `george_winter.png` | 128×128 | 4 |
| Gus | `gus.png` | 128×128 | 4 |
| Gus (Winter) | `gus_winter.png` | 128×128 | 4 |
| Jas | `jas.png` | 128×192 | 6 |
| Jas (Winter) | `jas_winter.png` | 128×192 | 6 |
| Jodi | `jodi.png` | 128×192 | 6 |
| Jodi (Beach) | `jodi_beach.png` | 128×128 | 4 |
| Jodi (Winter) | `jodi_winter.png` | 128×192 | 6 |
| Kent | `kent.png` | 128×192 | 6 |
| Kent (Winter) | `kent_winter.png` | 128×192 | 6 |
| Krobus | `krobus.png` | 128×320 | 10 |
| Krobus (Trenchcoat) | `krobus_trenchcoat.png` | 64×64 | 1 |
| Lewis | `lewis.png` | 128×192 | 6 |
| Lewis (Winter) | `lewis_winter.png` | 128×192 | 6 |
| Linus | `linus.png` | 128×192 | 6 |
| Linus (Winter) | `linus_winter.png` | 128×192 | 6 |
| Marnie | `marnie.png` | 128×192 | 6 |
| Marnie (Beach) | `marnie_beach.png` | 128×192 | 6 |
| Marnie (Winter) | `marnie_winter.png` | 128×192 | 6 |
| Pam | `pam.png` | 128×192 | 6 |
| Pam (Beach) | `pam_beach.png` | 128×192 | 6 |
| Pam (Winter) | `pam_winter.png` | 128×192 | 6 |
| Pierre | `pierre.png` | 128×192 | 6 |
| Pierre (Beach) | `pierre_beach.png` | 128×128 | 4 |
| Pierre (Winter) | `pierre_winter.png` | 128×192 | 6 |
| Robin | `robin.png` | 128×256 | 8 |
| Robin (Beach) | `robin_beach.png` | 128×256 | 8 |
| Robin (Winter) | `robin_winter.png` | 128×256 | 8 |
| Sandy | `sandy.png` | 128×128 | 4 |
| Vincent | `vincent.png` | 128×128 | 4 |
| Vincent (Winter) | `vincent_winter.png` | 128×128 | 4 |
| Willy | `willy.png` | 128×128 | 4 |
| Willy (Winter) | `willy_winter.png` | 128×128 | 4 |
| Wizard | `wizard.png` | 128×64 | 2 |

### 特殊角色 / Special Characters

| 角色 NPC | 文件名 | 尺寸 | 表情数 |
|----------|--------|------|--------|
| Answeringmachine | `answeringmachine.png` | 64×64 | 1 |
| Bear | `bear.png` | 128×128 | 4 |
| Birdie | `birdie.png` | 128×128 | 4 |
| Bouncer | `bouncer.png` | 64×64 | 1 |
| Dwarf | `dwarf.png` | 64×64 | 1 |
| Fizz | `fizz.png` | 128×128 | 4 |
| Gil | `gil.png` | 128×64 | 2 |
| Governor | `governor.png` | 128×128 | 4 |
| Grandpa | `grandpa.png` | 128×64 | 2 |
| Gunther | `gunther.png` | 64×64 | 1 |
| Henchman | `henchman.png` | 128×128 | 4 |
| Marlon | `marlon.png` | 64×64 | 1 |
| Morris | `morris.png` | 128×128 | 4 |
| Mrqi | `mrqi.png` | 128×64 | 2 |
| Parrotboy | `parrotboy.png` | 128×128 | 4 |
| Parrotboy (Winter) | `parrotboy_winter.png` | 128×128 | 4 |
| Safariguy | `safariguy.png` | 128×128 | 4 |
