# 天气系统图标需求

## 当前已有
- ✅ `sunny.png` - 晴天图标（12x8像素）

## 需要添加的图标（位置：`src/main/resources/assets/stardewcraft/textures/gui/`）

所有图标尺寸：**12x8像素**（与 sunny.png 相同）

### 1. `rainy.png` - 雨天图标
- 显示雨滴或云朵 + 雨滴
- 参考星露谷物语原版：LooseSprites/Cursors.png 中的雨天图标

### 2. `stormy.png` - 雷暴图标  
- 显示乌云 + 闪电
- 参考星露谷物语原版：LooseSprites/Cursors.png 中的雷暴图标

### 3. `snowy.png` - 雪天图标
- 显示雪花或云朵 + 雪花
- 参考星露谷物语原版：LooseSprites/Cursors.png 中的雪天图标

### 4. `windy.png` - 风/碎片天气图标
- 显示叶子飘动或风的图案
- 参考星露谷物语原版：LooseSprites/Cursors.png 中的碎片天气图标

## 星露谷物语原版参考位置

在原版的 `Content/LooseSprites/Cursors.png` 中：
- 天气图标通常在右上角 HUD 区域
- 可以参考原版 GameLocation.cs 的 `updateWeatherIcon()` 方法

## 使用方式

这些图标会显示在游戏右上角的时钟 HUD 中，位置坐标 (29, 16)，与 sunny.png 相同的位置。

## 天气类型映射

Java 代码中的天气类型 → 图标文件：
- `"Sun"` → `sunny.png` ✅ 已有
- `"Rain"` → `rainy.png` ⚠️ 需要
- `"Storm"` → `stormy.png` ⚠️ 需要  
- `"Snow"` → `snowy.png` ⚠️ 需要
- `"Wind"` → `windy.png` ⚠️ 需要

## 实现状态

✅ Java 代码已完成，会自动根据天气类型加载对应图标
✅ HUD 集成已完成，会在右上角时钟中显示
⚠️ 只需要美术提供剩余4个图标文件即可
